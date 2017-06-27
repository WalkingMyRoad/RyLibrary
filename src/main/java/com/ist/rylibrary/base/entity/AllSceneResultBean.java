package com.ist.rylibrary.base.entity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by minyuchun on 2017/4/7.
 */

public class AllSceneResultBean {
    private String retcode;
    private String retinfo;
    private List<TipsBean> tipsData;
    private List<SceneQABean> sceneQas;
    private List<SceneBean> scenes;
    private List<PageBean> pages;
    private List<CustomSceneBean> customScenes;
    private Robotsbean robots;
    private AnswerRule answerRule;
    private List<SceneQABean> sceneQasNew;
//    private JSONArray sceneQas;
//    private JSONArray scenes;
//    private JSONArray pages;
//    private JSONArray customScenes;
//    private JSONObject robots;


    public AllSceneResultBean() {
    }

    public AllSceneResultBean(String retcode, String retinfo, List<TipsBean> tipsData, List<SceneQABean> sceneQas, List<SceneBean> scenes, List<PageBean> pages, List<CustomSceneBean> customScenes, Robotsbean robots, AnswerRule answerRule, List<SceneQABean> sceneQasNew) {
        this.retcode = retcode;
        this.retinfo = retinfo;
        this.tipsData = tipsData;
        this.sceneQas = sceneQas;
        this.scenes = scenes;
        this.pages = pages;
        this.customScenes = customScenes;
        this.robots = robots;
        this.answerRule = answerRule;
        this.sceneQasNew = sceneQasNew;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetinfo() {
        return retinfo;
    }

    public void setRetinfo(String retinfo) {
        this.retinfo = retinfo;
    }

    public List<SceneQABean> getSceneQas() {
        if(sceneQas!=null){
            return sceneQas;
        }else if(sceneQasNew!=null){
            return sceneQasNew;
        }else{
            return null;
        }
    }

    public void setSceneQas(List<SceneQABean> sceneQas) {
        this.sceneQas = sceneQas;
    }

    public List<SceneBean> getScenes() {
        return scenes;
    }

    public void setScenes(List<SceneBean> scenes) {
        this.scenes = scenes;
    }

    public List<PageBean> getPages() {
        return pages;
    }

    public void setPages(List<PageBean> pages) {
        this.pages = pages;
    }

    public List<CustomSceneBean> getCustomScenes() {
        return customScenes;
    }

    public void setCustomScenes(List<CustomSceneBean> customScenes) {
        this.customScenes = customScenes;
    }

    public Robotsbean getRobots() {
        return robots;
    }

    public void setRobots(Robotsbean robots) {
        this.robots = robots;
    }

    public AnswerRule getAnswerRule() {
        return answerRule;
    }

    public void setAnswerRule(AnswerRule answerRule) {
        this.answerRule = answerRule;
    }

    public List<SceneQABean> getSceneQasNew() {
        return sceneQasNew;
    }

    public void setSceneQasNew(List<SceneQABean> sceneQasNew) {
        this.sceneQasNew = sceneQasNew;
    }

    public List<TipsBean> getTipsData() {
        return tipsData;
    }

    public void setTipsData(List<TipsBean> tipsData) {
        this.tipsData = tipsData;
    }

    @Override
    public String toString() {
        return "AllSceneResultBean{" +
                "retcode='" + retcode + '\'' +
                ", retinfo='" + retinfo + '\'' +
                ", tipsData=" + tipsData +
                ", sceneQas=" + sceneQas +
                ", scenes=" + scenes +
                ", pages=" + pages +
                ", customScenes=" + customScenes +
                ", robots=" + robots +
                ", answerRule=" + answerRule +
                ", sceneQasNew=" + sceneQasNew +
                '}';
    }

    public JSONArray getJaSceneQas() throws JSONException {
        return new JSONArray(new Gson().toJson(getSceneQas()));
    }
    public JSONArray getJaScenes() throws JSONException {
        return new JSONArray(new Gson().toJson(getScenes()));
    }
    public JSONArray getJaPages() throws JSONException {
        return new JSONArray(new Gson().toJson(getPages()));
    }
    public JSONArray getJaCustomScenes() throws JSONException {
        return new JSONArray(new Gson().toJson(getCustomScenes()));
    }
    public JSONObject getJsRobots() throws JSONException {
        return new JSONObject(new Gson().toJson(getRobots(),Robotsbean.class));
    }
}
