package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/3/30.
 * 基础的动作执行类 包括页面动作，底盘动作，眼睛动作以及头部动作
 */

public class BaseActionBean {
    /***
     * 动作之前要说的话，第一步执行
     */
//    private String talkBeforeAction;
    /***
     * 是否在语音结束后才执行动作
     */
    private boolean isActionAfterTalk;
    /**
     *  需要执行的动作，在语音说完后执行，根据上面的boolean 类型判断执行时间
     */
    private String action;
    /***
     * 动作发生错误后说的话，在动作发生错误时
     */
    private String talkErrorAction;

//    public String getTalkBeforeAction() {
//        return talkBeforeAction;
//    }
//
//    public void setTalkBeforeAction(String talkBeforeAction) {
//        this.talkBeforeAction = talkBeforeAction;
//    }

    public boolean isActionAfterTalk() {
        return isActionAfterTalk;
    }

    public void setActionAfterTalk(boolean actionAfterTalk) {
        isActionAfterTalk = actionAfterTalk;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTalkErrorAction() {
        return talkErrorAction;
    }

    public void setTalkErrorAction(String talkErrorAction) {
        this.talkErrorAction = talkErrorAction;
    }

    @Override
    public String toString() {
        return "BaseActionBean{" +
//                "talkBeforeAction='" + talkBeforeAction + '\'' +
                ", isActionAfterTalk=" + isActionAfterTalk +
                ", action='" + action + '\'' +
                ", talkErrorAction='" + talkErrorAction + '\'' +
                '}';
    }
}
