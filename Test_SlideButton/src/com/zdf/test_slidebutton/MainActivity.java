package com.zdf.test_slidebutton;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zdf.test_slidebutton.SlideButton.OnChangedListener;

public class MainActivity extends Activity {
	private final static int SWITCH_SEEKBAR_MAX = 1;
	SeekBar mSlideBtn1 = null;
	private BluetoothAdapter adapter;
	ImageView mSlideView = null;
	private boolean mIsSwitchFromUser = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSlideBtn1 = (SeekBar) findViewById(R.id.switch_captrue_change_btn);
		mSlideBtn1.setMax(SWITCH_SEEKBAR_MAX);
		mSlideBtn1.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		
		mSlideView = (ImageView) findViewById(R.id.slide_view);
//		mSlideView.setOnClickListener(mOnClickListener);
		mSlideView.setOnTouchListener(mOnTouchListener);
		
		
		
		SlideButton myBtn = (SlideButton)findViewById(R.id.slide_btn);
        myBtn.SetOnChangedListener(mOnChangedListener);
        adapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	float touchDownX = 0;
	float touchUpX = 0;
	boolean bMoved = false;
	private final OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			Log.d("zdf", "onTouch, action = " + action);
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				touchDownX = event.getX();
				mSlideView.setImageResource(bMoved? R.drawable.generic_switch1 : R.drawable.generic_switch2);
				bMoved = !bMoved;
//				break;
				return true;
			case MotionEvent.ACTION_MOVE:
//				break;
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
//				float d = event.getX() - touchDownX;
//				if (Math.abs(d) > mSlideView.getWidth() / 2) {
//					mSlideView.setImageResource(d < 0 ? R.drawable.generic_switch1 : R.drawable.generic_switch2);
//				}
				break;
			default:
				break;
			}
			return false;
		}
		
	};
	
	boolean bClicked = false;
	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d("zdf", "onClick");
			mSlideView.setImageResource(bClicked ? R.drawable.generic_switch1 : R.drawable.generic_switch2);
			bClicked = !bClicked;
		}
	};
	
//	boolean bMoved = false;
	private final OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			Log.d("zdf", "onStopTrackingTouch, seekBar.getProgress() = " + seekBar.getProgress() + ", seekBar.getMax() = " + seekBar.getMax());
			if (!mIsSwitchFromUser) {
				seekBar.setProgress(seekBar.getMax() - seekBar.getProgress());
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			Log.d("zdf", "onStartTrackingTouch");
			mIsSwitchFromUser = false;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Log.d("zdf", "onProgressChanged, progress = " + progress + ", fromUser = " + fromUser);
			mIsSwitchFromUser = fromUser;
		}
	};
	
	private final OnChangedListener mOnChangedListener = new OnChangedListener() {
		@Override
		public void OnChanged(boolean checkState) {
			// TODO Auto-generated method stub
//			if (checkState){
//				//打开蓝牙
//				adapter.enable();
//				Toast.makeText(MainActivity.this, "蓝牙打开了。。。", Toast.LENGTH_SHORT).show();
//			}else{
//				//关闭蓝牙
//				adapter.disable();
//				Toast.makeText(MainActivity.this, "蓝牙关闭了。。。", Toast.LENGTH_SHORT).show();
//			}
		}
	};
}
