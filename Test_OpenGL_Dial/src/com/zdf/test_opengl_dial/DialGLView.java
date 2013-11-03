package com.zdf.test_opengl_dial;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DialGLView extends GLSurfaceView {
	private DialGLRenderer mRenderer;
	private float mTouchDownX = 0;
	private float mTouchDownY = 0;
	private float mTotalMovedX = 0;
	private float mTotalMovedY = 0;

	public DialGLView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mRenderer = new DialGLRenderer();
		setRenderer(mRenderer);
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			@Override
			public void run() {
				float x = event.getX();
				float y = event.getY();
				int action = event.getAction();
				
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mTouchDownX = x;
					mTouchDownY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					float movedX = x - mTouchDownX;
					float movedY = y - mTouchDownY;
					mRenderer.TranslatePlane((mTotalMovedX + movedX) / 100, (mTotalMovedY + movedY) / 100);
					break;
				case MotionEvent.ACTION_UP:
					mTotalMovedX += x - mTouchDownX;
					mRenderer.TranslatePlane(mTotalMovedX / 100, mTotalMovedY / 100);
					break;
				default:
					break;
				}
			}
		});

//		return super.onTouchEvent(event);
		return true;
	}

}
