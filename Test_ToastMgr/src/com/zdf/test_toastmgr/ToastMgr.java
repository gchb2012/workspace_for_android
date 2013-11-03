/*----------------------------------------------------------------------------------------------
*
* This file is ArcSoft's property. It contains ArcSoft's trade secret, proprietary and      
* confidential information. 
* 
* The information and code contained in this file is only for authorized ArcSoft employees 
* to design, create, modify, or review.
* 
* DO NOT DISTRIBUTE, DO NOT DUPLICATE OR TRANSMIT IN ANY FORM WITHOUT PROPER AUTHORIZATION.
* 
* If you are not an intended recipient of this file, you must not copy, distribute, modify, 
* or take any action in reliance on it. 
* 
* If you have received this file in error, please immediately notify ArcSoft and 
* permanently delete the original and any copy of any file and any printout thereof.
*
*---------------------------------------------------------------------------------------------*/
package com.zdf.test_toastmgr;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastMgr {
	private static Context mContext = null;
	private static Toast mToast = null;

	public ToastMgr() {
	}

	public static void showToast(Context context, String str, int length) {
		if (null == context)
			return;
		
		if (null == mToast || mContext != context) {
			Log.v("zdf", "111111, context = " + context);
			mContext = context;
			mToast = Toast.makeText(context, str, length);
			mToast.show();
		} else {
			Log.v("zdf", "222222");
			mToast.setText(str);
		}
		
		if (null != mToast) {
			mToast.show();
		}
	}
	
	public static void showToast(Context context, int resId, int length) {
		if (null == context)
			return;
		
		showToast(context, context.getString(resId), length);
	}
	
	public static void cancelToast() {
		if(mToast != null){
			mToast.cancel();
			mToast = null;
		}
	}
	
}
