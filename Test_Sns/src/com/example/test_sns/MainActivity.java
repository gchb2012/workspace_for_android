package com.example.test_sns;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;

import com.arcsoft.sns.SnsMainActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String strTestFile = Environment.getExternalStorageDirectory() + "/1.jpg";
		Intent sendIntent = new Intent(SnsMainActivity.ACTION); //there is full-screen problem.
//		sendIntent.setDataAndType(mMediaManager.getMediaFileUri(mReviewIndex), "image/*");
//		sendIntent.putExtra(SnsMainActivity.ExtraMediaId, fi.id);
		sendIntent.setDataAndType(Uri.fromFile(new File(strTestFile)), "image/*");
		startActivity(sendIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
