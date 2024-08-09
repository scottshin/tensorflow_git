#include "jni.h"
#include "JNIHelp.h"
#include "log.h"


#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <sys/time.h>
#include <pthread.h>
#include <sys/time.h>
#include <signal.h>
#include <stdlib.h>





#include "decomp.h"



#include <fcntl.h>
#include <termios.h>


#define LOG_TAG 		"native"



#include "uart.h"
#include "sctrl.h"
//#include 	<pthread.h>



termios m_termAttribute;
/**
 *
 */

static BYTE SongBuff[gbSONG_BUFFER_SIZE];


MidiPlayInfo g_y2k_midi;




static int readFileFrom(char * const read_buffer, const char * filename, const int file_size)
{
	ssize_t ret = 1;
	char * buf = read_buffer;
	int fd  = open(filename, O_RDONLY);
	int len = file_size;

	if(fd == -1)
		return -1;

	while(len != 0 && (ret = read(fd, buf, len)) != 0) {
		if(ret == -1) {
			if(errno == EINTR)
				continue;

			ret = -2;
			break;
		}
		len -= ret;
		buf += ret;
	}

	close(fd);
	return (ret == -2) ? -2 : file_size;
}
static int getFileSize(const char * filename)
{
	struct stat buf;
	int    size = -1;

    if(stat(filename, &buf) == 0)
		size = buf.st_size;

	return size;
}


void
MidiParser(const char * midifile, const char * kasafile)
{
//	CurCharCountry = DSP_JAPAN;

//	memset( (char *)&MidiInf,  0x00, sizeof(MIDI_INFO_T ) );
//	memset( (char *)&LyricInf, 0x00, sizeof(LYRIC_INFO_T) );

//	g_logger.Print(
//		  "%s size => %d\n"
//		, kasafile
//		, readFileFrom((char * const)KasaBuff, kasafile, LyricInf.nDataSize = getFileSize(kasafile))
//	);


LOGI("midisize %d", getFileSize(midifile) );

    readFileFrom((char * const)SongBuff, midifile, getFileSize(midifile));




/*
	MidiTrackParsing ();
    GGetSet_SongInfo();
	LyricConvertToYMG();
	MidiLyricSyncMake(strwidth);

	LyricInf.nTitleActive = ON;
	LyricInf.nGanjuActive = ON;
	LyricInf.nPlayActive  = ON;

	MidiTempoTimeOld = _gettimeUS();
*/
}





MidiPlayInfo * getMidiInfo(void)
{
	return &g_y2k_midi;
}

static MidiPlayInfo * init_y2k_midi_info(MidiPlayInfo * info, UARTComm * midi_dev)
{


    LOGI("init_y2k_midi_info ");

	info->delay = 7*1000000;
	info->init_delay = 500*1000;

	strcpy(info->init_midi, "!CNU55000");
	strcpy(info->start_midi, "!KST");
	strcpy(info->stop_midi, "!KTO");
	info->init_midi[info->len_init_midi = strlen(info->init_midi)]    = (BYTE)0x0A;
	info->start_midi[info->len_start_midi = strlen(info->start_midi)] = (BYTE)0x0A;
	info->stop_midi[info->len_stop_midi = strlen(info->stop_midi)]    = (BYTE)0x0A;
	info->len_init_midi++;
	info->len_start_midi++;
	info->len_stop_midi++;
	info->midi_dev = midi_dev;
	return info;
}


void * Y2K_midi_play(void * arg)
{
/*
LOGI("#####   Y2K_midi_play....");


//LOGI("sleep %d", g_y2k_midi.init_delay);
//	MidiPlayInfo * mi = (MidiPlayInfo * )&arg;

    MidiPlayInfo * mi = (MidiPlayInfo*) &g_y2k_midi;
//	UARTComm * y2k = mi->midi_dev;
	UARTComm *y2k = (UARTComm*) &g_midi_dev;



LOGI("start cmd  length %s %d", mi->init_midi, mi->len_init_midi );

	if(y2k->write(mi->init_midi, mi->len_init_midi) == 0)
	{
	    LOGE("write err..");
	    return NULL;
	}

LOGI("sleep %d", mi->init_delay);
	usleep(mi->init_delay * 10 );
LOGI("delay out");

LOGI("start cmd  length %s %d", mi->start_midi, mi->len_start_midi );

	if(y2k->write(mi->start_midi, mi->len_start_midi) == 0)
	{
	    LOGE("write 2 err..");
		return NULL;
	}

	    LOGE("write playing ..");
//	while(LyricInf.nPlayActive == ON)
	usleep(1000*10000);


    LOGE("write stop... ..");
//	y2k->write(mi->stop_midi, mi->len_stop_midi);
*/
	return NULL;
}

UARTComm g_midi_dev[3];

void init_ymg()
{


	LOGI("Send serial..");
	char buf[] = { 0x21, 0x42, 0x4f, 0x4f, 0x0a };      // !BOO
	int n = g_midi_dev[0].write(buf, 5 );

/*
	char buf[] = { 0x21, 'C', 'N', 'U', '5', '5', '0', '0', '0', 0x0a };
	int n = g_midi_dev[0].write(buf, 10 );

	char buf2[] = { 0x21, 'K', 'S', 'T', 0x0a };
	n = g_midi_dev[0].write(buf2, 5 );
*/

}


static jint tty_init( JNIEnv* env, jobject clazz)
{


	LOGI(" tty_init_____________");


//	UARTComm g_midi_dev("/dev/ttyS0");


	g_midi_dev[0].open( "/dev/ttyS3" ) ;
	g_midi_dev[0].setupAttribute( 0, B9600 );
	

	g_midi_dev[1].open( "/dev/ttyS1" ) ;
	g_midi_dev[1].setupAttribute( 0, B115200 );

	g_midi_dev[2].open( "/dev/ttyS2" ) ;
	g_midi_dev[2].setupAttribute( 0, B115200 );



	LOGI("tty_init ");


    MidiParser(
		"/storage/emulated/0/Movies/JP00001A.MID"
		, "/storage/emulated/0/Movies/JP00001A.TXT"
	);

	LOGI("midiparser");

//fd =sctrl_connect( "/dev/ttyS1", 115200, 8, 0, 1 );


    init_ymg();

//	char buf[] = { 0x90, 0x40, 0x7f, 0x0};
//	int n = g_midi_dev.write(buf, 3 );

//	LOGI("write retrun  %d", n );


/*

	usleep(1000);

    char buf2[] = { 0x90, 0x43, 0x7f };
	midi1.write( buf2, 3);
*/
//send_msg( buf, 3);

}




static jint tty_send_sysex( JNIEnv* env, jobject clazz, jbyteArray array, jint len2 ) 
{




	jboolean isCopy;

	size_t len = env->GetArrayLength( array );
	jbyte *nativeBytes = env->GetByteArrayElements(array, &isCopy);
	{
	
	unsigned char *midibuf = NULL;
	midibuf = new unsigned char [len];
	memcpy( midibuf, nativeBytes, len );

	g_midi_dev[1].write( (char*)midibuf, len );

	}
	env->ReleaseByteArrayElements( array, nativeBytes, JNI_ABORT);





//	jbyte* bufferPtr = (*env)->GetByteArrayElements( env, array, 0 );
//	jsize lengthOfArray = (*env).GetArrayLength( env, array );

//	g_midi_dev[1].write( (char*)bufferPtr, len );

//	(*env)->ReleaseByteArrayElements( env, array, bufferPtr, 0 );
}

/**
 *
 */
static jint tty_send3( JNIEnv* env, jobject clazz, jint cmd, jint para1, jint para2 )
{

//	LOGI("Send tty.. %x %x %x", cmd, para1, para2);
	char buf[] = { cmd, para1, para2, 0 };
//	if ( para2 == 0 )
//		g_midi_dev[1].write(buf, 2 );
//	else
		g_midi_dev[1].write(buf, 3 );

}

static jint tty_send2( JNIEnv* env, jobject clazz, jint cmd, jint para1 )
{

//	LOGI("Send tty.. %x %x %x", cmd, para1, para2);
	char buf[] = { cmd, para1, 0, 0 };
	g_midi_dev[1].write(buf, 2 );
}


void play2(void *args)
{
#if 0
	long        newTime;
	int         idog_cnt;
	const  int  MIDI_START_DELAY = 500*000;

	pthread_t thread_id;
	//LENGTH_FUNC_T * invokers[] = {renderer, lineputter, bridge};
	/*int ret = pthread_create(&thread_id, NULL, JamakRender, (void *)invokers);
	if(!ret) {
		errno = ret;
		perror("pthread_create");
		return;
	}*/
	int cnt = 0;
	char init_midi[30]  = "!CNU00001";
	char start_midi[30] = "!KST";
	char stop_midi[30]  = "!KTO";
	int  len_start_midi, len_stop_midi, len_init_midi;

	init_midi[len_init_midi = strlen(init_midi)]    = (BYTE)0x0A;
	start_midi[len_start_midi = strlen(start_midi)] = (BYTE)0x0A;
	stop_midi[len_stop_midi = strlen(stop_midi)]    = (BYTE)0x0A;
	len_init_midi++;
	len_start_midi++;
	len_stop_midi++;
	UARTComm midi("/dev/ttyS0");

//	if(IS_Y2K_MIDI())
	{
		if(midi.write(init_midi, len_init_midi) == 0)
			return;

		usleep(MIDI_START_DELAY);

		if(midi.write(start_midi, len_start_midi) == 0)
			return;
	}
//	else
//		usleep(MIDI_START_DELAY);


    usleep(1000 * 10 );
/*
	while(LyricInf.nPlayActive == ON) {
		idog_cnt = 0;
		newTime = _gettimeUS();

		while( (newTime-MidiTempoTimeOld) >= (long)MidiTempoClock ) {
		    idog_cnt++;
		    if( idog_cnt >= (int)0x200000 ) break;
			MidiTempoTimeOld += MidiTempoClock;
			MidiPlayProc();
		}

		if(cnt++ == 1) {
			cnt = 0;
			ColoringProc(args);
		}
		usleep((int)MidiInf.nTickTimeUS);
	}

	if(IS_Y2K_MIDI())
*/
		midi.write(stop_midi, len_stop_midi);

	#endif
}



static jint tty_play( JNIEnv* env, jobject clazz )
{
LOGI("sleep %d", g_y2k_midi.init_delay);

   init_y2k_midi_info ( &g_y2k_midi, &g_midi_dev[0] );

LOGI("sleep %d", g_y2k_midi.init_delay);
LOGI("sleep %d", g_y2k_midi.init_delay);
LOGI("sleep %d", g_y2k_midi.init_delay);
LOGI("sleep %d", g_y2k_midi.init_delay);

	Y2K_midi_play(&g_y2k_midi);

//play2(0);

}

/**
 *
 */

 static jint tty_cva( JNIEnv* env, jobject clazz, jint value)
 {

	LOGI("Send serial..  MUSIC VOL");
	char buf[] = { 0x21, 'C', 'V', 'A', 0x0, 0x0a };      // !CVA#\n 
	buf[4] = (0x30 + value);
	int n = g_midi_dev[0].write(buf, 6 );

 }
/*
 * JNI registration.
 */
static JNINativeMethod gMethods[] =
{
    /* name, signature, funcPtr */
    { "tty_init", "()I", (void*)  tty_init},
    { "tty_send3", "(III)I", (void*)  tty_send3},
    { "tty_send2", "(II)I", (void*)  tty_send2},
    { "tty_cva", "(I)I", (void*)  tty_cva},
    { "tty_play", "()I", (void*) tty_play },
    { "tty_send_sysex", "([BI)I", (void*)  tty_send_sysex},
  //  { "Huffman_DSR", "(J[BJJ)I", (void*)   Huffman_DSR },
   // { "Huffman_DSR", "(Ljava/lang/String;[BJJ)I", (void*)   Huffman_DSR },
};


//static const char *classPathName = "com/devscott/karaengine/deviceTTY";
static const char *classPathName = "com/ymg/device/deviceTTY";
int register_decomp(JNIEnv* env)
{
    return jniRegisterNativeMethods(env, classPathName, gMethods, NELEM(gMethods));
}

