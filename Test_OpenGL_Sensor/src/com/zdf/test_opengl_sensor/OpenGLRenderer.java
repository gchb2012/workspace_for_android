package com.zdf.test_opengl_sensor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.zdf.test_opengl_sensor.sensor.SensorMonitor;
import com.zdf.test_opengl_sensor.shape.Cube;
import com.zdf.test_opengl_sensor.shape.Group;
import com.zdf.test_opengl_sensor.shape.Mesh;



public class OpenGLRenderer implements Renderer {
	SensorMonitor mSensorMonitor = null;
	private Mesh mRoot;
	private float[] mAngles = new float[3];
	
	public OpenGLRenderer() {
		Group group = new Group();
		Cube cube = new Cube(1, 1, 1);
//		cube.mRx = 45;
//		cube.mRy = 45;
		group.add(cube);
		
//		Plane plane = new Plane(2, 2, 1, 1);
//		group.add(plane);
		
//		Icosahedron obj = new Icosahedron();
//		obj.mRx = 45;
//		obj.mRy = 45;
//		group.add(obj);
		
		mRoot = group;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, ratio, 0.1f, 100.0f);
		
//		GLU.gluLookAt(gl, 0, 10, 0, 0, 0, -10, 0, 1, 0);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		mAngles = getRotateAngles();
		if (null == mAngles)
			return;
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(0, 0, -4);
		gl.glRotatef(-mAngles[1], 1, 0, 0);
		gl.glRotatef(mAngles[0], 0, 1, 0);
		gl.glRotatef(-mAngles[2], 0, 0, 1);
		mRoot.draw(gl);
	}
	
	public void setSensorMonitor(SensorMonitor sensorMonitor) {
		mSensorMonitor = sensorMonitor;
	}
	
	public float[] getRotateAngles() {
		if (null == mSensorMonitor)
			return null;
		
		float[] angleValues = mSensorMonitor.getAngleValues();
		if (null == angleValues)
			return null;
		
		float[] rAngles = new float[3];
		rAngles[0] = (float) (180 * angleValues[0] / Math.PI);
		rAngles[1] = (float) (180 * angleValues[1] / Math.PI);
		rAngles[2] = (float) (180 * angleValues[2] / Math.PI);
		
		return rAngles;
	}
}
