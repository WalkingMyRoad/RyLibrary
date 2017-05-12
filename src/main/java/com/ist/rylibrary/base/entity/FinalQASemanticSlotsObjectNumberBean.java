package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/10.
 */

public class FinalQASemanticSlotsObjectNumberBean {
    private String real;

    public FinalQASemanticSlotsObjectNumberBean() {
    }

    public FinalQASemanticSlotsObjectNumberBean(String real) {
        this.real = real;
    }

    public String getReal() {
        return real;
    }

    public void setReal(String real) {
        this.real = real;
    }

    @Override
    public String toString() {
        return "FinalQASemanticSlotsObjectNumberBean{" +
                "real='" + real + '\'' +
                '}';
    }
}
