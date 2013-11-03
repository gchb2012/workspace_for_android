package com.zdf.OpenGLView;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import com.zdf.OpenGLView.Utils.BufferUtil;
import com.zdf.OpenGLView.Utils.DataManager;

public class OpenGLRenderer implements Renderer {
	private float mRed, mGreen, mBlue;
	
	// ������
	// ����ʵ�����Ƕ����������ε��������㣬�������ֱ���x,y,z�����꣬����ѧ��ֱ������ϵ��ͬ
	private float[] mTriangleArray = {
			0f, 1f, 0f, 	// �϶���
			-1f, -1f, 0f, 	// ���¶���
			1f, -1f, 0f 	// ���¶���
	};
	// �����FloatBuffer������android�������α���Ľṹ
	private FloatBuffer mTriangleBuffer = null;
	// ��������ɫ����
	private float[] mTriangleColorArray = { 
			1f, 0f, 0f, 1f, // ��
			0f, 1f, 0f, 1f, // ��
			0f, 0f, 1f, 1f 	// ��
	};
	private FloatBuffer mTriangleColorBuffer = null;
	// ��ת������
	private float rotateTriangle = 0.0f;

	// �ı���
	// ��������Կ��������ǰ�����ʱ��ķ���ͼ
	private float[] mQuadsArray = { 
			1f, 1f, 0f, 	// ����
			-1f, 1f, 0f, 	// ����
			-1f, -1f, 0f, 	// ����
			1f, -1f, 0f 	// ����
	};
	private FloatBuffer mQuadsBuffer = null;
	// �ı�����ɫ����
	private float[] mQuadsColorArray = { 
			1f, 0f, 0f, 1f, // ��
			0f, 1f, 0f, 1f, // ��
			0f, 0f, 1f, 1f, // ��
			1f, 0f, 1f, 1f	// ��
	};
	private FloatBuffer mQuadsColorBuffer = null;
	// ��ת�ı���
	private float rotateQuads = 0.0f;

	// ����׶
	private float[] mPyramidArray = { 
			0.0f, 1.0f, 0.0f, //
			-1.0f, -1.0f, 1.0f, //
			1.0f, -1.0f, 1.0f, //

			0.0f, 1.0f, 0.0f, //
			1.0f, -1.0f, 1.0f, //
			1.0f, -1.0f, -1.0f, //

			0.0f, 1.0f, 0.0f, //
			1.0f, -1.0f, -1.0f, //
			-1.0f, -1.0f, -1.0f, //

			0.0f, 1.0f, 0.0f, //
			-1.0f, -1.0f, -1.0f, //
			-1.0f, -1.0f, 1.0f //
	};
	private FloatBuffer mPyramidBuffer;
	private float[] mPyramidColorArray = { 
			1.0f, 0.0f, 0.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //

			1.0f, 0.0f, 0.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f, //

			1.0f, 0.0f, 0.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //

			1.0f, 0.0f, 0.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f //
	};
	private FloatBuffer mPyramidColorBuffer;
	private float rotatePyramid = 0.0f;

	// ������
	private float[] mCubeArray = {
			// ǰ
			1.0f, 1.0f, 1.0f, //
			-1.0f, 1.0f, 1.0f, //
			-1.0f, -1.0f, 1.0f, //
			1.0f, -1.0f, 1.0f, //

			// ��
			-1.0f, 1.0f, -1.0f, //
			1.0f, 1.0f, -1.0f, //
			1.0f, -1.0f, -1.0f, //
			-1.0f, -1.0f, -1.0f, //

			// ��
			-1.0f, 1.0f, 1.0f, //
			-1.0f, 1.0f, -1.0f, //
			-1.0f, -1.0f, -1.0f, //
			-1.0f, -1.0f, 1.0f, //

			// ��
			1.0f, 1.0f, -1.0f, //
			1.0f, 1.0f, 1.0f, //
			1.0f, -1.0f, 1.0f, //
			1.0f, -1.0f, -1.0f, //

			// ��
			1.0f, 1.0f, -1.0f, //
			-1.0f, 1.0f, -1.0f, //
			-1.0f, 1.0f, 1.0f, //
			1.0f, 1.0f, 1.0f, //

			// ��
			1.0f, -1.0f, 1.0f, //
			-1.0f, -1.0f, 1.0f, //
			-1.0f, -1.0f, -1.0f, //
			1.0f, -1.0f, -1.0f, //
	};
	private FloatBuffer mCubeBuffer;
	private float[] mCubeColorArray = { 
			0.0f, 1.0f, 0.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f, //
			0.0f, 1.0f, 0.0f, 1.0f, //

			1.0f, 0.5f, 0.0f, 1.0f, //
			1.0f, 0.5f, 0.0f, 1.0f, //
			1.0f, 0.5f, 0.0f, 1.0f, //
			1.0f, 0.5f, 0.0f, 1.0f, //

			1.0f, 0.0f, 0.0f, 1.0f, //
			1.0f, 0.0f, 0.0f, 1.0f, //
			1.0f, 0.0f, 0.0f, 1.0f, //
			1.0f, 0.0f, 0.0f, 1.0f, //

			1.0f, 1.0f, 0.0f, 1.0f, //
			1.0f, 1.0f, 0.0f, 1.0f, //
			1.0f, 1.0f, 0.0f, 1.0f, //
			1.0f, 1.0f, 0.0f, 1.0f, //

			0.0f, 0.0f, 1.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //
			0.0f, 0.0f, 1.0f, 1.0f, //

			1.0f, 0.0f, 1.0f, 1.0f, //
			1.0f, 0.0f, 1.0f, 1.0f, //
			1.0f, 0.0f, 1.0f, 1.0f, //
			1.0f, 0.0f, 1.0f, 1.0f //
	};
	private FloatBuffer mCubeColorBuffer;
	private float rotateCube = 0.0f;

	// ��������������
	private float[] mTextureCoordQuadsArray = { 
//			1.0f, 1.0f, // ͼƬ�����Ͻ�
//			0.0f, 1.0f, // ͼƬ�����Ͻ�
//			0.0f, 0.0f, // ͼƬ�����½�
//			1.0f, 0.0f // ͼƬ�����½�
			
			/**
			 * ����Ӧ����ͼƬ������������ζ�������λ�ö�Ӧ�ģ�
			 * ����androidͼ������ϵͳ��Opengl es����ϵͳ��һ��
			 * (y�ᷴ��Opengl����Ϊ+y��Android����Ϊ+y)��
			 * ��������y������Ҫ������д��
			 */
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f //
	};
	private FloatBuffer mTextureCoordQuadsBuffer;
	private float rotateTextureQuads = 0.0f;

	// ��������������
	private float[] mTextureCoordCubeArray = {
			// ǰ
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// ��
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// ��
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// ��
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// ��
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// ��
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //
	};
	private FloatBuffer mTextureCoordCubeBuffer;
	private float rotateTextureCube = 0.0f;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("zdf", "OpenGL: onSurfaceCreated");
		/**
		 * ����smooth shading����Ӱƽ������
		 * ��Ӱƽ��ͨ������ξ�ϸ�Ļ��ɫ�ʣ������ⲿ�����ƽ����
		 */
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		/**
		 * ���������Ļʱ���õ���ɫ��
		 */
		// Set the background color to black ( rgba ).
		gl.glClearColor(0f, 0f, 0f, 0.5f);
		
		/**
		 * �������ǹ���depth buffer(��Ȼ���)�ġ�
		 * ����Ȼ�������Ϊ��Ļ����Ĳ㡣��Ȼ��治�ϵĶ����������Ļ�ڲ��ж�����и��١�
		 */
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
//		/**
//		 * �����޳�Ч�� for test
//		 */
//		// ����openggl���޳�Ч�������ǿ���������Ͳ�������Ȼ��������Ч��
//		gl.glEnable(GL10.GL_CULL_FACE);
//		// ������ʱ�뷽��Ϊ����
//		gl.glFrontFace(GL10.GL_CCW);
//		// ���ñ��汻�޳�������
//		gl.glCullFace(GL10.GL_BACK);
		
		/**
		 * �������OpenGL����ϣ��������õ�͸��������
		 * ���ʮ����΢��Ӱ�����ܡ���ʹ��͸��ͼ��������һ�㡣
		 */
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// ��SurfaceCreate���湹�����Buffer
		mTriangleBuffer = BufferUtil.floatToBuffer(mTriangleArray);
		mTriangleColorBuffer = BufferUtil.floatToBuffer(mTriangleColorArray);
		
		mQuadsBuffer = BufferUtil.floatToBuffer(mQuadsArray);
		mQuadsColorBuffer = BufferUtil.floatToBuffer(mQuadsColorArray);

		mPyramidBuffer = BufferUtil.floatToBuffer(mPyramidArray);
		mPyramidColorBuffer = BufferUtil.floatToBuffer(mPyramidColorArray);

		mCubeBuffer = BufferUtil.floatToBuffer(mCubeArray);
		mCubeColorBuffer = BufferUtil.floatToBuffer(mCubeColorArray);

		mTextureCoordQuadsBuffer = BufferUtil
				.floatToBuffer(mTextureCoordQuadsArray);
		mTextureCoordCubeBuffer = BufferUtil
				.floatToBuffer(mTextureCoordCubeArray);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.v("zdf", "OpenGL: onSurfaceChanged");
		/**
		 * ���������Ļ��С
		 */
		gl.glViewport(0, 0, width, height);
		// gl.glViewport(width / 4, height / 4, width * 3 / 4, height * 3 / 4);

		/**
		 * ����͸��ͼ
		 * ��ζ��ԽԶ�Ķ���������ԽС����ô��������һ����ʵ��۵ĳ�����
		 * ָ���κ��µı任��Ӱ��projection matrix(ͶӰ����)��ͶӰ������Ϊ���ǵĳ�������͸�ӡ�
		 * �˴�͸�Ӱ��ջ��ڴ��ڿ�Ⱥ͸߶ȵ�45���ӽ������㡣
		 * 0.1f��100.0f�������ڳ��������ܻ�����ȵ������յ㡣
		 */
		float ratio = (float) width / height;
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, ratio, 0.1f, 100.0f);
		
		/**
		 * ����ģ�͹۲����
		 * ָ���κ��µı任����Ӱ�� modelview matrix(ģ�͹۲����)��
		 * ģ�͹۲�����д�������ǵ�����ѶϢ��
		 */
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		Log.v("zdf", "OpenGL: onDrawFrame");
		// �����Ļ����Ȼ���
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		/**
		 * ��������glLoadIdentity()֮����ʵ���Ͻ���ǰ���Ƶ�����Ļ���ģ�
		 * X������������ң�Y������������ϣ�Z������������⡣
		 * OpenGL��Ļ���ĵ�����ֵ��X��Y���ϵ�0.0f�㡣
		 * �������������ֵ�Ǹ�ֵ����������ֵ��
		 * ������Ļ��������ֵ��������Ļ�׶��Ǹ�ֵ��
		 * ������Ļ��Ǹ�ֵ���Ƴ���Ļ������ֵ��
		 */
		// ���õ�ǰ��ģ�͹۲����
		gl.glLoadIdentity();

		// ���ú�ɫ����
		// gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		// �����Ļ���ı䱳������ɫ
		gl.glClearColor(mRed, mGreen, mBlue, 0.0f);

		// ��������
		drawTriangle(gl);

		// ���ı���
//		drawQuads(gl);

		// ������׶
		drawPyramid(gl);

		// ��������
		drawCube(gl);

		// �������ı���
//		drawTextureQuads(gl);

		// ������������
		drawTextureCube(gl);
	}

	private void drawTriangle(GL10 gl) {
		// gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		// �����ε���ɫΪ��ɫ��͸����Ϊ��͸��
		// gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mTriangleColorBuffer);
		// ƽ�ơ���glTranslatef(x, y, z)�е����ƶ���ʱ���������������Ļ�����ƶ�����������뵱ǰ���ڵ���Ļλ�á�
		gl.glTranslatef(0.0f, 2.5f, -10.0f);
		// ��y����ת
		gl.glRotatef(rotateTriangle, 0.0f, 1.0f, 0.0f);
		// ���ö��㣬��һ�������������ά����������3ά���ڶ�����������ʾbuffer����ŵ���float��������������0������Ϊ���ǵ��������������ǽ��յ����еģ�û��ʹ��offset�����Ĳ����ŵ��Ǵ�Ŷ��������buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTriangleBuffer);
		// �����飬��һ��������ʾ�������Σ��ڶ���������first��������������count����ʾ��buffer���������Ŀ�ʼ�͸���
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		// Ҳ����ʹ�ô˺�����������
//		gl.glDrawElements(GL10.GL_TRIANGLES,3,GL10.GL_FLOAT, mIndexBuffer);
		
		/**
		 * ����Լ�������˵��һ�£�
		 * GL_TRIANGLES: ���������ζ��OpenGLʹ���������������ͼ�Ρ����ԣ��ڿ�ʼ���������㣬���ö���1������2������3�����һ�������Ρ���ɺ�������һ���������������������Σ�ֱ�����������
		 * GL_TRIANGLE_STRIP: OpenGL��ʹ�ý��ʼ���������������Ȼ�����ÿ�����㣬��Щ���㽫ʹ��ǰ2������һ�����һ�������Ρ����Ե����������һ�����ڶ�������һ�������Ρ����ĸ��㽫�ڵڶ��������������������Ρ�
		 * Ҳ����˵��0��1��2�����������һ�������Σ�1��2��3��������Ҳ���һ�������Ρ�
		 * GL_TRIANGLE_FAN: ��������ʼ��2�����㣬Ȼ�����ÿ�����㣬��OpenGL����Щ����������ǰһ�����Լ�����ĵ�һ������һ�����һ�������Ρ���3���㽫�� �ڶ����㣨ǰһ�����͵�һ���㣨��һ����.����һ�������Ρ�
		 * Ҳ����˵��ͬ����0��1��2��3��4�����㡣
		 * ��STRIP״̬���ǣ�0��1��2��1��2��3��2�������Ρ�
		 * ��FAN״̬���ǣ�0,1,2��0,2,3��2�������Ρ�
		 */

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		rotateTriangle += 2.2f;
	}

	private void drawQuads(GL10 gl) {
		// gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

//		gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mQuadsColorBuffer);
		// ����������1.5����λ��Y�᲻�������������Ļ10.0f����λ
		gl.glTranslatef(0.0f, 2.5f, -10.0f);
		// ��x����ת
		gl.glRotatef(rotateQuads, 1.0f, 0.0f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		rotateQuads -= 2.15f;
	}

	private void drawPyramid(GL10 gl) {
		gl.glLoadIdentity();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glTranslatef(-1.3f, 0.0f, -10.0f);
		gl.glRotatef(rotatePyramid, 0.0f, 1.0f, 0.0f); // ��Y����ת

		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mPyramidColorBuffer);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mPyramidBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 12); // ����׶��4���棬ÿ����3����

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		rotatePyramid += 2.2f;
	}

	private void drawCube(GL10 gl) {
		gl.glLoadIdentity();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glTranslatef(1.3f, 0.0f, -10.0f);
		gl.glRotatef(rotateCube, 1.0f, 1.0f, 1.0f);

		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mCubeColorBuffer);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mCubeBuffer);
		// ���λ�6����
		for (int i = 0; i < 6; i++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, i * 4, 4);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		rotateCube -= 2.15f;
	}

	private void initTexture(GL10 gl) {
		// ׼��һ������
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, intBuffer);
		// ȡ��OpenGL׼��������
		int texture = intBuffer.get();
		// ��ͼƬ�󶨵����2D��������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		Bitmap mBitmap = DataManager.getBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
		// ��ʾͼ��ʱ�������ȷŴ�ñ�ԭʼ����������С�ñ�ԭʼ������Сʱ��OpenGL����ƽ�����˲���ʽLINEAR
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
	}

	private void drawTextureQuads(GL10 gl) {
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		initTexture(gl);

		gl.glTranslatef(0.0f, -2.5f, -10.0f);
		gl.glRotatef(rotateTextureQuads, 1.0f, 0.0f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoordQuadsBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		rotateTextureQuads -= 2.15f;
	}

	private void drawTextureCube(GL10 gl) {
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		initTexture(gl);

		gl.glTranslatef(0.0f, -2.5f, -10.0f);
		gl.glRotatef(rotateTextureCube, 1.0f, 1.0f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mCubeBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoordCubeBuffer);
		for (int i = 0; i < 6; i++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, i * 4, 4);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		rotateTextureCube -= 2.15f;
	}

	public void setColor(float r, float g, float b) {
		mRed = r;
		mGreen = g;
		mBlue = b;
	}

}
