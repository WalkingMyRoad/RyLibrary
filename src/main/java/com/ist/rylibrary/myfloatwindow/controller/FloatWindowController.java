package com.ist.rylibrary.myfloatwindow.controller;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.myfloatwindow.event.FloatWindowEvent;
import com.ist.rylibrary.myfloatwindow.service.FloatWindowService;
import com.ist.rylibrary.myfloatwindow.view.FloatWindowBigView;
import com.ist.rylibrary.myfloatwindow.view.FloatWindowSmallView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by minyuchun on 2017/3/7.
 */

public class FloatWindowController {
    /**悬浮窗控制实例*/
    private static FloatWindowController mFloatWindowController;
    /**
     * 小悬浮窗View的实例
     */
    private FloatWindowSmallView smallWindow;
    /**
     * 大悬浮窗View的实例
     */
    private FloatWindowBigView bigWindow;
    /**
     * 小悬浮窗View的参数
     */
    private LayoutParams smallWindowParams;
    /**
     * 大悬浮窗View的参数
     */
    private LayoutParams bigWindowParams;
    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private WindowManager mWindowManager;
    /**
     * 用于获取手机可用内存
     */
    private ActivityManager mActivityManager;
    /**
     * 悬浮窗回调监听
     */
    private FloatWindowListener mFloatWindowListener;
    /***
     * 用具记录大的悬浮窗在位置的距离屏幕有房的距离，用于小图标的使用
     */
    private int bigToRightWidth;
    /**最近一次漂浮窗状态*/
    private static int lastFloatWindowState = FloatWindowService.FLOAT_OPEN;

    /**
     * 获取悬浮窗控制实例
     * @return 返回实例
     */
    public static FloatWindowController getInstance(){
        if(mFloatWindowController == null){
            mFloatWindowController = new FloatWindowController();
        }
        return mFloatWindowController;
    }

    /**
     * 构造函数
     */
    public FloatWindowController(){

    }

    public void open(){
        openBigCloseSmall();
    }

    /**
     * 悬浮窗发送消息
     * @param state 消息类型
     */
    public void post(int state){
        lastFloatWindowState = state;
        post(false,state,null,null);
    }
    /**
     * 悬浮窗发送消息
     * @param messageRobot  机器人说的话
     * @param messagePerson 人说的话
     */
    public void post(String messageRobot,String messagePerson){
        EventBus.getDefault().post(new FloatWindowEvent(false,lastFloatWindowState,messageRobot,messagePerson));
    }
    /**
     * 悬浮窗发送消息
     * @param isCustom 是否自定义
     * @param state 发送的类型
     * @param messageRobot  机器人说的话
     * @param messagePerson 人说的话
     */
    public void post(boolean isCustom,int state,String messageRobot,String messagePerson){
        lastFloatWindowState = state;
        EventBus.getDefault().post(new FloatWindowEvent(isCustom,state,messageRobot,messagePerson));
    }

    /**
     * 自定义回收函数
     * @param isCustom 是否自定义
     * @param state 发送的类型
     * @param messageRobot 机器人说的话
     * @param messagePerson 人说的话
     */
    public void recovery(boolean isCustom,int state,String messageRobot,String messagePerson){
        if(mFloatWindowListener!=null){
            lastFloatWindowState = state;
            mFloatWindowListener.FloatWindowMessage(isCustom,state,messageRobot,messagePerson);
        }
    }

    /**
     * 悬浮窗监听
     */
    public interface FloatWindowListener{
        /***
         *  aiui自定义返回
         * @param isCustom  是否自定义
         * @param state 返回类型
         * @param messageRobot  机器人说的话
         * @param messagePerson 人说的话
         */
        void FloatWindowMessage(boolean isCustom,int state,String messageRobot,String messagePerson);
    }

    /**
     * 设置悬浮窗回调监听
     * @param listener 新建的监听
     */
    public void setFloatWindowListener(FloatWindowListener listener){
        mFloatWindowListener = listener;
    }


    /**打开大悬浮的对话框*/
    public void openBigCloseSmall(){
        openBig();
        closeSmall();
    }
    /**打开小对话的悬浮框*/
    public void openSmallCloseBig(){
        openSmall();
        closeBig();
    }
    /**打开大悬浮窗*/
    public void openBig(){
        lastFloatWindowState = FloatWindowService.FLOAT_OPEN;
        if(!isBigWindowShowing()){
            createBigWindow(RyApplication.getContext());
        }
    }
    /**关闭大悬浮窗*/
    public void closeBig(){
        if(isBigWindowShowing()){
            removeBigWindow(RyApplication.getContext());
        }
    }
    /**打开大悬浮窗*/
    public void openSmall(){
        lastFloatWindowState = FloatWindowService.FLOAT_CLOSE;
        if(!isSmallWindowShowing()){
            createSmallWindow(RyApplication.getContext());
        }
    }
    /**关闭大悬浮窗*/
    public void closeSmall(){
        if(isSmallWindowShowing()){
            removeSmallWindow(RyApplication.getContext());
        }
    }


    public void defaultHandling(int state,String messageRobot,String messagePerson){
        switch (state){
            case FloatWindowService.FLOAT_OPEN:
                openBigCloseSmall();
                speak(messageRobot,messagePerson);
                break;
            case FloatWindowService.FLOAT_CLOSE:
                openSmallCloseBig();
                break;
            case FloatWindowService.FLOAT_DESTROY:
                closeBig();
                closeSmall();
                break;
        }

    }

    /***
     *  说话显示
     * @param robotMessage  机器人说的话
     * @param personMessage  人说的话
     */
    public void speak(String robotMessage,String personMessage){
        if(robotMessage == null && personMessage==null){
            if(isBigWindowShowing()){
//                setSonic(false);
            }
            return;
        }
        if(robotMessage!=null && robotMessage.length()>0){
            robotSpeak(robotMessage);
        }if(personMessage!=null && personMessage.length()>0){
            personSpeak(personMessage);
        }
    }

    /**
     * 机器人说的话
     * @param message 说话的内容
     */
    public void robotSpeak(String message){
        if(isBigWindowShowing()){
            setRobotSpeak(message);
//            setSonic(true);
        }
    }

    /**
     * 人说的话
     * @param message 说话的内容
     */
    public void personSpeak(String message){
        if(isBigWindowShowing()){
            setPersonSpeak(message);
        }
    }


    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public void createSmallWindow(Context context) {
        lastFloatWindowState = FloatWindowService.FLOAT_CLOSE;
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            smallWindow = new FloatWindowSmallView(context);
//            if (smallWindowParams == null) {
                smallWindowParams = new LayoutParams();
                smallWindowParams.type = LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
//                smallWindowParams.x = screenWidth / 2 - FloatWindowSmallView.viewWidth / 2;
                smallWindowParams.x = screenWidth - bigToRightWidth - FloatWindowSmallView.viewWidth;
                smallWindowParams.y = screenHeight - FloatWindowSmallView.viewHeight;
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
//            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }
    /**
     * 创建一个大悬浮窗。位置为屏幕正中间。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public void createBigWindow(Context context) {
        lastFloatWindowState = FloatWindowService.FLOAT_OPEN;
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (bigWindow == null) {
            bigWindow = new FloatWindowBigView(context);
//            if (bigWindowParams == null) {//用于存储当前的位置以便后面显示在上一次的位置
                bigWindowParams = new LayoutParams();
                bigWindowParams.x = screenWidth / 2 - FloatWindowBigView.viewWidth / 2;
                bigToRightWidth = bigWindowParams.x;
                bigWindowParams.y = screenHeight - FloatWindowBigView.viewHeight;
                bigWindowParams.type = LayoutParams.TYPE_PHONE;
                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
                bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                bigWindowParams.width = FloatWindowBigView.viewWidth;
                bigWindowParams.height = FloatWindowBigView.viewHeight;
//            }
            bigWindow.setParams(bigWindowParams);
            windowManager.addView(bigWindow, bigWindowParams);
        }
    }

    /**
     * 设置语音条下的 动画是否播放
     * @param isDynamic  是否播放动画
     */
    public void setSonic(boolean isDynamic){
        if (bigWindow!=null){
            bigWindow.setSonicDynamic(isDynamic);
        }
    }

    public void setRobotSpeak(String message){
        bigWindow.setTxtSpeakRobot(message);
    }

    public void  setPersonSpeak(String message){
        bigWindow.setTxtSpeakPerson(message);
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public void removeBigWindow(Context context) {
        if (bigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWindow);
            bigWindow = null;
        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public boolean isBigWindowShowing(){
        return bigWindow != null;
    }
    public boolean isSmallWindowShowing() {
        return smallWindow != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     *            必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    private ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

}
