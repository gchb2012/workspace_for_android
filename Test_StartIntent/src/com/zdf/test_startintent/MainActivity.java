package com.zdf.test_startintent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("zdf", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		launchIntent();
		openBrowser();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("zdf", "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void launchIntent() {
		Intent intent = new Intent(); 
        intent.setClassName("com.arcsoft.beautyshot.demo","com.arcsoft.beautyshot.demo.BeautyShotActivity");
//		intent.setClassName("com.arcsoft.picaction","com.arcsoft.picaction.PicAction");
//		intent.setClassName("com.arcsoft.demo.eyemouse","com.arcsoft.demo.eyemouse.ArcSoftEyeMouse");
		startActivity(intent);
	}
	
	protected void openBrowser() {
//		Uri uri = Uri.parse("http://www.baidu.com");
		Uri uri = Uri.parse("http://192.168.42.1");
//		Uri uri = Uri.parse("rtsp://192.168.42.1/live");
//		Uri uri = Uri.parse("http://192.168.42.1:25555/31/302e332e302e34363662313736392d643834392d316337322d313631652d333135633732363438323163/2f746d702f667573655f642f2e61646d735f7468756d626e61696c5f746d702f66356565333161392d386630632d633930302d343336662d6339323631613963323730322e6a7067");
//		Uri uri = Uri.parse("http://192.168.42.1/pref/config");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

}
