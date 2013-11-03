package com.arcsoft.android.camera.utils;

public class Panorama2Info {
	
	public int	mFlag;
	public int	mAngle;
	public int	mPreviewW;
	public int	mPreviewH;
	
	public void setFlag(int flag) {
		this.mFlag = flag;
	}
	public int getFlag() {
		return mFlag;
	}
	
	public void setAngle(int angle) {
		this.mAngle = angle;
	}
	public int getAngle() {
		return mAngle;
	}
	
	public void setPreviewSize(int w, int h){
		mPreviewW = w;
		mPreviewH = h;
	}
	
	public static String getPackageName(){
		return Panorama2Info.class.getName().replace('.', '/');
	}

}
