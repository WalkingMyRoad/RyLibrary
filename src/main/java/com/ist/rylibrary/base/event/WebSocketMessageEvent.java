package com.ist.rylibrary.base.event;

import com.ist.rylibrary.base.service.AiuiService;
import com.ist.rylibrary.base.service.WebSocketService;

/**
 * Created by minyuchun on 2017/3/25.
 */

public class WebSocketMessageEvent {
    /**是否是自定义处理*/
    private boolean isCustom;
    /**消息的类型*/
    private int type;
    /**消息内容*/
    private String message;

    public WebSocketMessageEvent(boolean isCustom,int messageType,String message){
        this.isCustom = isCustom;
        this.type = messageType;
        this.message = message;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
