package com.zdf.test_mediastore;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.Vector;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class FileUtils {

	public static int MRESULT_SDCARD_NOT_EXIST = -1;
	public static int MRESULT_CHECK_EXCEPTION = -2;

	/**
	 * 将文件大小转换为字符�?
	 * 
	 * @param size
	 *            单位：B
	 * @return 转换后的字符�?
	 */
	public static String formatSize(long size) {
		if (size <= 0) {
			return "0MB";
		}
		if (size < 1024) {
			return String.valueOf(size) + "B";
		}

		long size_kb = size / 1024;
		if (size_kb < 1024) {
			return String.valueOf(size_kb) + "KB";
		}

		double size_mb = (double) size / 1024 / 1024;
		// 如需对结果进行四舍五入，执行下面注释掉的部分即可。此处和win7下看到的尺寸保持�?��，只保留小数点两位，不进行取舍运算�?
		// new DecimalFormat("0.##").format(size_mb) + "MB"
		if (size_mb < 1024) {
			String strSize = String.valueOf(size_mb);
			int index = strSize.lastIndexOf(".");
			return strSize
					.substring(0, index + 3 < strSize.length() ? index + 3
							: strSize.length())
					+ "MB";
		}

		double size_bg = (double) size / 1024 / 1024 / 1024;

		String strSize = String.valueOf(size_bg);
		int index = strSize.lastIndexOf(".");
		return strSize.substring(0, index + 3 < strSize.length() ? index + 3
				: strSize.length())
				+ "GB";
	}

	/**
	 * �?��外部存储是否可用
	 * 
	 * @param requireWriteAccess
	 *            是否�?���?��可写
	 * @return true - 可用�?false - 不可�?
	 * @see {@link #checkFsWritable()}
	 */
	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (requireWriteAccess) {
				boolean writable = checkFsWritable();
				return writable;
			} else {
				return true;
			}
		} else if (!requireWriteAccess
				&& Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * �?��文件系统是否可写
	 * 
	 * @return true - 可写�?false - 其他
	 */
	private static boolean checkFsWritable() {
		/**
		 * Create a temporary file to see whether a volume is really writable.
		 * It's important not to put it in the root directory which may have a
		 * limit on the number of files.
		 * */
		String directoryName = Environment.getExternalStorageDirectory()
				.toString();
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}
		return directory.canWrite();
	}

	/**
	 * 获取SD卡的可用空间
	 * 
	 * @return Unit: KB
	 */
	public static long getSDCardAvailableSize() {
		try {
			if (!hasStorage(true)) {
				return MRESULT_SDCARD_NOT_EXIST;
			} else {
				String storageDirectory = Environment
						.getExternalStorageDirectory().toString();
				StatFs stat = new StatFs(storageDirectory);
				float freespace = (float) stat.getAvailableBlocks()
						* stat.getBlockSize();
				return (long) (freespace / 1024);
			}
		} catch (Exception ex) {
			return MRESULT_CHECK_EXCEPTION;
		}
	}

	/**
	 * 获取Android系统data的可用空�?
	 * 
	 * @return Unit: KB
	 */
	public static long getAndroidDataAvailableSize() {
		try {
			String storageDirectory = Environment.getDataDirectory().toString();
			StatFs stat = new StatFs(storageDirectory);
			float freespace = (float) stat.getAvailableBlocks()
					* stat.getBlockSize();
			return (long) (freespace / 1024);
		} catch (Exception ex) {
			return MRESULT_CHECK_EXCEPTION;
		}
	}

	/**
	 * 获取Android系统可用内存
	 * 
	 * @return Unit: KB
	 */
	public static long getMemoryAvailableSize(Context context) {
		if (context == null) {
			return -1;
		}

		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (manager == null) {
			return -1;
		}

		ActivityManager.MemoryInfo memoryinfo = null;
		memoryinfo = new ActivityManager.MemoryInfo();
		manager.getMemoryInfo(memoryinfo);
		long available = memoryinfo.availMem;
		return available / 1024;
	}

	public static boolean isLocalItem(Uri uri) {
		String path = uri.toString();
		if (path != null
				&& (path.startsWith("file://") || path.startsWith("content://"))) {
			return true;
		}

		return false;
	}

	public static String getMPOFilePath(String filepath) {
		File srcFile = new File(filepath);
		String srcName = srcFile.getName();
		int dotpos = srcName.lastIndexOf('.');
		if (dotpos > 0) {
			srcName = srcName.substring(0, dotpos);
		}
		final String cmpname = srcName;
		final String cmpeext = ".mpo";

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				int dotpos = filename.lastIndexOf('.');
				if (dotpos < 0) {
					return false;
				}
				String ext = filename.substring(dotpos).toLowerCase();
				String name = filename.substring(0, dotpos);
				return name.equals(cmpname) && ext.equals(cmpeext);
			}
		};
		File parent = srcFile.getParentFile();
		String[] result = srcFile.getParentFile().list(filter);
		return (result != null && result.length > 0) ? parent.getAbsolutePath()
				+ "/" + result[0] : null;
	}

	public static String getNewFileName(String strOldFileName) {
		if (null == strOldFileName || 0 == strOldFileName.length())
			return null;

		int lIndex = 0;
		String strDst = strOldFileName;
		// 如果文件存在，那么直接删�?
		File newFile = new File(strDst);
		if (newFile.exists()) {
			newFile.delete();

		}
		// if (new File(strDst).exists()) {
		// String strType = getExtension(strDst);
		// String strPath = trimExtension(strDst);
		// do {
		// strDst = strPath + "_" + ++lIndex + "." + strType;
		// } while (new File(strDst).exists());
		// }
		return strDst;
	}

	public static String getExtension(File f) {
		return (f != null) ? getExtension(f.getName()) : "";
	}

	public static String getExtension(String filename) {
		return getExtension(filename, "");
	}

	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1);
			}
		}
		return defExt;
	}

	public static String trimExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length()))) {
				return filename.substring(0, i);
			}
		}
		return filename;
	}

	public static String uriToFilePath(Context activity, Uri uri) {
		if (null == uri || null == activity) {
			return null;
		}

		String tempString = null;
		Cursor cur = null;
		String uriStr = null;

		try {
			uriStr = uri.toString();
			if ((null != uriStr)
					&& (uriStr.startsWith("http://") || uri.toString()
							.startsWith("rtsp://"))) {
				tempString = Uri.decode(uri.toString());
			} else if (uri.toString().startsWith("file://")) {
				tempString = Uri.decode(uri.toString()).substring(7);
			} else {
				ContentResolver cr = activity.getContentResolver();

				cur = cr.query(uri, null, null, null, null);
				if (null != cur) {
					if (cur.moveToFirst()) {
						int idIndex = cur
								.getColumnIndex(MediaStore.Audio.Media.DATA);
						tempString = cur.getString(idIndex);
					}
				}
			}
			if (cur != null) {
				cur.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return tempString;
	}

	public static boolean deleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存�?
		if (!file.exists()) { // 不存在返�?false
			return flag;
		} else {
			// 判断是否为文�?
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件�?
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文�?
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔�?
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则�?�?
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文�?包括子目�?
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文�?
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目�?
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public static long getFolderSizeFormated(String path) {
		if (null == path)
			return 0;

		File f = new File(path);

		long size = getFolderSize(f);

		return size;
	}

	public static long getFileSize(String filePath) {
		if (null == filePath)
			return 0;

		try {
			File f = new File(filePath);
			return f.length();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// 递归
	public static long getFolderSize(File f)// 取得文件夹大�?
	{
		long size = 0;
		try {
			File flist[] = f.listFiles();
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFolderSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}

		} catch (Exception e) {
		}
		return size;
	}

	public static String getVersionName(Context context)
			throws NameNotFoundException {
		if (null == context)
			return "";
		PackageManager packageManager = context.getPackageManager();

		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}

//	public static void deleteOldDBIfNeeded(Context context) {
//		File file = new File(OEMConfig.CACHE_BASE_PATH);
//		if (!file.exists())
//			return;
//
//		File[] files = file.listFiles();
//		if (null == files || 0 == file.length())
//			return;
//
//		try {
//			String dBName = LocalCacheProvider.getDBName(context);
//			for (File f : files) {
//				String fileName = f.getName();
//				// Log.e("test", "fileName = " + fileName);
//				if (fileName.startsWith(LocalCacheProvider.DBName))
//					if (!fileName.startsWith(dBName))
//						deleteFile(f.getAbsolutePath());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// Log.e("test", "after delete ======");
//		// files = file.listFiles();
//		// for (File f : files) {
//		// String fileName = f.getName();
//		// Log.e("test", "fileName = " + fileName);
//		// }
//	}
//
//	public static void recreateCacheMgr(Context context) {
//		if (null == context)
//			return;
//
//		((MediaPlusApplication) context.getApplicationContext())
//				.setCacheCleared(true);
//		if (OEMConfig.OPENGL_OPTIMIZE) {
//			CacheMgr cacheMgr = ((MediaPlusApplication) context
//					.getApplicationContext()).getLocalCacheManager();
//			if (null != cacheMgr) {
//				((LocalCacheMgr) cacheMgr).recreateCache();
//			}
//		}
//	}

	public static boolean isMediaStoreSupported(String filePath) {
		if (null == filePath)
			return false;

		try {
			String mimeType = MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension(
//							MimeTypeMap.getFileExtensionFromUrl(filePath));
							FileUtils.getExtension(filePath));

			Class<?> c = Class.forName("android.media.MediaFile");

			// call getFileTypeForMinmeType to get fileType
			Method getFileTypeForMimeType = c.getDeclaredMethod(
					"getFileTypeForMimeType", String.class);
			getFileTypeForMimeType.setAccessible(true);
			int fileType = (Integer) getFileTypeForMimeType.invoke(
					c.newInstance(), mimeType);

			// call isImageFileType
			Method isImageFileType = c.getDeclaredMethod("isImageFileType",
					Integer.TYPE);
			isImageFileType.setAccessible(true);
			Object isImageFile = isImageFileType.invoke(c.newInstance(),
					fileType);

			// call isVideoFile
			Method isVideoFileType = c.getDeclaredMethod("isVideoFileType",
					Integer.TYPE);
			isVideoFileType.setAccessible(true);
			Object isVideoFile = isVideoFileType.invoke(c.newInstance(),
					fileType);

			return (Boolean) isImageFile || (Boolean) isVideoFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static Uri filePathToContentUri(Uri filePath, long itemId,
			boolean isVideoOrImage) {
		if (null == filePath) {
			return null;
		}

		if (filePath.toString().startsWith("file://")) {
			if (isVideoOrImage) {
				return Uri.parse(Video.Media.EXTERNAL_CONTENT_URI.toString()
						+ "/" + itemId);
			} else {
				return Uri.parse(Images.Media.EXTERNAL_CONTENT_URI.toString()
						+ "/" + itemId);
			}
		} else {
			return Uri.parse(filePath.toString());
		}
	}

	public static Uri filePathToContentUri(Context context, Uri filePath,
			boolean isVideoOrImage) {
		if (null == filePath) {
			return null;
		}

		String pathName = isVideoOrImage ? Video.VideoColumns.DATA
				: Images.ImageColumns.DATA;
		Uri EXURI = isVideoOrImage ? Video.Media.EXTERNAL_CONTENT_URI
				: Images.Media.EXTERNAL_CONTENT_URI;
		String[] columnStrs = isVideoOrImage ? new String[] { Video.VideoColumns._ID }
				: new String[] { Images.ImageColumns._ID };
		int itemId = -1;
		if (filePath.toString().startsWith("file://")) {
			String path = filePath.getPath();
			Log.e("testP", "testP path1 is " + path);
			if (path != null) {
				ContentResolver cr = context.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(pathName).append("=")
						.append("'" + path + "'").append(")");
				Cursor cur = cr.query(EXURI, columnStrs, buff.toString(), null,
						null);
				Log.e("testP", "testP cur.getCount() is " + cur.getCount());
				if (cur.getCount() > 0 && cur.moveToFirst()) {
					itemId = cur.getInt(cur
							.getColumnIndex(Images.ImageColumns._ID));
				}
				
				if (itemId == -1) {
					return Uri.parse(filePath.toString());
				}
			}
			return Uri.parse(EXURI.toString() + "/" + itemId);
		} else {
			return Uri.parse(filePath.toString());
		}
	}
	
	public static void getSubFileFolder(String root, Vector<String> vecFileFolders) {  
		File file = new File(root);  
		File[] subFile = file.listFiles();
		
		if(null == subFile){
			return;
		}
		
		for (int i = 0; i < subFile.length; i++) {  
			if (subFile[i].isDirectory()) {
				vecFileFolders.add(subFile[i].getAbsolutePath());  
				getSubFileFolder(subFile[i].getAbsolutePath(), vecFileFolders);  
			}
		}  
	}
	
	public static void RecursionGetFilePath(String root, Vector<String> vecFile, String suffix) {  
		File file = new File(root);
		File[] subFile = file.listFiles();  
		
		if(null == subFile){
			return;
		}
		
		for (int i = 0; i < subFile.length; i++) {
			if (subFile[i].isDirectory()) {  
				RecursionGetFilePath(subFile[i].getAbsolutePath(), vecFile, suffix);  
			} else {
				String filename = subFile[i].getName();  
				if (suffix == null) {
					vecFile.add(subFile[i].getAbsolutePath());  
				}
				else {
					int totalLength = filename.length();
					int suffixLength = suffix.length();
					if (totalLength > suffixLength) {
						String temp = filename.substring(totalLength-suffixLength);
						if (temp != null && temp.equalsIgnoreCase(suffix)) {
							vecFile.add(subFile[i].getAbsolutePath()); 
						}
					}
				}
			}  
		}  
	}
}
