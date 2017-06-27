package com.ist.rylibrary.base.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxy
 * on 2017/5/24.
 * 立体式回复的实体（单条任务）
 */
public class  StereAnswerBean {
    /**前置条件**/
    private  String[] frontCon;
    /**答的ID**/
    private  String id;
    /**内容的类型**/
    private int contentType;
    /**展示次数*/
    private int showNum;

    /**内容**/
    private  String answerContent;

    private int status; // 0 ,1,2  //0是未执行  1是正在执行 2是已执行

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
    /**子项回答*/
    private List<StereAnswerBean> childrednList=new ArrayList<>();

    public String[] getFrontCon() {
        return frontCon;
    }

    public void setFrontCon(String[] frontCon) {
        this.frontCon = frontCon;
    }

    public StereAnswerBean() {

    }


    public List<StereAnswerBean> getChildrenList() {
        if(this.childrednList==null){
            return null;
        }else{
            return childrednList;
        }
    }

    public void addChildList(StereAnswerBean node) {
        this.childrednList.add(node);
    }


    public String getId() {
        return id;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getContentType() {
        return contentType;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    @Override
    public String toString() {
        String result="StereAnswerBean{" +
                "frontCon='" + frontCon + '\'' +
                ", id='" + id + '\'' +
                ", contentType='" + contentType + '\'' +
                ", answerContent='" + answerContent + '\'' +
                ", childrednList='" + childrednList.toString() + '\'' +
                '}';
        return result + "}";
    }



}
