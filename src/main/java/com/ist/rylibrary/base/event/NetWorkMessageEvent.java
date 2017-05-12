package com.ist.rylibrary.base.event;

import com.ist.rylibrary.base.service.AiuiService;

/**
 * Created by minyuchun on 2017/3/25.
 */

public class NetWorkMessageEvent {
    /**网络类型*/
    private int netType;
    /**是否是自定义处理*/
    private boolean isCustom;
    /**是否有网络*/
    private boolean isNetWork;
    /**是否能ping通服务器*/
    private boolean isPingServer;

    public NetWorkMessageEvent(int type,boolean isCustom,boolean isNetWork,boolean isPingServer){
        this.netType = type;
        this.isCustom = isCustom;
        this.isNetWork = isNetWork;
        this.isPingServer = isPingServer;
    }
    public boolean isCustom(){
        return isCustom;
    }

    public int getNetType() {
        return netType;
    }

    public boolean isNetWork() {
        return isNetWork;
    }

    public boolean isPingServer() {
        return isPingServer;
    }
}
