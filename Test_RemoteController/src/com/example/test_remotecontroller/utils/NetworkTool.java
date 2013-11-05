package com.example.test_remotecontroller.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

/**
 * 提供监听网络的方�?
 * @author fanzhang
 *
 */
public class NetworkTool implements Recyclable
{
	public	class	NetworkStateInfo
	{
		/**
		 * @see	android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY
		 */
		public boolean noconnectivity;
		/**
		 * @see android.net.ConnectivityManager.EXTRA_IS_FAILOVER
		 */
		public boolean failover;
		/**
		 * @see NetworkInfo
		 */
		public	NetworkInfo	networkInfo;
	}
	
	public	static	interface	IOnConnectivityChangeListener
	{
		void	OnConnectivityChanged(NetworkStateInfo info);
	}
	
	public	static	interface	IOnSettingChangeListener
	{
		void	OnBackgroundDataSettingChanged();
	}
	
	public	void	setOnConnectivityChangeListener(IOnConnectivityChangeListener listener)
	{
		if (null == listener)
		{
			unregisterConnectiveReceiver(mContext);
		}
		else
		{
			registerConnectiveReceiver(mContext);
		}
		mConnectiveListener = listener;
	}
	
	public	void	setOnSettingChangeListener(IOnSettingChangeListener listener)
	{
		if (null == listener)
		{
			unregisterSettingReceiver(mContext);
		}
		else
		{
			registerSettingReceiver(mContext);
		}
		mSettingListener = listener;
	}
	
	public	NetworkTool(Context context)
	{
		mContext	= context;
	}

	@Override
	public void recycle()
	{
		unregisterConnectiveReceiver(mContext);
		unregisterSettingReceiver(mContext);
		mContext	= null;
		
	}
	
	private void registerConnectiveReceiver(Context context)
	{
		if (mConnectiveReceiver != null)
		{
			return;
		}
		mConnectiveReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				NetworkStateInfo	info	= new NetworkStateInfo();
				
				info.noconnectivity 	= intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                info.failover			= intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//                info.networkInfo	= intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                info.networkInfo	= intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
               
                if (null != info.networkInfo && info.networkInfo.getState().equals(NetworkInfo.State.CONNECTING))
                	return;
         
				if (null == mHandler)
				{
					mHandler	= new Handler();
				}
				final	NetworkStateInfo tmpinfo = info;
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (mConnectiveListener != null)
						{
							mConnectiveListener.OnConnectivityChanged(tmpinfo);
						}
					}
				});
			}
		};
			
//		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		context.registerReceiver(mConnectiveReceiver, filter);
	}
	
	private void unregisterConnectiveReceiver(Context context)
	{
		if(null == mConnectiveReceiver)
		{
			return;
		}
		context.unregisterReceiver(mConnectiveReceiver);
	}
	
	private void registerSettingReceiver(Context context)
	{
		if (mSettingReceiver != null)
		{
			return;
		}
		mSettingReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				if (null == mHandler)
				{
					mHandler	= new Handler();
				}
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (mSettingListener != null)
						{
							mSettingListener.OnBackgroundDataSettingChanged();
						}
					}
				});
			}
		};
				
		IntentFilter filter = new IntentFilter(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
		context.registerReceiver(mSettingReceiver, filter);
	}
	
	private void unregisterSettingReceiver(Context context)
	{
		if(null == mSettingReceiver)
		{
			return;
		}
		context.unregisterReceiver(mSettingReceiver);
	}
	
	private Context	mContext	= null;
	private BroadcastReceiver mConnectiveReceiver 	= null;
	private BroadcastReceiver mSettingReceiver 	= null;
	private Handler		mHandler				= null;
	private IOnConnectivityChangeListener	mConnectiveListener	= null;
	private IOnSettingChangeListener	mSettingListener	= null;
}
