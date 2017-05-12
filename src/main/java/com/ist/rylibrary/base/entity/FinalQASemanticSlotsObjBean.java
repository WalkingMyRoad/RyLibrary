package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/10.
 */

public class FinalQASemanticSlotsObjBean {
    private String que;
    private String item;
    private String type;
    private String var;
    private String act;
    private String actobj;
    private FinalQASemanticSlotsObjBankBean bank;
    private FinalQASemanticSlotsObjAreaBean area;

    public FinalQASemanticSlotsObjBean() {
    }

    public FinalQASemanticSlotsObjBean(String que, String item, String type, String var, String act, String actobj, FinalQASemanticSlotsObjBankBean bank, FinalQASemanticSlotsObjAreaBean area) {
        this.que = que;
        this.item = item;
        this.type = type;
        this.var = var;
        this.act = act;
        this.actobj = actobj;
        this.bank = bank;
        this.area = area;
    }

    public String getActobj() {
        return actobj;
    }

    public void setActobj(String actobj) {
        this.actobj = actobj;
    }

    public String getQue() {
        return que;
    }

    public void setQue(String que) {
        this.que = que;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public FinalQASemanticSlotsObjBankBean getBank() {
        return bank;
    }

    public void setBank(FinalQASemanticSlotsObjBankBean bank) {
        this.bank = bank;
    }

    public FinalQASemanticSlotsObjAreaBean getArea() {
        return area;
    }

    public void setArea(FinalQASemanticSlotsObjAreaBean area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "FinalQASemanticSlotsObjBean{" +
                "que='" + que + '\'' +
                ", item='" + item + '\'' +
                ", type='" + type + '\'' +
                ", var='" + var + '\'' +
                ", act='" + act + '\'' +
                ", actobj='" + actobj + '\'' +
                ", bank=" + bank +
                ", area=" + area +
                '}';
    }
}
