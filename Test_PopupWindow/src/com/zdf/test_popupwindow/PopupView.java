package com.zdf.test_popupwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PopupView extends RelativeLayout {
	private Context mContext;

	public PopupView(Context context) {
		super(context);
		mContext = context;
	}

	public PopupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public PopupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	public void init() {
		ListView listView = (ListView) findViewById(R.id.list_view);
		
		String[] showOptions = {"111", "2222", "333"};
		SingleAdapter adapter = new SingleAdapter(mContext, showOptions);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Log.v("zdf", "listView onItemClick!!!!");
				Toast.makeText(mContext, "listView onItemClick!!!!", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
