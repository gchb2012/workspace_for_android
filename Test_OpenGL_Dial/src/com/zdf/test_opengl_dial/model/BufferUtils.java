package com.zdf.test_opengl_dial.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferUtils {

	public static FloatBuffer floatToBuffer(float[] a) {
		// �ȳ�ʼ��buffer������ĳ���*4����Ϊһ��floatռ4���ֽ�
		ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
		// ����������nativeOrder
		mbb.order(ByteOrder.nativeOrder());
		FloatBuffer fBuffer = mbb.asFloatBuffer();
		fBuffer.put(a);
		fBuffer.position(0);
		return fBuffer;
	}

	public static ShortBuffer shotToBuffer(short[] a) {
		// �ȳ�ʼ��buffer������ĳ���*4����Ϊһ��floatռ4���ֽ�
		ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 2);
		// ����������nativeOrder
		mbb.order(ByteOrder.nativeOrder());
		ShortBuffer sBuffer = mbb.asShortBuffer();
		sBuffer.put(a);
		sBuffer.position(0);
		return sBuffer;
	}
}
