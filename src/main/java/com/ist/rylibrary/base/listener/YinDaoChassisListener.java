package com.ist.rylibrary.base.listener;

/**
 * Created by minyuchun on 2017/4/21.
 * 引导流程底盘监听
 */

public interface YinDaoChassisListener {
    /***
     * 引导流程 底盘完成的监听回调
     * @param isComplete 是否正常完成
     * @param where  到达后的点位名称
     * @return 返回值用于在完成后确认是否删除监听
     */
    boolean onChassisComplete(boolean isComplete, String where);
    /**
     * 引导流程的错误监听
     * @return 返回值用于在完成后确认是否删除监听
     */
    boolean onError();
}
