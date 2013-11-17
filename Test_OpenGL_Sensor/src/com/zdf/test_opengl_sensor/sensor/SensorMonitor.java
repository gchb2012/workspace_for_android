package com.zdf.test_opengl_sensor.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorMonitor implements SensorEventListener {
	private Context mContext = null;
	private SensorManager mSensorManager = null;
	private Sensor mAccSensor = null;
	private Sensor mGyroSensor = null;
	private Sensor mMagSensor = null;
	private Sensor mGravSensor = null;
	private Sensor mLacSensor = null;
	private Sensor mRotSensor = null;
	private Sensor mPxySensor = null;
	
	private final float N2S = 1e-9f;
	private final float[] mAngleValues = new float[3];
	private final float[] mRotationMatrix = new float[16];

	public SensorMonitor(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		// 加速度
		mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// 陀螺仪
		mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		// 地磁场
		mMagSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		// 重力加速度
		mGravSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		// 线性加速度
		mLacSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		// 旋转向量
		mRotSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		// 距离
		mPxySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		registerSensor();
	}

	public void release() {
		unregisterSensor();
	}

	private void registerSensor() {
		int rate = SensorManager.SENSOR_DELAY_NORMAL;
		boolean bRet = false;

		// Accelerometer
		if (mSensorManager != null) {
			bRet = mSensorManager.registerListener(this, mAccSensor, rate);
		}
		if (!bRet) {
			Log.e("zdf", "AccSensor register failed!");
		}
		
		// Gyroscope
		if (mGyroSensor != null) {
			bRet = mSensorManager.registerListener(this, mGyroSensor, rate);
		}
		if (!bRet) {
			Log.e("zdf", "GyroSensor register failed!");
		}

		// Magnetometer
		if (mSensorManager != null) {
			bRet = mSensorManager.registerListener(this, mMagSensor, rate);
		}
		if (!bRet) {
			Log.e("zdf", "MagSensor register failed!");
		}

		// Gravity
		if (mGravSensor != null) {
			bRet = mSensorManager.registerListener(this, mGravSensor, rate);
		}
		if (!bRet) {
			Log.e("zdf", "GravSensor register failed!");
		}
		
		// Linear Accelerometer
		if (mLacSensor != null) {
			bRet = mSensorManager.registerListener(this, mLacSensor, rate);
		}
		if (!bRet) {
			Log.e("zdf", "mLacSensor register failed!");
		}

		// Rotation Vector
		if (mRotSensor != null) {
			bRet = mSensorManager.registerListener(this, mRotSensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (!bRet) {
			Log.e("zdf", "RotationVector Sensor register failed!");
		}

		// Proximity
		if (mPxySensor != null) {
			bRet = mSensorManager.registerListener(this, mPxySensor, rate);
		}
		if (!bRet) {
			Log.e("zdf", "Proximity Sensor register failed!");
		}
	}

	private void unregisterSensor() {
		if (null != mSensorManager) {
			mSensorManager.unregisterListener(this);
			mSensorManager = null;
		}
	}

	private long mLastTime = 0;
	@Override
	public void onSensorChanged(SensorEvent event) {
		long timestamp = event.timestamp;
		float[] values = event.values;
		int sensorType = event.sensor.getType();
		
		switch (sensorType) {
		case Sensor.TYPE_ACCELEROMETER:
//			Log.v("zdf", "onSensorChanged(TYPE_ACCELEROMETER), values[0] = " + values[0]);
//			Log.v("zdf", "onSensorChanged(TYPE_ACCELEROMETER), values[1] = " + values[1]);
//			Log.v("zdf", "onSensorChanged(TYPE_ACCELEROMETER), values[2] = " + values[2]);
			break;

		case Sensor.TYPE_GYROSCOPE:
			// 陀螺仪： 三坐标轴上的旋转角度分量（rad/s）
			if (0 == mLastTime) {
				mLastTime = timestamp;
				break;
			}
			long curTime = timestamp;
			float deltatime = (curTime - mLastTime) * N2S;
			mLastTime = curTime;
			
			double axisX = values[0];
			double axisY = values[1];
			double axisZ = values[2];
//			Log.v("zdf", "onSensorChanged(TYPE_GYROSCOPE), axisX = " + axisX);
//			Log.v("zdf", "onSensorChanged(TYPE_GYROSCOPE), axisY = " + axisY);
//			Log.v("zdf", "onSensorChanged(TYPE_GYROSCOPE), axisZ = " + axisZ);
			
			mAngleValues[0] += axisX * deltatime;
			mAngleValues[1] += axisY * deltatime;
			mAngleValues[2] += axisZ * deltatime;
//			Log.v("zdf", "onSensorChanged(TYPE_GYROSCOPE), mAngleValues[0] = " + mAngleValues[0]);
//			Log.v("zdf", "onSensorChanged(TYPE_GYROSCOPE), mAngleValues[1] = " + mAngleValues[1]);
//			Log.v("zdf", "onSensorChanged(TYPE_GYROSCOPE), mAngleValues[2] = " + mAngleValues[2]);
//			Log.v("zdf", "-----------------");
			break;

		case Sensor.TYPE_MAGNETIC_FIELD:
			break;

		case Sensor.TYPE_GRAVITY:
			break;
			
		case Sensor.TYPE_LINEAR_ACCELERATION:
			break;

		case Sensor.TYPE_ROTATION_VECTOR:
			SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values);
			break;

		case Sensor.TYPE_PROXIMITY:
			break;

		default:
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
	public float[] getAngleValues() {
		return mAngleValues;
	}
	
	public float[] getRotationMatrix() {
		return mRotationMatrix;
	}

}
