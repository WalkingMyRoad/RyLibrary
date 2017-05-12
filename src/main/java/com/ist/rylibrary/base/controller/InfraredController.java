package com.ist.rylibrary.base.controller;

import android.app.Activity;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.event.InfraredMessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minyuchun on 2017/3/27.
 */

public class InfraredController {

    private static InfraredController mInfraredController;

    private InfraredListener mInfraredListener;

    private String TAG = "ChassisController";
    /**休眠页面跳转的条件判断,最多10个条件类型*/
    private List<Boolean> isDormancyCondition = new ArrayList<>();

    public static InfraredController getInstance(){
        if(mInfraredController == null){
            mInfraredController = new InfraredController();
        }
        return mInfraredController;
    }

    public InfraredController(){

    }

    /**
     * 发送默认的地盘消息
     * @param isPersonExist  消息内容
     * @param frequency 频率积累
     */
    public void post(boolean isPersonExist,int frequency){
        post(false,isPersonExist,frequency);
    }
    /***
     * 发送地盘消息
     * @param isCustom 是否自定义处理
     * @param isPersonExist 是否有人
     * @param frequency 频率积累
     */
    public void post(boolean isCustom,boolean isPersonExist,int frequency){
        EventBus.getDefault().post(new InfraredMessageEvent(isCustom,isPersonExist,frequency));
    }

    /**
     * 自定义消息回收处理
     * @param isCustom  是否自定义
     * @param isPersonExist  是否有人
     * @param frequency     频率积累
     */
    public void recovery(boolean isCustom,boolean isPersonExist,int frequency){
        if(mInfraredListener!=null){
            mInfraredListener.InfraredMessage(isCustom,isPersonExist,frequency);
        }
    }

    /**
     * 设置地盘监听
     * @param listener 新的监听
     */
    public InfraredController setInfraredListener(InfraredListener listener) {
        this.mInfraredListener = listener;
        return mInfraredController;
    }

//    public boolean

    public InfraredController is(boolean isNext){
        if(isDormancyCondition==null){
            isDormancyCondition = new ArrayList<>();
        }
        isDormancyCondition.add(isNext);
        return mInfraredController;
    }
    /**顶层的topActivity判断*/
    public boolean TopActivityJudge(Class clazz){
        String topActivityName = ActivityController.getInstance().getTopActivity() == null?
                "":ActivityController.getInstance().getTopActivity().getClass().getSimpleName();
        RyApplication.getLog().d("判断值 获取当前界面"+topActivityName);
        RyApplication.getLog().d("判断值 获取传界面"+clazz.getClass().getSimpleName());
        if(topActivityName.contains(clazz.getSimpleName())){
            return true;
        }else{
            return false;
        }
    }

    /***
     * 默认的底层处理方式
     * @return 返回是否正确
     */
    public boolean build(){
        try {
            if(isDormancyCondition!=null){
                RyApplication.getLog().d("判断值 " + isDormancyCondition.toString());
                if(!isDormancyCondition.contains(false)){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            isDormancyCondition = null;
        }
        return false;
    }

    /***
     * 　底盘监听回调
     */
    public interface InfraredListener{
        /**
         *  红外消息回调
         * @param isCustom 是否自定义
         * @param isPersonExist 是否有人
         * @param frequency  频率积累
         */
        void InfraredMessage(boolean isCustom,boolean isPersonExist,int frequency);
    }


}
