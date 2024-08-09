#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include "Uart.h"


#include "log.h"

void UARTComm::setupAttribute(termios * t, int b )
{
	if(m_fd > 0) {
		if(t == NULL) {
			memset( &m_termAttribute, 0, sizeof(m_termAttribute) );
			m_termAttribute.c_cflag = b;// B9600; //B115200;
			m_termAttribute.c_cflag |= CS8;
			m_termAttribute.c_cflag |= CLOCAL;
			m_termAttribute.c_cflag |= CREAD;
			m_termAttribute.c_iflag = 0;
			m_termAttribute.c_oflag = 0;
			m_termAttribute.c_lflag = 0;
			m_termAttribute.c_cc[VTIME] = 0;
			m_termAttribute.c_cc[VMIN] = 1;
		}
		else
			m_termAttribute = *t;
	
		tcflush(m_fd, TCIFLUSH );
		tcsetattr(m_fd, TCSANOW, &m_termAttribute);
	}



}

void UARTComm::close(void)
{
	if(m_fd > 0) {
		::close(m_fd);
		m_fd = 0;
	}
}

void UARTComm::open(const char * dev_node, mode_t mode)
{
	if(mode == 0)
	{
		mode = (O_RDWR | O_NOCTTY | O_NONBLOCK);
	}

	m_fd = ::open(dev_node, mode);


    LOGI("m_fd is device node %s, m_fd : %d", dev_node, m_fd );
}


int UARTComm::write(const char * buf, int len)
{
	ssize_t ret, nr;

	if(m_fd <= 0)
	{
    	LOGE("com port fd is 0 or minus");
		return 0;
	}
	
	while(len != 0 && (ret = ::write(m_fd, buf, len)) != 0) {
		if(ret == -1) {
			if(errno == EINTR)
				continue;

			// error
			break;
		}
		len -= ret;
		buf += ret;
	}
	
	return 1;
}

