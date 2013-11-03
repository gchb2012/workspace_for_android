package com.zdf.test_mediastore;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		
		String externalStorageDir = Environment.getExternalStorageDirectory().toString(); // "/storage/sdcard0"   "/mnt/sdcard"
		Log.v("zdf", "#### externalStorageDir = " + externalStorageDir); 
		
//		String RootStorageDir = Environment.getRootDirectory().toString();
//		Log.v("zdf", "#### RootStorageDir = " + RootStorageDir); // "/system"
		
//		String filePath = "/storage/sdcard0/Media+/RolleiActionCam/Contents/FILE0333.mov";
//		String filePath = "/sdcard/Media+/RolleiActionCam/Contents/FILE0333.mov";
//		String filePath = "/mnt/sdcard/Media+/RolleiActionCam/Contents/FILE0333.mov";
		String filePath = externalStorageDir + "/Media+/RolleiActionCam/Contents/FILE0333.mov";
		Log.v("zdf", "#### filePath = " + filePath);
		
		ContentResolver cr = getContentResolver();
		
		Uri VideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI; //content://media/external/video/media
//		Uri FileUri = MediaStore.Files.getContentUri("external");
		Uri FileUri = Uri.parse("content://media/external/file");
		
		boolean isMediaStoreSupported = FileUtils.isMediaStoreSupported(filePath);
		Log.e("zdf", "#### queryDownloadMovVideoCursor, isMediaStoreSupported = " + isMediaStoreSupported);
		
		String selection = MediaStore.Video.Media.DATA + "=" + "\"" + filePath + "\"";
		Cursor cursorVideos = cr.query(VideoUri, null, selection, null, null);
		Log.v("zdf", "#### cursorVideos = " + cursorVideos);
		Log.v("zdf", "#### cursorVideos.getCount() = " + cursorVideos.getCount());
		
		Cursor cursorFiles = cr.query(FileUri, null, selection, null, null);
		Log.v("zdf", "#### cursorFiles = " + cursorFiles);
		Log.v("zdf", "#### cursorFiles.getCount() = " + cursorFiles.getCount());
		
		File deleteFile = new File(filePath);
		if (deleteFile.exists()) {
			deleteFile.delete();
		}
		
//		String where = MediaStore.Video.Media.DATA + "=" + "\"" + filePath + "\"";
//		cr.delete(FileUri, where, selectionArgs)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * if path contain character '#', Uri can not parse it, so as not to scan to
	 * media database. However, if path contain '#', thumb engine crash. so we
	 * now replace '#' to ' ' on DownloadPoolDriver.
	 * */
	private void scanSingleFile(String filePath) {
		if (filePath == null) {
			return;
		}
		// broadcast to scan file
		boolean bmnt = Environment.getExternalStorageDirectory().getPath()
				.startsWith("/mnt");
//		bmnt = mDownloadDestination
//				.equalsIgnoreCase(UpDownloadUtils.DOWNLOAD_DEST_SDCARD) ? bmnt
//				: false;
		File file = new File(bmnt ? ("/mnt" + filePath) : filePath);

		MediaScannerConnection.scanFile(mContext,
				new String[] { file.toString() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						Log.i("test", "Scanned " + path + ":");
						Log.i("test", "-> uri=" + uri);

						/*
						 * ͨ��scanfileд��data
						 * base��date���ܴ�1000������Ҫ��֤����date�ֶ�
						 */
						ContentResolver cr = mContext.getContentResolver();
						long datemodified = 0;
						long dateadded = 0;
						Cursor cursor = cr.query(uri, null, null, null, null);
						if (cursor != null && cursor.moveToFirst()) {
							datemodified = cursor.getLong(cursor
									.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
							dateadded = cursor.getLong(cursor
									.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
							cursor.close();
						}

						ContentValues values = new ContentValues();
						if (datemodified > 0
								&& String.valueOf(datemodified).length() > 10) {
							values.put(MediaStore.MediaColumns.DATE_MODIFIED,
									datemodified / 1000);
						}
						if (dateadded > 0
								&& String.valueOf(dateadded).length() > 13) {
							values.put(MediaStore.MediaColumns.DATE_ADDED,
									dateadded / 1000);
						}

						if (values.size() > 0) {
							cr.update(uri, values, null, null);
						}
					}
				});
	}
	
}
