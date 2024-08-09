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
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import ai.api.android.AIConfiguration;

public class RsvSingleton {

	public Activity mAct = null;
	public Context mContext = null;
	public Handler mKeywordHandler = null;
	public Handler mSpeechHandler = null;

	private static final String TAG = RsvSingleton.class.getSimpleName();


	private final int MY_UI = 1001;
	private static RsvSingleton uniqueInstance;

	ArrayList<Song> rsvList = new ArrayList<Song>();


	// other useful instance variables here

	private RsvSingleton() {


	}


	public static RsvSingleton getInstance() {

		if (uniqueInstance == null) {

			uniqueInstance = new RsvSingleton();

		}
		return uniqueInstance;

	}


	// other useful methods here


	// 마이크 부분을 쓰기 위한 초기 셋팅
	public synchronized void Reserv(Song s )
	{

		rsvList.add( s );

	}

	public
	ArrayList<Song> getRsvList()
	{
		return rsvList;
	}


}


