package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/31.
 */

public class FaceInformationAttributesGlassBean {
    private int value;

    public FaceInformationAttributesGlassBean() {
    }

    public FaceInformationAttributesGlassBean(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FaceInformationAttributesGlassBean{" +
                "value=" + value +
                '}';
    }
}
