package com.ninano.karaoke.aiflow.aiflowremote.view.mic;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ninano.karaoke.aiflow.aiflowremote.R;

public class SpeechToTextActivity extends Activity implements OnClickListener {
    private final int GOOGLE_STT = 1000, MY_UI = 1001;
    private ArrayList<String> mResult;
    private String mSelectedString;
    private TextView mResultTextView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_main);

        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.hide).setOnClickListener(this);

        mResultTextView = (TextView) findViewById(R.id.result);
    }

    @Override
    public void onClick(View v) {
        int view = v.getId();

        if (view == R.id.show) {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");

            startActivityForResult(i, GOOGLE_STT);
        } else if (view == R.id.hide) {
//            startActivityForResult(new Intent(this, CustomUIActivity.class), MY_UI);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == GOOGLE_STT || requestCode == MY_UI)) {
            showSelectDialog(requestCode, data);
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
                    msg = "ERROR_NO_MATCH";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    msg = "ERROR_RECOGNIZER_BUSY";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    msg = "ERROR_SERVER";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    msg = "ERROR_SPEECH_TIMEOUT";
                    break;
            }

            if (msg != null)
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSelectDialog(int requestCode, Intent data) {
        String key = "";
        if (requestCode == GOOGLE_STT)
            key = RecognizerIntent.EXTRA_RESULTS;
        else if (requestCode == MY_UI)
            key = SpeechRecognizer.RESULTS_RECOGNITION;

        mResult = data.getStringArrayListExtra(key);
        String[] result = new String[mResult.size()];
        mResult.toArray(result);
        mResultTextView.setText(mResult.get(0));
    }
}