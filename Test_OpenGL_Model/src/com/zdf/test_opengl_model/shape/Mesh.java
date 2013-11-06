package com.zdf.test_opengl_model.shape;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.zdf.test_opengl_model.shape.utils.BufferUtils;

public class Mesh {
	// vertex buffer.
	private FloatBuffer mVerticesBuffer = null;

	// indices buffer.
	private ShortBuffer mIndicesBuffer = null;
	// The number of indices.
	private int mNumOfIndices = -1;

	// Flat Color
	private float[] mRgba = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	// Smooth Colors
	private FloatBuffer mColorsBuffer = null;

	// Translate params
	public float mTx = 0;
	public float mTy = 0;
	public float mTz = 0;

	// Rotate params
	public float mRx = 0;
	public float mRy = 0;
	public float mRz = 0;

	public void draw(GL10 gl) {
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer);
		
		gl.glColor4f(mRgba[0], mRgba[1], mRgba[2], mRgba[3]);
		if (mColorsBuffer != null) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorsBuffer);
		}

		gl.glTranslatef(mTx, mTy, mTz);
		gl.glRotatef(mRx, 1, 0, 0);
		gl.glRotatef(mRy, 0, 1, 0);
		gl.glRotatef(mRz, 0, 0, 1);

		if (mNumOfIndices > 0) {
			gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices,
					GL10.GL_UNSIGNED_SHORT, mIndicesBuffer);
		}
		
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (mColorsBuffer != null) {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
	
	protected void setVertices(float[] vertices) {
		mVerticesBuffer = BufferUtils.floatToBuffer(vertices);
	}

	protected void setIndices(short[] indices) {
		mIndicesBuffer = BufferUtils.shotToBuffer(indices);
		mNumOfIndices = indices.length;
	}

	protected void setColor(float red, float green, float blue, float alpha) {
		mRgba[0] = red;
		mRgba[1] = green;
		mRgba[2] = blue;
		mRgba[3] = alpha;
	}

	protected void setColors(float[] colors) {
		mColorsBuffer = BufferUtils.floatToBuffer(colors);
	}


}
