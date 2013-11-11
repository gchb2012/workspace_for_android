package com.zdf.test_opengl_sensor;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.zdf.test_opengl_sensor.sensor.SensorMonitor;

public class OpenGLView extends GLSurfaceView {
	private OpenGLRenderer mRenderer;

	public OpenGLView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mRenderer = new OpenGLRenderer();
		setRenderer(mRenderer);
	}
	
	public void setSensorMonitor(SensorMonitor sensorMonitor) {
		if(null != mRenderer) {
			mRenderer.setSensorMonitor(sensorMonitor);
		}
	}
}
