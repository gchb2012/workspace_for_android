package com.zdf.test_jni;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	
	static {
		System.loadLibrary("myjni");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
		     Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		}

		
		int i = native_add(1, 1);
		Log.v("zdf", "i = " + i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	static native int native_add(int a, int b);

}
