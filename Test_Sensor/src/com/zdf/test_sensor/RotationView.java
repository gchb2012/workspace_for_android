package com.zdf.test_sensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class RotationView extends View implements Runnable {
	private Context mContext = null;
	private Bitmap mBitmap = null;
	private Matrix mMatrix = new Matrix();
	private float mAngle = 0;
	private float mTranslate = 300;
	private float mCenterX = 0;
	private float mCenterY = 0;
	
	public RotationView(Context context) {
		super(context);
		init(context);
	}
	
	public RotationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RotationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		mContext = context;
		mBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.horizontal_solid_line)).getBitmap();
//		new Thread(this).start();
		
		DisplayMetrics metrics = new DisplayMetrics();
		metrics = context.getResources().getDisplayMetrics();
		mCenterX = metrics.widthPixels / 2;
		mCenterY = metrics.heightPixels / 2;
		
		mTranslate = mCenterY;
	}

	@Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);

        mMatrix.reset();  
        mMatrix.setRotate(mAngle, mCenterX, 0); 
//        mAngle = (mAngle + 20) % 360;
        
        Log.v("zdf", "onDraw, mAngle = " + mAngle);
        canvas.translate(0, mTranslate);
        canvas.drawBitmap(mBitmap, mMatrix, null);
	}
	
	public void setAngle(float angle) {
		mAngle = angle;
	}
	
	public void setTranslate(float translate) {
		mTranslate -= translate;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.currentThread().isInterrupted()) {
            try {  
                Thread.sleep(10);  
            } catch (Exception e) {  
                Thread.currentThread().interrupt();  
            }  
            postInvalidate();  
        }
	}

}
