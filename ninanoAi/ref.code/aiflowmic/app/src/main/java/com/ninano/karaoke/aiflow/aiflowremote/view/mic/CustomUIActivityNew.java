package com.ninano.karaoke.aiflow.aiflowremote.view.mic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninano.karaoke.aiflow.aiflowremote.R;
//import com.ninano.karaoke.aiflow.aiflowremote.base.AIConfiguration;
import com.ninano.karaoke.aiflow.aiflowremote.base.BaseActivity;
import com.ninano.karaoke.aiflow.aiflowremote.base.BluetoothPref;
import com.ninano.karaoke.aiflow.aiflowremote.base.Config;
import com.ninano.karaoke.aiflow.aiflowremote.base.LanguageConfig;
//import com.ninano.karaoke.aiflow.aiflowremote.bluetooth.BluetoothChatService;
//import com.ninano.karaoke.aiflow.aiflowremote.bluetooth.Constants;
//import com.ninano.karaoke.aiflow.aiflowremote.bluetooth.DeviceListActivity;
import com.ninano.karaoke.aiflow.aiflowremote.utils.CustomTypeFace;

import java.util.ArrayList;

import static com.ninano.karaoke.aiflow.aiflowremote.utils.CustomTypeFace.DEMILIGTH;
import static com.ninano.karaoke.aiflow.aiflowremote.utils.CustomTypeFace.MEDIUM;

//import ai.api.AIConfiguration;

public class CustomUIActivityNew extends BaseActivity implements View.OnClickListener{

    private static final String TAG = CustomUIActivityNew.class.getSimpleName();

    private TextView tvLoading, tvResultValue;

    private ImageView ivMic = null;
    private RelativeLayout rlVoiceIntoract ,rlSttui;

    private ImageButton ibFindAndPair;
    private CheckBox cbToggle, cbBluetooth;

    private SpeechRecognizer mRecognizer;

    private final int INPUT_MIN_LENGTH = 10000;
    private final int MY_UI = 1001;
    public static final int READY = 0, END = 1, FINISH = 2;


    private LanguageConfig mSelectedLanguage = Config.languages[1]; //1:JP
    private LanguageConfig mPrevSelectedlanguage = null;

    //Default Lang Setting
//    private AIConfiguration.SupportedLanguages mLang = AIConfiguration.SupportedLanguages.fromLanguageTag(mSelectedLanguage.getLanguageCode());
//    private AIConfiguration mConfig = new AIConfiguration(mSelectedLanguage.getAccessToken(),
//            mLang,
//            AIConfiguration.RecognitionEngine.System);

    private Handler mmHandler = new Handler();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READY:
                    tvLoading.setVisibility(View.VISIBLE);
                    tvLoading.setText(getResources().getString(R.string.mic_text_talk));
                    tvResultValue.setText("");

                    onActionMicChange(true);
//                    ivMic.setActionUp();
                    break;
                case END:
                    tvLoading.setVisibility(View.VISIBLE);
                    tvLoading.setText(getResources().getString(R.string.mic_text_finish));

                    onActionMicChange(false);
                    break;
                case FINISH:
                    onActionMicChange(false);
                    onRecStop();
                    break;
            }
        }
    };


    /*
     * Google sample
     * */
    //Name of the connected device
    private String mConnectedDeviceName = null;
//    private BluetoothChatService mChatService = null;
    //String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    private void setStatus(int resId) {
        if (null == CustomUIActivityNew.this) {
            return;
        }
        if (null == tvLoading) {
            return;
        }
//        tvConnectValue.setText(resId);
        //bluetooth on/off
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        if (null == CustomUIActivityNew.this) {
            return;
        }
        if (null == tvLoading) {
            return;
        }
        //bluetooth on/off
//        tvConnectValue.setText(subTitle);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(CustomUIActivityNew.this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkAudioRecordPermission();

    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

//        if(mIsRecording) mIsRecording = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    /*
     * google sample End
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        setContentView(R.layout.custom_micro_ui_new);

        rlSttui = (RelativeLayout)findViewById(R.id.stt_ui);
        ivMic = (ImageView) findViewById(R.id.ivMic);
        rlVoiceIntoract = (RelativeLayout) findViewById(R.id.rlVoiceIntoract);

        tvLoading = (TextView) findViewById(R.id.tvLoading);
        tvLoading.setTypeface(CustomTypeFace.setFont(CustomUIActivityNew.this, DEMILIGTH));

        tvResultValue = (TextView) findViewById(R.id.tvResultValue);
        tvResultValue.setTypeface(CustomTypeFace.setFont(CustomUIActivityNew.this, MEDIUM));

        cbBluetooth = (CheckBox)findViewById(R.id.cbBluetooth);
        ibFindAndPair = (ImageButton)findViewById(R.id.ibFindAndPair);
        cbToggle = (CheckBox) findViewById(R.id.cbToggle);

        ibFindAndPair.setOnClickListener(this);
        cbToggle.setOnClickListener(this);

        cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onActionLangToggle(b);
                Toast.makeText(CustomUIActivityNew.this, "cbToggle Change ["+b+"]", Toast.LENGTH_SHORT).show();
            }
        });

        rlSttui.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "rlSttui ACTION_UP");
                        onRecStart( 1 );
                        return true;
                }
                return false;
            }
        });

        rlSttui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "rlSttui onClick");
                onRecStart( 2 );
            }
        });



        tvLoading.setText(getResources().getString(R.string.mic_text_wait));
    }

    private void setVolumeImg(final int step) {
//        Log.i(TAG, "SET VOL STEP["+step+"]");
        float bgAlpha = (float)(0.1 * step);
//        Log.i(TAG, "SET VOL ["+bgAlpha+"]");
        if(bgAlpha > 0.4) bgAlpha = 0.4f;
        rlVoiceIntoract.setAlpha(bgAlpha);
    }

    // 마이크 리스너 등록
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) {
            int step = (int) (rmsdB );
            setVolumeImg(((step > 0 ? step * 1 : 1)+1));
        }

        @Override
        public void onResults(Bundle results) {
            Log.i(TAG, "REC Result");
            mHandler.removeMessages(END);

            Intent i = new Intent();
            i.putExtras(results);

            showResultData(MY_UI, i);
            onRecStop();
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
            showResultData(error, null);
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

    @Override
    public void finish() {
        mHandler.removeMessages(READY);
        mHandler.removeMessages(END);
        mHandler.removeMessages(FINISH);
        super.finish();
    }

    // 마이크 부분을 쓰기 위한 초기 셋팅
    public void onRecStart(int  config) {

        final Intent sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Language Model 비교: LANGUAGE_MODEL_FREE_FORM(일반적인 받아쓰기에 유리),
        // LANGUAGE_MODEL_WEB_SEARCH(의미 있는 구문 획득에 유리)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

//        final String language = config.getLanguage().replace('-', '_');
        final String language = "ko-KR";

        Log.i(TAG, "LANG["+language+"]");
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        sttIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        // WORKAROUND for https://code.google.com/p/android/issues/detail?id=75347
        sttIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{language});

        if (mRecognizer != null) {
            mRecognizer.stopListening();
        }

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(sttIntent);
    }

    public void onRecStop() {
        if (mRecognizer != null) {
            mRecognizer.stopListening();
            mRecognizer.cancel();
            mRecognizer.destroy();
        }
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
            tvResultValue.setText(mResult.get(0));


            if (mResult != null && mResult.get(0) != null) {
                connectDevice(mResult.get(0));
            }
        } else {
            String msg = null;
            switch (resultCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    msg = "ERROR_AUDIO.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    msg = "ERROR_CLIENT.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    msg = "ERROR_INSUFFICIENT_PERMISSIONS.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    msg = "ERROR_NETWORK.ERROR_NETWORK_TIMEOUT";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    msg = getResources().getString(R.string.google_reco_timeout);//"ERROR_NO_MATCH";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    msg = "ERROR_RECOGNIZER_BUSY";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    msg = "ERROR_SERVER";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    msg = getResources().getString(R.string.google_reco_timeout);//"ERROR_SPEECH_TIMEOUT";
                    break;
            }

            if (msg != null) {
                if(resultCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || resultCode == SpeechRecognizer.ERROR_NO_MATCH) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }else{
                    Log.i(TAG, "ERROR : ["+msg+"]");
                }
            }

        }
    }

    private void connectDevice(String data) {
        Log.i(TAG, "connectDevice");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeMessages(FINISH);
    }

    @Override
    public void onClick(View view) {
        Intent serverIntent = null;
        switch (view.getId()) {
            case R.id.ibFindAndPair:
                //INSECURE 보안상 취약
//                serverIntent = new Intent(CustomUIActivityNew.this, DeviceListActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                Toast.makeText(CustomUIActivityNew.this, "ibFindAndPair", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onActionLangToggle(boolean type){
        //         true :  kr
        // default false :  jp
        if(type){
            mSelectedLanguage = (LanguageConfig) Config.languages[0];
            cbToggle.setChecked(true);
        }else{
            cbToggle.setChecked(false);
            mSelectedLanguage = (LanguageConfig) Config.languages[1];
        }

    }

    private void onActionBluetoothChange(boolean type){
//        if(type){
            cbBluetooth.setChecked(type);
//        }
    }

    private void onActionMicChange(boolean type){
        if(type){
            ivMic.setBackgroundResource(R.drawable.mic_on);
        }else{
            ivMic.setBackgroundResource(R.drawable.mic_off);
            rlVoiceIntoract.setAlpha(0.1f);
        }
    }
}