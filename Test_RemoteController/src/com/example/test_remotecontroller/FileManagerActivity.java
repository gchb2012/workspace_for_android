package com.example.test_remotecontroller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test_remotecontroller.FileManagerActivity.RemoteListAdapter.ViewHolder;
import com.example.test_remotecontroller.command.CommandMgr;
import com.example.test_remotecontroller.command.CommandMgr.AmbaCommandData;
import com.example.test_remotecontroller.command.CommandMgr.ICommandConnectionListener;
import com.example.test_remotecontroller.download.FileDownloadMgr;
import com.example.test_remotecontroller.download.AmbaDataSocket.DownloadResult;
import com.example.test_remotecontroller.download.AmbaDataSocket.DownloadTask;
import com.example.test_remotecontroller.download.FileDownloadMgr.IDownlaodConnectionListener;
import com.example.test_remotecontroller.utils.AppUtilDef;
import com.example.test_remotecontroller.utils.NetworkTool;
import com.example.test_remotecontroller.utils.NetworkTool.NetworkStateInfo;
import com.example.test_remotecontroller.utils.ToastMgr;

public class FileManagerActivity extends Activity {
	private NetworkTool mNetworkTool = null;
	
	private TextView mBtnBackToPrevious = null;
	private ListView mRemoteListView = null;
	private RemoteListAdapter mRemoteListAdapter = null;
	
	private LinkedList<RemoteFileItemData> mRemoteFileList = new LinkedList<RemoteFileItemData>();
	private ArrayList<Integer> mNeedDownloadThumbIndexList = new ArrayList<Integer>();
	private Object mSyncObj = new Object();
	private boolean mIsThreadStop = false;
	
	private String mCurSelectedFileName = null;
	private int mCurDownloadFileIndex = 0;
	
	private String mCurPath = AMBA_MEDIA_ROOT_PATH;
	
	private static final int MSG_REFRESH_LIST_ADAPTER = 0x01;
	private static final int MSG_REFRESH_LIST_THUMB = 0x02;
	private static final int MSG_SHOW_MEDIA_INFO_DIALOG = 0x03;
	
	private static final String AMBA_ROOT_PATH = "/";
	private static final String AMBA_MEDIA_ROOT_PATH = "/tmp/fuse_d/DCIM/100MEDIA";
	private static final String IMAGE_SUFFIX = ".jpg";
	private static final String VIDEO_SUFFIX = ".mp4";
	private static final String SMALL_VIDEO_SUFFIX = "_thm.mp4";
	private static final String VIDEO_THUMB_SUFFIX = ".thm";
	
	public class RemoteFileItemData {
		private Bitmap fileThumb = null;
		private String fileName = "";
		private String fileDate = "";
		private boolean isDir = false;
		private boolean isVideo = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("zdf", "[FileManagerActivity] onCreate");

		setContentView(R.layout.activity_file_manager);

		initUI();
		initNetworkTool();
		initConnection();
		startGetThumbThread();
		
		// test
//		RemoteFileItemData data1 = new RemoteFileItemData();
//		data1.fileName = "111111";
//		data1.fileDate = "aaaaa";
//		RemoteFileItemData data2 = new RemoteFileItemData();
//		data2.fileName = "222222";
//		data2.fileDate = "bbbbb";
//		mRemoteFileList.add(data1);
//		mRemoteFileList.add(data2);
	}
	
	private void initUI() {
		mBtnBackToPrevious = (TextView) findViewById(R.id.tv_back_to_previous);
		if (null != mBtnBackToPrevious) {
			mBtnBackToPrevious.setOnClickListener(mOnClickListener);
		}
		mRemoteListAdapter = new RemoteListAdapter(this);
		mRemoteListView = (ListView) findViewById(R.id.list_view);
		if (null != mRemoteListView) {
			mRemoteListView.setAdapter(mRemoteListAdapter);
			mRemoteListView.setOnItemClickListener(mOnItemClickListener);
		}
	}
	
	private void initConnection() {
		CommandMgr.instance().initConnection(mCommandConnectionListener);
		FileDownloadMgr.instance().initConnection(mDownloadConnectionListener);
	}
	
	private void uninitConnection() {
//		CommandMgr.instance().releaseConnection();
		FileDownloadMgr.instance().releaseConnection();
	}
	
	private void initNetworkTool() {
		mNetworkTool = new NetworkTool(this);
		mNetworkTool.setOnSettingChangeListener(mNetworkSettingListener);
		mNetworkTool.setOnConnectivityChangeListener(mNetworkConnectivityListener);
	}

	private final NetworkTool.IOnSettingChangeListener mNetworkSettingListener = new NetworkTool.IOnSettingChangeListener() {
		@Override
		public void OnBackgroundDataSettingChanged() {
			Log.d("zdf", "[FileManagerActivity] OnBackgroundDataSettingChanged");
		}
	};
	
	private final NetworkTool.IOnConnectivityChangeListener mNetworkConnectivityListener = new NetworkTool.IOnConnectivityChangeListener() {
		@Override
		public void OnConnectivityChanged(NetworkStateInfo info) {
			if (info.networkInfo == null
					|| info.networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
				return;
			}
			
			boolean isWifiConnected = info.networkInfo.isConnected();
			Log.d("zdf", "[FileManagerActivity] OnConnectivityChanged, isWifiConnected = " + isWifiConnected);
			
			if (isWifiConnected) {
//				sendCommand(AppUtilDef.MSG_ID_AMBA_PWD, null, null);
//				sendCommand(AppUtilDef.MSG_ID_AMBA_LS, null, null);
				sendCommand(AppUtilDef.MSG_ID_AMBA_CD, null, AMBA_MEDIA_ROOT_PATH);
			} else {
				ToastMgr.showToast(FileManagerActivity.this, "Wifi disconnect!", Toast.LENGTH_SHORT);
			}
		}
	};
	
	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back_to_previous:
				sendCommand(AppUtilDef.MSG_ID_AMBA_CD, null, getPreviousPath(mCurPath));
				break;
			default:
				break;
			}
		}
	};
	
	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {
			if (null == mRemoteFileList)
				return;
			
			boolean isDir = mRemoteFileList.get(position).isDir;
			String filePath = mRemoteFileList.get(position).fileName;
			Log.v("zdf", "[FileManagerActivity] onItemClick, position = " + position + ", fileName = " + filePath);
			if (isDir) {
				sendCommand(AppUtilDef.MSG_ID_AMBA_CD, null, filePath);
			} else {
				mCurSelectedFileName = mRemoteFileList.get(position).fileName;
				Log.v("zdf", "[FileManagerActivity] onItemClick, mCurSelectedFileName = " + mCurSelectedFileName);
				sendCommand(AppUtilDef.MSG_ID_AMBA_GET_MEDIAINFO, null, mCurSelectedFileName);
			}
		}
	};
	
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v("zdf", "handleMessage, msg.what = " + msg.what);
			switch (msg.what) {
			case MSG_REFRESH_LIST_ADAPTER:
				if (null != mRemoteListAdapter) {
					mRemoteListAdapter.notifyDataSetChanged();
				}
				break;
			case MSG_REFRESH_LIST_THUMB:
				int thumbIndex = msg.arg1;
				refreshListThumb(thumbIndex);
				break;
			case MSG_SHOW_MEDIA_INFO_DIALOG:
				AmbaCommandData cmdData = (AmbaCommandData) msg.obj;
				showMediaInfoDialog(cmdData);
				break;
			default:
				break;
			}
		}
	};
	
	private void sendCommand(int msgId, String type, String param) {
		CommandMgr.instance().sendCommand(msgId, type, param, -1, -1);
	}
	
	private void sendCommand(int msgId, String type, String param, int offset, int fetch_size) {
		CommandMgr.instance().sendCommand(msgId, type, param, offset, fetch_size);
	}
	
	private void parseFileList(AmbaCommandData cmdData) {
		if (null == cmdData)
			return;
		
		JSONArray fileListArray = (JSONArray) cmdData.listing;
		
		if (null == mRemoteFileList) {
			mRemoteFileList = new LinkedList<RemoteFileItemData>();
		}
		
		mRemoteFileList.clear();
		
		try {
			for (int i = 0; i < fileListArray.length(); i++) {
				JSONObject jsonObj = (JSONObject) fileListArray.get(i);
				Iterator it = jsonObj.keys();
//				while (it.hasNext()) {
				if (it.hasNext()) {
					String key = (String) it.next();
					String value = jsonObj.getString(key);
					Log.v("zdf", "[FileManagerActivity] parseFileList, " + key + ": " + value);
					
					if (null == key || null == value) {
						break;
					}
					
					RemoteFileItemData itemData = new RemoteFileItemData();
					itemData.fileName = key;
					itemData.fileDate = value;
					itemData.isDir = key.endsWith("/");
					
					mRemoteFileList.add(itemData);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void showMediaInfoDialog(AmbaCommandData cmdData) {
		String fileThumb = cmdData.fileThumb;
		int fileSize = cmdData.fileSize;
		String fileDate = cmdData.fileDate;
		String fileResolution = cmdData.fileResolution;
		String fileDuration = cmdData.fileDuration;
		String fileMediaType = cmdData.fileMediaType;
		
		String strMediaInfo = "Size: " + fileSize + "\r\n"
				+ "Date: " + fileDate + "\r\n"
				+ "Resolution: " + fileResolution + "\r\n"
				+ "Duration: " + fileDuration + "\r\n"
				+ "Media Type: " + fileMediaType;
		
		AlertDialog mAlertDialog = new AlertDialog.Builder(this)
				.setTitle(mCurSelectedFileName)
				.setMessage(strMediaInfo)
				.setCancelable(true)
				.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendCommand(AppUtilDef.MSG_ID_AMBA_DEL_FILE, null, mCurSelectedFileName);
					}
				})
				.setNegativeButton("OK", null)
				.create();
		mAlertDialog.show();
	}
	
	private String getPreviousPath(String strCurPath) {
		Log.v("zdf", "[FileManagerActivity] getPreviousPath, strCurPath = " + strCurPath);
		if (null == strCurPath || 0 == strCurPath.length() || strCurPath.equals("/"))
			return AMBA_ROOT_PATH;
		
		if (strCurPath.endsWith("/")) {
			strCurPath.substring(0, strCurPath.length() - 1);
		}
		Log.v("zdf", "[FileManagerActivity] getPreviousPath, strCurPath2 = " + strCurPath);
		
		if (!strCurPath.contains("/"))
			return strCurPath;
		
		int lastDirIndex = strCurPath.lastIndexOf("/");
		strCurPath = strCurPath.substring(0, lastDirIndex + 1);
		Log.v("zdf", "[FileManagerActivity] getPreviousPath, strCurPath3 = " + strCurPath);
		
		return strCurPath;
	}
	
	private void startGetThumbThread() {
		Log.d("zdf", "[FileManagerActivity] startGetThumbThread");
		new Thread(new Runnable() {
			@Override
			public void run() {
				mIsThreadStop = false;
				while(!mIsThreadStop) {
					if (null == mNeedDownloadThumbIndexList || 0 == mNeedDownloadThumbIndexList.size()) {
//						threadWait(mSyncObj);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					if (null != mRemoteFileList) {
						mCurDownloadFileIndex = mNeedDownloadThumbIndexList.get(0);
						String fileName = mRemoteFileList.get(mCurDownloadFileIndex).fileName;
						Log.d("zdf", "[FileManagerActivity] startGetThumbThread, fileName = " + fileName);
						sendCommand(AppUtilDef.MSG_ID_AMBA_GET_THUMB, AppUtilDef.TYPE_THUMB, fileName);
//						mNeedDownloadThumbIndexList.remove(0);
					}
					threadWait(mSyncObj);
				}
			}
		}).start();
	}
	
	private void stopGetThumbThread() {
		mIsThreadStop = true;
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
	
	private void refreshListThumb(int thumbIndex) {
		Log.v("zdf", "[FileManagerActivity] refreshListThumb, thumbIndex = " + thumbIndex);
		if (null == mRemoteListView)
			return;
		
		Bitmap thumb = mRemoteFileList.get(thumbIndex).fileThumb;
		Log.v("zdf", "[FileManagerActivity] refreshListThumb, thumb = " + thumb);
		if (null == thumb)
			return;
		
		int childCount = mRemoteListView.getChildCount();
		int firstVisiblePos = mRemoteListView.getFirstVisiblePosition();
		int thumbVisibleIndex = thumbIndex - firstVisiblePos;
		if(thumbVisibleIndex >= 0 && thumbVisibleIndex < childCount) {
			ViewHolder holder = (ViewHolder) mRemoteListView.getChildAt(thumbVisibleIndex).getTag();
			if(holder != null) {
				holder.fileThumb.setImageBitmap(thumb);
			}
		}
	}
	
	@Override
	protected void onResume() {
		Log.d("zdf", "[FileManagerActivity] onResume");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.d("zdf", "[FileManagerActivity] onPause");
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("zdf", "[FileManagerActivity] onDestory");
		stopGetThumbThread();
		uninitConnection();
		
		if (mNetworkTool != null) {
			mNetworkTool.recycle();
			mNetworkTool = null;
		}
		
		if (null != mRemoteFileList) {
			mRemoteFileList.clear();
			mRemoteFileList = null;
		}
	}
	
	
	public class RemoteListAdapter extends BaseAdapter {
		
		public final class ViewHolder {
			public ImageView fileThumb;
			public TextView fileName;
			public TextView fileDate;
		}

		private final Context mContext;
		private final LayoutInflater mInflater;

		public RemoteListAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}
		
		private ViewHolder setSettingView(ViewHolder holder, int index) {
			if (null == mRemoteFileList)
				return holder;
			Log.v("zdf", "[FileManagerActivity] setSettingView, mRemoteFileList.get(" + index + ").fileName = " + mRemoteFileList.get(index).fileName);
			
			RemoteFileItemData itemData = mRemoteFileList.get(index);
			if (null == itemData)
				return holder;
			
			Bitmap thumbBitmap = mRemoteFileList.get(index).fileThumb;
			Log.v("zdf", "[FileManagerActivity] setSettingView, thumbBitmap = " + thumbBitmap);
			if (null == thumbBitmap) {
				holder.fileThumb.setImageResource(R.drawable.thumbnail_default_photo);
				
				if (null != mNeedDownloadThumbIndexList && !mNeedDownloadThumbIndexList.contains(index)) {
					mNeedDownloadThumbIndexList.add(0, index);
//						for (int i = mNeedDownloadThumbIndexList.size() - 1; i > 10; i--) {
//							mNeedDownloadThumbIndexList.remove(i);
//						}
				}
//				synchronized (mNeedDownloadThumbIndexList) {
//					Log.v("zdf", "[FileManagerActivity] setSettingView, mNeedDownloadThumbIndexList = " + mNeedDownloadThumbIndexList);
//				}
			} else {
				holder.fileThumb.setImageBitmap(thumbBitmap);
			}
			holder.fileThumb.setTag(index);
			holder.fileThumb.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = (Integer) v.getTag();
					String fileName = mRemoteFileList.get(index).fileName;
					Log.v("zdf", "[FileManagerActivity] onClick(fileThumb), index = " + index + ", fileName = " + fileName);
					mCurSelectedFileName = fileName;
					sendCommand(AppUtilDef.MSG_ID_AMBA_GET_FILE, null, fileName, 0, 0);
					
					//test
//					mCurDownloadFileIndex = index;
//					sendCommand(AppUtilDef.MSG_ID_AMBA_GET_THUMB, AppUtilDef.TYPE_THUMB, fileName);
				}
			});
			holder.fileName.setText(itemData.fileName);
			holder.fileDate.setText(itemData.fileDate);
			
			return holder;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.file_list_item, null);

				holder = new ViewHolder();
				holder.fileThumb = (ImageView) convertView.findViewById(R.id.file_thumb);
				holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
				holder.fileDate = (TextView) convertView.findViewById(R.id.file_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder = setSettingView(holder, position);

			return convertView;
		}

		@Override
		public int getCount() {
			if (null == mRemoteFileList)
				return 0;
			
			return mRemoteFileList.size();
		}
		
		@Override
		public RemoteFileItemData getItem(int position) {
			if (null == mRemoteFileList)
				return null;
			
			return mRemoteFileList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}
	}
	
	private final ICommandConnectionListener mCommandConnectionListener = new ICommandConnectionListener() {
		@Override
		public void onConnectionCreated() {
			
		}

		@Override
		public void onConnectionInterrupt() {
			
		}
		
		@Override
		public void onCommandResponded(AmbaCommandData cmdData) {
			if (null == cmdData)
				return;
			
			int errorCode = cmdData.errorCode;
			int msgId = cmdData.msgId;
			String type = cmdData.type;
			Object param = cmdData.param;
//			String permission = cmdData.permission;
//			Object options = cmdData.options;
			String pwd = cmdData.pwd;
//			Object listing = cmdData.listing;
			Log.i("zdf", "[FileManagerActivity] onCommandResponded, msgId = " + msgId);
			
			if (AppUtilDef.SYSTEM_RETURN_SUCCESSFUL != errorCode) {
				if (AppUtilDef.MSG_ID_AMBA_GET_THUMB == msgId) {
					Log.e("zdf", "[FileManagerActivity] ************** download error!!!!!!!!!");
					mNeedDownloadThumbIndexList.remove(0);
					threadWake(mSyncObj);
				}
				return;
			}
			
			switch (msgId) {
			case AppUtilDef.MSG_ID_AMBA_PWD:
				sendCommand(AppUtilDef.MSG_ID_AMBA_CD, null, AMBA_MEDIA_ROOT_PATH);
				break;
			case AppUtilDef.MSG_ID_AMBA_CD:
				mCurPath = pwd;
				sendCommand(AppUtilDef.MSG_ID_AMBA_LS, null, null);
				break;
			case AppUtilDef.MSG_ID_AMBA_LS:
				parseFileList(cmdData);
				if (null != mHandler) {
					mHandler.sendEmptyMessage(MSG_REFRESH_LIST_ADAPTER);
				}
				break;
			case AppUtilDef.MSG_ID_AMBA_GET_MEDIAINFO:
				if (null != mHandler) {
					Message msg = mHandler.obtainMessage(MSG_SHOW_MEDIA_INFO_DIALOG, cmdData);
					mHandler.sendMessage(msg);
				}
				break;
			case AppUtilDef.MSG_ID_AMBA_GET_THUMB:
				DownloadTask thumbTask = new DownloadTask();
				thumbTask.fileIndex = mCurDownloadFileIndex;
				thumbTask.isThumb = true;
				thumbTask.needDownloadSize = cmdData.downloadTotalSize;
				thumbTask.md5sum = cmdData.downloadMd5sum;
//				thumbTask.filePath = "/sdcard/amba_download_thumb_" + cmdData.downloadThumbFileName; //目前回值中还没有这个参数
//				thumbTask.filePath = "/sdcard/amba_download_thumb_" + mCurSelectedFileName;
				thumbTask.filePath = "/sdcard/amba_download_thumb_" + mRemoteFileList.get(mCurDownloadFileIndex).fileName;
//				FileDownloadMgr.instance().startDownloadTask(task);
				FileDownloadMgr.instance().addDownloadTask(thumbTask);
//				threadWake(mSyncObj);
				break;
			case AppUtilDef.MSG_ID_AMBA_GET_FILE:
				DownloadTask fileTask = new DownloadTask();
				fileTask.fileIndex = mCurDownloadFileIndex;
				fileTask.isThumb = false;
				fileTask.needDownloadSize = cmdData.downloadRemSize;
//				fileTask.totalSize = cmdData.downloadTotalSize;
				fileTask.md5sum = cmdData.downloadMd5sum;
				fileTask.filePath = "/sdcard/amba_download_file_" + mCurSelectedFileName;
//				FileDownloadMgr.instance().startDownloadTask(task);
				FileDownloadMgr.instance().addDownloadTask(fileTask);
				break;
			case AppUtilDef.MSG_ID_AMBA_DEL_FILE:
				sendCommand(AppUtilDef.MSG_ID_AMBA_LS, null, null);
				break;
			case AppUtilDef.MSG_ID_AMBA_PUT_FILE:
				break;
			case AppUtilDef.MSG_ID_AMBA_NOTIFICATION:
//				if (null == type)
//					break;
//				
//				if (type.equalsIgnoreCase(AppUtilDef.NOTIFICATION_GET_FILE_COMPLETE)
//						|| type.equalsIgnoreCase(AppUtilDef.NOTIFICATION_GET_FILE_TIMEOUT)) {
//					if (null != mSocketClient) {
//						mSocketClient.stopFileDownloadThread();
//					}
//				}
				break;
			default:
				break;
			}
			
		}
	};
	
	private final IDownlaodConnectionListener mDownloadConnectionListener = new IDownlaodConnectionListener() {
		@Override
		public void onConnectionCreated() {
			
		}

		@Override
		public void onConnectionInterrupt() {
			
		}

		@Override
		public void onDownloadStart() {
			
		}

		@Override
		public void onDownloading(int progress) {
			
		}

		@Override
		public void onDownloadFinished(DownloadResult result) {
			Log.v("zdf", "[FileManagerActivity] onDownloadFinished, result = " + result);
			if (null == result || null == mRemoteFileList)
				return;
			
			Bitmap thumb = BitmapFactory.decodeFile(result.filePath);
			Log.v("zdf", "[FileManagerActivity] onDownloadFinished, thumb = " + thumb);
			if (null != thumb) {
				mRemoteFileList.get(result.fileIndex).fileThumb = Bitmap.createBitmap(thumb);
//				thumb.recycle();
//				thumb = null;
				
				if (null != mHandler) {
					Message msg = mHandler.obtainMessage(MSG_REFRESH_LIST_THUMB);
					msg.arg1 = result.fileIndex;
					mHandler.sendMessage(msg);
				}
			}
			
			mNeedDownloadThumbIndexList.remove(0);
			threadWake(mSyncObj);
			
//			if (null != mHandler) {
//				mHandler.sendEmptyMessage(MSG_REFRESH_LIST_ADAPTER);
//			}
		}

		@Override
		public void onThumbDownloadFininshed(Bitmap bitmap) {
			
		}
	};
}
