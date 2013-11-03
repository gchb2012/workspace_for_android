package com.zdf.test_toastmgr;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastMgr2 {

	private static Context context = null;
	private static Toast toast = null;

	/**
	 * 
	 * @param context
	 *            使用时的上下文
	 * 
	 * @param hint
	 *            在提示框中需要显示的文本
	 * 
	 * @return 返回一个不会重复显示的toast
	 * 
	 * */

	public static Toast getToast(Context context, String hint) {
		if (ToastMgr2.context == context) {
			toast.cancel();
			toast.setText(hint);
			Log.v("zdf", "没有新创建");
		} else {
			Log.v("zdf", "创建了一个新的toast");

			ToastMgr2.context = context;
			toast = Toast.makeText(context, hint, Toast.LENGTH_LONG);
		}
		return toast;
	}

}