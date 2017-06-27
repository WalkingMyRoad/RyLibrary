package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/31.
 * 脸的特征属性
 */

public class FaceInformationAttributesBean {
    /**性别*/
    private FaceInformationAttributesGenderBean gender;
    /**年龄*/
    private FaceInformationAttributesAgeBean age;
    /**笑容*/
    private FaceInformationAttributesSmileBean smiling;
    /**是否佩戴眼镜*/
    private FaceInformationAttributesGlassBean glass;
    /**人脸姿势*/
    private FaceInformationAttributesHeadposeBean headpose;

    public FaceInformationAttributesBean(FaceInformationAttributesGenderBean gender, FaceInformationAttributesAgeBean age, FaceInformationAttributesSmileBean smiling, FaceInformationAttributesGlassBean glass, FaceInformationAttributesHeadposeBean headpose) {
        this.gender = gender;
        this.age = age;
        this.smiling = smiling;
        this.glass = glass;
        this.headpose = headpose;
    }

    public FaceInformationAttributesBean() {
    }

    public FaceInformationAttributesGenderBean getGender() {
        return gender;
    }

    public void setGender(FaceInformationAttributesGenderBean gender) {
        this.gender = gender;
    }

    public FaceInformationAttributesAgeBean getAge() {
        return age;
    }

    public void setAge(FaceInformationAttributesAgeBean age) {
        this.age = age;
    }

    public FaceInformationAttributesSmileBean getSmiling() {
        return smiling;
    }

    public void setSmiling(FaceInformationAttributesSmileBean smiling) {
        this.smiling = smiling;
    }

    public FaceInformationAttributesGlassBean getGlass() {
        return glass;
    }

    public void setGlass(FaceInformationAttributesGlassBean glass) {
        this.glass = glass;
    }

    public FaceInformationAttributesHeadposeBean getHeadpose() {
        return headpose;
    }

    public void setHeadpose(FaceInformationAttributesHeadposeBean headpose) {
        this.headpose = headpose;
    }


    @Override
    public String toString() {
        return "FaceInformationAttributesBean{" +
                "gender=" + gender +
                ", age=" + age +
                ", smiling=" + smiling +
                ", glass=" + glass +
                ", headpose=" + headpose +
                '}';
    }
}
