package com.ist.rylibrary.base.listener;

/**
 * Created by maxy
 * on 2017/5/25.
 * 人脸识别上传异常
 */
public interface FaceAddErrorListener {
    public void onComplete(String faceId,String gender);
    public void errorMessage(String msg);
}
