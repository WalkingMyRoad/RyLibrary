package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/3/23.
 */

public class CustomSceneBean {
    private String customSceneId;
    private SceneBean sceneBean;
    private String customSceneName;
    private String businessCode;
    private String mallId;
    private String imageUrl;

    public CustomSceneBean() {
    }

    public CustomSceneBean(String customSceneId, SceneBean sceneBean, String customSceneName, String businessCode, String mallId, String imageUrl) {
        this.customSceneId = customSceneId;
        this.sceneBean = sceneBean;
        this.customSceneName = customSceneName;
        this.businessCode = businessCode;
        this.mallId = mallId;
        this.imageUrl = imageUrl;
    }

    public String getCustomSceneId() {
        return customSceneId;
    }

    public void setCustomSceneId(String customSceneId) {
        this.customSceneId = customSceneId;
    }

    public SceneBean getSceneBean() {
        return sceneBean;
    }

    public void setSceneBean(SceneBean sceneBean) {
        this.sceneBean = sceneBean;
    }

    public String getCustomSceneName() {
        return customSceneName;
    }

    public void setCustomSceneName(String customSceneName) {
        this.customSceneName = customSceneName;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getMallId() {
        return mallId;
    }

    public void setMallId(String mallId) {
        this.mallId = mallId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public String toString() {
        return "CustomSceneBean{" +
                "customSceneId='" + customSceneId + '\'' +
                ", sceneBean=" + sceneBean +
                ", customSceneName='" + customSceneName + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", mallId='" + mallId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
