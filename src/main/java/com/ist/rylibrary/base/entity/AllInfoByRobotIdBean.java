package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/25.
 */

public class AllInfoByRobotIdBean {
    private AllInfoRobotInfoBean robotInfo;
    private AllInfoMallInfoBean mallInfo;

    public AllInfoByRobotIdBean() {
    }

    public AllInfoByRobotIdBean(AllInfoRobotInfoBean robotInfo, AllInfoMallInfoBean mallInfo) {
        this.robotInfo = robotInfo;
        this.mallInfo = mallInfo;
    }

    public AllInfoRobotInfoBean getRobotInfo() {
        return robotInfo;
    }

    public void setRobotInfo(AllInfoRobotInfoBean robotInfo) {
        this.robotInfo = robotInfo;
    }

    public AllInfoMallInfoBean getMallInfo() {
        return mallInfo;
    }

    public void setMallInfo(AllInfoMallInfoBean mallInfo) {
        this.mallInfo = mallInfo;
    }

    @Override
    public String toString() {
        return "AllInfoByRobotIdBean{" +
                "robotInfo=" + robotInfo +
                ", mallInfo=" + mallInfo +
                '}';
    }
}
