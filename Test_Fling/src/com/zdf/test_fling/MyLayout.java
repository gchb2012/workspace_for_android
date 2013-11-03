package com.zdf.test_fling;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MyLayout extends RelativeLayout implements OnTouchListener, OnGestureListener{
	GestureDetector mGestureDetector = null;
	private ImageView ivHandle = null;

	public MyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}
	
	public void init() {
		mGestureDetector = new GestureDetector(this);
		
		setOnTouchListener(this);
		
		ivHandle = (ImageView) findViewById(R.id.iv_handle);
		if (null != ivHandle) {
			ivHandle.setOnTouchListener(this);
		}
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		
//		int action = event.getAction();
//		Log.v("zdf", "onInterceptTouchEvent, " + action);
//		
////		return super.onInterceptTouchEvent(event);
//		return true;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		int action = event.getAction();
//		Log.v("zdf", "onTouchEvent, " + action);
//		
////		return super.onTouchEvent(event);
//		return true;
//	}

	float downX = 0;
//	float lastX = 0;
//	long donwEventTime = 0;
//	long lastEventTime = 0;
//	boolean isMoved = false;
	final int FLING_MIN_DISTANCE = 100;
	final int FLING_MIN_VELOCITY = 200;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		int viewId = v.getId();
		float x = event.getX();
		long eventTime = event.getEventTime();
		Log.v("zdf", "onTouch, " + action + ", view: " + viewId);
		
		
//		if (viewId == R.id.iv_handle)
//			return mGestureDetector.onTouchEvent(event);
//		else
//			return true;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downX = x;
//			lastX = 0;
//			donwEventTime = eventTime;
//			lastEventTime = 0;
//			isMoved = false;
			break;
		case MotionEvent.ACTION_MOVE: {
//			if (isMoved) {
//				float distance = x - lastX;
//				int duration = (int) Math.abs(eventTime - lastEventTime);
//				float velocityX = getVelocityX(distance, duration);
//				Log.v("zdf", "-- onTouch, ACTION_MOVE, distance = " + distance
//						+ ", duration = " + duration + ", velocityX = "
//						+ velocityX);
//			}
//			lastX = x;
//			lastEventTime = eventTime;
//			isMoved = true;
		}
			break;
		case MotionEvent.ACTION_UP: {
			float distance = x - downX;
			int duration = (int) Math.abs(event.getEventTime() - event.getDownTime());
			float velocityX = getVelocityX(distance, duration);
			Log.v("zdf", "-- onTouch, ACTION_UP, distance = " + distance
					+ ", duration = " + duration + ", velocityX = "
					+ velocityX);
			if (velocityX > FLING_MIN_VELOCITY) {
				if (distance > FLING_MIN_DISTANCE)
					Log.v("zdf", "----- fling right");
				else if (distance < -FLING_MIN_DISTANCE)
					Log.v("zdf", "----- fling left");
			}
//			isMoved = false;
		}
			break;
		}
		return true;
	}
	
	private float getVelocityX(float distance, int duration) {
		if (0 == duration)
			return 0;
		return Math.abs((distance / duration) * 1000);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.v("zdf", "Gesture: onDown");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.v("zdf", "Gesture: onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.v("zdf", "Gesture: onSingleTapUp");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		Log.v("zdf", "Gesture: onScroll, distanceX = " + distanceX + ", distanceY = " + distanceY);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.v("zdf", "Gesture: onLongPress");
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.v("zdf", "Gesture: onFling, velocityX = " + velocityX + ", velocityY = " + velocityY);
		return false;
	}

}
