package com.zdf.test_broadcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SecondActivity extends Activity {
	private Button mBtnSendStaticBroadcast = null;
	private Button mBtnSendDynamicBroadcast = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		mBtnSendStaticBroadcast = (Button) findViewById(R.id.btn_send_static_broadcast);
		if (null != mBtnSendStaticBroadcast) {
			mBtnSendStaticBroadcast.setOnClickListener(mOnClickListener);
		}
		
		mBtnSendDynamicBroadcast = (Button) findViewById(R.id.btn_send_dynamic_broadcast);
		if (null != mBtnSendDynamicBroadcast) {
			mBtnSendDynamicBroadcast.setOnClickListener(mOnClickListener);
		}
	}
	
	private void sendStaticBroadcast() {
		Intent intent = new Intent(SecondActivity.this, StaticBroadcastReceiver.class);
//		Intent intent = new Intent("com.zdf.staticbroadcast.action.rec");
		intent.putExtra("msg", "This is a static broadcast msg.");
		sendBroadcast(intent);
	}
	
	private void sendDynamicBroadcast() {
		Intent intent = new Intent("com.zdf.dynamicbroadcast.action.rec");
		intent.putExtra("msg", "This is a dynamic broadcast msg.");
		sendBroadcast(intent);
	}
	
	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_send_static_broadcast:
				sendStaticBroadcast();
				break;
				
			case R.id.btn_send_dynamic_broadcast:
				sendDynamicBroadcast();
				break;
				
			default:
				break;
			}
		}
	};
}
