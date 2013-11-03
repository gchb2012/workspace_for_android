package com.example.test_activityorientation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private RelativeLayout mBottomBar = null;
	private Button mBtnStartPortraitActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.e("zdf", "onCreate");
		
		initUI();
	}
	
	private void initUI() {
		mBottomBar = (RelativeLayout) findViewById(R.id.layout_bottom_bar);
		mBtnStartPortraitActivity = (Button) findViewById(R.id.btn_start_portrait_activity);
		mBtnStartPortraitActivity.setOnClickListener(mOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		Log.e("zdf", "----- [MainActivity] onConfigurationChanged, newConfig.orientation = " + newConfig.orientation + ", curOrientation = " + getResources().getConfiguration().orientation);
		Log.e("zdf", "----- [MainActivity] onConfigurationChanged, mBottomBar position = [" + mBottomBar.getLeft() + ", " + mBottomBar.getTop() + ", " + mBottomBar.getRight() + ", " + mBottomBar.getBottom() + "]");
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start_portrait_activity:
				startPortraitActivity();
				break;
			default:
				break;
			}
		}
	};
	
	private void startPortraitActivity() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, PortraitActivity.class);
		startActivity(intent);
	}

}
