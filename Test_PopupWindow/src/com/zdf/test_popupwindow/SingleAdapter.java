package com.zdf.test_popupwindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SingleAdapter extends BaseAdapter {

	private Context mContext = null;
	private String [] mStrListItem = null;
	
	public SingleAdapter(Context context, String []array){
		mContext = context;
		mStrListItem = array;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mStrListItem == null ? 0 : mStrListItem.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mStrListItem == null ? null : mStrListItem[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.listview_item, null);
			tv = (TextView) convertView.findViewById(R.id.list_item_text);
			convertView.setTag(tv);
		} else {
			tv = (TextView) convertView.getTag();
		}

		if (tv != null) {
			tv.setText(mStrListItem[position]);
		}
		return convertView;
	}

}
