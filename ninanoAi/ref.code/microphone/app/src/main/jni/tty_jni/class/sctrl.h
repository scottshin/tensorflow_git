#ifndef __SCTRL_H__
#define __SCTRL_H__

#ifdef  __cplusplus                                                             
# define BEGIN_DECLS  extern "C" {                                       
# define END_DECLS    }                                                  
#else                                                                           
# define BEGIN_DECLS                                                     
# define END_DECLS                                                       
#endif 

BEGIN_DECLS

#define	CMD_CR0			0x0
#define	CMD_CR1			0x1
#define	CMD_CR2			0x2
#define	CMD_CR3			0x3
#define	CMD_CR4			0x4
#define	CMD_CR5			0x5
#define	CMD_CR6			0x6
#define	CMD_CR7			0x7
#define	CMD_CR8			0x8
#define	CMD_CR9			0x9
#define	CMD_CRA			0xa
#define	CMD_CRB			0xb
#define	CMD_CRC			0xc
#define	CMD_CRD			0xd

typedef enum
{
    MSG_CMD,
    MSG_RDSTATUS
} msg_type_t;

int sctrl_connect(const char *dev, int baud, char parity, int data_bit, int stop_bit);
int sctrl_close(void);
int sctrl_send_cmd(uint8_t cmd, msg_type_t msg_type, char *result);

END_DECLS
#endif
