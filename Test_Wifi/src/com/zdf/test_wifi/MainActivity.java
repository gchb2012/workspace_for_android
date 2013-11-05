package com.zdf.test_wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mConnectiveReceiver, filter);
		
		IntentFilter wifiFilter = new IntentFilter();
		wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); //?
		registerReceiver(mWifiStateReceiver, wifiFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mConnectiveReceiver);
		unregisterReceiver(mWifiStateReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private BroadcastReceiver mConnectiveReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			NetworkStateInfo	info	= new NetworkStateInfo();
			
//			info.noconnectivity 	= intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//            info.failover			= intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//            info.networkInfo	= intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			
			Log.v("zdf", "mConnectiveReceiver, intent.getAction() = " + intent.getAction());
            
			NetworkInfo networkInfo	= intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			Log.v("zdf", "mConnectiveReceiver, networkInfo.getType() = " + networkInfo.getType() + ", networkInfo.isConnected() = " + networkInfo.isConnected());
		}
	};
	
	private BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.v("zdf", "mWifiStateReceiver, action = " + action);
			
			if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
				
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo info = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				Log.e("zdf", "#### mWifiStateReceiver, info.getState() = " + info.getState());
				if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// 如果断开连接
					System.out.println("wifi网络连接断开 ");
				} else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
					System.out.println("wifi网络正在连接...");
				} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
					System.out.println("wifi网络已连接");
				}
			} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				//test
				NetworkInfo networkinfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				Log.i("zdf", "xxxx mWifiStateReceiver, networkinfo = " + networkinfo);
				if (null != networkinfo)
				Log.i("zdf", "xxxx mWifiStateReceiver, networkinfo.getState() = " + networkinfo.getState());
				
				// WIFI开关
				int wifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_DISABLED);
				Log.e("zdf", "#### mWifiStateReceiver, wifiState = " + wifiState);
				
				switch (wifiState) { 
	            case WifiManager.WIFI_STATE_DISABLED: 
	                break; 
	            case WifiManager.WIFI_STATE_DISABLING: 
	                break; 
	            case WifiManager.WIFI_STATE_ENABLED: 
	                break; 
	            case WifiManager.WIFI_STATE_ENABLING: 
	                break; 
	            case WifiManager.WIFI_STATE_UNKNOWN: 
	                break; 
	            } 
			}
			
		}
	};

}
