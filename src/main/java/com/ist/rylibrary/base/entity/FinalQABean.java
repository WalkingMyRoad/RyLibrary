package com.ist.rylibrary.base.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by minyuchun on 2017/3/25.
 * 讯飞解析的问答类
 */

public class FinalQABean {
    /**service*/
    private String service;
    /**用户的问的基本语句*/
    private String text;
    /***/
    private FinalQASemanticBean semantic;
    /***/
    private String rc;
    /***/
    private String operation;
    private FinalQAData data;
    /**匹配的答 来自讯飞*/
    private FinalQAnswerBean answer;
    /**默认的回答*/
    String[] answers = new String[]{"哎呀，我没听明白呢","贵宾，您说慢点，RR还在学习中呢","没有听清楚呢，您在大声的说一遍"};

    public FinalQABean() {
    }

    public FinalQABean(String service, String text, FinalQASemanticBean semantic, String rc, String operation, FinalQAData data, FinalQAnswerBean answer) {
        this.service = service;
        this.text = text;
        this.semantic = semantic;
        this.rc = rc;
        this.operation = operation;
        this.data = data;
        this.answer = answer;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FinalQASemanticBean getSemantic() {
        return semantic;
    }

    public void setSemantic(FinalQASemanticBean semantic) {
        this.semantic = semantic;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public FinalQAnswerBean getAnswer() {
        return answer;
    }

    public void setAnswer(FinalQAnswerBean answer) {
        this.answer = answer;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public FinalQAData getData() {
        return data;
    }

    public void setData(FinalQAData data) {
        this.data = data;
    }

    public String getoneAnswer(){
        if(answer!=null){
            return answer.getText();
        }else{
            return answers[new Random().nextInt(answers.length)];
        }
    }

    @Override
    public String toString() {
        return "FinalQABean{" +
                "service='" + service + '\'' +
                ", text='" + text + '\'' +
                ", semantic=" + semantic +
                ", rc='" + rc + '\'' +
                ", operation='" + operation + '\'' +
                ", data=" + data +
                ", answer=" + answer +
                ", answers=" + Arrays.toString(answers) +
                '}';
    }
}
