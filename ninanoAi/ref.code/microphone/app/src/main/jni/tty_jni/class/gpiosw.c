#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#define GPJ0_BASE	0xe0200240
#define GPJ0_CTRL	(GPJ0_BASE+0x00)
#define GPJ0_DAT	(GPJ0_BASE+0x04)

#define SW0_GPIO	0
#define SW1_GPIO	1
#define	DEVFILE		"/dev/regm"

typedef struct  __ioctl_msg_s {
	int rc;
	uint32_t base;
	uint32_t offset;
	uint32_t val;
}  ioctl_msg_s;

#define     RET_SUCCESS     (0)
#define     RET_FAILED      (1)
#define     RET_MEM_ERR     (2)
#define     RET_UNKNOWN_CMD (3)

#define REGISTER_MAGIC		(0xd4)
#define IOC_GETREGISTER		_IO(REGISTER_MAGIC, 1)
#define IOC_SETREGISTER		_IO(REGISTER_MAGIC, 2)

static int reg_write(unsigned int addr, unsigned int offset, unsigned int val)
{
	ioctl_msg_s msg;
	int fd;
	int ret;

	fd = open(DEVFILE, O_RDWR);
	if (fd < 0)
	{
		printf("Open pseudo device failed\n");
		return -1;
	}

	msg.base = addr;
	msg.offset = 0;
	msg.val = val;
	ret = ioctl(fd, IOC_SETREGISTER, &msg );
	if(ret <0 )
		fprintf(stderr, "error: ioctl.");

	close(fd);

	return ret;
}

static int reg_read(unsigned int addr, unsigned int offset, unsigned int *val)
{
	ioctl_msg_s msg;
	int fd;
	int ret;


	fd = open(DEVFILE, O_RDONLY);
	if (fd < 0)
	{
		printf("Open pseudo device failed\n");
		return -1;
	}

	msg.base = addr;
	msg.offset = 0;
	msg.val = 0;

	ret = ioctl(fd, IOC_GETREGISTER, &msg );
	if(ret <0 )
		fprintf(stderr, "error: ioctl.");

	close(fd);
	*val = msg.val;

	return ret;
}

int gpiosw_init(void)
{
	unsigned int val;


	gpiosw_stop();


	// initialize 
	if(reg_read(GPJ0_CTRL, 0, &val) != RET_SUCCESS)
		return -1;

	val &= ~(0xff);
	val |= (0x11);

	if(reg_write(GPJ0_CTRL, 0, val) != RET_SUCCESS)
		return -1;


	return 0;
}

int gpiosw_up(void)
{
	unsigned int val;
	int gpio;

	if(reg_read(GPJ0_DAT, 0, &val) != RET_SUCCESS)
		return -1;

	val &= (~0x3);
	val |= 0x1;
	if(reg_write(GPJ0_DAT, 0, val) != RET_SUCCESS)
		return -1;

	return 0;
}

int gpiosw_down(void)
{
	unsigned int val;
	int gpio;

	if(reg_read(GPJ0_DAT, 0, &val) != RET_SUCCESS)
		return -1;

	val &= (~0x3);
	val |= 0x2;
	if(reg_write(GPJ0_DAT, 0, val) != RET_SUCCESS)
		return -1;

	return 0;
}

int gpiosw_stop(void)
{
	unsigned int val;
	int gpio;

	if(reg_read(GPJ0_DAT, 0, &val) != RET_SUCCESS)
		return -1;

	val &= (~0x3);
	val |= 0x3;
	if(reg_write(GPJ0_DAT, 0, val) != RET_SUCCESS)
		return -1;

	return 0;
}
