package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/24.
 */

public class FinalQASemanticSlotsObjBankBean {
    private String dst;

    public FinalQASemanticSlotsObjBankBean() {
    }
    public FinalQASemanticSlotsObjBankBean(String dst) {
        this.dst = dst;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "FinalQASemanticSlotsObjBankBean{" +
                "dst='" + dst + '\'' +
                '}';
    }
}
