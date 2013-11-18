package com.zdf.test_matcher;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private PathMatcher mMatcher;
	
	private static final int LOCAL_IMAGE_ALBUMSET = 0;
    private static final int LOCAL_VIDEO_ALBUMSET = 1;
    private static final int LOCAL_IMAGE_ALBUM = 2;
    private static final int LOCAL_VIDEO_ALBUM = 3;
    private static final int LOCAL_IMAGE_ITEM = 4;
    private static final int LOCAL_VIDEO_ITEM = 5;
    private static final int LOCAL_ALL_ALBUMSET = 6;
    private static final int LOCAL_ALL_ALBUM = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMatcher = new PathMatcher();
        mMatcher.add("/local/image", LOCAL_IMAGE_ALBUMSET);
        mMatcher.add("/local/video", LOCAL_VIDEO_ALBUMSET);
        mMatcher.add("/local/all", LOCAL_ALL_ALBUMSET);

        mMatcher.add("/local/image/*", LOCAL_IMAGE_ALBUM);
        mMatcher.add("/local/video/*", LOCAL_VIDEO_ALBUM);
        mMatcher.add("/local/all/*", LOCAL_ALL_ALBUM);
        mMatcher.add("/local/image/item/*", LOCAL_IMAGE_ITEM);
        mMatcher.add("/local/video/item/*", LOCAL_VIDEO_ITEM);
        
        checkPathInMatcher();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void checkPathInMatcher() {
		String strPath = "/local/all/*";
		Path path = Path.fromString(strPath);
		
		int kind = mMatcher.match(path);
		Log.v("zdf", "checkPathInMatcher, match kind = " + kind);
		
		String strVar = mMatcher.getVar(0);
		Log.v("zdf", "checkPathInMatcher, getVar(0) = " + strVar);
		
		int var = mMatcher.getIntVar(0);
		Log.v("zdf", "checkPathInMatcher, getIntVar(0) = " + var);
	}

}
