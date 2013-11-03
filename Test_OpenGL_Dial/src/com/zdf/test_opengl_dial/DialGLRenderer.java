package com.zdf.test_opengl_dial;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.zdf.test_opengl_dial.model.BufferUtils;
import com.zdf.test_opengl_dial.model.Mesh;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class DialGLRenderer implements Renderer {
	private float[] mQuadsArray = { 
			1f, 1f, 0f, 	// 右上
			-1f, 1f, 0f, 	// 左上
			-1f, -1f, 0f, 	// 左下
			1f, -1f, 0f 	// 右下
	};
	private FloatBuffer mQuadsBuffer = null;
	
	private float[] mPlaneArray = { 
			10f, 10f, 0f,
			-10f, 10f, 0f,
			-10f, -10f, 0f,
			10f, -10f, 0f
	};
	private FloatBuffer mPlaneBuffer = null;
	
	private float mHoriTrans = 0;
	private float mVertTrans = 0;
	
	private float mRotateAngle = 0;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		mQuadsBuffer = BufferUtils.floatToBuffer(mQuadsArray);
		mPlaneBuffer = BufferUtils.floatToBuffer(mPlaneArray);
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
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
//		drawQuads(gl);
//		drawQuads2(gl);
//		drawQuads3(gl);
//		drawQuads4(gl);
//		
//		drawPlane(gl);
//		
//		mRotateAngle += 2.0f;
		
		drawTest(gl);
	}
	
	private void drawTest(GL10 gl) {
		float vertices[] = { 
				-1.0f, 1.0f, 0.0f, // 0, Top Left
				-1.0f, -1.0f, 0.0f, // 1, Bottom Left
				1.0f, -1.0f, 0.0f, // 2, Bottom Right
				1.0f, 1.0f, 0.0f, // 3, Top Right
		};

		short[] indices = { 0, 1, 2, 0, 2, 3 };
		
		Mesh m = new Mesh();
		m.setVertices(vertices);
		m.setIndices(indices);
		m.draw(gl);
	}
	
	private void drawQuads(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.2f);
		gl.glTranslatef(0.0f, -2.0f, -10.0f);
		
		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		gl.glRotatef(mRotateAngle, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(0.0f, 0.0f, 10.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
	}
	
	private void drawQuads2(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.2f);
		gl.glTranslatef(-10.0f, -2.0f, -20.0f);
		gl.glRotatef(-90, 0.0f, 1.0f, 0.0f);
		
		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		gl.glRotatef(mRotateAngle, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(0.0f, 0.0f, 10.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
	}
	
	private void drawQuads3(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.2f);
		gl.glTranslatef(0.0f, -2.0f, -30.0f);
		
		gl.glTranslatef(0.0f, 0.0f, 10.0f);
		gl.glRotatef(mRotateAngle, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
	}
	
	private void drawQuads4(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.2f);
		gl.glTranslatef(10.0f, -2.0f, -20.0f);
		gl.glRotatef(90, 0.0f, 1.0f, 0.0f);
		
		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		gl.glRotatef(mRotateAngle, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(0.0f, 0.0f, 10.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
	}
	
	private void drawPlane(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		gl.glTranslatef(mHoriTrans, -3.0f, -20.0f + mVertTrans);
		gl.glRotatef(-90, 1.0f, 0.0f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mPlaneBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
	}
	
	public void TranslatePlane(float horizontal, float vertical) {
		mHoriTrans += horizontal;
		mVertTrans += vertical;
	}
	
}
