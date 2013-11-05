package com.zdf.test_popupwindow;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button mBtnShowPupupWindow = null;
	private PopupWindow mPopupWindow = null;
	private PopupView mPopupView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBtnShowPupupWindow = (Button)findViewById(R.id.btn_show_popupwindow);
		mBtnShowPupupWindow.setOnClickListener(mOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_show_popupwindow:
//				View popupView = initPupupView();
//				showPupupWindow(popupView);
				
				initPupupView2();
				showPupupWindow(mPopupView);
				break;

			default:
				break;
			}
		}
	};
	
	private View initPupupView() {
		LayoutInflater mInflater = LayoutInflater.from(this);
		View popupView = mInflater.inflate(R.layout.popupview, null);
		ListView listView = (ListView) popupView.findViewById(R.id.list_view);
		
		String[] showOptions = {"111", "2222", "333"};
		SingleAdapter adapter = new SingleAdapter(this, showOptions);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Log.v("zdf", "listView onItemClick!!!!");
				Toast.makeText(MainActivity.this, "listView onItemClick!!!!", Toast.LENGTH_SHORT).show();
			}
		});
		
		return popupView;
	}
	
	private void initPupupView2() {
		if (null == mPopupView) {
			LayoutInflater mInflater = LayoutInflater.from(this);
			mPopupView = (PopupView)mInflater.inflate(R.layout.popupview2, null);
			mPopupView.init();
		}
	}
	
	private void showPupupWindow(View popupView) {
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			mPopupWindow = new PopupWindow(popupView, 600, 400, true);
		}
		
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				mPopupWindow = null;
			}
		});

		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		View showView = this.getWindow().getDecorView();
		mPopupWindow.showAtLocation(showView, Gravity.CENTER, 0, 25);
	}
}
