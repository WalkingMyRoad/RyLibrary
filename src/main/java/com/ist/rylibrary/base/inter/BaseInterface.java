package com.ist.rylibrary.base.inter;

/**
 * Created by minyuchun on 2017/4/19.
 * 基础接口实现类
 */

public interface BaseInterface {
    /***
     * 接口调用开始
     * @return  是否需要继续允许以下方法
     */
    boolean start();
    /***
     * 进程执行
     * @return
     */
    boolean doProcess();
    /***
     *
     * @param isNormal
     */
    void end(boolean isNormal);
}
