package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/16.
 */

public class AnswerRule {
    /**同一问题连续问的次数*/
    int answerContent;
    /**答复内容 0 随机  1 按顺序*/
    int questionNum;
    /**语音包 0 随机  1 按顺序*/
    int voicePackage;

    public AnswerRule() {
    }

    public AnswerRule(int answerContent, int questionNum, int voicePackage) {
        this.answerContent = answerContent;
        this.questionNum = questionNum;
        this.voicePackage = voicePackage;
    }

    public int getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(int answerContent) {
        this.answerContent = answerContent;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public int getVoicePackage() {
        return voicePackage;
    }

    public void setVoicePackage(int voicePackage) {
        this.voicePackage = voicePackage;
    }

    @Override
    public String toString() {
        return "AnswerRule{" +
                "answerContent=" + answerContent +
                ", questionNum=" + questionNum +
                ", voicePackage=" + voicePackage +
                '}';
    }
}
