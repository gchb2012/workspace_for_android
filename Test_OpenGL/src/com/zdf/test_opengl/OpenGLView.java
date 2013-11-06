package com.zdf.test_opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class OpenGLView extends GLSurfaceView {
	private OpenGLRenderer mRenderer;

	public OpenGLView(Context context) {
		super(context);

		mRenderer = new OpenGLRenderer();
		setRenderer(mRenderer);
		
		// 设置render刷新方式，默认为RENDERMODE_CONTINUOUSLY，自动刷。
//		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		mRenderer.setColor((float)0.5, 0, 0);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.setColor(event.getX() / getWidth(), event.getY()
						/ getHeight(), 1.0f);
			}
		});

//		return super.onTouchEvent(event);
		return true;
	}
}
