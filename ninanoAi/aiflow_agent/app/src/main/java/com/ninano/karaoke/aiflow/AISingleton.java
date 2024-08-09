package com.ninano.karaoke.aiflow;

//import android.bluetooth.BluetoothAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.ninano.karaoke.aiflow.base.Config;
import com.ninano.karaoke.aiflow.base.LanguageConfig;
import com.ninano.karaoke.aiflow.utils.ViewUtils;

import java.util.ArrayList;

import ai.api.android.AIConfiguration;

public class AISingleton {

	public Activity mAct = null;
	public Context mContext = null;
	public Handler mKeywordHandler = null;
	public Handler mSpeechHandler = null;

	private static final String TAG = AISingleton.class.getSimpleName();


	private final int MY_UI = 1001;
	private static AISingleton uniqueInstance;


	// other useful instance variables here

	static private SpeechRecognizer mRecognizer = null;
	static public LanguageConfig mSelectedLanguage = Config.languages[ Constants.mSpokenLanguage ]; // 0:KR, 1:JP


	//Default Lang Setting
	static public AIConfiguration.SupportedLanguages mLang;
	static public AIConfiguration mConfig;


	final Intent sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


	static boolean mReady = false;

	private AISingleton() {


	}


	public static AISingleton getInstance() {

		if (uniqueInstance == null) {

			uniqueInstance = new AISingleton();

			mLang = AIConfiguration.SupportedLanguages.fromLanguageTag(mSelectedLanguage.getLanguageCode());
			mConfig = new AIConfiguration(mSelectedLanguage.getAccessToken(), mLang,
					AIConfiguration.RecognitionEngine.System);






		}
		return uniqueInstance;

	}


	// other useful methods here


	// 마이크 부분을 쓰기 위한 초기 셋팅
	public synchronized void doRecStart(AIConfiguration config, Activity _act) {

		mAct = _act;




//		if (AISingleton.getInstance().mRecognizer != null) {
//			AISingleton.getInstance().mRecognizer.stopListening();
//		}


		if ( AISingleton.getInstance().mRecognizer == null ) {



			// Language Model 비교: LANGUAGE_MODEL_FREE_FORM(일반적인 받아쓰기에 유리),
			// LANGUAGE_MODEL_WEB_SEARCH(의미 있는 구문 획득에 유리)
			sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


			final String language = config.getLanguage().replace('-', '_');
			Log.i(TAG, "LANG[" + language + "]");
			sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
			sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
			sttIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
			sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());

			// WORKAROUND for https://code.google.com/p/android/issues/detail?id=75347
			sttIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{language});


		}





		AISingleton.getInstance().mRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
		AISingleton.getInstance().mRecognizer.setRecognitionListener(listener);
		AISingleton.getInstance().mRecognizer.startListening(sttIntent);

	}

	private void doRecStop_() {

		if (AISingleton.getInstance().mRecognizer != null) {
			AISingleton.getInstance().mRecognizer.stopListening();

			AISingleton.getInstance().mRecognizer.cancel();
			AISingleton.getInstance().mRecognizer.destroy();
			AISingleton.getInstance().mRecognizer = null;
		}


		mHandler.removeMessages(READY);
		mHandler.removeMessages(END);
		mHandler.removeMessages(FINISH);

	}


	public static final int READY = 0, END = 1, FINISH = 2;


	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case READY:
//					tvLoading.setVisibility(View.VISIBLE);
//					tvLoading.setText(getResources().getString(R.string.mic_text_talk));
//					tvResultValue.setText("");
					onActionMicChange(true);
//                    //ivMic.setActionUp();
					Log.i(TAG, "mic_text_talk");


//					SoundPool sound_pool;
//					int sound_beep;
//					sound_pool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
//					sound_beep = sound_pool.load( mContext, R.raw.tone, 1 );
//					sound_pool.play( sound_beep, 1f, 1f, 0, 0, 1f );




//					Toast.makeText( mContext, "말씀하세요" , Toast.LENGTH_SHORT).show();
					ViewUtils.showToast(mAct,
							Constants.mSpokenLanguage == 0 ? "말씀하세요" : "言ってください",
							Toast.LENGTH_SHORT);

					break;

				case END:
//					tvLoading.setVisibility(View.VISIBLE);
//					tvLoading.setText(getResources().getString(R.string.mic_text_finish));
//
					onActionMicChange(false);
					Log.i(TAG, "mic_text_tfinish ");
					break;

				case FINISH:
					onActionMicChange(false);
					doRecStop_();
					break;
			}
		}
	};


	private void onActionMicChange(boolean type) {


		mReady = type;
		if ( mSpeechHandler != null) {
				mSpeechHandler.sendEmptyMessage(type == true ? 88 : 99);
		}
	}

	private void finish_() {
		mHandler.removeMessages(READY);
		mHandler.removeMessages(END);
		mHandler.removeMessages(FINISH);

	}


	private ArrayList<String> mResult;

	private void showResultData(int resultCode, Intent data) {
		if (resultCode == MY_UI) {
			String key = SpeechRecognizer.RESULTS_RECOGNITION;

			mResult = data.getStringArrayListExtra(key);
			String[] result = new String[mResult.size()];
			mResult.toArray(result);

			//tvResultValue.setText(mResult.get(0));


			//Toast.makeText( mContext, mResult.get(0) , Toast.LENGTH_SHORT).show();

			ViewUtils.showToast(mAct, mResult.get(0),Toast.LENGTH_SHORT);


			if (mResult != null && mResult.get(0) != null) {
				//ppp connectDevice(mResult.get(0));


//				Intent intent = new Intent();
//				intent.putExtra("return", mResult.get(0));
//				setResult(RESULT_OK, intent);

				// 메시지 얻어오기
				Message msg = mKeywordHandler.obtainMessage();
				msg.obj = new String(mResult.get(0));
				mKeywordHandler.sendMessage(msg);



			}
		} else {
			String msg = null;
			switch (resultCode) {
				case SpeechRecognizer.ERROR_AUDIO: msg = "ERROR_AUDIO.";		break;
				case SpeechRecognizer.ERROR_CLIENT: msg = "ERROR_CLIENT.";		break;
				case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
					msg = "ERROR_INSUFFICIENT_PERMISSIONS.";
					break;
				case SpeechRecognizer.ERROR_NETWORK:
				case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
					msg = "ERROR_NETWORK.ERROR_NETWORK_TIMEOUT";
					break;
				case SpeechRecognizer.ERROR_NO_MATCH:
					msg = "ERROR_RECORDTIMEOUT"; //getResources().getString(R.string.google_reco_timeout);//"ERROR_NO_MATCH";

					break;
				case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: msg = "ERROR_RECOGNIZER_BUSY";	break;
				case SpeechRecognizer.ERROR_SERVER: 		msg = "ERROR_SERVER";	break;
				case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
					msg = "ERROR_SPEECH_TIMEOUT";// getResources().getString(R.string.google_reco_timeout);//"ERROR_SPEECH_TIMEOUT";
					break;
			}

			if (msg != null) {
				if (resultCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || resultCode == SpeechRecognizer.ERROR_NO_MATCH) {
					Toast.makeText( mContext, msg, Toast.LENGTH_SHORT).show();
				} else {
					Log.i(TAG, "ERROR : [" + msg + "]");
					Toast.makeText( mContext, "ERROR : " + msg, Toast.LENGTH_SHORT).show();
				}
			}
		}


/*
            Handler handler = new Handler()     {
                @Override
                public void handleMessage(Message msg)
                {
                    finish();   // activity 종료
                }
            };
            handler.sendEmptyMessageDelayed(0, 500);    // ms, 3초후 종료시킴
*/


		//
		//
		if ( mSpeechHandler != null )
			mSpeechHandler.sendEmptyMessage( END );


	}



	// 마이크 리스너 등록
	private RecognitionListener listener = new RecognitionListener() {
		@Override
		public void onRmsChanged(float rmsdB) {
			int step = (int) (rmsdB );
			//ppp setVolumeImg(((step > 0 ? step * 1 : 1)+1));


			if ( mReady ) {
				Message msg = new Message();
				msg.what = 55;
				msg.arg1 = (((step > 0 ? step * 1 : 1) + 1));

				if (mSpeechHandler != null)
					mSpeechHandler.sendMessage(msg);
			}
		}

		@Override
		public void onResults(Bundle results) {
			Log.i(TAG, "REC Result");
			mHandler.removeMessages(END);

			Intent i = new Intent();
			i.putExtras(results);

			showResultData(MY_UI, i);
			doRecStop_();
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			Log.i(TAG, "REC READY ");
			mHandler.sendEmptyMessage(READY);
		}

		@Override
		public void onEndOfSpeech() {
			Log.i(TAG, "REC  END");
			mHandler.sendEmptyMessage(END);
		}

		@Override
		public void onError(int error) {
			Log.i(TAG, "REC error:" + error);
			mHandler.sendEmptyMessage(END);
			//
			showResultData(error, null);
			doRecStop_();
		}

		@Override
		public void onBeginningOfSpeech() {
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
		}
	};


}


