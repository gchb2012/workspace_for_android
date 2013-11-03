package com.zdf.OpenGLView.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.zdf.TestOpenGL.R;

public class DataManager {
	private static Bitmap mBitmap;

	public static void init(Resources res) {
		mBitmap = BitmapFactory.decodeResource(res, R.drawable.icon);
	}

	public static Bitmap getBitmap() {
//		updateBitmap(mBitmap);
		return mBitmap;
	}

	public static void updateBitmap(Bitmap src) {
		if (src == null)
			return;

		int w = pow2(src.getWidth());
		int h = pow2(src.getHeight());
		Bitmap b = Bitmap.createBitmap(w, h,
				src.hasAlpha() ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		c.drawBitmap(src, 0, 0, null);
		src = b;
	}

	public static int pow2(float val) {
		int x = (int) (Math.log(val) / Math.log(2));

		if ((1 << x) >= val)
			return 1 << x;
		else
			return 1 << (1 + x);
	}
}
