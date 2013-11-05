package com.example.test_remotecontroller.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;

public class AmbaDataSocket {
	private static final String EOL = "\r\n";
	private static final char LEFT_BRACE = '{'; //123
	private static final char RIGHT_BRACE = '}'; //125
	
	private static final String AMBA_HOST = "192.168.42.1";
	private static final int AMBA_DATA_PORT = 8787;
	private static final int SOCKET_TIMEOUT = 2000;
	private static final int DOWNLOAD_BUFFER_SIZE = 100 * 1024;
	
	private Socket mSocket = null;
	private InputStream mReader = null;
	private OutputStream mWriter = null;
	private Thread mConnectThread = null;
	private Thread mDataReceiveThread = null;
	private boolean mIsThreadStop = false; 
	private boolean mIsConnected = false;
	private boolean mHaveDownloadTask = false;
	private Object mSyncObj = new Object();
	private IDataSocketStatusListener mDataSocketStatusListener = null;
	
	private DownloadTask mDownloadTask = null;
	
	public static class DownloadTask {
		public int fileIndex = 0; 
		public boolean isThumb = false;
		public int priority = 0;
		public int needDownloadSize = 0;
//		public int totalSize = 0;
		public String md5sum = null;
		public String filePath = null;
		
		@Override
		public String toString() {
			String str = "DownloadTask: fileIndex = " + fileIndex + ", isThumb = " + isThumb + ", priority = " + priority + ", needDownloadSize = " + needDownloadSize/* + ", totalSize = " + totalSize*/
					+ ", md5sum = " + md5sum + ", filePath = " + filePath;
			return str;
		}
	}
	
	public static class DownloadResult {
		public int fileIndex = 0; 
		public String filePath = null;
		public boolean isThumb = false;
		public String md5sum = null;
		
		@Override
		public String toString() {
			String str = "DownloadTask: fileIndex = " + fileIndex + ", isThumb = " + isThumb
					+ ", md5sum = " + md5sum + ", filePath = " + filePath;
			return str;
		}
	}
	
	public interface IDataSocketStatusListener {
		public void onSocketConnected(boolean bSuc);
		public void onDataReceiveStart();
		public void onDataReceiving(int progress);
		public void onDataReceiveFinished(DownloadResult result);
		public void onThumbReceiveFinished(byte[] thumbData);
	}
	
	private boolean initSocket() {
		try {
			Log.v("zdf", "[AmbaDataSocket] initSocket 111");
			mSocket = new Socket(AMBA_HOST, AMBA_DATA_PORT);
			mSocket.setSoTimeout(SOCKET_TIMEOUT);
			Log.v("zdf", "[AmbaDataSocket]initSocket 222");
			mReader = mSocket.getInputStream();
			mWriter = mSocket.getOutputStream();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.v("zdf", "[AmbaDataSocket] initSocket, IOException e = " + e);
			return false;
		}
	}
	
	private void releaseSocket() {
		Log.v("zdf", "[AmbaDataSocket] releaseSocket");
		if (null != mSocket) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSocket = null;
		}
		
		if (null != mReader) {
			try {
				mReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mReader = null;
		}
		
		if (null != mWriter) {
			try {
				mWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mWriter = null;
		}
	}
	
	private int readData(byte[] dataBuffer) {
		if (null == mReader)
			return -1;
		
		int length = -1;
		try {
			Log.d("zdf", "********** [AmbaDataSocket] readData, before data read");
			length = mReader.read(dataBuffer);
			Log.d("zdf", "********** [AmbaDataSocket] readData, length = " + length);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("zdf", "********** [AmbaDataSocket] readData, IOException e = " + e);
		}
		
		return length;
	}
	
	public void connectSocket(IDataSocketStatusListener listener) {
		Log.v("zdf", "[AmbaDataSocket] connectSocket");
		mDataSocketStatusListener = listener;
		
		try {
			if (null != mConnectThread) {
				mConnectThread.join();
				mConnectThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mConnectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (initSocket()) {
					mIsConnected = true;
					startReceiveDataThread();
				}
				if (null != mDataSocketStatusListener) {
					mDataSocketStatusListener.onSocketConnected(mIsConnected);
				}
			}
		});
		mConnectThread.start();
	}
	
	public void disconnectSocket() {
		releaseSocket();
		mDataSocketStatusListener = null;
		mIsConnected = false;
	}
	
	public boolean isConnected() {
		return mIsConnected;
	}
	
	public void startDownloadTask(DownloadTask task) {
		Log.v("zdf", "[AmbaDataSocket] startDownloadTask");
		mDownloadTask = task;
		mHaveDownloadTask = true;
		threadWake(mSyncObj);
	}
	
	public void startReceiveDataThread() {
		Log.v("zdf", "[AmbaDataSocket] startReceiveDataThread");
		try {
			if (null != mDataReceiveThread) {
				mDataReceiveThread.join();
				mDataReceiveThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mDataReceiveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				mIsThreadStop = false;
				while (!mIsThreadStop) {
					if (!mHaveDownloadTask) {
						threadWait(mSyncObj);
						continue;
					}
					
					if (null == mDownloadTask)
						continue;
					
					try {
						File tempFile = new File(mDownloadTask.filePath);
	//					File dstFile = new File("/sdcard/amba_download_file.mp4");
						if (tempFile.exists())
							tempFile.delete();
						RandomAccessFile accessFile = new RandomAccessFile(tempFile, "rw");
						byte[] dataBuffer = new byte[DOWNLOAD_BUFFER_SIZE];
						int totalSize = 0;
						
						while (true) {
							int length = readData(dataBuffer); //可能有未读完的问题，待修改。。
							Log.d("zdf", "[AmbaDataSocket] mDataReceiveThread(run()), readData length = " + length);
						
							if (length == -1)
								break;
							
							totalSize += length;
							accessFile.write(dataBuffer, 0, length);
							
							Log.d("zdf", "[AmbaDataSocket] mDataReceiveThread(run()), readData length = " + length + ", totalSize = " + totalSize
									+ ", mDownloadTask.needDownloadSize = " + mDownloadTask.needDownloadSize);
							if (totalSize >= mDownloadTask.needDownloadSize) {
								break;
							}
						}
						
						if (null != accessFile) {
							accessFile.close();
							accessFile = null;
						}
						
	//					tempFile.renameTo(dstFile);
	//					tempFile.delete();
						Log.d("zdf", "[AmbaDataSocket] mDataReceiveThread(run()), readData finished!!!!");
						if (null != mDataSocketStatusListener) {
							DownloadResult result = new DownloadResult();
							result.fileIndex = mDownloadTask.fileIndex;
							result.filePath = mDownloadTask.filePath;
							result.isThumb = mDownloadTask.isThumb;
							result.md5sum = mDownloadTask.md5sum;
							mDataSocketStatusListener.onDataReceiveFinished(result);
						}
						
						mHaveDownloadTask = false;
					} catch (IOException e) {
						e.printStackTrace();
						Log.d("zdf", "[AmbaDataSocket] mDataReceiveThread(run()), IOException e = " + e);
					}
				}
			}
		});
		mDataReceiveThread.start();
	}
	
	public void stopReceiveDataThread() {
		Log.v("zdf", "[AmbaDataSocket] stopReceiveDataThread");
		mIsThreadStop = true;
		threadWake(mSyncObj);
		
		try {
			if (null != mDataReceiveThread) {
				mDataReceiveThread.join();
				mDataReceiveThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
}
