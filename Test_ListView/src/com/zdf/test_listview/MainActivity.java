package com.zdf.test_listview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        RelativeLayout listLayout = (RelativeLayout) View.inflate(this, R.layout.listview, null);
        RelativeLayout gridLayout = (RelativeLayout) View.inflate(this, R.layout.gridview, null);
        ListView listview = (ListView)listLayout.findViewById(R.id.list_view);
		GridView gridview = (GridView)gridLayout.findViewById(R.id.grid_view);
		ListAdapter listAdapter = new ListAdapter(this);
		listview.setAdapter(listAdapter);
		gridview.setAdapter(listAdapter);
		
		addContentView(listLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//		addContentView(gridLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private class ListAdapter extends BaseAdapter {
		Context mContext = null;
        public ListAdapter(Context c) {
            this.mContext = c;
        }

        public int getCount() {
        	return 10;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	Holder holder = null;
        	if (convertView == null) {
        		convertView = View.inflate(mContext, R.layout.listview_item, null);
//				convertView = View.inflate(mContext, R.layout.gridview_item, null);
				holder = new Holder();
//				holder.imageView = (ImageView) convertView.findViewById(R.id.facelist_image);
//				holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				holder.textView = (TextView) convertView.findViewById(R.id.facelist_name);
//				holder.textView.setText(mFaceInfoList.get(position).strName);
//				holder.textView.setText(mFaceInfoMap.get(mFaceFlagList.get(position)));
				convertView.setTag(holder);
			} else {
				holder = (Holder)convertView.getTag();
			}
     
			return convertView;
		}
        
        private class Holder {
			ImageView imageView;
			TextView  textView;
		}
	}
}
