package com.ist.rylibrary.base.listener;

import android.os.Handler;
import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.ActivityController;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.JiangJieController;
import com.ist.rylibrary.base.controller.WebSocketController;
import com.ist.rylibrary.base.controller.YinDaoController;
import com.ist.rylibrary.base.service.AiuiService;
import com.ist.rylibrary.myfloatwindow.controller.FloatWindowController;
import com.renying.m4.AIUIListener;

import org.json.JSONObject;

/**
 * Created by minyuchun on 2017/3/25.
 * aiui 讯飞语义监听
 */

public class BaseAiuiListener implements AIUIListener{
    /**是否需要处理语音*/
    public static boolean isDealVoice = true;
    /**是否启用原始语音针对wenSocket*/
    public static boolean isOpenResultRawToWebSocket = false;
    /**允许打断websock说话*/
    public static boolean isDealWebSocketVoice = true;

    /**播放的类型*/
    private String AiuiType;
    /***
     * 初始化 aiui监听
     * @param aiuiType 板子类型
     */
    public BaseAiuiListener(String aiuiType){
        this.AiuiType = aiuiType;
        RyApplication.getLog().d("原始语音类型 "+aiuiType);
    }

    @Override
    public void onResultRaw(String s) {
        //原始识别内容
        RyApplication.getLog().d("BaseAiuiListener onResultRaw 原始语音 "+s);
        if(isOpenResultRawToWebSocket && isDealWebSocketVoice && s.length()>1){
            WebSocketController.getInstance().post(s);
            FloatWindowController.getInstance().post(null,s);
        }
    }
    @Override
    public void onResult(JSONObject jsonObject) {
        RyApplication.getLog().d("BaseAiuiListener onResult 原始语音在这里 "+jsonObject.toString()+"，是否需要处理语音 "+isDealVoice);
        if(!isOpenResultRawToWebSocket){//不使用原始语
            if (isDealVoice){//能被打断
                AiuiController.getInstance().analysisResult(jsonObject);
            }
        }
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onStat(String s) {
        RyApplication application=(RyApplication)RyApplication.getContext().getApplicationContext();
        //
        RyApplication.getLog().d("公共状态： "+s);
        if(s.equals("ready")){//休眠
            application.setAiuiWorkIng(false);
        }else if(s.equals("work")){//已经唤醒
            application.setAiuiWorkIng(true);
        }

}

    @Override
    public void onWakeup(int i, int i1) {
//        isDealVoice = true;
    }

    @Override
    public void onSleep() {
        try{
            if(AiuiController.getInstance().isAutoWakeUp() &&
                    !YinDaoController.getInstance().isInYindaoProcess() &&
                    !JiangJieController.getInstance().isInJiangJieProcess() &&
                    isDealVoice){
                AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSpeekBegin() {
        FloatWindowController.getInstance().setSonic(true);
        FloatWindowController.getInstance().post(null,"begin");
    }

    @Override
    public void onSpeekEnd() {
        FloatWindowController.getInstance().setSonic(false);
        FloatWindowController.getInstance().post(null,"end");
    }

    @Override
    public void onSpeek(int i) {

    }

    @Override
    public void onExit(String s, int i) {

    }

    @Override
    public void onWifiStat(boolean b, String s) {

    }

    @Override
    public void onCrash(String s) {

    }

}
