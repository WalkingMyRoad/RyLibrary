package com.ist.rylibrary.base.entity;

import java.util.List;

/**
 * Created by minyuchun on 2017/3/30.
 * 讲解流程的单类
 */

public class JiangJieBean {
    /**当前单独类中点位的名称*/
    private String pointName;
    /**到达点位后是否再说话*/
    private String isSpeakArrive;
    /**点位指令,到达后的指令匹配值*/
    private String pointInstructions;
    /**点位说完话后说去下一个点位播放与的间隔时间*/
    private int pointSleepTime;
    /**去下一个点位前播放的语音*/
    private String nextPointSpeak;
    /**点位需要弹出的界面*/
    private List<String> ponitPages;
    /**是否已经到达下一个点位，用于区分播放到达前和到达后的语音*/
    private boolean isArrivalPoint;

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getPointInstructions() {
        return pointInstructions;
    }

    public void setPointInstructions(String pointInstructions) {
        this.pointInstructions = pointInstructions;
    }

    public int getPointSleepTime() {
        return pointSleepTime;
    }

    public void setPointSleepTime(int pointSleepTime) {
        this.pointSleepTime = pointSleepTime;
    }

    public String getNextPointSpeak() {
        return nextPointSpeak;
    }

    public String getIsSpeakArrive() {
        return isSpeakArrive;
    }

    public void setIsSpeakArrive(String isSpeakArrive) {
        this.isSpeakArrive = isSpeakArrive;
    }

    public void setNextPointSpeak(String nextPointSpeak) {
        this.nextPointSpeak = nextPointSpeak;
    }

    public List<String> getPonitPages() {
        return ponitPages;
    }

    public void setPonitPages(List<String> ponitPages) {
        this.ponitPages = ponitPages;
    }

    public boolean isArrivalPoint() {
        return isArrivalPoint;
    }

    public void setArrivalPoint(boolean arrivalPoint) {
        isArrivalPoint = arrivalPoint;
    }

    @Override
    public String toString() {
        return "JiangJieBean{" +
                "pointName='" + pointName + '\'' +
                ", isSpeakArrive='" + isSpeakArrive + '\'' +
                ", pointInstructions='" + pointInstructions + '\'' +
                ", pointSleepTime=" + pointSleepTime +
                ", nextPointSpeak='" + nextPointSpeak + '\'' +
                ", ponitPages=" + ponitPages +
                ", isArrivalPoint=" + isArrivalPoint +
                '}';
    }
}
