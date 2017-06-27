package com.ist.rylibrary.listener;

/**
 * Created by maxy
 * on 2017/5/23.
 * 人脸识别的结果
 */
public interface FaceResultListener {
    /**
     * 人脸处理的结果
     * @param isNewPerson  是否是新用户
     * @param result  人脸对比的结果字符串
     * @return  是否消除监听
     */
    public boolean dealFace(boolean isNewPerson,String result);
}
