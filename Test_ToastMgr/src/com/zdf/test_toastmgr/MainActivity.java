package com.zdf.test_toastmgr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Button mBtnShowToast = null;
	private Button mBtnCancelToast = null;
	private Toast mToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBtnShowToast = (Button) findViewById(R.id.btn_show_toast);
		mBtnCancelToast = (Button) findViewById(R.id.btn_cancel_toast);
		mBtnShowToast.setOnClickListener(mOnClickLinstener);
		mBtnCancelToast.setOnClickListener(mOnClickLinstener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private OnClickListener mOnClickLinstener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_show_toast:
				ToastMgr.showToast(MainActivity.this, "111111", Toast.LENGTH_SHORT);
//				mToast.makeText(MainActivity.this, "111111", Toast.LENGTH_LONG).show();
//				ToastMgr2.getToast(MainActivity.this, "11111111").show();
				break;
			case R.id.btn_cancel_toast:
				ToastMgr.showToast(MainActivity.this, "222222", Toast.LENGTH_SHORT);
//				if (mToast != null)
//				mToast.cancel();
//				mToast.makeText(MainActivity.this, "222222", Toast.LENGTH_LONG).show();
//				if (mToast != null)
//					mToast.cancel();
//				ToastMgr2.getToast(MainActivity.this, "222222222").show();
				break;
			default:
				break;
			}
		}
		
	};

}
