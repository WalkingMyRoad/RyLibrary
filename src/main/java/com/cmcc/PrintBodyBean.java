package com.cmcc;

/**
 * Created by minyuchun on 2017/6/14.
 * 打印主体
 */

public class PrintBodyBean {
    /**名称*/
    private String name;
    /**人数*/
    private int number;
    /**时间*/
    private String time;
    /**提醒*/
    private String remind;

    public PrintBodyBean() {
    }

    public PrintBodyBean(String name, int number, String time, String remind) {
        this.name = name;
        this.number = number;
        this.time = time;
        this.remind = remind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    @Override
    public String toString() {
        return "PrintBodyBean{" +
                "name='" + name + '\'' +
                ", number=" + number +
                ", time='" + time + '\'' +
                ", remind='" + remind + '\'' +
                '}';
    }
}
