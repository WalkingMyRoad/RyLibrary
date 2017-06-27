package com.ist.rylibrary.base.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.myfloatwindow.controller.FloatWindowController;
import com.ist.rylibrary.myfloatwindow.service.FloatWindowService;

import org.json.JSONObject;

/**
 * Created by minyuchun on 2017/3/22.
 */

public class BaseBoardCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("star_send")){//明星脸处理
            try{
                String information = intent.getExtras().getString("information");
                RyApplication.getLog().d("明星脸信息 "+information);
                JSONObject jsonObject = new JSONObject(information);
                if(jsonObject.has("result") && jsonObject.has("message")){
                    String result = jsonObject.getString("result");
                    String message = jsonObject.getString("message");
                    if(result.equals("speak")){//说话
                        AiuiController.getInstance().post(message);
                    }else if(result.equals("pageChange")) {//页面切换
                        RyApplication.getLog().d("明星脸页面切换 "+message);
                        SceneController.getInstance().changeScene("star",message);
                    }else if(result.equals("operation")){ //操作
                        if(message.equals("hideFloating")) {//隐藏对话框，当明星脸确认打开后返回对话框状态
                            FloatWindowController.getInstance().post(FloatWindowService.FLOAT_DESTROY);
                        }
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(action.equals("com.wewins.facedetect.notify")){
//            RyApplication.getLog().d("人脸识别的信息： "+information);
        }
    }

    /***
     * 广播处理类
     * @param context
     * @param intent
     */
}
