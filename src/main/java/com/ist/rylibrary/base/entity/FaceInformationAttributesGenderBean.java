package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/31.
 */

public class FaceInformationAttributesGenderBean {
    private String value;

    public FaceInformationAttributesGenderBean(String value) {
        this.value = value;
    }

    public FaceInformationAttributesGenderBean() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FaceInformationAttributesGenderBean{" +
                "value='" + value + '\'' +
                '}';
    }
}
