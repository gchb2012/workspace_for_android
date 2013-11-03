package com.zdf.test_multiimages;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;

public class MainActivity extends Activity {
	private final static int MSG_PANORAMA_STITCH = 1;
	private final static int PANORAMA_STITCH_CYCLE = 1000;
	private Bitmap mBitmap = null;
	
	private MultiImageView mImageView = null;
	private MultiSurfaceView mSurfaceView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
		
		mImageView = (MultiImageView) findViewById(R.id.multi_image_view);
		mSurfaceView = (MultiSurfaceView) findViewById(R.id.multi_surface_view);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessageDelayed(MSG_PANORAMA_STITCH, PANORAMA_STITCH_CYCLE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeMessages(MSG_PANORAMA_STITCH);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PANORAMA_STITCH:
//				mImageView.drawMultiBitmap(mBitmap);
				mSurfaceView.drawMultiBitmap(mBitmap);
				mHandler.sendEmptyMessageDelayed(MSG_PANORAMA_STITCH, PANORAMA_STITCH_CYCLE);
				break;
			default:
				break;
			}
		};
	};

}
