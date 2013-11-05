package com.example.test_remotecontroller.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.example.test_remotecontroller.command.AmbaCmdSocket.ICmdSocketStatusListener;
import com.example.test_remotecontroller.utils.AppUtilDef;


public class CommandMgr {

	private static CommandMgr sInstance = null;
	private Context mContext = null;
	private AmbaCmdSocket mCmdSocket = null;
	private int mTokenId = 0;
	private ICommandConnectionListener mCommandConnectionListener = null;
	
	public class AmbaCommandData {
		public int msgId = 0;
		public int errorCode = 0;
		public String type = null;
		public Object param = null;
		public String permission = null;
		public Object options = null;
		public String pwd = null;
		public Object listing = null;
		
		// media info
		public String fileThumb = null;
		public int fileSize = -1;
		public String fileDate = null;
		public String fileResolution = null;
		public String fileDuration = null;
		public String fileMediaType = null;
		
		// file download
		public int downloadRemSize = 0;
		public int downloadTotalSize = 0;
		public String downloadMd5sum = null;
		public String downloadThumbFileName = null;
	}
	
	public interface ICommandConnectionListener {
		public void onConnectionCreated();
		public void onConnectionInterrupt();
		public void onCommandResponded(AmbaCommandData cmdData);
	}
	
	public static void initSingleton(Context context) {
		if (sInstance != null) {
			throw new IllegalStateException("Already initialized.");
		}
		sInstance = new CommandMgr(context);
		sInstance.init();
	}

	public static void uninitSingleton() {
		if (null == sInstance) {
			throw new IllegalStateException("Not initialized.");
		}
		sInstance.uninit();
		sInstance = null;
	}

	public static CommandMgr instance() {
		if (sInstance == null) {
			throw new IllegalStateException("Uninitialized.");
		}
		return sInstance;
	}
	
	private CommandMgr(Context context) {
		mContext = context;
	}
	
	private void init() {
		
	}
	
	private void uninit() {
		
	}
	
	public void sendCommand(int msgId, String type, String param, int offset, int fetch_size) {
		String strCmd = makeCommand(mTokenId, msgId, type, param, offset, fetch_size);
		
		if (null != mCmdSocket) {
			mCmdSocket.sendCommand(strCmd);
		}
	}
	
	private String makeCommand(int tokenId, int msgId, String type, String param, int offset, int fetch_size) {
		JSONObject paramJSON = new JSONObject();
		try {
			if (fetch_size >= 0) {
				paramJSON.put(AppUtilDef.KEY_FETCH_SIZE, fetch_size);
			}
			if (offset >= 0) {
				paramJSON.put(AppUtilDef.KEY_OFFSET, offset);
			}
			if (null != param && param.length() > 0) {
				paramJSON.put(AppUtilDef.KEY_PARAM, param);
			}
			if (null != type && type.length() > 0) {
				paramJSON.put(AppUtilDef.KEY_TYPE, type);
			}
			if (msgId > 0) {
				paramJSON.put(AppUtilDef.KEY_MSG_ID, msgId);
			}
			if (tokenId >= 0) {
				paramJSON.put(AppUtilDef.KEY_TOKEN, tokenId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return paramJSON.toString();
	}
	
	private AmbaCommandData parseMessage(String strMsg) {
		if (null == strMsg)
			return null;
		
		AmbaCommandData cmdData = new AmbaCommandData();
		
		try {
			if (strMsg != null) {
				JSONObject jsonObj = new JSONObject(strMsg);
				
				if (jsonObj.has(AppUtilDef.KEY_RVAL)) {
					cmdData.errorCode = jsonObj.getInt(AppUtilDef.KEY_RVAL);
				}
				if (jsonObj.has(AppUtilDef.KEY_MSG_ID)) {
					cmdData.msgId = jsonObj.getInt(AppUtilDef.KEY_MSG_ID);
				}
				if (jsonObj.has(AppUtilDef.KEY_TYPE)) {
					cmdData.type = jsonObj.getString(AppUtilDef.KEY_TYPE);
				}
				if (jsonObj.has(AppUtilDef.KEY_PARAM)) {
					cmdData.param = jsonObj.get(AppUtilDef.KEY_PARAM);
				}
				if (jsonObj.has(AppUtilDef.KEY_PERMISSION)) {
					cmdData.permission = jsonObj.getString(AppUtilDef.KEY_PERMISSION);
				}
				if (jsonObj.has(AppUtilDef.KEY_OPTIONS)) {
					cmdData.options = jsonObj.get(AppUtilDef.KEY_OPTIONS);
				}
				if (jsonObj.has(AppUtilDef.KEY_PWD)) {
					cmdData.pwd = jsonObj.getString(AppUtilDef.KEY_PWD);
				}
				if (jsonObj.has(AppUtilDef.KEY_LISTING)) {
					cmdData.listing = jsonObj.get(AppUtilDef.KEY_LISTING);
				}
				// media info
				if (jsonObj.has(AppUtilDef.KEY_FILE_THUMB)) {
					cmdData.fileThumb = jsonObj.getString(AppUtilDef.KEY_FILE_THUMB);
				}
				if (jsonObj.has(AppUtilDef.KEY_FILE_SIZE)) {
					cmdData.fileSize = jsonObj.getInt(AppUtilDef.KEY_FILE_SIZE);
				}
				if (jsonObj.has(AppUtilDef.KEY_FILE_DATE)) {
					cmdData.fileDate = jsonObj.getString(AppUtilDef.KEY_FILE_DATE);
				}
				if (jsonObj.has(AppUtilDef.KEY_FILE_RESOLUTION)) {
					cmdData.fileResolution = jsonObj.getString(AppUtilDef.KEY_FILE_RESOLUTION);
				}
				if (jsonObj.has(AppUtilDef.KEY_FILE_DURATION)) {
					cmdData.fileDuration = jsonObj.getString(AppUtilDef.KEY_FILE_DURATION);
				}
				if (jsonObj.has(AppUtilDef.KEY_FILE_MEDIA_TYPE)) {
					cmdData.fileMediaType = jsonObj.getString(AppUtilDef.KEY_FILE_MEDIA_TYPE);
				}
				// file download
				if (jsonObj.has(AppUtilDef.KEY_DOWNLOAD_REM_SIZE)) {
					cmdData.downloadRemSize = jsonObj.getInt(AppUtilDef.KEY_DOWNLOAD_REM_SIZE);
				}
				if (jsonObj.has(AppUtilDef.KEY_DOWNLOAD_TOTAL_SIZE)) {
					cmdData.downloadTotalSize = jsonObj.getInt(AppUtilDef.KEY_DOWNLOAD_TOTAL_SIZE);
				}
				if (jsonObj.has(AppUtilDef.KEY_DOWNLOAD_THUMB_FILE_NAME)) {
					cmdData.downloadThumbFileName = jsonObj.getString(AppUtilDef.KEY_DOWNLOAD_THUMB_FILE_NAME);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.v("zdf", "[CommandMgr] JSONException e = " + e);
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("zdf", "[CommandMgr] Exception e = " + e);
		}
		
		return cmdData;
	}
	
	public void initConnection(ICommandConnectionListener listener) {
		Log.v("zdf", "[CommandMgr] initConnection");
		mCommandConnectionListener = listener;
		
		if (null == mCmdSocket) {
			mCmdSocket = new AmbaCmdSocket();
		}
		
		if (!mCmdSocket.isConnected()) {
			mCmdSocket.connectSocket(mCmdSocketStatusListener);
		}
	}
	
	public void releaseConnection() {
		Log.v("zdf", "[CommandMgr] releaseConnection");
		if (null != mCmdSocket) {
			mCmdSocket.disconnectSocket();
			mCmdSocket.stopReceiveMsgThread();
			mCommandConnectionListener = null;
		}
	}
	
	public void setTokenId(int tokenId) {
		mTokenId = tokenId;
	}
	
	public void setConnectionListener(ICommandConnectionListener listener) {
		mCommandConnectionListener = listener;
	}
	
	private final ICmdSocketStatusListener mCmdSocketStatusListener = new ICmdSocketStatusListener() {
		@Override
		public void onSocketConnected(boolean bSuc) {
			Log.i("zdf", "[CommandMgr] onSocketConnected, bSuc = " + bSuc);
			if (null != mCommandConnectionListener) {
				if (bSuc) {
					mCommandConnectionListener.onConnectionCreated();
				} else {
					mCommandConnectionListener.onConnectionInterrupt();
				}
			}
		}

		@Override
		public void onMessageReceived(String strMsg) {
			Log.i("zdf", "[CommandMgr] onMessageReceived");
			AmbaCommandData cmdData = parseMessage(strMsg);
			if (null != mCommandConnectionListener && null != cmdData) {
				mCommandConnectionListener.onCommandResponded(cmdData);
			}
		}
	};
}
