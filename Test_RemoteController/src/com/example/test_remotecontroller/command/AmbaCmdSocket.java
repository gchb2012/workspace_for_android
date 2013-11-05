package com.example.test_remotecontroller.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Process;
import android.util.Log;

public class AmbaCmdSocket {
	private static final String EOL = "\r\n";
	private static final char LEFT_BRACE = '{'; //123
	private static final char RIGHT_BRACE = '}'; //125
	
	private static final String AMBA_HOST = "192.168.42.1";
	private static final int AMBA_CMD_PORT = 7878;
	private static final int SOCKET_TIMEOUT = 2000;
	
	private Socket mSocket = null;
	private BufferedReader mReader = null;
	private PrintWriter mWriter = null;
	private Thread mConnectThread = null;
	private Thread mMsgReceiveThread = null;
	private boolean mIsThreadStop = false; 
	private boolean mIsConnected = false;
	private ICmdSocketStatusListener mCmdSocketStatusListener = null;
	
	public interface ICmdSocketStatusListener {
		public void onSocketConnected(boolean bSuc);
		public void onMessageReceived(String strMsg);
	}
	
	private boolean initSocket() {
		try {
			Log.v("zdf", "[AmbaCmdSocket] initSocket 111");
			mSocket = new Socket(AMBA_HOST, AMBA_CMD_PORT);
//			mSocket.setSoTimeout(SOCKET_TIMEOUT);
			Log.v("zdf", "[AmbaCmdSocket] initSocket 222");
			mReader = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
			mWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					mSocket.getOutputStream())), true);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.v("zdf", "[AmbaCmdSocket] initSocket, IOException e = " + e);
			return false;
		}
	}
	
	private void releaseSocket() {
		Log.v("zdf", "[AmbaCmdSocket] releaseSocket");
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
			mWriter.close();
			mWriter = null;
		}
	}
	
	private String readLine() {
		if (null == mReader)
			return null;
		Log.v("zdf", "[AmbaCmdSocket] readLine");
		
		int braceCount = 0;
		boolean canStopRead = false;
		int readChar = 0;
		StringBuffer readStr = new StringBuffer();
		while (true) {
			try {
				// 如果空闲无消息过来时，线程会阻塞在mReader.read()中，并通过设置TimeOut来控制阻塞时间
//				Log.v("zdf", "[AmbaCmdSocket] readLine, before read");
				readChar = mReader.read();
//				Log.e("zdf", "******** [AmbaCmdSocket] readLine, readChar = " + readChar);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("zdf", "[AmbaCmdSocket] readLine, IOException e = " + e);
				break;
			}
			
			if (readChar != 0) {
				readStr.append((char)readChar);
			}
			
			if (LEFT_BRACE == readChar) {
				braceCount++;
				canStopRead = true;
			} else if (RIGHT_BRACE == readChar) {
				braceCount--;
			}
			
			if (canStopRead && 0 == braceCount) {
				Log.e("zdf", "[AmbaCmdSocket] readLine finished!!!!");
				break;
			}
		}
		Log.e("zdf", "[AmbaCmdSocket] readLine, readStr = " + readStr.toString().trim());

		return readStr.toString().trim();
	}
	
	public void connectSocket(ICmdSocketStatusListener listener) {
		mCmdSocketStatusListener = listener;
		
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
					startReceiveMsgThread();
				}
				if (null != mCmdSocketStatusListener) {
					mCmdSocketStatusListener.onSocketConnected(mIsConnected);
				}
			}
		});
		mConnectThread.start();
	}
	
	public void disconnectSocket() {
		releaseSocket();
		mCmdSocketStatusListener = null;
		mIsConnected = false;
	}
	
	public boolean isConnected() {
		return mIsConnected;
	}
	
	public void sendCommand(String strCmd) {
		if (null != mSocket && mSocket.isConnected()) {
			if (!mSocket.isOutputShutdown()) {
				Log.e("zdf", "[AmbaCmdSocket] sendCommand, strCmd = " + strCmd);
				mWriter.println(strCmd + EOL);
			}
		}
	}
	
	public void startReceiveMsgThread() {
		try {
			if (null != mMsgReceiveThread) {
				mMsgReceiveThread.join();
				mMsgReceiveThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mMsgReceiveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				mIsThreadStop = false;
				while (!mIsThreadStop) {
					String strMsg = readLine();
					if (null != mCmdSocketStatusListener) {
						mCmdSocketStatusListener.onMessageReceived(strMsg);
					}
				}
			}
		});
		mMsgReceiveThread.start();
	}
	
	public void stopReceiveMsgThread() {
		mIsThreadStop = true;
		
		try {
			if (null != mMsgReceiveThread) {
				mMsgReceiveThread.join();
				mMsgReceiveThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

