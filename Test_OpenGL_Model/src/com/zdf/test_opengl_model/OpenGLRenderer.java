package com.zdf.test_opengl_model;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.zdf.test_opengl_model.shape.Cube;
import com.zdf.test_opengl_model.shape.Group;
import com.zdf.test_opengl_model.shape.Mesh;
import com.zdf.test_opengl_model.shape.Plane;
import com.zdf.test_opengl_model.shape.Icosahedron;

public class OpenGLRenderer implements Renderer {
	private Mesh root;
	
	public OpenGLRenderer() {
		Group group = new Group();
//		Cube cube = new Cube(1, 1, 1);
//		cube.mRx = 45;
//		cube.mRy = 45;
//		group.add(cube);
		
//		Plane plane = new Plane(2, 2, 1, 1);
//		group.add(plane);
		
		Icosahedron obj = new Icosahedron();
//		obj.mRx = 45;
//		obj.mRy = 45;
		group.add(obj);
		
		root = group;
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
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(0, 0, -4);
		root.draw(gl);
	}
}
