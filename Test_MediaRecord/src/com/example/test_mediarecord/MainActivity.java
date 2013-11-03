package com.example.test_mediarecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements Callback {
	private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null;
	private Camera mCamera = null;
	private MediaRecorder mMediaRecorder = null;
	private Button mRecordBtn = null;
	private boolean mIsRecording = false;
	
	private String mVideoFilePath = null;
	
	private static final String PICMOVIE_RECORD_VIDEO_DIR = "/mnt/sdcard/test_mediarecord/";
	
	private static final int PREVIEW_SIZE_WIDTH = 640;
	private static final int PREVIEW_SIZE_HEIGHT = 480;
	private static final int VIDEO_SIZE_WIDTH = 640;
	private static final int VIDEO_SIZE_HEIGHT = 480;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		createPicMovieDir();
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mRecordBtn = (Button) findViewById(R.id.btn_record);
		mRecordBtn.setOnClickListener(mClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mSurfaceHolder = holder;
		openCamera(0);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		closeCamera();
		stopRecord(); 
		mSurfaceView = null;
		mSurfaceHolder = null;
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_record:
				if (!mIsRecording) {
					startRecord();
				} else {
					stopRecord();
				}
				break;
			default:
				break;
			}
		}
	};
	
	private void createPicMovieDir() {
		new File(PICMOVIE_RECORD_VIDEO_DIR).mkdirs();
	}
	
	private void openCamera(int cameraId) {
		if (null == mCamera) {
			mCamera = Camera.open(cameraId);
		} else {
			try {
				mCamera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Parameters parameters = mCamera.getParameters();
//		parameters.setPreviewFrameRate(5);
//		parameters.setPictureFormat(PixelFormat.JPEG);
//		parameters.set("jpeg-quality", 85);// ’’∆¨÷ ¡ø
		parameters.setPreviewSize(PREVIEW_SIZE_WIDTH, PREVIEW_SIZE_HEIGHT);
		
		if (cameraId == 0) {
			parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
			parameters.setFocusMode("continuous-video");
		}
		
		parameters.setRecordingHint(true);
		if ("true".equals(parameters.get("video-stabilization-supported"))) {
			parameters.set("video-stabilization", "true");
		}
		
		mCamera.setParameters(parameters);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
	}
	
	private void closeCamera() {
		if (null != mCamera) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	private void openRecord() {
		if (mMediaRecorder == null){
			mMediaRecorder = new MediaRecorder();
		} else {
			mMediaRecorder.reset();
		}
		
		try {
			mCamera.unlock();
			mMediaRecorder.setCamera(mCamera);
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setCaptureRate(30);
			mMediaRecorder.setVideoFrameRate(30);
			mMediaRecorder.setVideoEncodingBitRate(6000000);
			mMediaRecorder.setVideoSize(VIDEO_SIZE_WIDTH, VIDEO_SIZE_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startRecord() {
		mIsRecording = true;
		mRecordBtn.setText(R.string.btn_stop_record);
		
		openRecord();
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
		String timeStamp = dateformat.format(System.currentTimeMillis());
		mVideoFilePath = PICMOVIE_RECORD_VIDEO_DIR + "Video_" + timeStamp + ".mp4";
		mMediaRecorder.setOutputFile(mVideoFilePath);
		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecord() {
		mRecordBtn.setText(R.string.btn_start_record);
		
		if (mMediaRecorder != null) {
			if (mIsRecording) {
				try {
					mMediaRecorder.stop();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
			
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
		
		mIsRecording = false;
	}
}
