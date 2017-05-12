package com.ist.rylibrary.base.event;

import com.ist.rylibrary.base.service.AiuiService;

/**
 * Created by minyuchun on 2017/3/25.
 */

public class AiuiMessageEvent {
    /**aiui说话类型
     * 0 默认说话类型
     * -1 停止识别  与开始识别配对使用
     * 1 开启识别
     * */
    private int type;
    /**是否是自定义处理*/
    private boolean isCustom;
    /**传递的字符串数据*/
    private String message;

//    public AiuiMessageEvent(int type){
//        initMessageEvent(type,false,null);
//    }
//    public AiuiMessageEvent(String message){
//        initMessageEvent(AiuiService.AIUI_TYPE_DEFAULT,false,message);
//    }
//    public AiuiMessageEvent(boolean isCustom,String message){
//        initMessageEvent(AiuiService.AIUI_TYPE_DEFAULT,isCustom,message);
//    }
    public AiuiMessageEvent(int type,boolean isCustom,String message){
        this.type = type;
        this.isCustom = isCustom;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public boolean isCustom(){
        return isCustom;
    }

    public String getMessage() {
        return message;
    }
}
