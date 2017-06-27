package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/3/29.
 * 引导流程的执行类
 */

public class DipanBean extends BaseActionBean {
    /**
     * 动作执行后说的话
     */
    private String talkAfterAction;
    /***
     * 引导底盘动作发生错误后说的话，在底盘移动发生错误时
     */
    private String actionErrorAction;

    public String getTalkAfterAction() {
        return talkAfterAction;
    }

    public void setTalkAfterAction(String talkAfterAction) {
        this.talkAfterAction = talkAfterAction;
    }

    public String getActionErrorAction() {
        return actionErrorAction;
    }

    public void setActionErrorAction(String actionErrorAction) {
        this.actionErrorAction = actionErrorAction;
    }

    @Override
    public String toString() {
        return "DipanBean{" +
//                "talkBeforeAction='" + getTalkBeforeAction() + '\'' +
                ", isActionAfterTalk=" + isActionAfterTalk() +
                ", action='" + getAction() + '\'' +
                ", talkErrorAction='" + getActionErrorAction() + '\'' +
                ",talkAfterAction='" + talkAfterAction + '\'' +
                ", actionErrorAction='" + actionErrorAction + '\'' +
                '}';
    }
}
