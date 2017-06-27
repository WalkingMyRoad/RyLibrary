package com.ist.rylibrary.base.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.iflytek.thirdparty.E;
import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.event.PhoneSocketMessageEvent;
import com.ist.rylibrary.base.event.WebSocketMessageEvent;
import com.ist.rylibrary.base.listener.RyRRttsListener;
import com.ist.rylibrary.base.module.BaseActivity;
import com.ist.rylibrary.base.module.BaseVideoViewActivity;
import com.ist.rylibrary.base.server.MinaServerHandler;
import com.ist.rylibrary.base.service.WebSocketService;
import com.ist.rylibrary.base.util.TimeUtil;
import com.ist.rylibrary.base.util.ToolUtil;
import com.ist.rylibrary.base.util.Utils;
import com.ist.rylibrary.listener.PlayVideoListener;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by maxy
 * on 2017/5/10.
 * 手机控制程序Socket
 */
public class PhoneSocketController {
    private static int PORT = 6666;
    private   NioSocketAcceptor acceptor = null;
    private  String TAG="PhoneSocketController";
    private static PlayVideoListener playVideoListener;
    private Handler handler = new Handler();
    private IoSession mSession;
    private int randCount = 0;
    private int time;
    private int count;
    private String[] data;
    private static boolean isRand = false;

    private Handler mHandler;

    private static PhoneSocketController phoneSocketController;
    private  InetSocketAddress socketAddress;

    public static PhoneSocketController getInstance(){
        if(phoneSocketController == null){
            phoneSocketController = new PhoneSocketController();
        }
        return phoneSocketController;
    }

    public static PlayVideoListener getPlayVideoListener() {
        return playVideoListener;
    }

    public static void setPlayVideoListener(PlayVideoListener playVideoListener) {
        PhoneSocketController.playVideoListener = playVideoListener;
    }

    private PhoneSocketController(){
        mHandler = new Handler();
    }
    /**
     * socket 消息
     * @param message 消息内容
     */
    public void post(String message, IoSession session){
        Context context= RyApplication.getContext();
        EventBus.getDefault().post(new PhoneSocketMessageEvent(message,session,context));
    }


    public  void startPhoneSocket(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connect();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    //创建一个非阻塞的server端的Socket
                    if(acceptor == null){
                        acceptor = new NioSocketAcceptor();
                        //避免重启时提示地址被占用,注意加上面那句，同时这句话要放在下面的bind这句前面，如果放后面就不生效，没作用了。
                        acceptor.setReuseAddress(true);
                    }
                    //设置过滤器（使用mina提供的文本换行符编解码器）
                    acceptor.getFilterChain().addLast("codec",
                            new ProtocolCodecFilter(
                                    new TextLineCodecFactory(
                                            Charset.forName("UTF-8"),
                                            LineDelimiter.WINDOWS.getValue(),
                                            LineDelimiter.WINDOWS.getValue())));
                    //自定义的编解码器
                    //acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CharsetCodecFactory()));
                    //设置读取数据的换从区大小
                    acceptor.getSessionConfig().setReadBufferSize(2048);
                    //读写通道10秒内无操作进入空闲状态
                    acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
                    //为接收器设置管理服务
                    acceptor.setHandler(new MinaServerHandler());
                    //绑定端口
                    if(socketAddress == null){
                        socketAddress=new InetSocketAddress(PORT);
                    }
                    Log.i(TAG,"服务的状态："+acceptor.isActive()+" socketAddress.isUnresolved()==="+ socketAddress.isUnresolved());
                    acceptor.bind(socketAddress);

                    Log.i(TAG,"服务器启动成功...    端口号未：" + PORT);

                }catch (IOException e){
                    Log.i(TAG,"服务器启动异常，已经启动...",e);
                    try{
                        if(acceptor!=null){
                            if(socketAddress!=null){
                                acceptor.unbind(socketAddress);
                                socketAddress=null;
                            }
                            Log.i(TAG,"关闭服务,重新连接！！");
                            acceptor.unbind();
                            acceptor=null;
                        }
                    }catch (Exception E){
                        E.printStackTrace();
                    }
                    try{
                        Thread.sleep(300);
                        Log.i(TAG,"重新启动服务！");
                        connect();
                    }catch (Exception er){
                        e.printStackTrace();
                    }
                }catch(Exception e){
                    Log.i(TAG,"服务器启动异常...",e);
                    e.printStackTrace();
                    closeSocket();
                }
            }
        },5000);
    }

    /**
     * 处理消息
     * @param message
     */
    public void dealMessage(String message,IoSession session,Context context){
        Log.i(TAG,"开始处理消息！");
        if(message.startsWith("{")){
            try{
                JSONObject jsonObject=new JSONObject(message);
                String reqCmd=jsonObject.getString("reqCmd");
                String reqSerial=jsonObject.getString("reqSerial");
                try{
                    //接收到手机端发送的消息，回一个消息给手机端
                    JSONObject respJson=new JSONObject();
                    respJson.put("reqCmd","RRRespReceived");
                    respJson.put("reqSerial",reqSerial);
                    respJson.put("receivedInfo",message);
                    session.write(respJson);
                }catch (Exception e){
                    e.printStackTrace();
                }
                SceneController.getInstance().stopLth();
                if(reqCmd.equals("serverRrCommonReq")){
                    String contentType=jsonObject.getString("contentType");
                    final     String content=jsonObject.getString("content");
                    String guideText="";
                    if(jsonObject.has("guideText")){
                        guideText=jsonObject.getString("guideText");
                    }
                    if(contentType.equals("1")){//文本
                        stopAudio();
                        AiuiController.getInstance().startTTS(content);
                    }else if(contentType.equals("2")){//音频
                        if(guideText!=null&&!guideText.equals("")){
                            AiuiController.getInstance().startTTS(guideText);
                        }
                        playMP3(content);
                    }else if(contentType.equals("3")){//视频
                      Log.i(TAG,"播放视频");
                        try{
                             MediaVoiceController.stopMP3();
                            if(guideText!=null&&!guideText.equals("")){
                                AiuiController.getInstance().startTTS(guideText);
                            }
                            if(playVideoListener!=null){
                                playVideoListener.playVideo(content);
                            }else{
                                BaseActivity.startVideoViewActivity(content);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(contentType.equals("4")){//眼睛动作
                        SceneController.getInstance().dealEye(content);
                    }else if(contentType.equals("5")){//头部动作
                        SceneController.getInstance().dealHead(content);
                    }else if(contentType.equals("6")){//底盘动作
                        SceneController.getInstance().dealChassisAction(content);
                    }
                }else if(reqCmd.equals("asrStatusChange")){
                    String asrStatus=jsonObject.getString("asrStatus");
                     dealAiuiStatus(asrStatus);
                }else if(reqCmd.equals("asrRandom")){
                    isRand = true;
                    mSession = session;
                    String randStatus = jsonObject.getString("randStatus");
                    dealRandVoice(randStatus,jsonObject);
                }else {
                   Log.i(TAG,"手机端心跳包！");
                    try{
                        //接收到手机端发送的消息，回一个消息给手机端
                        JSONObject respJson=new JSONObject();
                        respJson.put("reqCmd","RRRespHeart");
                        respJson.put("reqSerial",reqSerial);
                        session.write(respJson);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Log.i(TAG,"===不是JSON对象==="+message);
        }
    }

    /**
     *处理随机语音
     * */
    private void dealRandVoice(String status,JSONObject jsonObject){
        if(status.equals("0")){
            Log.i(TAG,"客户端停止了随机播放");
            if(randRunnable != null){
                handler.removeCallbacks(randRunnable);
            }
            randCount = 0;
            isRand = false;
        }else if(status.equals("1")){
            try {
                time = Integer.valueOf(jsonObject.getString("randTime"));
                count = Integer.valueOf(jsonObject.getString("randCount"));
                String content = jsonObject.getString("randContent");
                data = content.split("#");
                if(randRunnable != null){
                    handler.postDelayed(randRunnable,100);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable randRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                randCount ++;
                Log.i(TAG,"时间=="+time + "--总次数=="+ count);
                if(randCount  <=count){
                    Log.i(TAG,"进入随机播放");
                    int rand = Utils.getRandom(1,data.length);
                    Log.i(TAG,"随机数是=="+ rand);
                    if(data == null){
                        return;
                    }
                    String playContent = data[rand - 1];
                    SceneController.getInstance().playVoice("", playContent, false);
                    AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                        @Override
                        public boolean onComplete(String s) {
                            if(isRand){
                                Log.i(TAG,"本次随机语音播放结束");
                                handler.postDelayed(randRunnable,time * 1000);
                            }
                            return false;
                        }
                    });
                }else {
                    Log.i(TAG,"随机播放已全部完成，将状态回给客户端");
                    handler.removeCallbacks(randRunnable);
                    randCount = 0;
                    isRand = false;
                    try{
                        //将结束状态发给客户端
                        JSONObject respJson=new JSONObject();
                        respJson.put("reqCmd","asrRandom");
                        respJson.put("asrRandStatus","0");
                        if(mSession != null){
                            mSession.write(respJson);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 处理AIUI
     * asrStatus 0 关闭
     *           1 打开
     * **/
    private void dealAiuiStatus(String asrStatus){
        if(asrStatus.equals("0")){
            Log.i(TAG,"手机控制关闭语音识别！");
            stopAIUI();
        }else if(asrStatus.equals("1")){
            Log.i(TAG,"手机控制打开语音识别！");
            startAiui();
        }
    }
    /**停止语音识别**/
    private void stopAIUI(){
        //停止语音识别
        AiuiController.getInstance().aiui.AIUIStop();
        AiuiController.getInstance().setAutoWakeUp(false);
    }
    /**
     * 打开语音识别
     * **/
    private void startAiui(){
        RyApplication ryApplication=(RyApplication)RyApplication.getContext().getApplicationContext();
        if(ryApplication.isAiuiOpen()){
            Log.i(TAG,"---AIUI已经打开---");
            if(ryApplication.isAiuiWorkIng()){
                Log.i(TAG,"---AIUI正在工作，---");
            }else{
                Log.i(TAG,"---AIUI休眠唤醒，---");
                AiuiController.getInstance().setAutoWakeUp(true);
                AiuiController.getInstance().AiuiWakeUp();
            }
        }else{
            Log.i(TAG,"---AIUI没有打开---");
            AiuiController.getInstance().AiuiWork();
            AiuiController.getInstance().setAutoWakeUp(true);
            AiuiController.getInstance().AiuiWakeUp();
        }
    }
    public void closeSocket(){
        Log.i(TAG,"关闭连接！");
        Log.i(TAG,"关闭了ManageService服务 ="+ ToolUtil.getInstance().getThisPackActivityName(RyApplication.getContext()));
        if(acceptor!=null){
            if(socketAddress!=null){
                acceptor.unbind(socketAddress);
                socketAddress=null;
            }
            acceptor.unbind();
        }
    }


    /**停止所有的音频文件**/
   private void stopAudio(){
       try{
           MediaVoiceController.stopMP3();
       }catch (Exception e){
           e.printStackTrace();
       }
       try{
           if(playVideoListener!=null){
               playVideoListener.stopPlayVideo();
           }else{
               BaseVideoViewActivity activity=new   BaseVideoViewActivity();
               activity.stopVideoView();
               activity.finish();
           }


       }catch (Exception e){
           e.printStackTrace();
       }
   }

    /**
     * 播放音频
     * */
    private void playMP3(final  String path){
        Log.i(TAG,"播放的路径是："+path);
        try{
            if(playVideoListener!=null){
                playVideoListener.stopPlayVideo();
            }else{
                BaseVideoViewActivity activity= new  BaseVideoViewActivity();
                activity.stopVideoView();
                activity.finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       try{
           new Thread(new Runnable() {
               @Override
               public void run() {
                   MediaVoiceController.playVoice(path, new MediaPlayer.OnCompletionListener() {
                       @Override
                       public void onCompletion(MediaPlayer mediaPlayer) {
                           AiuiController.getInstance().setAllowInterrupt(true);
                       }
                   }, new MediaPlayer.OnErrorListener() {
                       @Override
                       public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                           AiuiController.getInstance().setAllowInterrupt(true);
                           return false;
                       }
                   });
               }
           }).start();
       }catch (Exception e){
           e.printStackTrace();
       }

    }

}
