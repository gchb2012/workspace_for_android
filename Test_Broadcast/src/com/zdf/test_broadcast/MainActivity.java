package com.zdf.test_broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button mBtnStartSecondActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IntentFilter dynamicFilter = new IntentFilter();
		dynamicFilter.addAction("com.zdf.dynamicbroadcast.action.rec");
		registerReceiver(mDynamicBroadcastReceiver, dynamicFilter);
		
		IntentFilter staticFilter = new IntentFilter();
		dynamicFilter.addAction("com.zdf.staticbroadcast.action.rec");
		registerReceiver(mStaticBroadcastReceiver, staticFilter);
		
		mBtnStartSecondActivity = (Button) findViewById(R.id.btn_start_second_activity);
		if (null != mBtnStartSecondActivity) {
			mBtnStartSecondActivity.setOnClickListener(mOnClickListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void startActivity() {
		Intent intent = new Intent(MainActivity.this, SecondActivity.class);
		startActivity(intent);
	}
	
	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start_second_activity:
				startActivity();
				break;

			default:
				break;
			}
		}
	};
	
	private final BroadcastReceiver mDynamicBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String strMsg = intent.getStringExtra("msg");
			Log.v("zdf", "[MainActivity] mDynamicBroadcastReceiver.onReceive, action = " + action + ", strMsg = " + strMsg);
		}
		
	};
	
	// useless
	private final StaticBroadcastReceiver mStaticBroadcastReceiver = new StaticBroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String strMsg = intent.getStringExtra("msg");
			Log.v("zdf", "[MainActivity] mStaticBroadcastReceiver.onReceive, action = " + action + ", strMsg = " + strMsg);
		};
	};

}
