package com.zdf.test_opengl_model;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class OpenGLView extends GLSurfaceView {
	private OpenGLRenderer mRenderer;

	public OpenGLView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mRenderer = new OpenGLRenderer();
		setRenderer(mRenderer);
	}
}
