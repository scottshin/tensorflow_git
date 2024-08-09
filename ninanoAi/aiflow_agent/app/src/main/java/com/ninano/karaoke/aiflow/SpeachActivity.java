package com.ninano.karaoke.aiflow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninano.karaoke.aiflow.base.BaseActivity;
import com.ninano.karaoke.aiflow.utils.CustomTypeFace;

import static com.ninano.karaoke.aiflow.utils.CustomTypeFace.DEMILIGTH;
import static com.ninano.karaoke.aiflow.utils.CustomTypeFace.MEDIUM;

//import ai.api.AIConfiguration;

public class SpeachActivity extends AppCompatActivity
{

    private static final String TAG = SpeachActivity.class.getSimpleName();

    private TextView tvLoading, tvResultValue;
    private ImageView ivMic;
    private RelativeLayout rlVoiceIntoract ,rlSttui;
    private final int INPUT_MIN_LENGTH = 10000;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default :
                    finish();
                    break;

                case 55:
                    setVolumeImg( msg.arg1 ) ;
                    break;

                case 88:  // action on
                case 99:
                    onActionMicChange( msg.what==88? true: false );
                    break;


            }
        }
    };

    /*
     * google sample End
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_speach);


        rlSttui = (RelativeLayout)findViewById(R.id.stt_ui);
        ivMic = (ImageView) findViewById(R.id.ivMic);
        rlVoiceIntoract = (RelativeLayout) findViewById(R.id.rlVoiceIntoract);

        tvLoading = (TextView) findViewById(R.id.tvLoading);
        //tvLoading.setTypeface(CustomTypeFace.setFont(SpeachActivity.this, DEMILIGTH));

        tvResultValue = (TextView) findViewById(R.id.tvResultValue);
        //tvResultValue.setTypeface(CustomTypeFace.setFont(SpeachActivity.this, MEDIUM));
        tvLoading.setText(getResources().getString(R.string.mic_text_wait));

//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if(extras == null) {
//
//            } else {
//                int type = extras.getInt("language");
//                Singleton.getInstance().mSelectedLanguage = (LanguageConfig) Config.languages[type];
//                Singleton.getInstance().mLang = AIConfiguration.SupportedLanguages.fromLanguageTag( Singleton.getInstance().mSelectedLanguage.getLanguageCode());
//                Log.i(TAG, "LANG["+ Singleton.getInstance().mLang +"]");
//                Singleton.getInstance().mConfig = new AIConfiguration( Singleton.getInstance().mSelectedLanguage.getAccessToken(), Singleton.getInstance().mLang, AIConfiguration.RecognitionEngine.System);
//            }
//        }

        AISingleton.getInstance().mSpeechHandler = mHandler;
    }

    private void setVolumeImg(final int step) {
//        Log.i(TAG, "SET VOL STEP["+step+"]");
        float bgAlpha = (float)(0.1 * step);
//        Log.i(TAG, "SET VOL ["+bgAlpha+"]");
        if(bgAlpha > 0.8) bgAlpha = 0.8f;
        rlVoiceIntoract.setAlpha(bgAlpha);
    }


//    @Override
//    public void onClick(View view) {
//        Intent serverIntent = null;
//        switch (view.getId()) {
////            case R.id.ibFindAndPair:
////                Toast.makeText(CustomUIActivityNew.this, "ibFindAndPair", Toast.LENGTH_SHORT).show();
////                break;
//        }
//    }
/*
    public void onActionLangToggle(boolean type){
        //         true :  kr
        // default false :  jp
        if(type){
            Singleton.getInstance().mSelectedLanguage = (LanguageConfig) Config.languages[0];
            cbToggle.setChecked(true);
        }else{
            cbToggle.setChecked(false);
            Singleton.getInstance().mSelectedLanguage = (LanguageConfig) Config.languages[1];
        }

        Singleton.getInstance().mLang = AIConfiguration.SupportedLanguages.fromLanguageTag(
                Singleton.getInstance().mSelectedLanguage.getLanguageCode());
        Log.i(TAG, "LANG["+ Singleton.getInstance().mLang +"]");
        Singleton.getInstance().mConfig = new AIConfiguration( Singleton.getInstance().mSelectedLanguage.getAccessToken(),
                Singleton.getInstance().mLang,
                AIConfiguration.RecognitionEngine.System);
    }
*/

    private void onActionMicChange(boolean type){
        if(type){
            ivMic.setBackgroundResource(R.drawable.mic_on);
        }else{
            ivMic.setBackgroundResource(R.drawable.mic_off);
            rlVoiceIntoract.setAlpha(0.1f);
        }
    }




}