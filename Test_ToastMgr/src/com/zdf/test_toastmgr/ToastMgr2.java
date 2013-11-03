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
	 *            ʹ��ʱ��������
	 * 
	 * @param hint
	 *            ����ʾ������Ҫ��ʾ���ı�
	 * 
	 * @return ����һ�������ظ���ʾ��toast
	 * 
	 * */

	public static Toast getToast(Context context, String hint) {
		if (ToastMgr2.context == context) {
			toast.cancel();
			toast.setText(hint);
			Log.v("zdf", "û���´���");
		} else {
			Log.v("zdf", "������һ���µ�toast");

			ToastMgr2.context = context;
			toast = Toast.makeText(context, hint, Toast.LENGTH_LONG);
		}
		return toast;
	}

}