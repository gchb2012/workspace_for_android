package com.zdf.test_aes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class FileManager {
	// 读文件函数，输入参数：文件名， 读取缓冲首地址，数据长度
	public static boolean ReadFile(String filename, char[] str) {
		try {
			File file = new File(filename);
			if (!file.exists())
				return false;
			
			FileReader fr = new FileReader(filename);
			fr.read(str, 0, (int)file.length());
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 写文件函数，输入参数：文件名， 输入缓冲首地址，数据长度
	public static boolean WriteFile(String filename, char[] str) {
		try {
			File file = new File(filename);
			if (!file.exists())
				file.createNewFile();
			
			FileWriter fw = new FileWriter(filename);
			fw.write(str, 0, str.length);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 删除文件函数，输入参数：文件名(全路径) 如 "/sdcard/test.txt"
	public static boolean DeleteFile(String filename) {
		File file;
		file = new File(filename);
		if (file.exists())
			file.delete();
		else
			return false;

		return true;
	}
}
