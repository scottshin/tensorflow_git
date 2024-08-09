#ifndef __GPIO_SW_H_
#define __GPIO_SW_H_

#ifdef  __cplusplus                                                             
# define BEGIN_DECLS  extern "C" {                                       
# define END_DECLS    }                                                  
#else                                                                           
# define BEGIN_DECLS                                                     
# define END_DECLS                                                       
#endif 

BEGIN_DECLS

int gpiosw_init(void);
int gpiosw_up(void);
int gpiosw_down(void);
int gpiosw_stop(void);

END_DECLS
#endif
