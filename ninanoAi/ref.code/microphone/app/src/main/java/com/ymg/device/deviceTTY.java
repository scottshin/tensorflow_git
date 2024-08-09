package com.ymg.device;


import android.util.Log;
//
//import com.devscott.karaengine.Global;
//import com.devscott.karaengine.IDeviceMIDI;
//import com.quarternote.androke.ATVKaraokeActivity;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

//import jp.kshoji.driver.midi.device.MidiOutputDevice;

public class deviceTTY implements IDeviceMIDI {


	private static String TAG = deviceTTY.class.getName();

	static {
		System.loadLibrary("ttyjni");
//		deviceTTY.smartad_gpiosw_init();
	}


	public native int tty_init();

	public native int tty_send_sysex( byte[] cmd, int len );
	public native int tty_send3(int cmd, int para1, int para2);
	public native int tty_send2(int cmd, int para1);
	public native int tty_play();
//	public native int smartad_gpiosw_init();

//	public native

	public native int tty_cva( int val );
	public native int tty_cvm( int val );

	public native int tty_read( int val);


	public native int gpio_set_score_mode();		// Only MICROPHONE
	public native int gpio_set_record_mode();		// MIX of MIC + MUSIC MIX
	public native int gpio_set_adb();



	public deviceTTY() {
	}

	public void close() {
	}

	@Override
	public void MIDI_INIT()
	{
		tty_init();
	}

	@Override
	public void MIDI_SYSEX(int type, byte[] cmd, int size) {

		tty_send_sysex( cmd, size);
	}

	@Override
	public void MIDI_SEND3( int type, int cmd, int para, int vol)
	{
		int port = (type & 0xfff0);
		if ( port == 0x0 || port == 0x90 || port == 0x140 )
		{


			int size = ((cmd &0xF0) == 0xC0 || (cmd&0xF0) == 0xD0) ? 2:3;
			if ( size == 3 ) {

//				/* mic debug */
//				{
//					if ((cmd & 0x0F) != 0)
//						return;
//					if ( vol  != 0)
//						vol =  90 ;
//				}

				tty_send3((byte) cmd, (byte) para, (byte) vol);

//				if ( cmd == 0x99 )
//					Log.e(TAG, "0x99 "+ para + "  " + vol );
//				if ( cmd == 0x97 )
//					Log.e(TAG, "0x97 "+ para + "  " + vol );
			}
			else
				tty_send2( (byte)cmd, (byte)para);
		}
	}

	@Override
	public void system_Exec(String strSyx) {

		int nPort = 0;


		File file = new File(strSyx);
			try {
				FileInputStream fis = new FileInputStream(file);
				int readcount = (int)file.length();
				byte[] buffer = new byte[readcount];
				fis.read(buffer);


				//	for(int i=0 ; i<file.length();i++){
				//		Log.d(TAG, ""+buffer[i]);
				//	}

				MIDI_SYSEX(nPort, buffer, readcount );

				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}


		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void MIDI_FILE(ByteBuffer buf, long pos, long len) {

	}


	@Override
	public void MIDI_PLAY() {

//		tty_play();
	}

	@Override
	public void MIDI_STOP() {

	}

	@Override
	public void MIDI_DESTORY() {

	}


	@Override
	public void MIDI_COMMAND(String str, int val) {

		if ( str.equals("MASTER_VOL") == true )
		{
			// val 0 ~ 15
			tty_cva( val );
			Log.e(TAG, "TTY_CVA (0~15) :" + val);
			if ( 0 < tty_read( val ) ) {

				Log.e(TAG, "TTY Readed");
			}
		}
		else
		if ( str.equals("MIC_VOL") == true)
		{
			// val 0 ~ 15
			tty_cvm( val );
			Log.e(TAG, "TTY_CVM (0~15) :" + val);
			if ( 0 < tty_read( val ) ) {
				Log.e(TAG, "TTY Readed");
			}
		}
	}


	@Override
	public void GPIO_COMMAND( String str)
	{
		if (str.equals("GPIO_SCORE") == true)
		{

			Log.e(TAG, "SET_SCORE_MODE");
			gpio_set_score_mode();
		}
		else
		if ( str.equals("GPIO_RECORD") == true)
		{
			Log.e(TAG, "SET_RECORD_MODE");
			gpio_set_record_mode();
		}
		else
		if ( str.equals("GPIO_ADB") == true )
		{
			gpio_set_adb();
		}
	}


}







