package com.zdf.test_sensor;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestSensorActivity extends Activity {
	private static final String TAG = "zdf";
	private SensorManager sm;
	private Sensor aSensor;
	private Sensor mSensor;
	private Sensor oSensor;
	private Sensor pSensor;
	private Sensor gSensor;
	private float[] mAccelerometerValues = new float[3];
	private float[] mMagneticFieldValues = new float[3];
	
	private TextView mTextView = null;
	private RotationView mRotationView = null;
	
	private long mLastTime = 0;
	private float mLastVelocity = 0;
	private float mLastY = 0;
	private float mMaxOffset = 0;
	private float mMinOffset = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		// 创建一个SensorManager来获取系统的传感器服务
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 加速度感应器
		mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // 磁场感应器
		oSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION); // 方向感应器
		pSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY); // 距离感应器
		gSensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY); // 距离感应器
		
        /**
         * 最常用的一个方法注册事件
         * 参数1 ：SensorEventListener监听器
         * 参数2 ：Sensor 一个服务可能有多个Sensor实现，此处调用getDefaultSensor获取默认的Sensor
         * 参数3 ：模式 可选数据变化的刷新频率
         */
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, oSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, pSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
        
        mTextView = (TextView)findViewById(R.id.text_view);
        mRotationView = (RotationView)findViewById(R.id.rotation_view);
        
        mLastTime = System.currentTimeMillis();
    }
    
    /**
     * SensorEventListener接口的实现，需要实现两个方法
     * 方法1 onSensorChanged 当数据变化的时候被触发调用
     * 方法2 onAccuracyChanged 当获得数据的精度发生变化的时候被调用，比如突然无法获得数据时
     */
    final SensorEventListener myListener = new SensorEventListener(){
    	@Override
        public void onSensorChanged(SensorEvent sensorEvent){
			switch (sensorEvent.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				float X_lateral = sensorEvent.values[0];
                float Y_longitudinal = sensorEvent.values[1];
                float Z_vertical = sensorEvent.values[2];
                Log.v(TAG, "TYPE_ACCELEROMETER, (" + X_lateral + ", " + Y_longitudinal + ", " + Z_vertical + ")");
                
                long duration = System.currentTimeMillis() - mLastTime;
                float a = Y_longitudinal - SensorManager.STANDARD_GRAVITY;
                Log.v(TAG, "#### a = " + a);
                float distance = mLastVelocity * duration + a * duration * duration / 2;
                Log.v(TAG, "#### distance = " + distance);
                
//                Log.v(TAG, "-------- " + (Y_longitudinal - mLastY));
//                mRotationView.setTranslate((Y_longitudinal - mLastY) * 10);
                mRotationView.setTranslate(distance);
                mRotationView.invalidate();
                mLastY = Y_longitudinal;
                
                if (Y_longitudinal > mMaxOffset) {
                	mMaxOffset = Y_longitudinal;
                } else if (Y_longitudinal < mMinOffset) {
                	mMinOffset = Y_longitudinal;
                }
                mLastTime = System.currentTimeMillis();
                mLastVelocity = mLastVelocity + a * duration;
                Log.v(TAG, "#### mLastVelocity = " + mLastVelocity);
                
				mAccelerometerValues = sensorEvent.values;
				calculateOrientation();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				mMagneticFieldValues = sensorEvent.values;
				break;
			case Sensor.TYPE_ORIENTATION:
				float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                Log.e(TAG, "TYPE_ORIENTATION, 方向角：" + x);
                Log.e(TAG, "TYPE_ORIENTATION, 俯仰角：" + y);
                Log.e(TAG, "TYPE_ORIENTATION, 翻转角：" + z);
                mRotationView.setAngle(z);
                mRotationView.invalidate();
                break;
			case Sensor.TYPE_PROXIMITY:
				Log.v(TAG, "TYPE_PROXIMITY: " + sensorEvent.values[0]);
				break;
			case Sensor.TYPE_GRAVITY:
				float X_g = sensorEvent.values[0];
                float Y_g = sensorEvent.values[1];
                float Z_g = sensorEvent.values[2];
                Log.v(TAG, "TYPE_GRAVITY, (" + X_g + ", " + Y_g + ", " + Z_g + ")");
				break;
			default:
				break;
			}
		}
    	
    	@Override
        public void onAccuracyChanged(Sensor sensor , int accuracy){
            Log.i(TAG, "onAccuracyChanged");
        }
    };
    
	private void calculateOrientation() {
		float[] values = new float[3];
		float[] R = new float[9];
		SensorManager.getRotationMatrix(R, null, mAccelerometerValues,
				mMagneticFieldValues);
		SensorManager.getOrientation(R, values);

		// 要经过一次数据格式的转换，转换为度
		values[0] = (float) Math.toDegrees(values[0]);
		Log.d(TAG, "values[0]: " + values[0]);
		Log.d(TAG, "values[1]: " + values[1]);
		Log.d(TAG, "values[2]: " + values[2]);
		Log.d(TAG, " ");
		// values[1] = (float) Math.toDegrees(values[1]);
		// values[2] = (float) Math.toDegrees(values[2]);

		if (values[0] >= -5 && values[0] < 5) {
			Log.v(TAG, "正北");
		} else if (values[0] >= 5 && values[0] < 85) {
			Log.v(TAG, "东北");
		} else if (values[0] >= 85 && values[0] <= 95) {
			Log.v(TAG, "正东");
		} else if (values[0] >= 95 && values[0] < 175) {
			Log.v(TAG, "东南");
		} else if ((values[0] >= 175 && values[0] <= 180)
				|| (values[0]) >= -180 && values[0] < -175) {
			Log.v(TAG, "正南");
		} else if (values[0] >= -175 && values[0] < -95) {
			Log.v(TAG, "西南");
		} else if (values[0] >= -95 && values[0] < -85) {
			Log.v(TAG, "正西");
		} else if (values[0] >= -85 && values[0] < -5) {
			Log.v(TAG, "西北");
		}
	}
    
	@Override
    public void onResume(){
    	sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, oSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onPause();
    }
    
	@Override
    public void onPause(){
        /*
         * 很关键的部分：注意，说明文档中提到，即使activity不可见的时候，感应器依然会继续的工作，测试的时候可以发现，没有正常的刷新频率
         * 也会非常高，所以一定要在onPause方法中关闭触发器，否则讲耗费用户大量电量，很不负责。
         * */
        sm.unregisterListener(myListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_test_sensor, menu);
        return true;
    }
}
