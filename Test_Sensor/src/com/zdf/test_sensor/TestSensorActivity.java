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
        
		// ����һ��SensorManager����ȡϵͳ�Ĵ���������
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // ���ٶȸ�Ӧ��
		mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // �ų���Ӧ��
		oSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION); // �����Ӧ��
		pSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY); // �����Ӧ��
		gSensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY); // �����Ӧ��
		
        /**
         * ��õ�һ������ע���¼�
         * ����1 ��SensorEventListener������
         * ����2 ��Sensor һ����������ж��Sensorʵ�֣��˴�����getDefaultSensor��ȡĬ�ϵ�Sensor
         * ����3 ��ģʽ ��ѡ���ݱ仯��ˢ��Ƶ��
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
     * SensorEventListener�ӿڵ�ʵ�֣���Ҫʵ����������
     * ����1 onSensorChanged �����ݱ仯��ʱ�򱻴�������
     * ����2 onAccuracyChanged ��������ݵľ��ȷ����仯��ʱ�򱻵��ã�����ͻȻ�޷��������ʱ
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
                Log.e(TAG, "TYPE_ORIENTATION, ����ǣ�" + x);
                Log.e(TAG, "TYPE_ORIENTATION, �����ǣ�" + y);
                Log.e(TAG, "TYPE_ORIENTATION, ��ת�ǣ�" + z);
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

		// Ҫ����һ�����ݸ�ʽ��ת����ת��Ϊ��
		values[0] = (float) Math.toDegrees(values[0]);
		Log.d(TAG, "values[0]: " + values[0]);
		Log.d(TAG, "values[1]: " + values[1]);
		Log.d(TAG, "values[2]: " + values[2]);
		Log.d(TAG, " ");
		// values[1] = (float) Math.toDegrees(values[1]);
		// values[2] = (float) Math.toDegrees(values[2]);

		if (values[0] >= -5 && values[0] < 5) {
			Log.v(TAG, "����");
		} else if (values[0] >= 5 && values[0] < 85) {
			Log.v(TAG, "����");
		} else if (values[0] >= 85 && values[0] <= 95) {
			Log.v(TAG, "����");
		} else if (values[0] >= 95 && values[0] < 175) {
			Log.v(TAG, "����");
		} else if ((values[0] >= 175 && values[0] <= 180)
				|| (values[0]) >= -180 && values[0] < -175) {
			Log.v(TAG, "����");
		} else if (values[0] >= -175 && values[0] < -95) {
			Log.v(TAG, "����");
		} else if (values[0] >= -95 && values[0] < -85) {
			Log.v(TAG, "����");
		} else if (values[0] >= -85 && values[0] < -5) {
			Log.v(TAG, "����");
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
         * �ܹؼ��Ĳ��֣�ע�⣬˵���ĵ����ᵽ����ʹactivity���ɼ���ʱ�򣬸�Ӧ����Ȼ������Ĺ��������Ե�ʱ����Է��֣�û��������ˢ��Ƶ��
         * Ҳ��ǳ��ߣ�����һ��Ҫ��onPause�����йرմ����������򽲺ķ��û������������ܲ�����
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
