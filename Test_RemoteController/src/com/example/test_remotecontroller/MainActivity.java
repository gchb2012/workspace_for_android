package com.example.test_remotecontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test_remotecontroller.command.CommandMgr;
import com.example.test_remotecontroller.command.CommandMgr.AmbaCommandData;
import com.example.test_remotecontroller.command.CommandMgr.ICommandConnectionListener;
import com.example.test_remotecontroller.download.FileDownloadMgr;
import com.example.test_remotecontroller.utils.AppUtilDef;

public class MainActivity extends Activity {
	private static final String AMBA_MEDIA_ROOT_PATH = "/tmp/fuse_d/DCIM/100MEDIA";
	
	private static final int MSG_SOCKET_CONNECTED = 0x01;
	private static final int MSG_RECEIVE_MESSAGE = 0x02;
	
	private boolean mIsSocketConnected = false;
	private boolean mIsSessionStarted = false;
	private boolean mIsRecording = false;
	
	private Button mBtnSocket = null;
	private Button mBtnSession = null;
	private Button mBtnCapture = null;
	private Button mBtnRecord = null;
	private Button mBtnSettings = null;
	private EditText mEtCD = null;
	private Button mBtnPWD = null;
	private Button mBtnCD = null;
	private Button mBtnLS = null;
	private Button mBtnFileManager = null;
	private TextView mTvShowInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initUI();
		CommandMgr.initSingleton(this);
		FileDownloadMgr.initSingleton(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		disconnectServer();
		releaseUI();
		CommandMgr.uninitSingleton();
		FileDownloadMgr.uninitSingleton();
	}
	
	private void initUI() {
		mBtnSocket = (Button) findViewById(R.id.btn_socket);
		mBtnSession = (Button) findViewById(R.id.btn_session);
		mBtnCapture = (Button) findViewById(R.id.btn_capture);
		mBtnRecord = (Button) findViewById(R.id.btn_record);
		mBtnSettings = (Button) findViewById(R.id.btn_settings);
		mEtCD = (EditText) findViewById(R.id.et_cd);
		mBtnPWD = (Button) findViewById(R.id.btn_pwd);
		mBtnCD = (Button) findViewById(R.id.btn_cd);
		mBtnLS = (Button) findViewById(R.id.btn_ls);
		mBtnFileManager = (Button) findViewById(R.id.btn_file_manager);
		mTvShowInfo = (TextView) findViewById(R.id.tv_show_info);
		
		if (null != mBtnSocket) {
			mBtnSocket.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnSession) {
			mBtnSession.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnCapture) {
			mBtnCapture.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnRecord) {
			mBtnRecord.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnSettings) {
			mBtnSettings.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnCD) {
			mBtnCD.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnPWD) {
			mBtnPWD.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnLS) {
			mBtnLS.setOnClickListener(mOnClickListener);
		}
		if (null != mBtnFileManager) {
			mBtnFileManager.setOnClickListener(mOnClickListener);
		}
		
		if (null != mEtCD) {
			mEtCD.setText(AMBA_MEDIA_ROOT_PATH);
			mEtCD.setSelection(AMBA_MEDIA_ROOT_PATH.length());
		}
	}
	
	private void releaseUI() {
		
	}
	
	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_socket:
				if (!mIsSocketConnected) {
					connectServer();
					if (null != mBtnSocket) {
						mBtnSocket.setText(R.string.socket_disconnect);
					}
				} else {
					disconnectServer();
					if (null != mBtnSocket) {
						mBtnSocket.setText(R.string.socket_connect);
					}
				}
				mIsSocketConnected = !mIsSocketConnected;
				break;
			case R.id.btn_session:
				if (!mIsSessionStarted) {
					startSession();
					if (null != mBtnSession) {
						mBtnSession.setText(R.string.stop_session);
					}
				} else {
					stopSession();
					if (null != mBtnSession) {
						mBtnSession.setText(R.string.start_session);
					}
				}
				mIsSessionStarted = !mIsSessionStarted;
				break;
			case R.id.btn_capture:
				takeCapture();
				break;
			case R.id.btn_record:
				if (!mIsRecording) {
					startRecord();
					if (null != mBtnRecord) {
						mBtnRecord.setText(R.string.stop_record);
					}
				} else {
					stopRecord();
					if (null != mBtnRecord) {
						mBtnRecord.setText(R.string.start_record);
					}
				}
				mIsRecording = !mIsRecording;
				break;
			case R.id.btn_settings:
				break;
			case R.id.btn_cd:
				break;
			case R.id.btn_pwd:
				pwd();
				break;
			case R.id.btn_ls:
				ls();
				break;
			case R.id.btn_file_manager:
				startFileManagerActivity();
				break;
			default:
				break;
			}
		}
	};
	
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RECEIVE_MESSAGE:
				break;
			case MSG_SOCKET_CONNECTED:
				break;
			default:
				break;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("zdf", "onActivityResult");
		CommandMgr.instance().setConnectionListener(mCommandConnectionListener);
	}
	
	private void connectServer() {
		Log.v("zdf", "connectServer");
		CommandMgr.instance().initConnection(mCommandConnectionListener);
	}
	
	private void disconnectServer() {
		Log.v("zdf", "disconnectServer");
		CommandMgr.instance().releaseConnection();
	}
	
	private void startSession() {
		Log.v("zdf", "startSession");
		sendCommand(AppUtilDef.MSG_ID_AMBA_START_SESSION, null, null);
	}
	
	private void stopSession() {
		Log.v("zdf", "stopSession");
		sendCommand(AppUtilDef.MSG_ID_AMBA_STOP_SESSION, null, null);
	}
	
	private void takeCapture() {
		Log.v("zdf", "takeCapture");
		sendCommand(AppUtilDef.MSG_ID_AMBA_TAKE_PHOTO, null, null);
	}
	
	private void startRecord() {
		Log.v("zdf", "startRecord");
		sendCommand(AppUtilDef.MSG_ID_AMBA_RECORD_START, null, null);
	}
	
	private void stopRecord() {
		Log.v("zdf", "stopRecord");
		sendCommand(AppUtilDef.MSG_ID_AMBA_RECORD_STOP, null, null);
	}
	
	private void cd() {
		Log.v("zdf", "cd");
		String strPath = mEtCD.getText().toString();
		sendCommand(AppUtilDef.MSG_ID_AMBA_CD, null, strPath);
	}
	
	private void pwd() {
		Log.v("zdf", "pwd");
		sendCommand(AppUtilDef.MSG_ID_AMBA_PWD, null, null);
	}
	
	private void ls() {
		Log.v("zdf", "ls");
		sendCommand(AppUtilDef.MSG_ID_AMBA_LS, null, null);
	}
	
	private void startFileManagerActivity() {
		Intent intent = new Intent();
		intent.setClass(this, FileManagerActivity.class);
		startActivityForResult(intent, 0);
	}
	
	private void sendCommand(int msgId, String type, String param) {
		CommandMgr.instance().sendCommand(msgId, type, param, -1, -1);
	}
	
	private final ICommandConnectionListener mCommandConnectionListener = new ICommandConnectionListener() {
		@Override
		public void onConnectionCreated() {
			Log.i("zdf", "[MainActivity] onConnectionPrepared");
		}

		@Override
		public void onConnectionInterrupt() {
			Log.i("zdf", "[MainActivity] onConnectionInterrupt");
		}

		@Override
		public void onCommandResponded(AmbaCommandData cmdData) {
			if (null == cmdData)
				return;
			
			int errorCode = cmdData.errorCode;
			int msgId = cmdData.msgId;
			String type = cmdData.type;
			Object param = cmdData.param;
			String permission = cmdData.permission;
			Object options = cmdData.options;
			String pwd = cmdData.pwd;
			Object listing = cmdData.listing;
			Log.i("zdf", "[MainActivity] onCommandResponded, msgId = " + msgId + ", type = " + type + ", errorCode = " + errorCode
					+ ", param = " + param + ", permission = " + permission + ", options = " + options + ", pwd = " + pwd + ", listing = " + listing);
			
			if (AppUtilDef.SYSTEM_RETURN_SUCCESSFUL != errorCode) {
//				sendErrorMessage(msgId, errorCode);
				return;
			}
			
			switch (msgId) {
			case AppUtilDef.MSG_ID_AMBA_START_SESSION:
				if (param == null)
					break;
				int tokenId = (Integer)param;
				if (tokenId > 0) {
					CommandMgr.instance().setTokenId(tokenId);
				}
//				mIsSessionStarted = true;
				break;
			case AppUtilDef.MSG_ID_AMBA_STOP_SESSION:
//				mIsSessionStarted = false;
				break;
			case AppUtilDef.MSG_ID_AMBA_STOP_FV:
				break;
			case AppUtilDef.MSG_ID_AMBA_RESET_FV:
				break;
			case AppUtilDef.MSG_ID_AMBA_GET_SETTING:
				break;
			case AppUtilDef.MSG_ID_AMBA_SET_SETTING:
				break;
			case AppUtilDef.MSG_ID_AMBA_GET_RECORD_TIME:
				break;
			case AppUtilDef.MSG_ID_AMBA_TAKE_PHOTO:
				break;
			case AppUtilDef.MSG_ID_AMBA_RECORD_START:
				break;
			case AppUtilDef.MSG_ID_AMBA_RECORD_STOP:
				break;
			case AppUtilDef.MSG_ID_AMBA_NOTIFICATION:
//				processUserOperation(type);
				break;
			default:
//				sendErrorMessage(msgId, errorCode);
				break;
			}
		}
	};

}
