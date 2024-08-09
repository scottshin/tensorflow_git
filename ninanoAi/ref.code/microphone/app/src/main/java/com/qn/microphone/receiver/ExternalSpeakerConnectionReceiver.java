package com.qn.microphone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.qn.microphone.MicAppConstants;

/**
 * BroadcastReceiver that listens for the ACTION_HEADSET_PLUG intent.
 */
public class ExternalSpeakerConnectionReceiver extends BroadcastReceiver {

	private static final String TAG = "ExternalSpeakerConnectionReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		Intent toggleMicIntent = new Intent(MicAppConstants.TOGGLE_MIC_INTENT);

		// 0 for unplugged, 1 for plugged
		if (intent.getIntExtra(MicAppConstants.ACTION_HEADSET_INTENT_STATE_KEY, -1) == 0) {
			Log.d(TAG, "External speaker unplugged ...");

			toggleMicIntent.putExtra(MicAppConstants.TOGGLE_MIC_INTENT_STATUS_KEY, MicAppConstants.SPEAKER_UNPLUGGED_CODE);
		} else {
			Log.d(TAG, "External speaker plugged ...");

			toggleMicIntent.putExtra(MicAppConstants.TOGGLE_MIC_INTENT_STATUS_KEY, MicAppConstants.SPEAKER_PLUGGED_CODE);
		}

		Log.d(TAG, "Sending broadcast to " + toggleMicIntent.getAction() + " with data: " + toggleMicIntent
				.getIntExtra(MicAppConstants.TOGGLE_MIC_INTENT_STATUS_KEY, -1));
		LocalBroadcastManager.getInstance(context).sendBroadcast(toggleMicIntent);
	}
}
