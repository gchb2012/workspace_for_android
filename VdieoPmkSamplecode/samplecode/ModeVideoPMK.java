package com.android.camera.mode;


import com.android.camera.AppSettings;
import com.android.camera.ArcGlobalDef;
import com.android.camera.ConfigMgr;
import com.android.camera.R;
import com.android.camera.utils.JUtils;
import com.android.camera.utils.LogUtils;
import com.android.camera.utils.SystemReceiverMgr;
import com.android.camera.utils.Utils;
import com.arcsoft.android.camera.engine.CameraEngine;
import com.arcsoft.android.camera.engine.CameraSettings;
import com.arcsoft.android.camera.engine.GlobalDefine;
import com.arcsoft.android.camera.engine.CameraEngine.Parameters;
import com.arcsoft.android.camera.engine.CameraEngine.PreviewCallback;
import com.arcsoft.android.camera.engine.SelectFrameResult;
import com.arcsoft.android.camera.utils.MSize;
import com.arcsoft.android.camera.utils.Panorama2Info;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class ModeVideoPMK extends ModeBaseCamera implements PreviewCallback{

	public static final String TAG = "ModeVideoPMK";
	
	public static final int	ASVL_PAF_NV21			= 0x802;
	
	private static final int	PANORAMA2_FLAG_STITCH		= 1;
	private static final int	PANORAMA2_FLAG_PREVIEW		= 2;
	private static final int	PANORAMA2_FLAG_ANGLE		= 3;
	private static final int	PANROAMA2_FLAG_SWITCH		= 4;
	private static final int	PANROAMA2_FLAG_RESET		= 5;

	private static final int	MSGTYPE_MERR_BASE				= 0x7000;
	private static final int	MSGTYPE_ERR_TRACE_DIRECTION		= MSGTYPE_MERR_BASE + 1; //方向错误
	private static final int	MSGTYPE_ERR_TRACE_NO_FEATURE	= MSGTYPE_MERR_BASE + 2; //跟踪错误
	private static final int	MSGTYPE_ERR_TRACE_INTERFERENCE	= MSGTYPE_MERR_BASE + 3; //移动过快
	private static final int	MSGTYPE_ERR_TRACE_NO_SUPPORT	= MSGTYPE_MERR_BASE + 4;
	
	private static final int	PROCESS_PREVIEW = 1;
	private static final int	RESET			= 2;
	private static final int	PROCESS_THUBNAIL = 3;
	private static final int	PROCESS_FINISH	= 4;
	
	private boolean mIsCapturing = false;
	private boolean mIsShowAnimation = false;
	private boolean mIsOnPause = false;
	
	private ProgressDialog mProgressDialog = null;
	private SelectFrameResult mTempResult = null;
	
	private Handler mVideoPMKHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch(msg.what) {
			case PROCESS_PREVIEW:
				processPreview((byte[]) msg.obj);
				break;
			case RESET:
				mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_RESET, null);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				
				if (isReviewOn()) {
					mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_FACETRACKINGVIEW_HIDE, null);
					changCameraState(ArcGlobalDef.ARCCAM_STATE_REVIEWING);
					LogUtils.v(LogUtils.TAG,TAG + "go to review");
					if(isCancleReview()) {
						if(null != mHandler){
							mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
						}
						return;
					}
					mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_REVIEW_CAPTURE, mFilePath);
				}
				break;
			case PROCESS_THUBNAIL:
				mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_CAPTURING, msg.obj);
				break;
			case PROCESS_FINISH:
				stopCapture();
				mCamEngine.startPreview();
				break;
			}
		}
		
	};
	
	public ModeVideoPMK(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Log.i("ArcsoftPMK", "ModeVideoPMK ");
	}
	
	@Override
	public boolean init(ModeBaseInfo baseInfo, boolean bSetEngine) {
		// TODO Auto-generated method stub
		Log.i("ArcsoftPMK", "init");
		boolean res = super.init(baseInfo, bSetEngine);
		
		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_SHOW, null);
		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_RESET, null);
		
		return res;
	}


	@Override
	public void unInit() {
		// TODO Auto-generated method stub
		super.unInit();
		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_HIDE, null);
		if (mVideoPMKHandler != null) {
			mVideoPMKHandler.removeMessages(PROCESS_PREVIEW);
			mVideoPMKHandler = null;
		}
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mIsOnPause = false;
		mCamEngine.setParameter(CameraSettings.KEY_FOCUS_MODE, AppSettings.VALUE_FOCUS_AUTO);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mIsOnPause = true; 
		if (mIsCapturing)
			stopCapture();
	}

	public void stopCapture() {
		if (!mIsProcessPreview)
			return;
		
		super.stopCapture();
		mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_APP_BUSY, true);
		Log.i("ArcsoftPMK", "stop capture");
		soundPlayer.playSoundEffect(AppSettings.VALUE_VIDEO_RECORD_SOUND_STOP);
		mCamEngine.stopPreviewCallback();
		mCamEngine.setVideoPMKParametersLock(false);
		processResult(true);
		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_RESET_VIEW, null);
		if (mIsOnPause) {
			mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_RESET, null);
		} else {
			mVideoPMKHandler.sendEmptyMessageDelayed(RESET, 1000);
			mProgressDialog = ProgressDialog.show(mContext, null, "Enhaning...");
		}
		mIsShowAnimation = false;
		mCamEngine.uninitVidePMK();
	}
	
	private void cancelCapture() {
		mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_APP_BUSY, true);
		Log.i("ArcsoftPMK", "cancel capture");
		soundPlayer.playSoundEffect(AppSettings.VALUE_VIDEO_RECORD_SOUND_STOP);
		mCamEngine.stopPreviewCallback();
		mCamEngine.setVideoPMKParametersLock(false);
		processResult(false);
		changCameraState(ArcGlobalDef.ARCCAM_STATE_PREVIEWING);
		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_RESET_VIEW, null);
		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_RESET, null);
		mIsShowAnimation = false;
		mCamEngine.uninitVidePMK();
		mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_APP_BUSY, false);
	}
	
	@Override
	protected int startCapture() {
		// TODO Auto-generated method stub
		if(mCameraState == ArcGlobalDef.ARCCAM_STATE_CAPTURING) {
			stopCapture();
			return 0;
		}
		
		Log.i("ArcsoftPMK", "startCapture ");
		mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_APP_BUSY, true);
		cancelTrackTask();
		mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_PREPARE_CAPTURE, true);
		changCameraState(ArcGlobalDef.ARCCAM_STATE_CAPTURING);
		mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_SHUTTER_BTN_RESET, null);
		
		mCameraApp.notifyUI(GlobalDefine.NotifyType.MSG_PHOTO_CAPTURED_INDEX, null);
		
		boolean res = mCamEngine.initVideoPMK();
		if (!res) {
			cancelCapture();
			mCamEngine.startPreview();
			return 0;
		}
			
		mCamEngine.setVideoPMKParametersLock(true);
		Panorama2Info info = new Panorama2Info();
		info.setFlag(PANROAMA2_FLAG_SWITCH);
		int res2 = CameraEngine.native_specProcess(info);
		Log.i("ArcsoftPMK", "native_specProcess : " + res2);
		mCamEngine.setPreviewCallback(ModeVideoPMK.this);
		mIsCapturing = true;
		
		soundPlayer.playSoundEffect(AppSettings.VALUE_VIDEO_RECORD_SOUND_START);
		mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_APP_BUSY, false);
		return 0;
	}

	@Override
	protected void initEngineCommonParam(Parameters param) {
		// TODO Auto-generated method stub
		super.initEngineCommonParam(param);
		
		Log.i("ArcsoftPMK", "initEngineCommonParam ");
	}
	
	protected void initEngineParamByMode(Parameters param){
		mSavingWithThread = true;
		/** set mode to camera engine */
		mCamEngine.setMode(AppSettings.VALUE_MODE_CAMERA_PANORAMA);
		param.set(AppSettings.KEY_PREVIEW_FORMAT, android.graphics.ImageFormat.NV21);
		param.set(AppSettings.KEY_PICTURE_FORMAT, AppSettings.VALUE_PICTURE_FORMAT_JPEG);
		/* focus mode */
		param.set(AppSettings.KEY_FOCUS_MODE, AppSettings.VALUE_FOCUS_AUTO);
		MSize mpreviewsize = Utils.getPreviewSize(AppSettings.VALUE_SIZE_720P);
		param.set(AppSettings.KEY_PREVIEW_SIZE, mpreviewsize);
		mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_IMAGE_SIZE, AppSettings.VALUE_SIZE_5M, true, false);
		MSize mimagesize = Utils.getResolution((Integer)mConfigMgr.getConfig(ConfigMgr.KEY_CONFIG_IMAGE_SIZE));
		param.set(AppSettings.KEY_IMAGE_SIZE, mimagesize);
		param.set(AppSettings.KEY_SELECT_CAMERA, (Integer)mConfigMgr.getConfig(ConfigMgr.KEY_CONFIG_SWAP_CAMERA));
		if(null!=mSystemReceiverrMgr)
			mSystemReceiverrMgr.getInformation(SystemReceiverMgr.KEY_REG_RECALCULATE_PICTURE_REMAINING);
		mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_SET_SURFACE_SIZE, AppSettings.VALUE_SIZE_720P);
	}

	@Override
	public void onPreviewFrame(Object data) {
		// TODO Auto-generated method stub
//		Log.e("ArcsoftPMK", "onPreviewFrame");
		
		if (!mIsCapturing)
			return;
		
		if (data == null) {
			mIsCapturing = false;
			return;
		}
		
		Message msg = new Message();
		msg.what = PROCESS_PREVIEW;
		msg.obj = data;
		
		mVideoPMKHandler.sendMessage(msg);
		
	}
	
	private boolean mIsProcessPreview = false;
	private void processPreview(byte[] data) {
		mTempResult = null;
		mTempResult = CameraEngine.native_processPreview(data);
		
		if (mTempResult == null)
			return;
		
		mIsProcessPreview = true;
		
		for(int i : mTempResult.frameRes) {
			Log.i("ArcsoftPMK", "result : " + i);
		}

//		Point pt = new Point(result[3], result[4]);
//		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_GUIDE_BOX_MOVING, pt);
		
		if (mTempResult.frameRes[0] != 0) {
			processError(mTempResult.frameRes[0]);
			stopCapture();
			mCamEngine.startPreview();
			return;
		}
		
		if (mTempResult.frameRes[2] != 0/*&& !mIsShowAnimation*/) {
			if (mTempResult.frameRes[2] == AppSettings.VALUE_PMK_DIRECTION_LEFTTORIGHT
				|| mTempResult.frameRes[2] == AppSettings.VALUE_PMK_DIRECTION_RIGHTTOLEFT) {
				if (!mIsShowAnimation) {
					mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_START_CAPTURE, mTempResult.frameRes[2]);
					mIsShowAnimation = true;
				}
			} else {
				processError(MSGTYPE_ERR_TRACE_NO_SUPPORT);
				cancelCapture();
				mCamEngine.startPreview();
				return;
			}
		} 
		
//		mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFY_PMK_CAPTURING, result[1]);
		if (mTempResult.stitchImgRes != null && !mIsUpdateThumbnail
				&& mTempResult.frameRes[1] % 10 == 0) {
			mIsUpdateThumbnail = true;
			Log.i("ArcsoftPMK", "Update Thumbnail ");
			processResData(mTempResult.stitchImgRes);
		} else {
			mIsUpdateThumbnail = false;
		}
		
		if (mTempResult.frameRes[1] >= 100) {
			mVideoPMKHandler.sendEmptyMessageDelayed(PROCESS_FINISH, 500);
		}

	}
	
	private boolean mIsUpdateThumbnail = false;
	private void processResData(final byte[] data) {
		// TODO Auto-generated method stub
		new Thread(){
			public void run() {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (bitmap == null) {
					Log.e("ArcsoftPMK", "Decode Bitmap is " + null);
					return;
				}
				Message msg = new Message();
				msg.what = PROCESS_THUBNAIL;
				msg.obj = bitmap;
				mVideoPMKHandler.sendMessage(msg);
			}
		}.start();
	}

	private void processError(int result) {
		// TODO Auto-generated method stub
		String str = null;
		switch(result) {
		case MSGTYPE_ERR_TRACE_DIRECTION:
			str = "Direction Error";
			break;
		case MSGTYPE_ERR_TRACE_NO_FEATURE:
			str = "TRACE NO FEATURE";
			break;
		case MSGTYPE_ERR_TRACE_INTERFERENCE:
			str = mCameraApp.getString(R.string.sp_pano_too_fast_prompt_NORMAL);
			break;
		case MSGTYPE_ERR_TRACE_NO_SUPPORT:
			str = "Not Support Vertical Direction";
		}
		
		if (str != null) {
			mCameraApp.notifyUI(ArcGlobalDef.CAMAPP_NOTIFT_PMK_PROCESS_ERROR, null);
			Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	private void processResult(boolean needSave) {
		mIsCapturing = false;
		mCamEngine.stopPreview();
		
		Panorama2Info info = new Panorama2Info();
		info.setFlag(PANORAMA2_FLAG_STITCH);
		int res = CameraEngine.native_specProcess(info);
		Log.e("ArcsoftPMK", "native_specProcess res : " + res);
		
		byte[] image = CameraEngine.native_postProcess();
		
		if (needSave)
			saveImage(image);
		else
			saveImage(null);
		mIsProcessPreview = false;
	}
	
	private void saveImage(byte[] data) {
		mAutoCaptureAfterFocus = false;
		mCancleReview = false;
		mCamEngine.setPeviewing(false);
		mUri = null;
		if(false == (Boolean)mConfigMgr.getConfig(ConfigMgr.KEY_CONFIG_MEMORY_CARD_MOUNTED)) {
			if(mHandler != null) {
				mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
			}
			return;
		}

		if(data == null) {
			if(mHandler != null) {
				LogUtils.e(LogUtils.TAG,TAG + "callback data is null");
				mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
			}
			return;
		}
			
//		if(mCameraApp.isPausing()){
//			changCameraState(ArcGlobalDef.ARCCAM_STATE_PREVIEWING);
//			mWaitJpegCallBack = false;
//			setNotBusyDelayed(500);
//			return;
//		}
		if(mSavingWithThread) {
			LogUtils.v(LogUtils.TAG,TAG + "****use thread to save image");
			mFilePath = getNextSavingPath();
			if(mFilePath == null) {
				if(null != mHandler){
					mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
				}
				return;
			}
			synchronized (mFilepathQueue) {
				mFilepathQueue.addAll(mFilepathList);
			}
			synchronized (mDataQueue) {
				mDataQueue.addLast(data);
				mDataQueue.notify();
			}
			if(isReviewOn()) {
				Bitmap bmp = JUtils.decodeBitmapFromArrayInModePMK((byte[])data, ArcGlobalDef.APP_REVIEW_SIZE.width, ArcGlobalDef.APP_REVIEW_SIZE.height);
				if(null == bmp){
					LogUtils.e(LogUtils.TAG,TAG + "decodeBitmapFromArray is null");
					if(null != mHandler){
						mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
					}
					return;
				}
				if(mLastOrientation != 0) {
					bmp = Utils.rotate(bmp, mLastOrientation, true);
				}
				mBitmapList.add(bmp);
			}
			
		} else {
			LogUtils.v(LogUtils.TAG,TAG + "****not use thread to save image");
			mFilePath = saveImageToFile((byte[])data);
			if(mFilePath == null) {
				LogUtils.e(LogUtils.TAG,TAG + "the mFilePath is null");
				if(null != mHandler){
					mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
				}
				return;
			}
			
			if(null != mMediaManager){
				MSize mimagesize = Utils.getResolution((Integer)mConfigMgr.getConfig(ConfigMgr.KEY_CONFIG_IMAGE_SIZE));
				mMediaManager.setImageResolution(mimagesize.width, mimagesize.height);
				mUri = mMediaManager.addMediaFileByFullPath(mFilePath, mLastOrientation, mLastLocation,mConfigMgr.is3D());
			}
			if(mUri == null) {
				LogUtils.e(LogUtils.TAG,TAG + "the mUri is null");
				if(null != mHandler){
					mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
				}
				return;
			}
			
			if(null!=mSystemReceiverrMgr)
				mSystemReceiverrMgr.getInformation(SystemReceiverMgr.KEY_REG_STORAGE_RECEIVER);
			
			if(mHandler != null) {
				mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_OK);
			}
		}

		try {
			if(false == isReviewOn()){
				LogUtils.v(LogUtils.TAG,TAG + "Review Off");
				changCameraState(ArcGlobalDef.ARCCAM_STATE_PREVIEWING);
				mCamEngine.startPreview();
				mConfigMgr.setConfig(ConfigMgr.KEY_CONFIG_FOCUS, AppSettings.VALUE_FOCUS_AUTO);
//				mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_QUICKFUNCTIONVIEW_SHOW, null);
			} else {
				changCameraState(ArcGlobalDef.ARCCAM_STATE_PREVIEWING);
//				mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_FACETRACKINGVIEW_HIDE, null);
//				changCameraState(ArcGlobalDef.ARCCAM_STATE_REVIEWING);
//				LogUtils.v(LogUtils.TAG,TAG + "go to review");
//				if(isCancleReview()) {
//					if(null != mHandler){
//						mHandler.sendEmptyMessage(ArcGlobalDef.CAMAPP_MSG_SAVE_FILE_ERROR);
//					}
//					return;
//				}
//				mCameraApp.onNotify(ArcGlobalDef.CAMAPP_NOTIFY_REVIEW_CAPTURE, mFilePath);
			}
 
		} catch(Exception e) {
			LogUtils.e(LogUtils.TAG,TAG + "error");
		}

		mWaitJpegCallBack = false;
		setNotBusyDelayed(500);
	}
}
