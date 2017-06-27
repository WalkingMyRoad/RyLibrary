package com.cmcc;

/**
 * Created by minyuchun on 2017/6/14.
 * 打印的内容
 */

public class PrintInfoBean {
    /**连接mac地址*/
    private String MAC;
    /**大标题*/
    private String headLine;
    /**副标题*/
    private String subtitle;
    /**打印主体*/
    private PrintBodyBean body;
    /**结束语*/
    private String ending;

    public PrintInfoBean() {
    }

    public PrintInfoBean(String MAC, String headLine, String subtitle, PrintBodyBean body, String ending) {
        this.MAC = MAC;
        this.headLine = headLine;
        this.subtitle = subtitle;
        this.body = body;
        this.ending = ending;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public PrintBodyBean getBody() {
        return body;
    }

    public void setBody(PrintBodyBean body) {
        this.body = body;
    }

    public String getEnding() {
        return ending;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    @Override
    public String toString() {
        return "PrintInfoBean{" +
                "MAC='" + MAC + '\'' +
                ", headLine='" + headLine + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", body=" + body +
                ", ending='" + ending + '\'' +
                '}';
    }
}
