package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/31.
 */

public class FaceInformationAttributesAgeBean {
    private int value;

    public FaceInformationAttributesAgeBean(int value) {
        this.value = value;
    }

    public FaceInformationAttributesAgeBean() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FaceInformationAttributesAgeBean{" +
                "value=" + value +
                '}';
    }
}
