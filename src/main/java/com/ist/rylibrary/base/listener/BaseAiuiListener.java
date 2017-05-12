package com.ist.rylibrary.base.listener;

import android.os.Handler;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.ActivityController;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.JiangJieController;
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
    }
    @Override
    public void onResult(JSONObject jsonObject) {
        RyApplication.getLog().d("BaseAiuiListener onResult 原始语音在这里 "+jsonObject.toString());
        AiuiController.getInstance().analysisResult(jsonObject);
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onStat(String s) {

    }

    @Override
    public void onWakeup(int i, int i1) {

    }

    @Override
    public void onSleep() {
        try{
            if(AiuiController.getInstance().isAutoWakeUp() &&
                    !YinDaoController.getInstance().isInYindaoProcess() &&
                    !JiangJieController.getInstance().isInJiangJieProcess()){
                AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSpeekBegin() {
        FloatWindowController.getInstance().setSonic(true);
    }

    @Override
    public void onSpeekEnd() {
        FloatWindowController.getInstance().setSonic(false);
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
