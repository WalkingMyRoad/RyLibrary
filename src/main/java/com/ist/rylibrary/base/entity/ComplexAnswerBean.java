package com.ist.rylibrary.base.entity;

import java.util.List;

/**
 * Created by minyuchun on 2017/5/16.
 * 新的场景问答接口
 */

public class ComplexAnswerBean {
    /**回复类型*/
    private String contentType;
    /**回复内容*/
    private String answerContent;
    /**展示次数*/
    private int showNum;
    /**前置条件*/
    private String frontCon;
    /**问答id*/
    private String id;

    public ComplexAnswerBean() {
    }

    public ComplexAnswerBean(String contentType, String answerContent, int showNum, String frontCon, String id) {
        this.contentType = contentType;
        this.answerContent = answerContent;
        this.showNum = showNum;
        this.frontCon = frontCon;
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public int getShowNum() {
        return showNum;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public String getFrontCon() {
        return frontCon;
    }

    public void setFrontCon(String frontCon) {
        this.frontCon = frontCon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ComplexAnswerBean{" +
                "contentType='" + contentType + '\'' +
                ", answerContent='" + answerContent + '\'' +
                ", showNum=" + showNum +
                ", frontCon='" + frontCon + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
