package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/6/13.
 */

public class TipsBean {
    private String id;
    private boolean isNewRecord;
    private String updateDate;
    private String sceneId;
    private String tips;
    private int priority;

    public TipsBean() {
    }

    public TipsBean(String id, boolean isNewRecord, String updateDate, String sceneId, String tips, int priority) {
        this.id = id;
        this.isNewRecord = isNewRecord;
        this.updateDate = updateDate;
        this.sceneId = sceneId;
        this.tips = tips;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "TipsBean{" +
                "id='" + id + '\'' +
                ", isNewRecord=" + isNewRecord +
                ", updateDate='" + updateDate + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", tips='" + tips + '\'' +
                ", priority=" + priority +
                '}';
    }
}
