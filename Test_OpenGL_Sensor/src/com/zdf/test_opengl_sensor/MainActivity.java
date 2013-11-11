package com.zdf.test_opengl_sensor;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.zdf.test_opengl_sensor.sensor.SensorMonitor;

public class MainActivity extends Activity {
	private OpenGLView mOpenGLView = null;
	private SensorMonitor mSensorMonitor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mOpenGLView = new OpenGLView(this, null);
		setContentView(mOpenGLView);
		
		mSensorMonitor = new SensorMonitor(this);
		mOpenGLView.setSensorMonitor(mSensorMonitor);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (null != mSensorMonitor) {
			mSensorMonitor.release();
			mSensorMonitor = null;
		}
	}

}
