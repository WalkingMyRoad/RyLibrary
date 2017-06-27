package com.cmcc;

/**
 * Created by minyuchun on 2017/6/12.
 * 读取身份证后的信息
 */

public class IDCardBean {
    /**打开读取身份证的连接是否正常*/
    private int openResult;
    /**读取身份证的操作是否正常*/
    private int readResult;
    /**姓名*/
    private String name;
    /**性别  男 | 女*/
    private String sex;
    /**名族*/
    private String nation;
    /**出生年月*/
    private String birthday;
    /**地址*/
    private String address;
    /**身份证号码*/
    private String IDCard_No;
    /**身份证发卡方*/
    private String issue;
    /**身份证到期日期*/
    private String end;
    /**身份证图片*/
    private String img;

    public int getOpenResult() {
        return openResult;
    }

    public void setOpenResult(int openResult) {
        this.openResult = openResult;
    }

    public int getReadResult() {
        return readResult;
    }

    public void setReadResult(int readResult) {
        this.readResult = readResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIDCard_No() {
        return IDCard_No;
    }

    public void setIDCard_No(String IDCard_No) {
        this.IDCard_No = IDCard_No;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "IDCardBean{" +
                "openResult=" + openResult +
                ", readResult=" + readResult +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", nation='" + nation + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", IDCard_No='" + IDCard_No + '\'' +
                ", issue='" + issue + '\'' +
                ", end='" + end + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
