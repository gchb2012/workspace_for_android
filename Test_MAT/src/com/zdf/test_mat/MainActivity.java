package com.zdf.test_mat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	List list = new ArrayList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		testMemoryLeak();
		
		displayBriefMemory();
		
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
		int memoryNum = activityManager.getMemoryClass();
		Log.v("zdf", "memoryNum = " + memoryNum);
		
//		Debug.getMemoryInfo(memoryInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void testMemoryLeak() {
		while (true) {
			Object o = new Object();
			list.add(o);
			o = null;
		}
	}
	
	private void displayBriefMemory() {
		final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);

		Log.i("zdf", "系统剩余内存:" + (info.availMem >> 10) + "k");
		Log.i("zdf", "系统是否处于低内存运行：" + info.lowMemory);
		Log.i("zdf", "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
	}

}
