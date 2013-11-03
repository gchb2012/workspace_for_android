package com.zdf.test_httpconnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private String mImageUrl = "http://192.168.42.1:25555/31/302e332e302e34363662313736392d643834392d316337322d313631652d333135633732363438323163/2f746d702f667573655f642f2e61646d735f7468756d626e61696c5f746d702f66356565333161392d386630632d633930302d343336662d6339323631613963323730322e6a7067";
	private String mCachePath = "/sdcard/mycache/cache";
	private Bitmap mBitmap = null;
	private TextView mTvStatus = null;
	private TextView mTvImageUrl = null;
	private ImageView mIvImageShow = null;
	private Button mBtnRefresh1 = null;
	private Button mBtnRefresh2 = null;
	
	private static final int MSG_SHOW_IMAGE = 0;
	private static final int MSG_SHOW_STATE = 1;
	private static final int MSG_SHOW_URL = 2;
	
	private static final int SCREEN_WIDTH = 720;
	private static final int SCREEN_HEIGHT = 1280;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		mTvStatus = (TextView) findViewById(R.id.tv_status);
		mTvImageUrl = (TextView) findViewById(R.id.tv_image_url);
		mIvImageShow = (ImageView) findViewById(R.id.iv_image_show);
		mBtnRefresh1 = (Button) findViewById(R.id.btn_refresh_image_1);
		mBtnRefresh2 = (Button) findViewById(R.id.btn_refresh_image_2);
		mBtnRefresh1.setOnClickListener(mOnClickLinstener);
		mBtnRefresh2.setOnClickListener(mOnClickLinstener);

//		startLoadImageThread();
		startLoadImageThreadWithCache();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private synchronized void startLoadImageThread() {
		Thread loadImageThread = new Thread(new LoadImageRunnable());
		loadImageThread.start();
	}
	
	private class LoadImageRunnable implements Runnable {
		@Override
		public void run() {
			mImageUrl = readDownloadUrlFromFile();
			mBitmap = loadInternetImage(mImageUrl);
			mHandler.sendEmptyMessage(MSG_SHOW_IMAGE);
		}
	};
	
	private Bitmap loadInternetImage(String path) {
		Log.v("zdf", "loadInternetImage, path = " + path);
		Bitmap bitmap = null;
		try {
			URL url = new URL(path);
		    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		    if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
		        bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
		        Log.d("zdf", "download image finished!");
			} else {
				Log.w("zdf", "download image failed!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			String strException = "IOException: " + e;
			Log.v("zdf", strException);
			
			sendMsg(MSG_SHOW_STATE, strException);
		}

		return bitmap;
	}
	
	private synchronized void startLoadImageThreadWithCache() {
		Thread loadImageThreadWithCache = new Thread(new LoadImageRunnableWithCache());
		loadImageThreadWithCache.start();
	}
	
	private class LoadImageRunnableWithCache implements Runnable {
		@Override
		public void run() {
			mImageUrl = readDownloadUrlFromFile();
			sendMsg(MSG_SHOW_URL, mImageUrl);
			mBitmap = loadInternetImageToCache(mCachePath, mImageUrl);
			sendMsg(MSG_SHOW_IMAGE, mImageUrl);
		}
	};
	
	private Bitmap loadInternetImageToCache(String cachePath, String path) {
		Log.v("zdf", "loadInternetImage, path = " + path);
		Bitmap bitmap = null;
		try {
			InputStream dis = null;
			OutputStream dos = null;
			File tmpfile = new File(cachePath);
			File outdir = tmpfile.getParentFile();
			
			if (!outdir.exists()) {
				if (!outdir.mkdirs()) {
					return null;
				}
			}
			
	    	if (tmpfile.exists()) {
				tmpfile.delete();
			}
	    	
			URL url = new URL(path);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	conn.setConnectTimeout(15000);
				conn.setReadTimeout(10000);
		    	conn.connect();
		    	int totalSize = conn.getContentLength();
		    	Log.v("zdf", "loadInternetImageToCache, totalSize = " + totalSize);
		    	
		    	dis = conn.getInputStream();
		    	dos = new FileOutputStream(cachePath);
		    	
		    	final int BUFFER_SIZE = 1024 * 20;
				byte[] buffer = new byte[BUFFER_SIZE];
				
				int read = -1;
				int count = 0;
				
				while (true) {
					Log.v("zdf", "@@@@@@@@@@ before read");
					read = dis.read(buffer);
					if (read == -1) {
						break;
					}
					
					count += read;
					Log.v("zdf", "@@@@@@@@@@ after read, read = " + read + ", count = " + count + ", Size: " + totalSize);
					dos.write(buffer, 0, read);
					
					sendMsg(MSG_SHOW_STATE, "read = " + read + ", count = " + count + ", Size: " + totalSize);
				}

//		        bitmap = BitmapFactory.decodeFile(mCachePath);
				bitmap = getValidBitmap(mCachePath);
				Log.d("zdf", "download image finished!");
			} else {
				Log.w("zdf", "download image failed!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			String strException = "IOException: " + e;
			Log.v("zdf", strException);
			
			sendMsg(MSG_SHOW_STATE, strException);
		}
		
		Log.v("zdf", "** end, bitmap = " + bitmap);
		return bitmap;

	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_IMAGE:
				mIvImageShow.setImageBitmap(mBitmap);
				break;
			case MSG_SHOW_STATE:
				mTvStatus.setText((String)msg.obj);
				break;
			case MSG_SHOW_URL:
				mTvImageUrl.setText((String)msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	private OnClickListener mOnClickLinstener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_refresh_image_1:
				mIvImageShow.setImageBitmap(null);
				startLoadImageThread();
				break;
			case R.id.btn_refresh_image_2:
				mIvImageShow.setImageBitmap(null);
				startLoadImageThreadWithCache();
				break;
			default:
				break;
			}
		}
	};
	
	private Bitmap getValidBitmap(String filePath){
		try{
			BitmapFactory.Options dbo = new BitmapFactory.Options();
			dbo.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, dbo);
		
			int nativeWidth = dbo.outWidth;
			int nativeHeight = dbo.outHeight;
        
			int wScale = 1, hScale = 1;
			if(nativeWidth > SCREEN_WIDTH){
				wScale = nativeWidth / SCREEN_WIDTH;
			}
			if(nativeHeight > SCREEN_HEIGHT){
				hScale = nativeHeight / SCREEN_HEIGHT;
			}
        
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = wScale > hScale ? hScale : wScale;
			Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);
			return bmp;
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}catch (IllegalArgumentException ie) {
			ie.printStackTrace();
		}
		
		return null;
	}
	
	private void sendMsg(int msgType, String strMsg) {
		Message msg = new Message();
		msg.what = msgType;
		msg.obj = strMsg;
		mHandler.sendMessage(msg);
	}
	
	private String readDownloadUrlFromFile() {
		String strUrlFilePath = Environment.getExternalStorageDirectory().toString() + "/Media+/tempurl";
		String strUrl = null;
		File file = new File(strUrlFilePath);    
		
		try {
			FileInputStream fis = new FileInputStream(file);
			int length = fis.available();

			byte[] buffer = new byte[length];
			fis.read(buffer);
			
			strUrl = EncodingUtils.getString(buffer, "UTF-8");
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return strUrl;
	}

}
