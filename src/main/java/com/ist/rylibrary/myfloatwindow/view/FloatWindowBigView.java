package com.ist.rylibrary.myfloatwindow.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ist.rylibrary.R;
import com.ist.rylibrary.base.controller.ActivityController;
import com.ist.rylibrary.myfloatwindow.controller.FloatWindowController;
import com.ist.rylibrary.myfloatwindow.service.FloatWindowService;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

/**
 * Created by minyuchun on 2017/3/7.
 */

public class FloatWindowBigView extends LinearLayout {
    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;
    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;
    /**
     * 用于更新大悬浮窗的位置
     */
    private WindowManager windowManager;
    /**
     * 大悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在大悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在大悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    private TextView txtSpeakRobot;

    private TextView txtSpeakPerson;

    private ImageView imgSonicStaticDynamic;

    private Button btnClose;
    /**动画集成*/
    private AnimationDrawable animationDrawable;

    public FloatWindowBigView(final Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_float_big, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        btnClose = (Button) findViewById(R.id.close_float);
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                if (ActivityController.getInstance().getCount()>0){
                    FloatWindowController.getInstance().post(FloatWindowService.FLOAT_CLOSE);
                }else{
                    FloatWindowController.getInstance().closeSmall();
                    FloatWindowController.getInstance().closeBig();
                }

            }
        });
        txtSpeakRobot = (TextView) findViewById(R.id.rr_speak);
        txtSpeakPerson = (TextView) findViewById(R.id.user_speak);
        imgSonicStaticDynamic = (ImageView) findViewById(R.id.sonic_static_dynamic);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
//                    openBigWindow();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将大悬浮窗的参数传入，用于更新大悬浮窗的位置。
     *
     * @param params
     *            大悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }


    public void setTxtSpeakRobot(String message){
        if(txtSpeakRobot!=null){
            txtSpeakRobot.setText(message);
        }
    }

    public void setTxtSpeakPerson(String message){
        if(txtSpeakPerson!=null){
            txtSpeakPerson.setText(message);
        }
    }

    /***
     * 设置是否播放动画效果
     * @param isDynamic  是播放动画
     */
    public void setSonicDynamic(boolean isDynamic){
        imgSonicStaticDynamic.setBackgroundResource(R.drawable.animation_microphone_sound_dynamic);
        animationDrawable = (AnimationDrawable) imgSonicStaticDynamic.getBackground();
        if(imgSonicStaticDynamic!=null){
            if(isDynamic){
                if(!animationDrawable.isRunning()){
                    animationDrawable.start();
                }
            }else{
                if(animationDrawable.isRunning()){
                    animationDrawable.stop();
                }
                imgSonicStaticDynamic.setImageResource(R.drawable.microphone_sound_static);
            }
        }
    }
}
