package com.ist.rylibrary.base.server;

import android.util.Log;
import android.view.MotionEvent;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.PhoneSocketController;
import com.ist.rylibrary.base.util.Utils;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by maxy
 * on 2017/5/5.
 */
public class MinaServerHandler extends IoHandlerAdapter {

    private String TAG="MinaServerHandler";
    private RyApplication ryApplication;


    //从端口接受消息，会响应此方法来对消息进行处理
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        String msg = message.toString();
        if("exit".equals(msg)){
            //如果客户端发来exit，则关闭该连接
            session.close(true);
        }

        Log.i(TAG,"服务器接受消息成功...msg=="+msg);
//         dealMessage(msg,session);
        PhoneSocketController.getInstance().post(msg,session);
        super.messageReceived(session, message);
    }

    //向客服端发送消息后会调用此方法
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Log.i(TAG,"服务器发送消息成功...");
        super.messageSent(session, message);
    }

    //关闭与客户端的连接时会调用此方法
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Log.i(TAG,"服务器与客户端断开连接...");
        super.sessionClosed(session);
    }

    //服务器与客户端创建连接
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        Log.i(TAG,"服务器与客户端创建连接...");
        super.sessionCreated(session);
    }

    //服务器与客户端连接打开
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        Log.i(TAG,"服务器与客户端连接打开...");
        ryApplication=(RyApplication)RyApplication.getContext().getApplicationContext();
        //向客户端发送消息
        Date date = new Date();
        DateFormat format = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
        AiuiController.getInstance().aiui.CheckStat();
        String time = format.format(date) + Utils.getRandom();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("reqCmd","RRRespConnect");
        jsonObject.put("reqSerial",time);
        if(ryApplication.isAiuiWorkIng()){
            jsonObject.put("asrStatus","1");
        }else{
            jsonObject.put("asrStatus","0");
        }

        session.write(jsonObject);

        super.sessionOpened(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        Log.i(TAG,"服务器进入空闲状态...");
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        Log.i(TAG,"服务器发送异常...");
        super.exceptionCaught(session, cause);
    }


}
