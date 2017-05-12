package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/6.
 */

public class HttpDefauleResultBean<T> {
    private String retcode;
    private String retinfo;
    private T data;


    public HttpDefauleResultBean() {
    }

    public HttpDefauleResultBean(String retcode, String retinfo, T data) {
        this.retcode = retcode;
        this.retinfo = retinfo;
        this.data = data;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetinfo() {
        return retinfo;
    }

    public void setRetinfo(String retinfo) {
        this.retinfo = retinfo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpDefauleResultBean{" +
                "retcode='" + retcode + '\'' +
                ", retinfo='" + retinfo + '\'' +
                ", date=" + data +
                '}';
    }
}
