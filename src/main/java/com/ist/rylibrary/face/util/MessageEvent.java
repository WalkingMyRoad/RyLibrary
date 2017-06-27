package com.ist.rylibrary.face.util;

/**
 * Created by dna on 2016-05-27.
 */
public class MessageEvent {
    private String notifyType;
    private String notifyData;

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getNotifyData() {
        return notifyData;
    }

    public void setNotifyData(String notifyData) {
        this.notifyData = notifyData;
    }

    public MessageEvent(String notifyType, String notifyData) {
        this.notifyType = notifyType;
        this.notifyData = notifyData;
    }
}
