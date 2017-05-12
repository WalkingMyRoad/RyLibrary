package com.ist.rylibrary.myfloatwindow.event;

import com.ist.rylibrary.myfloatwindow.service.FloatWindowService;

/**
 * Created by minyuchun on 2017/3/28.
 * 窗口实现的 event
 */

public class FloatWindowEvent {
    /**是否自定义*/
    private boolean isCustom;
    /**悬浮窗的状态*/
    private int floatState;
    /**机器人说的话*/
    private String robotMessage;
    /**人说的话*/
    private String personMessage;
    /**
     * 发送信息
     * @param messageRobot 机器人说的话
     * @param messagePerson  人说的话
     */
    public FloatWindowEvent(boolean isCustom,int state,String messageRobot,String messagePerson){
        this.isCustom = isCustom;
        this.floatState = state;
        this.robotMessage = messageRobot;
        this.personMessage = messagePerson;
    }
    public int getFloatState() {
        return floatState;
    }

    public String getRobotMessage() {
        return robotMessage;
    }

    public void setRobotMessage(String robotMessage) {
        this.robotMessage = robotMessage;
    }

    public String getPersonMessage() {
        return personMessage;
    }

    public void setPersonMessage(String personMessage) {
        this.personMessage = personMessage;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
