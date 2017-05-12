package com.ist.rylibrary.base.listener;

import com.ist.asr.RRtts;
import com.ist.asr.RRttsListener;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.JiangJieController;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.controller.YinDaoController;
import com.ist.rylibrary.base.service.InfraredService;
import com.ist.rylibrary.myfloatwindow.controller.FloatWindowController;

/**
 * Created by minyuchun on 2017/3/25.
 */

public class BaseRRttsListener implements RRttsListener {
    private RRtts mRRtts;
    public BaseRRttsListener(RRtts rRtts){
        this.mRRtts = rRtts;
    }
    @Override
    public void onInitError(int i) {

    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onSpeakPaused() {
        mRRtts.Resume();
    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i) {
        InfraredService.clearWaitGuideCount();
    }
    @Override
    public void onCompleted(String s) {
        try{
            //结束放音
            RyApplication.getLog().d("是否是语音 结束 s = " +s);
            AiuiController.getInstance().RRttsComplete(s);
            FloatWindowController.getInstance().post(null,null);
            if(s == null) {
                SceneController.getInstance().dealActionList(true, SceneController.getInstance().getBaseActionBeanList());
            }else{
                SceneController.getInstance().setBaseActionBeanList(null);
            }
            if(JiangJieController.getInstance().getJiangJieAiuiListener()!=null){//是否存在讲解流程 存在 就把调用
                if(JiangJieController.getInstance().getJiangJieAiuiListener().onAiuiComplete(s == null)){
                    JiangJieController.getInstance().setJiangJieAiuiListener(null);
                }
            }
            if(YinDaoController.getInstance().getYinDaoAiuiListener()!=null){
                if(YinDaoController.getInstance().getYinDaoAiuiListener().onAiuiComplete(s == null)){
                    YinDaoController.getInstance().setYinDaoAiuiListener(null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
