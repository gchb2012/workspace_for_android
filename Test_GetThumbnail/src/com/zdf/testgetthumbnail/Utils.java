package com.zdf.testgetthumbnail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.arcsoft.MediaPlayer.VideoThumbnailUtils;

public class Utils {
	private final static int MV2_SEEKMODE_KEYFRAME_ONLY = 0;
	private final static int MV2_SEEKMODE_CONTINUOUS = 1;
	private final static String LibraryPath = "/system/arcsoft/videoplayer/lib/";
	public static final String PLUGININ_STR = "/data/data/com.zdf.testgetthumbnail/ARCAVPPlugin.ini";
	public static final String[][] CopyFileNameArray = {
			{ "data/ARCAVPPlugin.ini", PLUGININ_STR }, { "", "" } };

	static {
		try {
			System.load(LibraryPath + "libplatform.so");
			System.load(LibraryPath + "libmv2_common.so");
			System.load(LibraryPath + "libmv2_mpplat.so");
			System.load(LibraryPath + "libarcsoft_mphttp.so");
			System.load(LibraryPath + "libAES_infoEncrypt.so");
			System.load(LibraryPath + "libmv2_sourceparser.so");
			System.load(LibraryPath + "libmv2_playerbase.so");
			System.load(LibraryPath + "libammf.so");
		} catch (Exception e) {
			Log.e("ArcVideoPlayer", e.toString());
		}
	}

	static public void loadres(Context context, String judgmentPath) {
		File file = new File(judgmentPath);
		if (!file.exists()) {
			int i = 0;

			while (null != CopyFileNameArray[i][0]
					&& 0 != CopyFileNameArray[i][0].length()) {
				copyAssetDataFile(context, CopyFileNameArray[i][0],
						CopyFileNameArray[i][1]);
				i++;
			}
		}
	}

	public static Bitmap getThumbnail(String filePath, int width, int height,
			int seekTime) {
		if (null == filePath)
			return null;
		if (0 == filePath.length()) {
			return null;
		}
		VideoThumbnailUtils thumbUtils = null;
		Bitmap bitmap = null;
		if (seekTime < 0)
			seekTime = 0;

		try {
			thumbUtils = new VideoThumbnailUtils();
			thumbUtils.setConfigFile(PLUGININ_STR);
			thumbUtils.setDataSource(filePath);
			thumbUtils.setAllowBlankOutput(false);
			thumbUtils
					.setFillStyle(com.arcsoft.MediaPlayer.VideoThumbnailUtils.FIT_OUT);
			thumbUtils.setRotation(0);

			// ammf bug: ���������4�ı�����ȡ����ͼƬ�����Ų�����
			width = (width >> 2) << 2;
			height = (height >> 2) << 2;

			thumbUtils.setTargetSize(width, height);

			thumbUtils.setOutputFormat(android.graphics.PixelFormat.RGB_565);
			thumbUtils.setSeekMode(MV2_SEEKMODE_KEYFRAME_ONLY);
			thumbUtils.prepare();
			bitmap = thumbUtils.captureFrame(seekTime);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} finally {
			if (thumbUtils != null) {
				thumbUtils.release();
				thumbUtils = null;
			}
		}
		return bitmap;
	}

	static boolean copyAssetDataFile(Context context, String srcFile,
			String desFile) {
		if (null == context)
			return false;

		AssetManager am = context.getAssets();
		int size = 0;
		try {
			if (am != null) {
				InputStream is = am.open(srcFile);

				File file = new File(desFile);

				// ��Ҫ�洢��λ��(�����λ���ǿ�д��)
				if (!file.exists()) { // �Ƿ����ļ�����
					File parent = file.getParentFile(); // �����ڵ�ʱ���ж����ļ����Ƿ����
					if (!parent.exists()) // ����ļ��в�����
					{
						parent.mkdirs(); // �������ļ���
					}
					file.createNewFile(); // �ļ��������򴴽����ļ�
				}

				FileOutputStream out = null; // �ļ������
				out = new FileOutputStream(desFile, true); // ���ļ��������

				size = is.available(); // ����ļ���С
				if (size >= 0) {
					byte[] buffer = new byte[size]; // ���仺��

					is.read(buffer); // ���뻺��
					out.write(buffer); // ������д��
					out.flush(); // д���ļ�ϵͳ
				}

				is.close(); // �رն����ļ�
				out.close(); // �ر����
			}
		} catch (FileNotFoundException e) {
			Log.e("ArcVideoPlayer", e.toString());
		} catch (SecurityException e) {
			Log.e("ArcVideoPlayer ", e.toString());
		} catch (IOException e) {
			Log.e("ArcVideoPlayer s", e.toString());
		}

		return true;
	}
}
