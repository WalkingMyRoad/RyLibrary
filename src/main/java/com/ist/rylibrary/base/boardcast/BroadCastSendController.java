package com.ist.rylibrary.base.boardcast;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;

/**
 * Created by minyuchun on 2017/3/31.
 */

public class BroadCastSendController {

    private static BroadCastSendController sBroadCastSendController;


    public static BroadCastSendController getInstance(){
        if(sBroadCastSendController == null){
            sBroadCastSendController = new BroadCastSendController();
        }
        return sBroadCastSendController;
    }

    public boolean sendStarBroadCast(String message){
        try{
            Bundle bundle = new Bundle();
            bundle.putString("information",message);
            return sendBroadCast("star_accept",bundle);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送广播
     * @param action 光笔的action
     * @return 返回是否正确发送广播
     */
    private boolean sendBroadCast(String action, Bundle bundle){
        try{
            Intent intent=new Intent();
            intent.setAction(action);
            intent.putExtras(bundle);
            RyApplication.getContext().sendBroadcast(intent);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
