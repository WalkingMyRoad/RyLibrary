package com.ist.rylibrary.base.controller;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.listener.YinDaoAiuiListener;
import com.ist.rylibrary.base.listener.YinDaoChassisListener;
import com.ist.rylibrary.base.service.AiuiService;
import com.renying.m4.AIUI;
import com.wewins.robot.Dipan;

/**
 * Created by minyuchun on 2017/3/30.
 * 引导流程控制类
 */

public class YinDaoController implements YinDaoChassisListener,YinDaoAiuiListener{
    /**引导类实例*/
    private static YinDaoController sYinDaoController;
    /**引导移动完成后的指令*/
    private String yindaoAction;
    /**是否在引导流程*/
    private  boolean isInYindaoProcess;

    private YinDaoChassisListener mYinDaoChassisListener;

    private YinDaoAiuiListener mYinDaoAiuiListener;

    public static YinDaoController getInstance(){
        if(sYinDaoController == null){
            sYinDaoController = new YinDaoController();
        }
        return sYinDaoController;
    }
    private YinDaoController(){
    }

    /**
     * 处理引导流程的底盘指令
     * @param action  引导指令
     */
    public void dealChassisAction(String action){
        try{
            YindaoStart();
            if(action.indexOf("_")>-1){
                String[] chassisSplit = action.split("_");
                if(chassisSplit.length == 3){
                    if(chassisSplit[2].equals("true")){
                        ChassisController.getInstance().post(true,action);
                    }else{
                        ChassisController.getInstance().post(chassisSplit[0]);
                    }
                    setYindaoAction(chassisSplit[0]+"_"+chassisSplit[1]);
                }else if (chassisSplit.length == 2){
                    ChassisController.getInstance().post(chassisSplit[0]);
                    setYindaoAction(action);
                }else{
                    ChassisController.getInstance().post(action);
                    setYindaoAction(action);
                }
            }
        }catch (Exception e){
            AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
            e.printStackTrace();
        }
    }

    public void YindaoStart(){
        RyApplication.getLog().d("YinDaoController 引导开始 ");
        isInYindaoProcess = true;
        AiuiController.getInstance().post(AiuiService.AIUI_TYPE_CLOSE);
        setYinDaoChassisListener(this);
    }


    /**
     * 引导流程结束
     */
    public void YindaoEnd(){
        RyApplication.getLog().d("YinDaoController 结束 ");
        this.yindaoAction = null;
        isInYindaoProcess = false;
        AiuiController.getInstance().post(AiuiService.AIUI_TYPE_OPEN);
        setYinDaoChassisListener(null);
        setYinDaoAiuiListener(null);
        ChassisController.getInstance().getHuman().stop();
    }

    @Override
    public boolean onChassisComplete(boolean isComplete, String where) {
        RyApplication.getLog().d("YinDaoController 引导动作完成后的话 "+yindaoAction+"是否安全完成 "+isComplete +"地点 "+where);
        setYinDaoAiuiListener(this);
        SceneController.getInstance().rrPeople(yindaoAction,null,"");
        return true;
    }

    @Override
    public boolean onError() {
        RyApplication.getLog().d("YinDaoController 底盘错误 ");
        YindaoEnd();
        return true;
    }

    @Override
    public boolean onAiuiComplete(boolean isComplete) {
        RyApplication.getLog().d("AiuiController 引导流程结束 isComplete="+isComplete);
        YindaoEnd();
        return true;
    }


    public boolean isInYindaoProcess() {
        return isInYindaoProcess;
    }

    public void setInYindaoProcess(boolean inYindaoProcess) {
        isInYindaoProcess = inYindaoProcess;
    }

    public void setYinDaoChassisListener(YinDaoChassisListener yinDaoChassisListener) {
        mYinDaoChassisListener = yinDaoChassisListener;
    }

    public YinDaoChassisListener getYinDaoChassisListener() {
        return mYinDaoChassisListener;
    }

    public void setYinDaoAiuiListener(YinDaoAiuiListener yinDaoAiuiListener) {
        mYinDaoAiuiListener = yinDaoAiuiListener;
    }

    public YinDaoAiuiListener getYinDaoAiuiListener() {
        return mYinDaoAiuiListener;
    }

    public void setYindaoAction(String yindaoAction) {
        RyApplication.getLog().d("YinDaoController 引导指令 "+yindaoAction);
        this.yindaoAction = yindaoAction;

    }
}
