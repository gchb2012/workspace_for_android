package com.zdf.test_opengl;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zdf.test_opengl.utils.DataManager;

public class MainActivity extends Activity {
	private OpenGLView mOpenGLView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 setContentView(R.layout.main);

		// 去标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DataManager.init(this.getResources());
		
		mOpenGLView = new OpenGLView(this);
//		setContentView(mOpenGLView);
		
		RelativeLayout mLayout = new RelativeLayout(this);
		mLayout.addView(mOpenGLView);
		
		TextView tv = new TextView(this);
        tv.setLayoutParams(new LayoutParams(300, 300));
        tv.setText("test");
        tv.setBackgroundColor(0x2200ff00);
        tv.setGravity(Gravity.CENTER);
        
		mLayout.addView(tv);
		setContentView(mLayout);
	}
}