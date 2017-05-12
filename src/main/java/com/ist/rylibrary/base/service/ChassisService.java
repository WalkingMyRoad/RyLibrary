package com.ist.rylibrary.base.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.ChassisController;
import com.ist.rylibrary.base.event.ChassisMessageEvent;
import com.ist.rylibrary.base.util.ToolUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by minyuchun on 2017/3/6.
 * 广播服务
 */

public class ChassisService extends Service {

    private static Intent sIntent;

    /***
     * 打开service
     * @param context
     */
    public static void getInstance(Context context) {
        sIntent = new Intent(context, ChassisService.class);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToolUtil.getInstance().loadEventBus(this);
        ChassisController.getInstance().connect(this);
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

    /**
     * eventsbus 回调
     * @param event 回调的类参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChassisMessageEvent event) {
        if (event != null) {
            try {
                RyApplication.getLog().d("接收到底盘动作要走了 "+event.getMessage() +"，"+event.getType());
                if(!event.isCustom()){
                    if(event.getType().equals(ChassisController.CHASSIS_TYPE_NAVIGATE)){
                        ChassisController.getInstance().getHuman().navigate(event.getMessage(),1000);
                    }else if(event.getType().equals(ChassisController.CHASSIS_TYPE_WALK)){
                        ChassisController.getInstance().walk(event.getMessage());
                    }
                }else{
                    ChassisController.getInstance().recovery(event.isCustom(),event.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
