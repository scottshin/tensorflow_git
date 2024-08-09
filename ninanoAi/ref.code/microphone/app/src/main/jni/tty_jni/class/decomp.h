#ifndef __decomp_h__
#define __decomp_h__



typedef unsigned char   BYTE;
typedef short int     _2BYTE;
typedef unsigned int  _4BYTE;
typedef unsigned long _8BYTE;
//typedef int      WORD;

#define ULONG        _4BYTE
#define WORD         _2BYTE
#define DWORD        _4BYTE



#define MELODY_CH               3
#define                         gbSONG_BUFFER_SIZE        0x50000
#define                         gbTEMP_BUFFER_SIZE        0x20000


class UARTComm;
#define MIDI_CMD_LEN 30
typedef struct _MidiPlayInfo
{
	int delay;
	int init_delay;
	char init_midi[MIDI_CMD_LEN];
	char start_midi[MIDI_CMD_LEN];
	char stop_midi[MIDI_CMD_LEN];
	int len_start_midi;
	int len_stop_midi;
	int len_init_midi;
	UARTComm * midi_dev;
} MidiPlayInfo;

int MidiTrackParsing(void);
void GGetSet_SongInfo(void);
void MidiLyricSyncMake(void);
void LyricConvertToYMG(void);
void * Y2K_midi_play(void * arg);
MidiPlayInfo * getMidiInfo(void);


#endif
