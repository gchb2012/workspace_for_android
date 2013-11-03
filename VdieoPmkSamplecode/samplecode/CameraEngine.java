package com.arcsoft.android.camera.engine;	

public class CameraEngine {
	public void setVideoPMKParametersLock(boolean toggle) {
		mParameters.setAutoExposureLock(toggle);
		mParameters.setAutoWhiteBalanceLock(toggle);
	}
	
	public void uninitVidePMK() {
		native_uninit();
	}
	
	public static native boolean native_init(int previewWidth, int previewHeight, int previewFormat, int captureWidth, int captureHeight);

    public static native void native_uninit();

    public static native SelectFrameResult native_processPreview(byte[] previewData);

    public static native byte[] native_postProcess();

    public static native int native_specProcess(Object obj);

	static {
		System.loadLibrary("videopanorama");
	}
	
}