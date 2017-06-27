package com.ist.rylibrary.base.event;

import android.content.Context;

import org.apache.mina.core.session.IoSession;

/**
 * Created by maxy
 * on 2017/5/10.
 */
public class PhoneSocketMessageEvent {
   private IoSession session;
    /**消息内容*/
    private String message;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PhoneSocketMessageEvent(String message, IoSession session,Context context){
        this.session = session;
        this.message = message;
        this.context=context;

    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
