package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/24.
 */

public class FinalQASemanticSlotsObjAreaBean {
    private String dst;
    private String src;

    public FinalQASemanticSlotsObjAreaBean() {
    }

    public FinalQASemanticSlotsObjAreaBean(String dst, String src) {
        this.dst = dst;
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public String toString() {
        return "FinalQASemanticSlotsObjAreaBean{" +
                "dst='" + dst + '\'' +
                ", src='" + src + '\'' +
                '}';
    }
}
