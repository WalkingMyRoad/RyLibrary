package com.ist.rylibrary.base.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.PhoneSocketController;
import com.ist.rylibrary.base.controller.WebSocketController;
import com.ist.rylibrary.base.event.PhoneSocketMessageEvent;
import com.ist.rylibrary.base.event.WebSocketMessageEvent;
import com.ist.rylibrary.base.util.ToolUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by maxy
 * on 2017/5/10.
 * 手机控制程序
 */
public class PhoneSocketService extends Service {
    private static String TAG="PhoneSocketService";
    /**开启服务Intent*/
    public static Intent sIntent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void  getInstance(Context context){
        startService(context);
    }

    private static void startService(Context context){
        Log.i(TAG,"打开PhoneSocketService");
        sIntent = new Intent(context, PhoneSocketService.class);
        context.startService(sIntent);
    }
    /***
     * 关闭服务
     * @param context
     */
    public static void finishService(Context context){
        Log.i(TAG,"关闭了Socket服务！");
        if(sIntent!=null){
            context.stopService(sIntent);
            PhoneSocketController.getInstance().closeSocket();
            sIntent = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToolUtil.getInstance().loadEventBus(this);
        Log.i(TAG,"PhoneSocketService打开");
        PhoneSocketController.getInstance().startPhoneSocket();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToolUtil.getInstance().relieveEventBus(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhoneSocketMessageEvent event) {
        if(event!=null){
            PhoneSocketController.getInstance().dealMessage(event.getMessage(),event.getSession(),event.getContext());
        }
    }
}
