package com.ymg.device;



		import java.nio.ByteBuffer;

public interface IDeviceMIDI {

	// native
	public void MIDI_INIT();
	public void MIDI_PLAY();
	public void MIDI_STOP();

	public void MIDI_DESTORY();


	/**
	 *
	 * @param type
	 * @param cmd
	 * @param size
	 */
	public void MIDI_SYSEX( int type, byte[]cmd, int size);
	public void MIDI_SEND3( int type, int cmd, int note, int vel );



	public void system_Exec(String strSyx );

	//
	public void MIDI_FILE( ByteBuffer buf, long pos, long len );

	public void MIDI_COMMAND(String str, int val );




	public void GPIO_COMMAND( String str);


}








