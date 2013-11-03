package com.zdf.test_aes;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Test_AESActivity extends Activity {
    /** Called when the activity is first created. */
	@SuppressWarnings("null")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String masterPassword = "Arcsoft01";
		String originalText = "0123456789aaa";
//		byte[] text = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8',
//				'9' };
//		byte[] password = new byte[] { 'a' };
		try {
			Log.i("zdf", "加密前: "+ originalText);
			String encryptingCode = AES.encrypt(masterPassword,
					originalText);
			Log.i("zdf", "加密后: "+ encryptingCode);
			String decryptingCode = AES.decrypt(masterPassword,
					encryptingCode);
			Log.i("zdf", "解密后: " + decryptingCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String content = "test";
//		String password = "12345678";
//		// 加密
//		System.out.println("加密前：" + content);
//		byte[] encryptResult = AES2.encrypt(content, password);
//		// 解密
//		byte[] decryptResult = AES2.decrypt(encryptResult, password);
//		System.out.println("解密后：" + new String(decryptResult));
		
		
//		String srcFile = "/mnt/sdcard/test_aes_src.txt";
//		String desFile = "/mnt/sdcard/test_aes_des";
		String srcFile = "/data/data/com.zdf.test_aes/test_aes_src.txt";
		String desFile = "/data/data/com.zdf.test_aes/test_aes_des";
		File file = new File(srcFile);
		Log.v("zdf", "src length = " + file.length());
		char[] srcData = new char[(int)file.length()];
		char[] desData = null;
		String encryptedData = null;
		String decryptedData = null;
		
		FileManager.ReadFile(srcFile, srcData);
//		if (null == srcData)
//			return;
		
		try {
			encryptedData = AES.encrypt(masterPassword, String.valueOf(srcData));
			decryptedData = AES.decrypt(masterPassword, encryptedData);
			Log.v("zdf", "srcData: "+ String.valueOf(srcData));
			Log.v("zdf", "encryptedData: "+ encryptedData);
			Log.v("zdf", "decryptedData: "+ decryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (null != encryptedData) {
			desData = encryptedData.toCharArray();
		}
		if (null != desData) {
			FileManager.WriteFile(desFile, desData);
		}
    }
}