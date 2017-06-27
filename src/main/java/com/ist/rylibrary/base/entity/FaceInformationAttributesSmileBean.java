package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/5/31.
 */

public class FaceInformationAttributesSmileBean {
    private float threshold;
    private float value;

    public FaceInformationAttributesSmileBean() {
    }

    public FaceInformationAttributesSmileBean(float threshold, float value) {
        this.threshold = threshold;
        this.value = value;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FaceInformationAttributesSmileBean{" +
                "threshold=" + threshold +
                ", value=" + value +
                '}';
    }
}
