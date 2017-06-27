package com.ist.rylibrary.base.entity;

import java.util.List;

/**
 * Created by minyuchun on 2017/3/23.
 */

public class SceneQABean {
    private String id;//sceneQasNew
    private List<SceneQasNewAnswerListBean> answerList;//sceneQasNew
    private boolean isNewRecord;//通用

    private String actionCode;
    private String answerFile;
    private String sceneQaId;
    private String sceneId;
    private String priority;
    private String topicId;
    private String answer;
    private String questions;
    private int actionType;

    public SceneQABean() {
    }

    public SceneQABean(String id, List<SceneQasNewAnswerListBean> answerList, boolean isNewRecord, String actionCode, String answerFile, String sceneQaId, String sceneId, String priority, String topicId, String answer, String questions, int actionType) {
        this.id = id;
        this.answerList = answerList;
        this.isNewRecord = isNewRecord;
        this.actionCode = actionCode;
        this.answerFile = answerFile;
        this.sceneQaId = sceneQaId;
        this.sceneId = sceneId;
        this.priority = priority;
        this.topicId = topicId;
        this.answer = answer;
        this.questions = questions;
        this.actionType = actionType;
    }

    public String getSceneQaId() {
        return sceneQaId;
    }

    public void setSceneQaId(String sceneQaId) {
        this.sceneQaId = sceneQaId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerFile() {
        return answerFile;
    }

    public void setAnswerFile(String answerFile) {
        this.answerFile = answerFile;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SceneQasNewAnswerListBean> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<SceneQasNewAnswerListBean> answerList) {
        this.answerList = answerList;
    }

    @Override
    public String toString() {
        return "SceneQABean{" +
                "id='" + id + '\'' +
                ", answerList=" + answerList +
                ", isNewRecord=" + isNewRecord +
                ", actionCode='" + actionCode + '\'' +
                ", answerFile='" + answerFile + '\'' +
                ", sceneQaId='" + sceneQaId + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", priority='" + priority + '\'' +
                ", topicId='" + topicId + '\'' +
                ", answer='" + answer + '\'' +
                ", questions='" + questions + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
