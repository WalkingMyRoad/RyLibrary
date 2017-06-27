package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/16.
 * 新的场景问答接口
 */

public class SceneQasNewAnswerListBean {
    private String id;
    private boolean isNewRecord;
    private String createDate;
    private String updateDate;
    private String qaQId;
    private String scopeType;
    private String scopeValue;
    private String answer;
    /**动作类型*/
    private int actionType;
    /**动作编码*/
    private String actionCode;
    /**回答的类型*/
    private String answerType;

    public SceneQasNewAnswerListBean() {
    }

    public SceneQasNewAnswerListBean(String id, boolean isNewRecord, String createDate, String updateDate, String qaQId, String scopeType, String scopeValue, String answer, int actionType, String actionCode, String answerType) {
        this.id = id;
        this.isNewRecord = isNewRecord;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.qaQId = qaQId;
        this.scopeType = scopeType;
        this.scopeValue = scopeValue;
        this.answer = answer;
        this.actionType = actionType;
        this.actionCode = actionCode;
        this.answerType = answerType;
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

    public String getQaQId() {
        return qaQId;
    }

    public void setQaQId(String qaQId) {
        this.qaQId = qaQId;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(String scopeValue) {
        this.scopeValue = scopeValue;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    @Override
    public String toString() {
        return "SceneQasNewAnswerListBean{" +
                "id='" + id + '\'' +
                ", isNewRecord=" + isNewRecord +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", qaQId='" + qaQId + '\'' +
                ", scopeType='" + scopeType + '\'' +
                ", scopeValue='" + scopeValue + '\'' +
                ", answer='" + answer + '\'' +
                ", actionType=" + actionType +
                ", actionCode='" + actionCode + '\'' +
                ", answerType='" + answerType + '\'' +
                '}';
    }
}
