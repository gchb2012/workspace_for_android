package com.zdf.test_multiimages;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class MultiImageView extends ImageView {
	private Context mContext = null;
	Bitmap mBitmap = null;
	int mMultiBitmapWidth = 0;
	private ArrayList<Bitmap> mBitmapList = null;
	
	public MultiImageView(Context context) {
		super(context);
		init(context);
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MultiImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		mContext = context;
		mBitmapList = new ArrayList<Bitmap>();
	}
	
	public void drawMultiBitmap(Bitmap bitmap) {
		mBitmapList.add(bitmap);
		mBitmap = bitmap;
		mMultiBitmapWidth += bitmap.getWidth();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		int x = 0;
//		for (int i = 0; i < mBitmapList.size(); i++) {
//			if (i > 1) {
//				x += mBitmapList.get(i - 1).getWidth() - 20;
//				Log.v("zdf", "x = " + x);
//			}
//			canvas.drawBitmap(mBitmapList.get(i), x, 0, null);
//		}
		Log.v("zdf", "mMultiBitmapWidth = " + mMultiBitmapWidth);
		canvas.save();
		canvas.translate(mMultiBitmapWidth, 0);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		canvas.restore();
		super.onDraw(canvas);
	}
	
}
