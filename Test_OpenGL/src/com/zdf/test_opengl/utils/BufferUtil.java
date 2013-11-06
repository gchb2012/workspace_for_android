package com.zdf.test_opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BufferUtil {
	public static FloatBuffer mBuffer;

	public static FloatBuffer floatToBuffer(float[] a) {
		// �ȳ�ʼ��buffer������ĳ���*4����Ϊһ��floatռ4���ֽ�
		ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
		// ����������nativeOrder
		mbb.order(ByteOrder.nativeOrder());
		mBuffer = mbb.asFloatBuffer();
		mBuffer.put(a);
		mBuffer.position(0);
		return mBuffer;
	}
}
