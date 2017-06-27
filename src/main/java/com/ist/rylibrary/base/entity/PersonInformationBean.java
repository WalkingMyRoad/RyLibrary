package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/6/1.
 * 识别到的人员的信息属性
 */

public class PersonInformationBean {
    /**face++中个人员的id*/
    private String id;
    /**是否是老用户使用*/
    private boolean isNewPerson;
    /**人员姓名*/
    private String name;
    /**性别*/
    private String gender;
    /**脸部图片地址*/
    private String facePath;
    /**人脸属性*/
    private FaceInformationAttributesBean attributes;

    public PersonInformationBean() {
    }

    public PersonInformationBean(String id, boolean isNewPerson, String name, String gender, String facePath, FaceInformationAttributesBean attributes) {
        this.id = id;
        this.isNewPerson = isNewPerson;
        this.name = name;
        this.gender = gender;
        this.facePath = facePath;
        this.attributes = attributes;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if(gender.equals("1")){
            gender = "Male";
        }else if(gender.equals("0")){
            gender = "Female";
        }else if(gender.equals("null")){
            gender = null;
        }
        this.gender = gender;
    }

    public boolean isNewPerson() {
        return isNewPerson;
    }

    public void setNewPerson(boolean newPerson) {
        isNewPerson = newPerson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.equals("null")){
            name = null;
        }
        this.name = name;
    }

    public FaceInformationAttributesBean getAttributes() {
        return attributes;
    }

    public void setAttributes(FaceInformationAttributesBean attributes) {
        this.attributes = attributes;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PersonInformationBean{" +
                "id='" + id + '\'' +
                ", isNewPerson=" + isNewPerson +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", facePath='" + facePath + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
