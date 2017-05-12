package com.ist.rylibrary.base.entity;

import java.util.List;

/**
 * Created by minyuchun on 2017/3/23.
 */

public class SceneBean {
    private boolean isNewRecord;
    private String sceneName;
    private String businessCode;
    private String pageId;
    private String guideTxt;
    /**[重复引导语]的语音文件*/
    private String guideFile;
    private String reGuideFile;
    private String extNlp;
    private String isMain;
    private int usePublicScene;
    private String sceneId;

    private List<SceneQABean> sceneQABeanList;

    public SceneBean() {
    }

    public SceneBean(boolean isNewRecord, String sceneName, String businessCode, String pageId, String guideTxt, String guideFile, String reGuideFile, String extNlp, String isMain, int usePublicScene, String sceneId) {
        this.isNewRecord = isNewRecord;
        this.sceneName = sceneName;
        this.businessCode = businessCode;
        this.pageId = pageId;
        this.guideTxt = guideTxt;
        this.guideFile = guideFile;
        this.reGuideFile = reGuideFile;
        this.extNlp = extNlp;
        this.isMain = isMain;
        this.usePublicScene = usePublicScene;
        this.sceneId = sceneId;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getGuideTxt() {
        return guideTxt;
    }

    public void setGuideTxt(String guideTxt) {
        this.guideTxt = guideTxt;
    }

    public String getGuideFile() {
        return guideFile;
    }

    public void setGuideFile(String guideFile) {
        this.guideFile = guideFile;
    }

    public String getReGuideFile() {
        return reGuideFile;
    }

    public void setReGuideFile(String reGuideFile) {
        this.reGuideFile = reGuideFile;
    }

    public String getExtNlp() {
        return extNlp;
    }

    public void setExtNlp(String extNlp) {
        this.extNlp = extNlp;
    }

    public String getIsMain() {
        return isMain;
    }

    public void setIsMain(String isMain) {
        this.isMain = isMain;
    }

    public int getUsePublicScene() {
        return usePublicScene;
    }

    public void setUsePublicScene(int usePublicScene) {
        this.usePublicScene = usePublicScene;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public List<SceneQABean> getSceneQABeanList() {
        return sceneQABeanList;
    }

    public void setSceneQABeanList(List<SceneQABean> sceneQABeanList) {
        this.sceneQABeanList = sceneQABeanList;
    }

    @Override
    public String toString() {
        return "SceneBean{" +
                "isNewRecord=" + isNewRecord +
                ", sceneName='" + sceneName + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", pageId='" + pageId + '\'' +
                ", guideTxt='" + guideTxt + '\'' +
                ", guideFile='" + guideFile + '\'' +
                ", reGuideFile='" + reGuideFile + '\'' +
                ", extNlp='" + extNlp + '\'' +
                ", isMain='" + isMain + '\'' +
                ", usePublicScene=" + usePublicScene +
                ", sceneId='" + sceneId + '\'' +
                ", sceneQABeanList=" + sceneQABeanList +
                '}';
    }
}
