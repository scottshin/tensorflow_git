#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <limits.h>
#include <time.h>
#include <unistd.h>
#include <assert.h>
#include <sys/ioctl.h>
#include <termios.h>
#include <inttypes.h>

#include "sctrl.h"

#include "JNIHelp.h"
#include "log.h"

#define FALSE	0
#define TRUE	1

#define REQ_LENGTH  		4
#define MAX_LENGTH			20
#define RESPONSE_TIMEOUT_SEC	5
#define RESPONSE_TIMEOUT_USEC	0
#define BYTE_TIMEOUT_SEC		50
#define BYTE_TIMEOUT_USEC		50

/* Random number to avoid errno conflicts */
#define ENOBASE 112345678

/* Protocol exceptions */
enum {
	EXCEPTION_ILLEGAL_COMMAND = 0x01,
	EXCEPTION_ILLEGAL_DATA_VALUE,
	EXCEPTION_BAD_DATA,
	EXCEPTION_ACKNOWLEDGE,
	EXCEPTION_NEGATIVE_ACKNOWLEDGE,
	EXCEPTION_MAX
};

#define EXILCMD  (ENOBASE + EXCEPTION_ILLEGAL_COMMAND)
#define EXILDATA (ENOBASE + EXCEPTION_ILLEGAL_DATA_VALUE)
#define EXACK    (ENOBASE + EXCEPTION_ACKNOWLEDGE)
#define EXNACK   (ENOBASE + EXCEPTION_NEGATIVE_ACKNOWLEDGE)

typedef struct _sctrl_t
{
	char device[64];
	int baud;
	uint8_t data_bit;
	uint8_t stop_bit;
	char parity;
} sctrl_t;

static int debug = FALSE;
static int s;
static struct termios old_tios;
static sctrl_t sctrl;

const char *sctrl_strerror(int errnum)
{
	switch (errnum)
	{
		case EXILCMD:
			return "Illegal function";
		case EXILDATA:
			return "Illegal data value";
		case EXACK:
			return "Acknowledge";
		case EXNACK:
			return "Negative acknowledge";
		default:
			return strerror(errnum);
	}
}

void error_print(const char *context)
{
	if (debug) {
		fprintf(stderr, "ERROR %s", sctrl_strerror(errno));
		if (context != NULL) {
			fprintf(stderr, ": %s\n", context);
		} else {
			fprintf(stderr, "\n");
		}
	}
}

static ssize_t serial_send(const uint8_t *req, int req_length)
{
    return write(s, req, req_length);
}

static ssize_t serial_recv(uint8_t *rsp, int rsp_length)
{
    return read(s, rsp, rsp_length);
}

static int serial_connect(char *dev, int baud, char parity, int data_bit, int stop_bit)
{
    struct termios tios;
    speed_t speed;

    if (debug)
        printf("Opening %s at %d bauds (%c, %d, %d)\n",
               dev, baud, parity, data_bit, stop_bit);

    /* The O_NOCTTY flag tells UNIX that this program doesn't want
       to be the "controlling terminal" for that port. If you
       don't specify this then any input (such as keyboard abort
       signals and so forth) will affect your process

       Timeouts are ignored in canonical input mode or when the
       NDELAY option is set on the file via open or fcntl */
    s = open(dev, O_RDWR | O_NOCTTY | O_NDELAY | O_EXCL);
    if (s == -1) {
        fprintf(stderr, "ERROR Can't open the device %s (%s)\n",
                dev, strerror(errno));
        return -1;
    }

    /* Save */
    tcgetattr(s, &old_tios);

    memset(&tios, 0, sizeof(struct termios));

    /* C_ISPEED     Input baud (new interface)
       C_OSPEED     Output baud (new interface)
    */
    switch (baud) {
    case 110:
        speed = B110;
        break;
    case 300:
        speed = B300;
        break;
    case 600:
        speed = B600;
        break;
    case 1200:
        speed = B1200;
        break;
    case 2400:
        speed = B2400;
        break;
    case 4800:
        speed = B4800;
        break;
    case 9600:
        speed = B9600;
        break;
    case 19200:
        speed = B19200;
        break;
    case 38400:
        speed = B38400;
        break;
    case 57600:
        speed = B57600;
        break;
    case 115200:
        speed = B115200;
        break;
    default:
        speed = B9600;
        if (debug) {
            fprintf(stderr,
                    "WARNING Unknown baud rate %d for %s (B9600 used)\n",
                    baud, dev);
        }
    }

    /* Set the baud rate */
    if ((cfsetispeed(&tios, speed) < 0) ||
        (cfsetospeed(&tios, speed) < 0)) {
        return -1;
    }

    /* C_CFLAG      Control options
       CLOCAL       Local line - do not change "owner" of port
       CREAD        Enable receiver
    */
    tios.c_cflag |= (CREAD | CLOCAL);
    /* CSIZE, HUPCL, CRTSCTS (hardware flow control) */

    /* Set data bits (5, 6, 7, 8 bits)
       CSIZE        Bit mask for data bits
    */
    tios.c_cflag &= ~CSIZE;
    switch (data_bit) {
    case 5:
        tios.c_cflag |= CS5;
        break;
    case 6:
        tios.c_cflag |= CS6;
        break;
    case 7:
        tios.c_cflag |= CS7;
        break;
    case 8:
    default:
        tios.c_cflag |= CS8;
        break;
    }

    /* Stop bit (1 or 2) */
    if (stop_bit == 1)
        tios.c_cflag &=~ CSTOPB;
    else /* 2 */
        tios.c_cflag |= CSTOPB;

    /* PARENB       Enable parity bit
       PARODD       Use odd parity instead of even */
    if (parity == 'N') {
        /* None */
        tios.c_cflag &=~ PARENB;
    } else if (parity == 'E') {
        /* Even */
        tios.c_cflag |= PARENB;
        tios.c_cflag &=~ PARODD;
    } else {
        /* Odd */
        tios.c_cflag |= PARENB;
        tios.c_cflag |= PARODD;
    }

    /* Read the man page of termios if you need more information. */

    /* This field isn't used on POSIX systems
       tios.c_line = 0;
    */

    /* C_LFLAG      Line options

       ISIG Enable SIGINTR, SIGSUSP, SIGDSUSP, and SIGQUIT signals
       ICANON       Enable canonical input (else raw)
       XCASE        Map uppercase \lowercase (obsolete)
       ECHO Enable echoing of input characters
       ECHOE        Echo erase character as BS-SP-BS
       ECHOK        Echo NL after kill character
       ECHONL       Echo NL
       NOFLSH       Disable flushing of input buffers after
       interrupt or quit characters
       IEXTEN       Enable extended functions
       ECHOCTL      Echo control characters as ^char and delete as ~?
       ECHOPRT      Echo erased character as character erased
       ECHOKE       BS-SP-BS entire line on line kill
       FLUSHO       Output being flushed
       PENDIN       Retype pending input at next read or input char
       TOSTOP       Send SIGTTOU for background output

       Canonical input is line-oriented. Input characters are put
       into a buffer which can be edited interactively by the user
       until a CR (carriage return) or LF (line feed) character is
       received.

       Raw input is unprocessed. Input characters are passed
       through exactly as they are received, when they are
       received. Generally you'll deselect the ICANON, ECHO,
       ECHOE, and ISIG options when using raw input
    */

    /* Raw input */
    tios.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);

    /* C_IFLAG      Input options

       Constant     Description
       INPCK        Enable parity check
       IGNPAR       Ignore parity errors
       PARMRK       Mark parity errors
       ISTRIP       Strip parity bits
       IXON Enable software flow control (outgoing)
       IXOFF        Enable software flow control (incoming)
       IXANY        Allow any character to start flow again
       IGNBRK       Ignore break condition
       BRKINT       Send a SIGINT when a break condition is detected
       INLCR        Map NL to CR
       IGNCR        Ignore CR
       ICRNL        Map CR to NL
       IUCLC        Map uppercase to lowercase
       IMAXBEL      Echo BEL on input line too long
    */
    if (parity == 'N') {
        /* None */
        tios.c_iflag &= ~INPCK;
    } else {
        tios.c_iflag |= INPCK;
    }

    /* Software flow control is disabled */
    tios.c_iflag &= ~(IXON | IXOFF | IXANY);

    /* C_OFLAG      Output options
       OPOST        Postprocess output (not set = raw output)
       ONLCR        Map NL to CR-NL

       ONCLR ant others needs OPOST to be enabled
    */

    /* Raw ouput */
    tios.c_oflag &=~ OPOST;

    /* C_CC         Control characters
       VMIN         Minimum number of characters to read
       VTIME        Time to wait for data (tenths of seconds)

       UNIX serial interface drivers provide the ability to
       specify character and packet timeouts. Two elements of the
       c_cc array are used for timeouts: VMIN and VTIME. Timeouts
       are ignored in canonical input mode or when the NDELAY
       option is set on the file via open or fcntl.

       VMIN specifies the minimum number of characters to read. If
       it is set to 0, then the VTIME value specifies the time to
       wait for every character read. Note that this does not mean
       that a read call for N bytes will wait for N characters to
       come in. Rather, the timeout will apply to the first
       character and the read call will return the number of
       characters immediately available (up to the number you
       request).

       If VMIN is non-zero, VTIME specifies the time to wait for
       the first character read. If a character is read within the
       time given, any read will block (wait) until all VMIN
       characters are read. That is, once the first character is
       read, the serial interface driver expects to receive an
       entire packet of characters (VMIN bytes total). If no
       character is read within the time allowed, then the call to
       read returns 0. This method allows you to tell the serial
       driver you need exactly N bytes and any read call will
       return 0 or N bytes. However, the timeout only applies to
       the first character read, so if for some reason the driver
       misses one character inside the N byte packet then the read
       call could block forever waiting for additional input
       characters.

       VTIME specifies the amount of time to wait for incoming
       characters in tenths of seconds. If VTIME is set to 0 (the
       default), reads will block (wait) indefinitely unless the
       NDELAY option is set on the port with open or fcntl.
    */
    /* Unused because we use open with the NDELAY option */
    tios.c_cc[VMIN] = 0;
    tios.c_cc[VTIME] = 0;

    if (tcsetattr(s, TCSANOW, &tios) < 0) {
        return -1;
    }

    return 0;
}

static void serial_close(void)
{
    /* Closes the file descriptor in RTU mode */

    tcsetattr(s, TCSANOW, &old_tios);
    close(s);
}

static int serial_select(fd_set *rfds, struct timeval *tv, int length_to_read)
{
    int s_rc;
	printf("rfds : %x\n", *rfds);
    while ((s_rc = select(s+1, rfds, NULL, NULL, tv)) == -1)
	{
        if (errno == EINTR)
		{
            if (debug)
                fprintf(stderr, "A non blocked signal was caught\n");

            /* Necessary after an error */
            FD_ZERO(rfds);
            FD_SET(s, rfds);
        }
		else
            return -1;
    }

    if (s_rc == 0)
	{
        /* Timeout */
        errno = ETIMEDOUT;
        return -1;
    }

    return s_rc;
}

int serial_flush(void)
{
	int rc = tcflush(s, TCIOFLUSH);
	if (rc != -1 && debug) {
		printf("%d bytes flushed\n", rc);
	}
	return rc;
}

int _sleep_and_flush(void)
{
	/* usleep source code */
	struct timespec request, remaining;
//	request.tv_sec = response_timeout.tv_sec;
//	request.tv_nsec = ((long int)response_timeout.tv_usec % 1000000) * 1000;
	request.tv_sec = RESPONSE_TIMEOUT_SEC;
	request.tv_nsec = (RESPONSE_TIMEOUT_USEC % 1000000) * 1000;

	while (nanosleep(&request, &remaining) == -1 && errno == EINTR)
		request = remaining;

	return serial_flush();
}

static int build_req (uint8_t cmd, uint8_t *req, msg_type_t msg_type)
{
	int base;
	char c;

	req[0] = 'C';
	if(msg_type == MSG_CMD)
	{
		c = cmd >> 4 & 0xf;
		base = c < 10 ? 48 : 55; 
		req[1] = c + base;

		c = cmd & 0xf;
		base = c < 10 ? 48 : 55; 
		req[2] = c + base;

		printf("reqp[1] : %x\n", req[1]);
		printf("reqp[2] : %x\n", req[2]);
	}
	else
	{
		req[1] = 'R';
		c = cmd & 0xf;
		base = c < 10 ? 48 : 55; 
		req[2] = c + base;
	}
    req[3] = 0x0d;

	printf("req : %c%c%c\n", req[0], req[1], req[2]);
	printf("req[3] : 0x%x\n", req[3]);

    return REQ_LENGTH;
}

static int send_msg(uint8_t *msg, int msg_length)
{
	int rc;
	int i;

	if (debug)
	{
		for (i = 0; i < msg_length; i++)
			printf("[%.2X]", msg[i]);
		printf("\n");
	}

	do
	{
		rc = serial_send(msg, msg_length);
		if (rc == -1)
		{
			error_print(NULL);
			int saved_errno = errno;

			if ((errno == EBADF || errno == ECONNRESET || errno == EPIPE))
			{
				serial_close();
				serial_connect(sctrl.device, sctrl.baud, sctrl.parity,
						sctrl.data_bit, sctrl.stop_bit);
			}
			else
			{
				_sleep_and_flush();
			}
			errno = saved_errno;
		}
	}
	while (rc == -1);

	if (rc > 0 && rc != msg_length)
	{
		errno = EXILDATA;
		return -1;
	}

	return rc;
}

static int compute_length_to_read(uint8_t cmd, msg_type_t msg_type)
{

	int length = 0;

	if (msg_type == MSG_CMD) {
		length = 2;
	} else {
		switch (cmd)
		{
			case CMD_CR0:
			case CMD_CR4:
			case CMD_CR7:
				length = 3;
				break;
			case CMD_CR1:
				length = 2;
				break;
			case CMD_CR2:
			case CMD_CR5:
				length = 0;
				break;
			case CMD_CR3:
				length = 6;
				break;
			case CMD_CR6:
				length = 17; // ?????
				break;
			case CMD_CR8:
			case CMD_CR9:
			case CMD_CRA:
			case CMD_CRB:
			case CMD_CRC:
			case CMD_CRD:
				printf("Undefined command: 0x%02x\n", cmd);
				break;
			default:
				length = 0;
				break;
		}
	}

	return length;
}



static int receive_msg(uint8_t cmd, msg_type_t msg_type, uint8_t *msg)
{
	int rc;
	fd_set rfds;
	struct timeval tv;
	struct timeval *p_tv;
	int length_to_read;
	int msg_length = 0;

	if (debug)
		printf("Waiting for a confirmation...\n");

	/* Add a file descriptor to the set */
	FD_ZERO(&rfds);
	FD_SET(s, &rfds);

	length_to_read = compute_length_to_read(cmd, msg_type);
	printf("length_to_read : %d\n", length_to_read);

	tv.tv_sec = RESPONSE_TIMEOUT_SEC;
	tv.tv_usec = RESPONSE_TIMEOUT_USEC;
	p_tv = &tv;

	while (length_to_read != 0)
	{
		rc = serial_select(&rfds, p_tv, length_to_read);
		printf("rc : %d\n", rc);
		if (rc == -1)
		{
			error_print("select");
			int saved_errno = errno;

			if (errno == ETIMEDOUT)
			{
				_sleep_and_flush();
			}
			else if (errno == EBADF)
			{
				serial_close();
				serial_connect(sctrl.device, sctrl.baud, sctrl.parity,
						sctrl.data_bit, sctrl.stop_bit);
			}
			errno = saved_errno;

			return -1;
		}

		rc = serial_recv(msg + msg_length, length_to_read);
		printf("rc : %d\n", rc);
		if (rc == 0) {
			errno = ECONNRESET;
			rc = -1;
		}

		if (rc == -1)
		{
			error_print("read");
			if (errno == ECONNRESET || errno == ECONNREFUSED || errno == EBADF)
			{
				int saved_errno = errno;
				serial_close();
				serial_connect(sctrl.device, sctrl.baud, sctrl.parity,
						sctrl.data_bit, sctrl.stop_bit);
				/* Could be removed by previous calls */
				errno = saved_errno;
			}
			return -1;
		}

		/* Display the hex code of each character received */
		if (debug) {
			int i;
			for (i=0; i < rc; i++)
				printf("<%.2X>", msg[msg_length + i]);
		}

		/* Sums bytes received */
		msg_length += rc;
		/* Computes remaining bytes */
		length_to_read -= rc;

		if (length_to_read > 0)
		{
//			tv.tv_sec = byte_timeout.tv_sec;
//			tv.tv_usec = byte_timeout.tv_usec;
			tv.tv_sec = BYTE_TIMEOUT_SEC;
			tv.tv_usec = BYTE_TIMEOUT_USEC;
			p_tv = &tv;
		}

		if(msg[msg_length-1] == 0x0d)
			break;
	}

	if (debug)
		printf("\n");

	return msg_length;
}

static int check_confirmation(uint8_t *rsp, int rsp_length,
		msg_type_t msg_type, char *result)
{
	int length = 0;

	if(rsp[0] == '?')
		return -1;

	if (msg_type == MSG_CMD)
	{
		if(rsp[0] == 0x06)
			return 0;
		else
			return -1;
	}
	else
   	{
		memcpy(result, rsp, rsp_length-1);
		length = rsp_length - 1;
	}

	return length;
}

int sctrl_send_cmd(uint8_t cmd, msg_type_t msg_type, char *result)
{
	int rc;
	int req_length;
	uint8_t req[MAX_LENGTH];
	uint8_t rsp[MAX_LENGTH];

	req_length = build_req (cmd, req, msg_type);


	LOGI("Send sctrl msg ");
	rc = send_msg(req, req_length);
	if (rc > 0)
	{
		rc = receive_msg(cmd, msg_type, rsp);
		if (rc == -1)
		{

			LOGI("recive msg fail ");
			return -1;

			}

		rc = check_confirmation(rsp, rc, msg_type, result);
	}

	return rc;
}

int sctrl_connect(const char *dev, int baud, char parity, int data_bit, int stop_bit)
{
	debug = TRUE;

	strncpy(sctrl.device, dev, 64);
	sctrl.baud = baud;
	sctrl.parity = parity;
	sctrl.data_bit = data_bit;
	sctrl.stop_bit = stop_bit;

	return serial_connect(sctrl.device, sctrl.baud, sctrl.parity, sctrl.data_bit,
			sctrl.stop_bit);
}

int sctrl_close(void)
{
	serial_close();
	return 0;
}
