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
package com.example.test_remotecontroller.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastMgr {
	private static Toast mToast = null;

	public ToastMgr() {
		
	}

	public static void showToast(Context context, String str, int length) {
		if (null == context)
			return;
		
		if (null == mToast) {
			mToast = Toast.makeText(context, str, length);
		} else {
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

