/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */
package com.qn.microphone.activity;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.widget.RadioButton;
import android.widget.Toast;

import com.qn.microphone.R;
import com.ymg.device.deviceTTY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class AudioRecordActivity extends Activity
{

	private static final String LOG_TAG = "AudioRecordTest";



	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	short[] audioData;



	private static String mFileName = null;

	private Button mRecordButton = null;
	private AudioRecord mRecorder = null;

	private Button   mPlayButton = null;
	private MediaPlayer   mPlayer = null;

	private Button mMidiButton = null;
	private Button mWaveButton = null;

	public deviceTTY midi = null ;//new deviceTTY();

	RadioButton		pin1;
	RadioButton		pin2;

	RadioButton   option1;
	RadioButton   option2;
	RadioButton   option3;
	RadioButton   option4;
	RadioButton   option5;
	RadioButton   option6;
	RadioButton   option7;
	RadioButton   option8;


	private int mPin = MediaRecorder.AudioSource.MIC;




	int bufferSize = 0;
	int blockSize = 256;
	boolean isRecording = false;
	private Thread recordingThread = null;




	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording() {

		mRecorder = new AudioRecord(mPin,RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
		int i = mRecorder.getState();
		if ( i==1)
			mRecorder.startRecording();
		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();

	}

	private void stopRecording() {
		if (null != mRecorder){
			isRecording = false;
			int i = mRecorder.getState();
			if (i==1)
				mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			recordingThread = null;
		}
		mFileName = getFilename();
		copyWaveFile(getTempFilename(), mFileName );
		deleteTempFile();
	}

//	class RecordButton extends Button {
//		boolean mStartRecording = true;
//
//		OnClickListener clicker = new OnClickListener() {
//			public void onClick(View v) {
//				onRecord(mStartRecording);
//				if (mStartRecording) {
//					setText("Stop recording");
//				} else {
//					setText("Start recording");
//				}
//				mStartRecording = !mStartRecording;
//			}
//		};
//
//		public RecordButton(Context ctx) {
//			super(ctx);
//			setText("Start recording");
//			setOnClickListener(clicker);
//		}
//	}
//
//	class PlayButton extends Button {
//		boolean mStartPlaying = true;
//
//		OnClickListener clicker = new OnClickListener() {
//			public void onClick(View v) {
//				onPlay(mStartPlaying);
//				if (mStartPlaying) {
//					setText("Stop playing");
//				} else {
//					setText("Start playing");
//				}
//				mStartPlaying = !mStartPlaying;
//			}
//		};
//
//		public PlayButton(Context ctx) {
//			super(ctx);
//			setText("Start playing");
//			setOnClickListener(clicker);
//		}
//	}

	public AudioRecordActivity() {

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/test.wav";
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);



		setContentView(R.layout.activity_record );		// custmize layout



		// buffer prepare
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING)*3;
		audioData = new short [bufferSize]; //short array that pcm data is put into.





		pin1 = (RadioButton) findViewById(R.id.buttonMIC );
		pin2 = (RadioButton) findViewById(R.id.buttonMIX);

		pin1.setChecked( true );

		RadioButton.OnClickListener pinOnClickListener = new RadioButton.OnClickListener()
		{

			public void onClick(View v) {

				if ( pin1.isChecked() ) {


					if ( midi != null)
					midi.GPIO_COMMAND("GPIO_SCORE");

					Toast.makeText(
							AudioRecordActivity.this, "MICROPHONE ONLY",
							Toast.LENGTH_LONG).show();
				}
				if ( pin2.isChecked() )
				{
					if ( midi != null)
					midi.GPIO_COMMAND("GPIO_RECORD");

					Toast.makeText(
							AudioRecordActivity.this, "MIC + MUSIC MIX",
							Toast.LENGTH_LONG).show();
				}




			}

		};

		pin1.setOnClickListener( pinOnClickListener );
		pin2.setOnClickListener( pinOnClickListener );



		//
		// =================
		//
		option1 = (RadioButton) findViewById(R.id.radioButton1);
		option2 = (RadioButton) findViewById(R.id.radioButton2);
		option3 = (RadioButton) findViewById(R.id.radioButton3);
		option4 = (RadioButton) findViewById(R.id.radioButton4);
		option5 = (RadioButton) findViewById(R.id.radioButton5);
		option6 = (RadioButton) findViewById(R.id.radioButton6);
		option7 = (RadioButton) findViewById(R.id.radioButton7);
		option8 = (RadioButton) findViewById(R.id.radioButton8);


		option1.setChecked(true);

		RadioButton.OnClickListener optionOnClickListener = new RadioButton.OnClickListener()
		{

			public void onClick(View v) {



				if ( option1.isChecked() )
					mPin = MediaRecorder.AudioSource.MIC;
				if ( option2.isChecked() )
					mPin = MediaRecorder.AudioSource.VOICE_UPLINK;
				if ( option3.isChecked() )
					mPin = MediaRecorder.AudioSource.VOICE_DOWNLINK;
				if ( option4.isChecked() )
					mPin = MediaRecorder.AudioSource.VOICE_CALL;
				if ( option5.isChecked() )
					mPin = MediaRecorder.AudioSource.CAMCORDER;
				if ( option6.isChecked() )
					mPin = MediaRecorder.AudioSource.VOICE_RECOGNITION;
				if ( option7.isChecked() )
					mPin = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
				if ( option8.isChecked() )
					mPin = MediaRecorder.AudioSource.REMOTE_SUBMIX;


				Toast.makeText(
						AudioRecordActivity.this, "Source Selected -  Pin : " + mPin ,
						Toast.LENGTH_LONG).show();

			}

		};


		option1.setOnClickListener( optionOnClickListener );
		option2.setOnClickListener( optionOnClickListener );
		option3.setOnClickListener( optionOnClickListener );
		option4.setOnClickListener( optionOnClickListener );
		option5.setOnClickListener( optionOnClickListener );
		option6.setOnClickListener( optionOnClickListener );
		option7.setOnClickListener( optionOnClickListener );
		option8.setOnClickListener( optionOnClickListener );





		mPlayButton = (Button) findViewById(R.id.buttonPLAY);
		mRecordButton = (Button) findViewById(R.id.buttonREC);
		mMidiButton = (Button) findViewById(R.id.buttonMIDI);
        mWaveButton = (Button) findViewById(R.id.buttonWAVE);

		mPlayButton.setOnClickListener( new Button.OnClickListener()
		{
			boolean mStartPlaying = true;

			@Override
			public void onClick(View v) {

				onPlay(mStartPlaying);
				mPlayButton.setText( mStartPlaying==true? "Stop playing" : "Start playing" );
				mStartPlaying = !mStartPlaying;
			}
		});


		mRecordButton.setOnClickListener( new Button.OnClickListener()
		{
			boolean mStartRecording = true;

			@Override
			public void onClick(View v) {
				onRecord(mStartRecording);

				mRecordButton.setText(mStartRecording ?"Stop recording" : "Start recording");
				mStartRecording = !mStartRecording;
			}
		});


		mMidiButton.setOnClickListener( new Button.OnClickListener()
		{

			@Override
			public void onClick(View v) {

				//jni.init();

				if (midi!=null)
				midi.MIDI_INIT();


			}
		});



		final SoundPool pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		final int soundID = pool.load(this, R.raw.solarium, 1 );
		mWaveButton.setOnClickListener( new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				pool.play(soundID,1,1, 0, 0, 1.0f);
			}
		});


/*
		LinearLayout ll = new LinearLayout(this);
		mRecordButton = new RecordButton(this);
		ll.addView(mRecordButton,
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						0));
		mPlayButton = new PlayButton(this);
		ll.addView(mPlayButton,
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						0));
		setContentView(ll);
*/
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}



	private void writeAudioDataToFile() {



		byte data[] = new byte[bufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;
		if (null != os) {
			while(isRecording) {
				read = mRecorder.read(data, 0, bufferSize);
				if (read > 0){
				}

				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}



/*
		String filePath = mFileName;
		short sData[] = new short[bufferSize/2];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (isRecording) {
			// gets the voice output from microphone to byte format

			mRecorder.read(sData, 0, bufferSize/2);
			Log.d("eray","Short wirting to file" + sData.toString());
			try {
				// // writes the data to file from buffer
				// // stores the voice buffer
				byte bData[] = short2byte(sData);
				os.write(bData, 0, bufferSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
	}

	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}



	private void deleteTempFile() {
		File file = new File(getTempFilename());
		file.delete();
	}

	private void copyWaveFile(String inFilename,String outFilename){
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

		//	AppLog.logString("File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while(in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void WriteWaveFileHeader(
			FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels,
			long byteRate) throws IOException
	{
		byte[] header = new byte[44];

		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;  // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8);  // block align
		header[33] = 0;
		header[34] = RECORDER_BPP;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}


	private String getFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() +
				AUDIO_RECORDER_FILE_EXT_WAV);
	}

	private String getTempFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

}