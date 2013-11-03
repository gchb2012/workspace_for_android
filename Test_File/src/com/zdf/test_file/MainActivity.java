package com.zdf.test_file;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import com.zdf.test_file.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		File dir = new File("/sdcard/test/.Temp/");
//		File dir = new File("/storage/sdcard0/Media+/.Temp/");
		File dir = new File("/sdcard/Media+/.Temp");
		boolean bdir = dir.mkdirs();
		Log.v("zdf", "bdir = " + bdir + ", dir.exists() = " + dir.exists());
		
		File file1 = new File("/sdcard/test/.Temp/test.txt_tmp");
		File file2 = new File("/sdcard/test/Contents/test.txt");
		boolean b = file1.renameTo(file2);
		Log.v("zdf", "renameTo: " + b);
		
//		file2.deleteOnExit();
		b = file2.delete();
		Log.v("zdf", "delete: " + b);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
