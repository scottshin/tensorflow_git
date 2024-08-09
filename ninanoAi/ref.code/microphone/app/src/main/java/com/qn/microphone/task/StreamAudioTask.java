package com.qn.microphone.task;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task that streams audio from mic to speaker.
 */
public class StreamAudioTask extends AsyncTask<Integer, Void, Void> {

	private static final String TAG = "StreamAudioTask";

	private static final int RECORDING_RATE = 8000;
	private static final int CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO;
	private static final int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO;
	private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	private static int AUDIO_RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(
			RECORDING_RATE, CHANNEL_IN, FORMAT);
	private static int AUDIO_TRACK_BUFFER_SIZE = AudioTrack.getMinBufferSize(
			RECORDING_RATE, CHANNEL_OUT, FORMAT);

	private Context mContext;

	private AudioRecord audioRecorder;

	private AudioTrack audioTrack;

	private byte[] audioBuffer = new byte[20];

	public StreamAudioTask(final Context context) {
		mContext = context;
	}

	@Override
	protected Void doInBackground(Integer... params) {
		try {
			Log.d(TAG, "Start AudioRecorder recording ...");
			audioRecorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, RECORDING_RATE, CHANNEL_IN, FORMAT,
					AUDIO_RECORD_BUFFER_SIZE);
			audioRecorder.startRecording();

			Log.d(TAG, "Start AudioTrack playing ...");
			audioTrack = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, RECORDING_RATE, CHANNEL_OUT, FORMAT, AUDIO_TRACK_BUFFER_SIZE,
					AudioTrack.MODE_STREAM);
			audioTrack.play();

			while (!isCancelled()) {
				audioRecorder.read(audioBuffer, 0, audioBuffer.length);
				audioTrack.write(audioBuffer, 0, audioBuffer.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onCancelled(Void aVoid) {
		Toast.makeText(mContext, "disabled", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "Audio streaming cancelled ...");
		audioRecorder.release();
		audioTrack.release();
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, "Audio streaming started ...");
		Toast.makeText(mContext, "enabled", Toast.LENGTH_SHORT).show();
	}
}
