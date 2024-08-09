package com.ninano.karaoke.aiflow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ninano.karaoke.aiflow.base.BaseActivity;
import com.ninano.karaoke.aiflow.base.TTS;

public class IntroActivity extends BaseActivity{
    public static final String TAG = IntroActivity.class.getName();

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), DemoMainActivity.class);
//            Intent intent = new Intent(getApplicationContext(), CustomUIActivityNew.class);



            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        TTS.init(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(r);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(r, 2000);
    }
}
