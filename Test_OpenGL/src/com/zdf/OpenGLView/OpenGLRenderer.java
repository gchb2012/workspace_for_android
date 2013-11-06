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
	
	// 三角形
	// 这里实际上是定义了三角形的三个顶点，三个数分别是x,y,z的坐标，和数学里直角坐标系相同
	private float[] mTriangleArray = {
			0f, 1f, 0f, 	// 上顶点
			-1f, -1f, 0f, 	// 左下顶点
			1f, -1f, 0f 	// 右下顶点
	};
	// 定义个FloatBuffer，这是android画三角形必须的结构
	private FloatBuffer mTriangleBuffer = null;
	// 三角形颜色数组
	private float[] mTriangleColorArray = { 
			1f, 0f, 0f, 1f, // 红
			0f, 1f, 0f, 1f, // 绿
			0f, 0f, 1f, 1f 	// 蓝
	};
	private FloatBuffer mTriangleColorBuffer = null;
	// 旋转三角形
	private float rotateTriangle = 0.0f;

	// 四边形
	// 从这里可以看出，我们按照逆时针的方向画图
	private float[] mQuadsArray = { 
			1f, 1f, 0f, 	// 右上
			-1f, 1f, 0f, 	// 左上
			-1f, -1f, 0f, 	// 左下
			1f, -1f, 0f 	// 右下
	};
	private FloatBuffer mQuadsBuffer = null;
	// 四边形颜色数组
	private float[] mQuadsColorArray = { 
			1f, 0f, 0f, 1f, // 红
			0f, 1f, 0f, 1f, // 绿
			0f, 0f, 1f, 1f, // 蓝
			1f, 0f, 1f, 1f	// 紫
	};
	private FloatBuffer mQuadsColorBuffer = null;
	// 旋转四边形
	private float rotateQuads = 0.0f;

	// 四棱锥
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

	// 立方体
	private float[] mCubeArray = {
			// 前
			1.0f, 1.0f, 1.0f, //
			-1.0f, 1.0f, 1.0f, //
			-1.0f, -1.0f, 1.0f, //
			1.0f, -1.0f, 1.0f, //

			// 后
			-1.0f, 1.0f, -1.0f, //
			1.0f, 1.0f, -1.0f, //
			1.0f, -1.0f, -1.0f, //
			-1.0f, -1.0f, -1.0f, //

			// 左
			-1.0f, 1.0f, 1.0f, //
			-1.0f, 1.0f, -1.0f, //
			-1.0f, -1.0f, -1.0f, //
			-1.0f, -1.0f, 1.0f, //

			// 右
			1.0f, 1.0f, -1.0f, //
			1.0f, 1.0f, 1.0f, //
			1.0f, -1.0f, 1.0f, //
			1.0f, -1.0f, -1.0f, //

			// 上
			1.0f, 1.0f, -1.0f, //
			-1.0f, 1.0f, -1.0f, //
			-1.0f, 1.0f, 1.0f, //
			1.0f, 1.0f, 1.0f, //

			// 下
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

	// 纹理正方形坐标
	private float[] mTextureCoordQuadsArray = { 
//			1.0f, 1.0f, // 图片的右上角
//			0.0f, 1.0f, // 图片的左上角
//			0.0f, 0.0f, // 图片的左下角
//			1.0f, 0.0f // 图片的右下角
			
			/**
			 * 本来应该是图片的坐标和正方形定点坐标位置对应的，
			 * 但是android图像坐标系统与Opengl es坐标系统不一致
			 * (y轴反向：Opengl向上为+y，Android向下为+y)，
			 * 所以这里y轴坐标要反过来写。
			 */
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f //
	};
	private FloatBuffer mTextureCoordQuadsBuffer;
	private float rotateTextureQuads = 0.0f;

	// 纹理立方体坐标
	private float[] mTextureCoordCubeArray = {
			// 前
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// 后
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// 左
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// 右
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// 上
			1.0f, 0.0f, //
			0.0f, 0.0f, //
			0.0f, 1.0f, //
			1.0f, 1.0f, //

			// 下
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
		 * 启用smooth shading（阴影平滑）。
		 * 阴影平滑通过多边形精细的混合色彩，并对外部光进行平滑。
		 */
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		/**
		 * 设置清除屏幕时所用的颜色。
		 */
		// Set the background color to black ( rgba ).
		gl.glClearColor(0f, 0f, 0f, 0.5f);
		
		/**
		 * 这三行是关于depth buffer(深度缓存)的。
		 * 将深度缓存设想为屏幕后面的层。深度缓存不断的对物体进入屏幕内部有多深进行跟踪。
		 */
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
//		/**
//		 * 设置剔除效果 for test
//		 */
//		// 设置openggl有剔除效果，就是看不到的面就不画，当然可以增加效率
//		gl.glEnable(GL10.GL_CULL_FACE);
//		// 设置逆时针方向为正面
//		gl.glFrontFace(GL10.GL_CCW);
//		// 设置背面被剔除，不画
//		gl.glCullFace(GL10.GL_BACK);
		
		/**
		 * 这里告诉OpenGL我们希望进行最好的透视修正。
		 * 这会十分轻微的影响性能。但使得透视图看起来好一点。
		 */
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// 在SurfaceCreate里面构建这个Buffer
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
		 * 设置输出屏幕大小
		 */
		gl.glViewport(0, 0, width, height);
		// gl.glViewport(width / 4, height / 4, width * 3 / 4, height * 3 / 4);

		/**
		 * 设置透视图
		 * 意味着越远的东西看起来越小。这么做创建了一个现实外观的场景。
		 * 指明任何新的变换将影响projection matrix(投影矩阵)。投影矩阵负责为我们的场景增加透视。
		 * 此处透视按照基于窗口宽度和高度的45度视角来计算。
		 * 0.1f，100.0f是我们在场景中所能绘制深度的起点和终点。
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
		 * 设置模型观察矩阵
		 * 指明任何新的变换将会影响 modelview matrix(模型观察矩阵)。
		 * 模型观察矩阵中存放了我们的物体讯息。
		 */
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		Log.v("zdf", "OpenGL: onDrawFrame");
		// 清除屏幕和深度缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		/**
		 * 当您调用glLoadIdentity()之后，您实际上将当前点移到了屏幕中心，
		 * X坐标轴从左至右，Y坐标轴从下至上，Z坐标轴从里至外。
		 * OpenGL屏幕中心的坐标值是X和Y轴上的0.0f点。
		 * 中心左面的坐标值是负值，右面是正值。
		 * 移向屏幕顶端是正值，移向屏幕底端是负值。
		 * 移入屏幕深处是负值，移出屏幕则是正值。
		 */
		// 重置当前的模型观察矩阵
		gl.glLoadIdentity();

		// 设置红色背景
		// gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		// 点击屏幕来改变背景的颜色
		gl.glClearColor(mRed, mGreen, mBlue, 0.0f);

		// 画三角形
		drawTriangle(gl);

		// 画四边形
//		drawQuads(gl);

		// 画四棱锥
		drawPyramid(gl);

		// 画立方体
		drawCube(gl);

		// 画纹理四边形
//		drawTextureQuads(gl);

		// 画纹理立方体
		drawTextureCube(gl);
	}

	private void drawTriangle(GL10 gl) {
		// gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		// 三角形的颜色为红色，透明度为不透明
		// gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mTriangleColorBuffer);
		// 平移。在glTranslatef(x, y, z)中当您移动的时候，您并不是相对屏幕中心移动，而是相对与当前所在的屏幕位置。
		gl.glTranslatef(0.0f, 2.5f, -10.0f);
		// 绕y轴旋转
		gl.glRotatef(rotateTriangle, 0.0f, 1.0f, 0.0f);
		// 设置顶点，第一个参数是坐标的维数，这里是3维，第二个参数，表示buffer里面放的是float，第三个参数是0，是因为我们的坐标在数组中是紧凑的排列的，没有使用offset，最后的参数放的是存放顶点坐标的buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTriangleBuffer);
		// 画数组，第一个参数表示画三角形，第二个参数是first，第三个参数是count，表示在buffer里面的坐标的开始和个数
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		// 也可以使用此函数画三角形
//		gl.glDrawElements(GL10.GL_TRIANGLES,3,GL10.GL_FLOAT, mIndexBuffer);
		
		/**
		 * 这里对几个参数说明一下：
		 * GL_TRIANGLES: 这个参数意味着OpenGL使用三个顶点来组成图形。所以，在开始的三个顶点，将用顶点1，顶点2，顶点3来组成一个三角形。完成后，在用下一组的三个顶点来组成三角形，直到数组结束。
		 * GL_TRIANGLE_STRIP: OpenGL的使用将最开始的两个顶点出发，然后遍历每个顶点，这些顶点将使用前2个顶点一起组成一个三角形。所以第三个点与第一个，第二个生成一个三角形。第四个点将于第二个，第三个生成三角形。
		 * 也就是说，0，1，2这三个点组成一个三角形，1，2，3这三个点也组成一个三角形。
		 * GL_TRIANGLE_FAN: 在跳过开始的2个顶点，然后遍历每个顶点，让OpenGL将这些顶点于它们前一个，以及数组的第一个顶点一起组成一个三角形。第3个点将与 第二个点（前一个）和第一个点（第一个）.生成一个三角形。
		 * 也就是说，同样是0，1，2，3这4个顶点。
		 * 在STRIP状态下是，0，1，2；1，2，3这2个三角形。
		 * 在FAN状态下是，0,1,2；0,2,3这2个三角形。
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
		// 坐标向右移1.5个单位，Y轴不动，最后移入屏幕10.0f个单位
		gl.glTranslatef(0.0f, 2.5f, -10.0f);
		// 绕x轴旋转
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
		gl.glRotatef(rotatePyramid, 0.0f, 1.0f, 0.0f); // 绕Y轴旋转

		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mPyramidColorBuffer);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mPyramidBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 12); // 四棱锥有4个面，每个面3个点

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
		// 依次画6个面
		for (int i = 0; i < 6; i++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, i * 4, 4);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		rotateCube -= 2.15f;
	}

	private void initTexture(GL10 gl) {
		// 准备一个纹理
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, intBuffer);
		// 取得OpenGL准备的纹理
		int texture = intBuffer.get();
		// 把图片绑定到这个2D纹理里面
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		Bitmap mBitmap = DataManager.getBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
		// 显示图像时，当它比放大得比原始的纹理大或缩小得比原始得纹理小时，OpenGL采用平滑的滤波方式LINEAR
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
