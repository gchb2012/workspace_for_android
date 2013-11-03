package com.zdf.testgetthumbnail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class Test_GetThumbnailActivity extends Activity {
	private String m_filePath = "/mnt/sdcard/MOVIE/02_H264-320x240.3g2";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Utils.loadres(this, Utils.PLUGININ_STR);
		Bitmap bitmap = Utils.getThumbnail(m_filePath, 120, 80, 0);
		Log.v("zdf", "bitmap = " + bitmap);
	}

}