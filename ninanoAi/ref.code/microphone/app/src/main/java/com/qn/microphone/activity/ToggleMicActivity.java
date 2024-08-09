package com.qn.microphone.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.qn.microphone.MicAppConstants;
import com.qn.microphone.R;
import com.qn.microphone.receiver.ExternalSpeakerConnectionReceiver;
import com.qn.microphone.task.StreamAudioTask;

/**
 * Displays UI for toggle button to turn mic on/off.
 */
public class ToggleMicActivity extends Activity {

	private static final String TAG = "ToggleMicActivity";

	private StreamAudioTask mStreamAudioTask;

	private BroadcastReceiver mBroadcastReceiver;

	private ToggleButton mToggleButton;

	private BroadcastReceiver mExternalSpeakerConnectionReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		mBroadcastReceiver = createBroadcastReceiver();

		setContentView(R.layout.toggle_mic);

		mToggleButton = createToggleButton();
		mToggleButton.setEnabled(false);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "Registering broadcast receiver ...");
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MicAppConstants
				.TOGGLE_MIC_INTENT));

		mExternalSpeakerConnectionReceiver = new ExternalSpeakerConnectionReceiver();
		getApplicationContext().registerReceiver(mExternalSpeakerConnectionReceiver, new IntentFilter(Intent
				.ACTION_HEADSET_PLUG));
	}

	@Override
	public void onPause() {
		if (mBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
		}
		if (mExternalSpeakerConnectionReceiver != null) {
			getApplicationContext().unregisterReceiver(mExternalSpeakerConnectionReceiver);
		}

		super.onPause();
	}

	private ToggleButton createToggleButton() {
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					Log.d(TAG, "Mic toggle enabled ...");

					mStreamAudioTask = new StreamAudioTask(getApplicationContext());
					mStreamAudioTask.execute();
				} else {
					// The toggle is disabled
					Log.d(TAG, "Mic toggle disabled ...");

					if (mStreamAudioTask != null) {
						mStreamAudioTask.cancel(true);
					}
				}
			}
		});

		return toggleButton;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		Log.d(TAG, "Creating broadcast receiver ...");
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "Received broadcast with data: " + intent.getIntExtra(MicAppConstants
						.TOGGLE_MIC_INTENT_STATUS_KEY, -1));

				if ((Integer) intent.getExtras().get(MicAppConstants.TOGGLE_MIC_INTENT_STATUS_KEY) ==
						MicAppConstants.SPEAKER_PLUGGED_CODE) {
					mToggleButton.setEnabled(true);
				} else {
					Log.d(TAG, "Speaker unplugged!");
					Toast.makeText(context, "Speaker unplugged", Toast.LENGTH_SHORT).show();
					if (mStreamAudioTask != null) {
						mStreamAudioTask.cancel(true);
					}
					mToggleButton.setChecked(false);
					mToggleButton.setEnabled(false);
				}
			}
		};
	}
}
