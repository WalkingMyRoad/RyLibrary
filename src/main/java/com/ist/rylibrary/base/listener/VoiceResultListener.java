package com.ist.rylibrary.base.listener;

import com.ist.rylibrary.base.entity.FinalQABean;

/**
 * Created by wjz on 2017/4/14.
 * (语音返回回调)
 */

public interface VoiceResultListener {
    void onConfim();  //cmd 确认
    void onCancle();  // cmd 取消，返回
    void onResult(String cmd);  //其他的一下cmd命令
    void onPhone(String phone); //号码达人返回
}
