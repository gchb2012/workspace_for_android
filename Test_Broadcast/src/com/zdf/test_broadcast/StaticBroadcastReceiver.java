package com.zdf.test_broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StaticBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String strMsg = intent.getStringExtra("msg");
		Log.v("zdf", "[StaticBroadcastReceiver] onReceive, action = " + action + ", strMsg = " + strMsg);
	}

}
