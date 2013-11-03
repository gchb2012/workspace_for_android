package com.zdf.deviceinfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DeviceInfoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        TextView deviceInfoView = (TextView) findViewById(R.id.device_info);
        
        String strDeviceInfo = getDeviceInfo(this);
        Log.v("zdf", strDeviceInfo);
        Log.v("zdf", " ");
        String strDeviceOtherInfo = getDeviceOtherInfo();
        Log.v("zdf", strDeviceOtherInfo);
        Log.v("zdf", " ");
        String strCameraParams = getCameraParameters();
        myLog("zdf", strCameraParams);
        
        deviceInfoView.setText(strDeviceInfo + strDeviceOtherInfo + formatCameraParameters(strCameraParams));
        
//		getVersionInfo(this);
    }
    
    private void myLog(String tag, String msg) {
    	int wholeStrNum = 4000;
    	int msgLength = msg.length();
    	Log.v("zdf", "msgLength = " + msgLength);
    	if (msgLength <= wholeStrNum) {
    		Log.v(tag, msg);
    	} else {
    		int nSegments = msgLength / (wholeStrNum + 1) + 1;
    		for (int i = 0; i < nSegments; i++) {
    			int start = i * wholeStrNum;
    			int end = Math.min((i + 1) * wholeStrNum, msgLength);
    			Log.v("zdf", "start = " + start + ", end = " + end);
    			Log.v(tag, (i + 1) + ". " + msg.substring(start, end));
    		}
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_device_info, menu);
        return true;
    }
    
    private String getDeviceInfo(Context context) {
    	String strDeviceInfo = 
    			getDeviceModelInfo() + "\n" + 
//    			getCPUSerial() + "\n\n" + 
    			getDeviceDisplayInfo(context) + "\n";
    	return strDeviceInfo;
    }
    
    private String getDeviceModelInfo() {
    	String strModelNumber = android.os.Build.MODEL;  //设备型号（Model number）：Galaxy Nexus
    	String strSDKVersion = android.os.Build.VERSION.RELEASE; //SDK版本：2.3.3, 4.0.3
    	int nSDKNumber = android.os.Build.VERSION.SDK_INT; //SDK版本号：11, 12, ...
    	
    	String strDeviceInfo = 
    			"Model number: " + strModelNumber + "\n" +
    			"SDK Version: " + strSDKVersion + "\n" +
    			"SDK Number: " + String.valueOf(nSDKNumber) + "\n";
    	return strDeviceInfo;
    }
    
    private String getVersionInfo(Context context) {
    	String strVersionInfo = null;
    	try {
	    	String packageName = context.getPackageName();
//	    	PackageInfo packageInfo2 = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
	    	PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
	    	int versionCode = packageInfo.versionCode;
	    	String versionName = packageInfo.versionName;
	    	
	    	strVersionInfo = 
	    			"Version Code: " + versionCode + "\n" + 
	    			"Version Name: " + versionName + "\n";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
    	}
    	
    	return strVersionInfo;
    }
    
    private String getDeviceDisplayInfo(Context context) {
    	DisplayMetrics metrics = new DisplayMetrics();
    	metrics = context.getResources().getDisplayMetrics();
//    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
//    	getWindowManager().getDefaultDisplay().getSize(size);
    	int screenWidth = metrics.widthPixels;
    	int screenHeight = metrics.heightPixels;
    	int densityDpi = metrics.densityDpi;
    	float density = metrics.density;
    	float xdpi = metrics.xdpi;
    	float ydpi = metrics.ydpi;
    	
    	String strDeviceDisplayInfo = 
    			"Screen Size: " + String.valueOf(screenWidth) + "x" + String.valueOf(screenHeight) + "\n" + 
    			"DPI Value: " + String.valueOf(densityDpi) + "\n" + 
    			"DPI x: " + xdpi + "\n" + 
    			"DPI y: " + ydpi + "\n" + 
    			"DPI Level: " + String.valueOf(density) + "\n";
    	return strDeviceDisplayInfo;
    }
    
//    private String getCurrentTime() {
//    	//获取当前时间 
//    	Long strCurrentTime = System.currentTimeMillis();
//		String date = DateFormat.format("yyyyMMddkkmmss", System.currentTimeMillis()).toString();
//    	//获取当前时间时区
//    	TimeZone.getDefault();
//    	
//    	String strTime = 
//    			"Time: " + 
//    	return 
//    }
    
    private String getDeviceOtherInfo() {
    	String strBoard 		= android.os.Build.BOARD;
    	String strBootLoader 	= android.os.Build.BOOTLOADER;
    	String strBrand 		= android.os.Build.BRAND;
    	String strCpuAbi 		= android.os.Build.CPU_ABI;
    	String strCpuAbi2 		= android.os.Build.CPU_ABI2;
    	String strDevice 		= android.os.Build.DEVICE;
    	String strFingerPrint 	= android.os.Build.FINGERPRINT;
    	String strHardware 		= android.os.Build.HARDWARE;
    	String strHost 			= android.os.Build.HOST;
    	String strId 			= android.os.Build.ID;
    	String strManufacturer 	= android.os.Build.MANUFACTURER;
    	String strModel 		= android.os.Build.MODEL;
    	String strProduct 		= android.os.Build.PRODUCT;
    	String strRadio 		= android.os.Build.RADIO;
    	String strSerial 		= android.os.Build.SERIAL;
    	String strTags	 		= android.os.Build.TAGS;
    	String strType	 		= android.os.Build.TYPE;
    	
    	String strDeviceOtherInfo = 
    			"Board: " + strBoard + "\n" +
    			"BootLoader: " + strBootLoader + "\n" +
    			"Brand: " + strBrand + "\n" +
    			"CpuAbi: " + strCpuAbi + "\n" +
    			"CpuAbi2: " + strCpuAbi2 + "\n" +
    			"Device: " + strDevice + "\n" +
    			"FingerPrint: " + strFingerPrint + "\n" +
    			"Hardware: " + strHardware + "\n" +
    			"Host: " + strHost + "\n" +
    			"Id: " + strId + "\n" +
    			"Manufacturer: " + strManufacturer + "\n" +
    			"Model: " + strModel + "\n" +
    			"Product: " + strProduct + "\n" +
    			"Radio: " + strRadio + "\n" +
    			"Serial: " + strSerial + "\n" +
    			"Tags: " + strTags + "\n" +
    			"Type: " + strType + "\n\n";
    	return strDeviceOtherInfo;
    }
    
    private String getCameraParameters() {
    	String strCameraParams = null;
    	try {
    	Camera camera = Camera.open(0);
    		strCameraParams = camera.getParameters().flatten() + "\n";
    		camera.release();
    	} catch (RuntimeException e) {
    		strCameraParams = e.getMessage() + "\n";
    		e.printStackTrace();
    	}
    	return strCameraParams;
    }
    
    private String formatCameraParameters(String strParames) {
    	if (null == strParames)
    		return "";
    	
    	String strFormat = "";
    	String[] strFields = strParames.split(";");
		for (String strEachParam: strFields) {
			strFormat += strEachParam + ";\n";
		}
		
		return strFormat;
    }
    
	public static String getSerialNumber() {
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serial;
	}
    
    /**  
	  * 获取CPU序列号  
	  *  
	  * @return CPU序列号(16位)  
	  * 读取失败为"0000000000000000"  
	  */   
	public static String getCPUSerial() {   
       String str = "", strCPU = "", cpuAddress = "0000000000000000";   
       try {   
			//读取CPU信息   
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");   
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());   
			LineNumberReader input = new LineNumberReader(ir);   
			//查找CPU序列号   
			for (int i = 1; i < input.getLineNumber(); i++) {
				str = input.readLine();   
				if (str != null) {   
					//查找到序列号所在行   
					if (str.indexOf("Serial") > -1) {   
						//提取序列号   
						strCPU = str.substring(str.indexOf(":") + 1, str.length());   
						//去空格   
						cpuAddress = strCPU.trim();   
						break;   
					}   
				}else{   
					//文件结尾   
					break;   
				}   
			}   
       } catch (IOException ex) {   
			//赋予默认值   
			ex.printStackTrace();   
       }   
       return "CPU Serial: " + cpuAddress;   
	}
}
