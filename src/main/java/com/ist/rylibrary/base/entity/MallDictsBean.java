package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/6.
 */

public class MallDictsBean {
    private String id;
    private boolean isNewRecord;
    private String createDate;
    private String updateDate;
    private String mallId;
    private String robotId;
    private String value;
    private String label;
    private String type;
    private String description;
    private String sort;


    public MallDictsBean(String id, boolean isNewRecord, String createDate, String updateDate, String mallId, String robotId, String value, String label, String type, String description, String sort) {
        this.id = id;
        this.isNewRecord = isNewRecord;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.mallId = mallId;
        this.robotId = robotId;
        this.value = value;
        this.label = label;
        this.type = type;
        this.description = description;
        this.sort = sort;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getMallId() {
        return mallId;
    }

    public void setMallId(String mallId) {
        this.mallId = mallId;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }


    @Override
    public String toString() {
        return "MallDictsBean{" +
                "id='" + id + '\'' +
                ", isNewRecord=" + isNewRecord +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", mallId='" + mallId + '\'' +
                ", robotId='" + robotId + '\'' +
                ", value='" + value + '\'' +
                ", label='" + label + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
