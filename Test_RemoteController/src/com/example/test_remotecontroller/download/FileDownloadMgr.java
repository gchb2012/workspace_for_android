package com.example.test_remotecontroller.download;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.test_remotecontroller.download.AmbaDataSocket.DownloadResult;
import com.example.test_remotecontroller.download.AmbaDataSocket.DownloadTask;
import com.example.test_remotecontroller.download.AmbaDataSocket.IDataSocketStatusListener;

public class FileDownloadMgr {
	private static FileDownloadMgr sInstance = null;
	private Context mContext = null;
	private AmbaDataSocket mDataSocket = null;
	private IDownlaodConnectionListener mDownloadConnectionListener = null;
	private ArrayList<DownloadTask> mDownloadQueue = new ArrayList<DownloadTask>();
	private Thread mDownloadThread = null;
	private Object mSyncObj = new Object();
	private boolean mDownloadTaskStop = false;
	
	public interface IDownlaodConnectionListener {
		public void onConnectionCreated();
		public void onConnectionInterrupt();
		public void onDownloadStart();
		public void onDownloading(int progress);
		public void onDownloadFinished(DownloadResult result);
		public void onThumbDownloadFininshed(Bitmap bitmap);
	}
	
	public static void initSingleton(Context context) {
		if (sInstance != null) {
			throw new IllegalStateException("Already initialized.");
		}
		sInstance = new FileDownloadMgr(context);
		sInstance.init();
	}

	public static void uninitSingleton() {
		if (null == sInstance) {
			throw new IllegalStateException("Not initialized.");
		}
		sInstance.uninit();
		sInstance = null;
	}

	public static FileDownloadMgr instance() {
		if (sInstance == null) {
			throw new IllegalStateException("Uninitialized.");
		}
		return sInstance;
	}
	
	private FileDownloadMgr(Context context) {
		mContext = context;
	}
	
	private void init() {
		
	}
	
	private void uninit() {
		
	}
	
	public void initConnection(IDownlaodConnectionListener listener) {
		Log.v("zdf", "[FileDownloadMgr] initConnection");
		mDownloadConnectionListener = listener;
		
		if (null == mDataSocket) {
			mDataSocket = new AmbaDataSocket();
		}
		
		if (!mDataSocket.isConnected()) {
			mDataSocket.connectSocket(mDataSocketStatusListener);
		}
	}
	
	public void releaseConnection() {
		Log.v("zdf", "[FileDownloadMgr] releaseConnection");
		if (null != mDataSocket) {
			mDataSocket.disconnectSocket();
			mDataSocket.stopReceiveDataThread();
			mDownloadConnectionListener = null;
		}
		
		stopDownloadTask();
	}
	
	public void setConnectionListener(IDownlaodConnectionListener listener) {
		mDownloadConnectionListener = listener;
	}
	
	public void setNeedDownloadSize(int needSize) {
		
	}
	
	public void addDownloadTask(DownloadTask task) {
		Log.d("zdf", "[FileDownloadMgr] addDownloadTask, task = " + task);
		if (null == mDownloadQueue) {
			mDownloadQueue = new ArrayList<DownloadTask>();
		}
		
		mDownloadQueue.add(task);
		threadWake(mSyncObj);
	}
	
//	public void startDownloadTask(DownloadTask task) {
//		Log.v("zdf", "[FileDownloadMgr] startDownloadTask, task = " + task);
//		if (null != mDataSocket) {
//			mDataSocket.startDownloadTask(task);
//		}
//	}
	
	private void startDownloadTask() {
		Log.v("zdf", "[FileDownloadMgr] startDownloadTask");
		
		mDownloadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				mDownloadTaskStop = false;
				while(!mDownloadTaskStop) {
					if (null == mDownloadQueue || 0 == mDownloadQueue.size()) {
						threadWait(mSyncObj);
						continue;
					}
					
					if (null != mDataSocket) {
						DownloadTask task = mDownloadQueue.get(0);
						Log.v("zdf", "[FileDownloadMgr] startDownloadTask(run()), task = " + task);
						mDataSocket.startDownloadTask(task);
						mDownloadQueue.remove(0);
						threadWait(mSyncObj);
					}
				}
			}
		});
		mDownloadThread.start();
	}
	
	private void stopDownloadTask() {
		mDownloadTaskStop = true;
		threadWake(mSyncObj);
	}
	
	private void threadWake(Object obj) {
		synchronized (obj) {
			obj.notify();
		}
	}

	private void threadWait(Object obj) {
		synchronized (obj) {
			try {
				obj.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private final IDataSocketStatusListener mDataSocketStatusListener = new IDataSocketStatusListener() {
		@Override
		public void onSocketConnected(boolean bSuc) {
			Log.i("zdf", "[FileDownloadMgr] onSocketConnected, bSuc = " + bSuc);
			if (null != mDownloadConnectionListener) {
				if (bSuc) {
					startDownloadTask();
					mDownloadConnectionListener.onConnectionCreated();
				} else {
					mDownloadConnectionListener.onConnectionInterrupt();
				}
			}
		}
		
		@Override
		public void onDataReceiveStart() {
			Log.v("zdf", "[FileDownloadMgr] onDataReceiveStart");
		}

		@Override
		public void onDataReceiving(int progress) {
			Log.v("zdf", "[FileDownloadMgr] onDataReceiving, progress = " + progress);
			
		}

		@Override
		public void onDataReceiveFinished(DownloadResult result) {
			Log.v("zdf", "[FileDownloadMgr] onDataReceiveFinished");
			if (null != mDownloadConnectionListener) {
				mDownloadConnectionListener.onDownloadFinished(result);
			}
			threadWake(mSyncObj);
		}

		@Override
		public void onThumbReceiveFinished(byte[] thumbData) {
			Log.v("zdf", "[FileDownloadMgr] onThumbReceiveFinished");
			if (null != mDownloadConnectionListener) {
				mDownloadConnectionListener.onThumbDownloadFininshed(null);
			}
		}
	};
}
