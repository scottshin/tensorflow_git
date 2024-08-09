#ifndef __UART__
#define __UART__

#include <termios.h>

class UARTComm
{
private:
	int     m_fd;
	termios m_termAttribute;
public:
	UARTComm() : m_fd(0) { }
	UARTComm(const char * dev_node) : m_fd(0) { open(dev_node); setupAttribute(0, B9600); }



	void open(const char * dev_node, mode_t mode = 0);
	void close(void);
	~UARTComm(void) { close(); }
	void setupAttribute(termios * t, int b );
	int write(const char * buf, const int len);
};

#endif

