package com.ninano.karaoke.aiflow;

//import android.bluetooth.BluetoothAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class Constants {


    public static final int MSG_SPLASH_COMPLETE = 0;
    public static  int mSpokenLanguage = 1;  // 0 kr 1 jp

}


