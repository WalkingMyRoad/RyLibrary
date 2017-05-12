package com.ist.rylibrary.base.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.NetWorkController;
import com.ist.rylibrary.base.event.AiuiMessageEvent;
import com.ist.rylibrary.base.event.NetWorkMessageEvent;
import com.ist.rylibrary.base.util.ToolUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NetWorkServer extends Service{
	/***/
	public static final int NETWORK_TYPE_ERROR = -1;
	/** 没有网络 */
	public static final int NETWORK_TYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORK_TYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORK_TYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORK_TYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORK_TYPE_WIFI = 4;
	/**是否需要自定义监听网络变化*/
	private static boolean isCustom;
	/**开始服务intent*/
	private static Intent sIntent;
	/**是否有网络*/
	private static boolean isNetWork = false;
	public static void  getInstance(Context context){
		startService(context);
	}
	public static void  getInstance(Context context,boolean custom){
		isCustom = custom;
		startService(context);
	}

	private static void startService(Context context){
		sIntent = new Intent(context, NetWorkServer.class);
		context.startService(sIntent);
	}

	/***
	 * 关闭服务
	 * @param context
	 */
	public static void finishService(Context context){
		if(sIntent!=null){
			context.stopService(sIntent);
			sIntent = null;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("NetWorkServer","onCreate");
		ToolUtil.getInstance().loadEventBus(this);
		try{
			IntentFilter mFilter=new IntentFilter();
			mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//添加网络监听广播
			registerReceiver(myNetReceiver,mFilter);
		}catch (Exception e){
			e.printStackTrace();
		}
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("NetWorkServer","onStartCommand");
		return START_NOT_STICKY;
	}


	/**aiui 监听需要长连接的*/
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(NetWorkMessageEvent event) {
		try{
			if(event!=null){
				int netType = event.getNetType();
				boolean isNetWork = event.isNetWork();
				boolean isCustom = event.isCustom();
				boolean isPingServer = event.isPingServer();
				if(!isCustom){
					if(netType == NETWORK_TYPE_ERROR){
						Toast.makeText(RyApplication.getContext(),"当前网络发生错误",Toast.LENGTH_LONG).show();
					}else if(netType == NETWORK_TYPE_INVALID){
						Toast.makeText(RyApplication.getContext(),"当前无网络连接",Toast.LENGTH_LONG).show();
					}else{
						if(isPingServer){
//							Toast.makeText(RyApplication.getContext(),"网络连接，且能ping通服务器",Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(RyApplication.getContext(),"网络连接，但未能ping通服务器",Toast.LENGTH_LONG).show();
						}
					}
				}else{
					NetWorkController.getInstance().recovery(netType,isCustom,isNetWork,isPingServer);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public BroadcastReceiver myNetReceiver =new BroadcastReceiver(){
        @SuppressWarnings("unused")
		@Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){//是网络状态的连接
				int type = NetWorkController.getInstance().getNetWorkType(context);
				if(type == NETWORK_TYPE_ERROR || type == NETWORK_TYPE_INVALID){
					NetWorkController.getInstance().post(type,isCustom,false,false);
					isNetWork = false;
				}else{
					if(NetWorkController.getInstance().ping()){
						isNetWork = true;
						NetWorkController.getInstance().post(type,isCustom,true,true);
					}else{
						NetWorkController.getInstance().post(type,isCustom,true,false);
						isNetWork = false;
					}
				}
            }
        }
    };

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("NetWorkServer","onDestroy");
		ToolUtil.getInstance().relieveEventBus(this);
		closeReceiver();
	}
	/***
	 * 关闭广播注册
	 */
	public void closeReceiver(){
		if(myNetReceiver!=null){
			unregisterReceiver(myNetReceiver);
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("NetWorkServer","IBinder");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("NetWorkServer","onUnbind");
		return false;
	}
}
