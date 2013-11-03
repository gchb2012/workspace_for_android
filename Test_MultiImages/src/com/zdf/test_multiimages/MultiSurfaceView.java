package com.zdf.test_multiimages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MultiSurfaceView extends SurfaceView {
	private SurfaceHolder mHolder;
	private int mMultiBitmapWidth = 0;

	public MultiSurfaceView(Context context) {
		super(context);
		init(context);
	}

	public MultiSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MultiSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		mHolder = getHolder();
	}
	
	public void drawMultiBitmap(Bitmap bitmap) {
		int nBitmapWidth = bitmap.getWidth();
		int nBitmapHeight = bitmap.getHeight();
		int nDrawLeft = mMultiBitmapWidth;
		int nDrawRight = nDrawLeft + nBitmapWidth;
		mMultiBitmapWidth = nDrawRight;
		
		Rect canvasRect = new Rect(nDrawLeft, 0 , nDrawRight, nBitmapHeight);
		Log.v("zdf", "canvasRect = " + canvasRect);
		Canvas canvas = mHolder.lockCanvas(canvasRect);
		Log.v("zdf", "canvas = " + canvas + ", mHolder = " + mHolder);
		if (canvas != null) {
			canvas.drawBitmap(bitmap, nDrawLeft, 0, null);
			mHolder.unlockCanvasAndPost(canvas);
		}
	}
	
}
