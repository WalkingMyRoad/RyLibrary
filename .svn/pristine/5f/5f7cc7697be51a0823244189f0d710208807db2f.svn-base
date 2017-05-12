package com.ist.rylibrary.base.event;

/**
 * Created by minyuchun on 2017/3/25.
 */

public class InfraredMessageEvent {
    /**是否是自定义处理*/
    private boolean isCustom;
    /**是否有人*/
    private boolean isPersonExist;
    /**红外检测的频率 有人没人次数叠加*/
    private int frequency;

    public InfraredMessageEvent(boolean isCustom,boolean isPersonExist,int frequency){
        this.isCustom = isCustom;
        this.isPersonExist = isPersonExist;
        this.frequency = frequency;
    }
    public boolean isPersonExist() {
        return isPersonExist;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
