package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/25.
 */

public class AllInfoRobotInfoBean {
    /**AI模式*/
    private String aiType;
    /**AI模式名称*/
    private String aiTypeName;
    /**机器人id*/
    private String robotId;
    /**机器人编号*/
    private String robotNo;
    /**待机图片路径*/
    private String standbyPic;
    /**机器人类型*/
    private String robotType;
    /**机器人名称*/
    private String robotName;

    public AllInfoRobotInfoBean() {
    }

    public AllInfoRobotInfoBean(String aiType, String aiTypeName, String robotId, String robotNo, String standbyPic, String robotType, String robotName) {
        this.aiType = aiType;
        this.aiTypeName = aiTypeName;
        this.robotId = robotId;
        this.robotNo = robotNo;
        this.standbyPic = standbyPic;
        this.robotType = robotType;
        this.robotName = robotName;
    }

    public String getAiType() {
        return aiType;
    }

    public void setAiType(String aiType) {
        this.aiType = aiType;
    }

    public String getAiTypeName() {
        return aiTypeName;
    }

    public void setAiTypeName(String aiTypeName) {
        this.aiTypeName = aiTypeName;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getRobotNo() {
        return robotNo;
    }

    public void setRobotNo(String robotNo) {
        this.robotNo = robotNo;
    }

    public String getStandbyPic() {
        return standbyPic;
    }

    public void setStandbyPic(String standbyPic) {
        this.standbyPic = standbyPic;
    }

    public String getRobotType() {
        return robotType;
    }

    public void setRobotType(String robotType) {
        this.robotType = robotType;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    @Override
    public String toString() {
        return "AllInfoRobotInfoBean{" +
                "aiType='" + aiType + '\'' +
                ", aiTypeName='" + aiTypeName + '\'' +
                ", robotId='" + robotId + '\'' +
                ", robotNo='" + robotNo + '\'' +
                ", standbyPic='" + standbyPic + '\'' +
                ", robotType='" + robotType + '\'' +
                ", robotName='" + robotName + '\'' +
                '}';
    }
}
