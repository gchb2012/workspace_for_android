package com.zdf.test_app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	Toast mToast = null;
	Button mBtnShowToast = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("zdf", "onCreate");
        setContentView(R.layout.activity_main);
        registerReceives();
        
//        String strTest = "IMG_201301112222_low.jpg";
//        String strTest2 = null;
//        String[] strFields = strTest.split("_");
//        
//        String strDeviceInfo = getDisplayMetrics(this);
//        Log.e("zdf", strDeviceInfo);
//         
//        Log.v("zdf", "111111111");
//        new Thread(){
//			public void run(){
//				Log.v("zdf", "22222222");
//			}
//        }.start();
//        
//        Handler handler = new Handler();
//        handler.post(new Runnable() {
//			@Override
//			public void run() {
//				Log.v("zdf", "333333333");
//			}
//        });
//        Log.v("zdf", "44444444");
        
        mBtnShowToast = (Button) findViewById(R.id.btn_show_toast);
        mBtnShowToast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(MainActivity.this, "test", Toast.LENGTH_LONG).show();
				
				if (mToast == null) {
					mToast = Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT);
		        } else {
//		        	mToast.cancel();
		        	mToast.setText("test");
		        }
				mToast.show();
				
				mBtnShowToast.setPivotX(30);
				mBtnShowToast.setRotationY(70);
			}
		});
        
//        String filePath = "xxxx/xxxx/我x x.jpg";
//        String ext = MimeTypeMap.getFileExtensionFromUrl(encodeGB(filePath));
//        Log.v("zdf", "ext: " + ext);
        
//        int i = 0;
//        for (int i = 0; i < 3; i++)
//        	switch (i) {
//        	case 0:
//        		Log.v("zdf", "i = " + i + ", 1111111");
//        		Log.v("zdf", "i = " + i + ", 2222222");
//        		break;
//        	case 1:
//        		Log.v("zdf", "i = " + i + ", 3333333");
//        		Log.v("zdf", "i = " + i + ", 4444444");
//        		break;
//        	case 2:
//        		Log.v("zdf", "i = " + i + ", 5555555");
//        		Log.v("zdf", "i = " + i + ", 6666666");
//        		break;
//        	default:
//        		break;
//        	}
//        }
        
        String strResponse = "{ \"rval\": 0, \"msg_id\": 3, \"param\": [ { \"stream_type\": \"rtsp\" }, { \"std_def_video\": \"on\" }, { \"stream_while_record\": \"on\" }, { \"video_resolution\": \"1920x1080 30P 16:9\" }, { \"video_quality\": \"S.Fine\" }, { \"photo_size\": \"2.8M (1920x1440 4:3)\" }, { \"photo_quality\": \"S.Fine\" }, { \"app_status\": \"record\" } ] }";
        Log.v("zdf", "strResponse = " + strResponse);
        try {
			JSONObject jsonObj = new JSONObject(strResponse);
			JSONArray jsonArray = jsonObj.getJSONArray("param");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObjParam = (JSONObject) jsonArray.get(i);
				Iterator it = jsonObjParam.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					String param = jsonObjParam.getString(key);
					Log.v("zdf", "key = " + key + ", param = " + param);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        	
        	
        	
    	ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        Log.v("zdf", "cn = " + cn);
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	Log.v("zdf", "onResume");
//    	registerReceives();
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	Log.v("zdf", "onPause");
//    	unRegisterReceives();
    	super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	Log.v("zdf", "onDestroy");
    	unRegisterReceives();
    	super.onDestroy();
    }
    
    // 转换中文编码
	public String encodeGB(String string) {
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0] + "/" + split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
		return split[0];
	}
    
    public static String getDisplayMetrics(Context cx) {
        String str = "";
        DisplayMetrics metrics = new DisplayMetrics();
		metrics = cx.getResources().getDisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int densityDpi = metrics.densityDpi;
        float density = metrics.density;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        str += "The absolute width: " + String.valueOf(screenWidth) + " pixels\n";
        str += "The absolute heightin: " + String.valueOf(screenHeight) + " pixels\n";
        str += "The logical density of the densityDpi: " + String.valueOf(densityDpi) + "\n";
        str += "The logical density of the display: " + String.valueOf(density) + "\n";
        str += "X dimension: " + String.valueOf(xdpi) + " pixels per inch\n";
        str += "Y dimension: " + String.valueOf(ydpi) + " pixels per inch\n";
        return str;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	Log.v("zdf", "onKeyDown, keyCode = " + keyCode);
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.v("zdf", "onKeyUp, keyCode = " + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			Log.v("zdf", "****** KEYCODE_HOME ******");
			break;
		}
		
		return super.onKeyUp(keyCode, event);
	}
    
    private BroadcastReceiver mHomeKeyReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra("reason");
				Set<String> s = intent.getCategories();
				Log.v("zdf", "mHomeKeyReceiver, reason = " + reason);
				if (reason != null) {
					if (reason.equals("homekey")) {
						
					} else if (reason.equals("recentapps")) {
						
					} else if (reason.equals("lock")) {
						
					}
				}
			}
		}
	};
	
	private void registerReceives() {
		Log.v("zdf", "registerReceives");
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		registerReceiver(mHomeKeyReceiver, intentFilter);
	}
	

	private void unRegisterReceives() {
		Log.v("zdf", "unRegisterReceives");
		if (null != mHomeKeyReceiver)
			unregisterReceiver(mHomeKeyReceiver);
	}

}
