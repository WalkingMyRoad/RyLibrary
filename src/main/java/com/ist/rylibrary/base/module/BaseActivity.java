package com.ist.rylibrary.base.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.ActivityController;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.listener.specialAiuiListener;
import com.ist.rylibrary.base.service.InfraredService;
import com.ist.rylibrary.base.ui.ProgressDialog;
import com.ist.rylibrary.base.ui.VerticalSeekBar;
import com.ist.rylibrary.base.util.BaseLogUtil;
import com.ist.rylibrary.base.util.TimeUtil;
import com.ist.rylibrary.face.activity.FloatCamera;
import com.ist.rylibrary.listener.FaceResultListener;
import com.wewins.facelibrary.api.ApiConstants;
import com.wewins.facelibrary.api.NewApiBase;
import com.wewins.facelibrary.api.rr.RRBusinessApi;
import com.wewins.facelibrary.utils.FileUtil;
import com.wewins.facelibrary.utils.ImageUtil;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by minyuchun on 2016/12/26.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, VerticalSeekBar.OnSeekBarChangeListener,specialAiuiListener {
    protected Context context;
    protected RyApplication ryApplication;
    /**
     * 使用LOG日志打印是出现的
     */
    protected String TAG;
    /**
     * 主体用于添加到frame中展示的样式
     */
    private RelativeLayout.LayoutParams paramsCenter;
    /**
     * frame 父控件
     */
    private RelativeLayout parent;
    /**
     * 外部添加 底部按钮栏
     */
    private LinearLayout footerButtonLayout;
    /**
     * 主体界面
     */
    private ViewGroup frameBody;
    /**
     * 标题栏,扩展使用
     */
    private Toolbar toolbar;
    /**
     * 中间的自定义标题,扩展使用
     */
    private TextView titleTxt;
    /**
     * 每个界面初始化的日志输出
     */
    public BaseLogUtil log;
    private GestureDetector gestureDetector;

    private AudioManager audioManager;
    private int maxVolume = 0;
    private VerticalSeekBar seekBar;
    private LinearLayout seekbar_liner;
    private ImageView voiceImage;
    private Handler handler = new Handler();
    private TextView txt_elec;

    /**
     * 电量展示
     **/
    private ImageView img_elec;
    /**
     * 加载框提示
     */
    protected static ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            TAG = this.getClass().getSimpleName();
            context = this;
            ryApplication=(RyApplication)getApplicationContext();
            //设置屏幕全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (getMainView() > 0) {
                setContentView(getMainView());
            } else {
                setContentView(R.layout.activity_frame);
            }

//            BaseLogUtil.LogCatType = Log.ERROR;
//            BaseLogUtil.isUseLogCat = false;
            log = new BaseLogUtil(this.getClass());
            log.d("onCreate");
            paramsCenter = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            paramsCenter.addRule(RelativeLayout.BELOW, R.id.frame_top);
            paramsCenter.addRule(RelativeLayout.ABOVE, R.id.frame_footer_main);
            paramsCenter.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            paramsCenter.leftMargin = 0;
            paramsCenter.rightMargin = 0;
            txt_elec = (TextView) findViewById(R.id.txt_elec);
            img_elec = (ImageView) findViewById(R.id.img_elec);

            ActivityController.getInstance().addActivity(this);
            initBaseView();
            setMenuAlwaysShow();
            initController();
            initView();
            initGes();
            initVoice();
//            SceneController.getInstance().setPageActionListener(new SceneController.pageActionListener() {
//                @Override
//                public boolean pageAction(String pageAction) {
//                    Log.i(TAG,"获取到页面的动作是："+pageAction);
//                    if(pageAction.equals("max_voice")){
//                        dealVoice("volume_plus");
//                    }else if(pageAction.equals("min_voice")){
//                        dealVoice("volume_minus");
//                    }
//                    return false;
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 public static void setFaceText(String text){
     try{
         Toast.makeText(RyApplication.getContext(),text,Toast.LENGTH_SHORT).show();
     }catch (Exception e){
         e.printStackTrace();
     }

 }
    public static void startVideoViewActivity(String path){

        try {
            Bundle bundle=new Bundle();
            bundle.putString("path",path);
            InfraredService.clearWaitGuideCount();
            Intent intent = new Intent(RyApplication.getContext(), BaseVideoViewActivity.class);
                intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            RyApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void initVoice() {
        try {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            seekBar = (VerticalSeekBar) findViewById(R.id.voice_seekbar);
            if (maxVolume != 0) {
                seekBar.setMax(maxVolume);  //设置最大值
            }
            seekBar.setOnSeekBarChangeListener(this);
            seekbar_liner = (LinearLayout) findViewById(R.id.seek_liner);
            voiceImage = (ImageView) findViewById(R.id.voice_image);
            voiceImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    voiceImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showVoice(1);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示音量进度条 1是点击 2是语音
    private void showVoice(int flag) {
        try {
            if (flag == 1) {
                if (voiceImage.getVisibility() == View.VISIBLE) {
                    voiceImage.setVisibility(View.GONE);
                    seekbar_liner.setVisibility(View.VISIBLE);
                } else {
                    voiceImage.setVisibility(View.VISIBLE);
                    seekbar_liner.setVisibility(View.GONE);
                }
            } else if (flag == 2) {
                if (voiceImage.getVisibility() == View.VISIBLE) {
                    voiceImage.setVisibility(View.GONE);
                    seekbar_liner.setVisibility(View.VISIBLE);
                }
            }
            handler.removeCallbacks(minVoiceImageRunnable);
            handler.postDelayed(minVoiceImageRunnable, 20000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //音量处理
    private void dealVoice(String insType) {
        try {
            if (insType.equals("volume_plus")) {  //增大音量
                if (seekBar != null && maxVolume != 0 && seekBar.getProgress() != maxVolume) {
                    dealSeek(seekBar.getProgress() + 1);
                }
            } else if (insType.equals("volume_minus")) {   //减小音量
                if (seekBar != null && seekBar.getProgress() != 0) {
                    dealSeek(seekBar.getProgress() - 1);
                }
            } else if (insType.equals("volume_max")) {  //最大音量
                if (seekBar != null && maxVolume != 0) {
                    dealSeek(maxVolume);
                }
            } else if (insType.equals("volume_min")) {  //最小音量
                if (seekBar != null) {
                    dealSeek(0);
                }
            } else if (insType.equals("mute")) {     //静音
                if (audioManager != null) {
                    audioManager.setRingerMode(AudioManager.STREAM_SYSTEM);
                    voiceImage.setImageResource(R.drawable.volume_mute);
                }
            } else if (insType.equals("unmute")) {   //静音恢复
                if (audioManager != null) {
                    audioManager.setRingerMode(AudioManager.STREAM_SYSTEM);
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, seekBar.getProgress(), 0);
                    voiceImage.setImageResource(R.drawable.volume);
                }
            }
            showVoice(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealSeek(int progress) {
        try {
            if (seekBar != null) {
                seekBar.setProgress(progress);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                }
            }
            checkVoice(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断显示
    private void checkVoice(int progress) {
        try {
            if (voiceImage != null) {
                if (progress == 0) {
                    voiceImage.setImageResource(R.drawable.volume_mute);
                } else {
                    voiceImage.setImageResource(R.drawable.volume);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGes() {
        try {
            gestureDetector = new GestureDetector(this, onGestureListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // go home
            return true;
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            boolean result = super.dispatchTouchEvent(ev);
            InfraredService.clearWaitGuideCount();
            if (result) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gestureDetector.onTouchEvent(ev);
    }

    /***
     * 初始化界面布局
     * 公共库中初始化控件以及设置页面布局，私有子类不可用
     */
    private void initBaseView() {
        try {
            parent = (RelativeLayout) findViewById(R.id.frame_parent);

//        frameTop=(LinearLayout)findViewById(R.id.frame_top);
//        frameFooter=(LinearLayout)findViewById(R.id.frame_footer);
            setButtonGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示加载框
     *
     * @return
     */
    protected ProgressDialog getProgressDialog() {
        return getProgressDialog(false);
    }

    /**
     * 展示加载框
     *
     * @return
     */
    protected ProgressDialog getProgressDialog(boolean isRejectClosed) {
        try {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(this, isRejectClosed);
            else if (!progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = new ProgressDialog(this, isRejectClosed);
            } else if (isRejectClosed) {
                progressDialog.rejectClosed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    /**
     * 关闭加载框
     *
     * @return
     */
    protected void closeProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 页面跳转
     * 从当前的Activity 跳转到 你现在传递的 Activity当中
     * @param clazz  想要打开的新Activity 文件
     * @param bundle  需要传递的值是什么
     */
    protected void forward(Class<?> clazz, Bundle bundle) {
        //跳转页面时候，引导的倒计清0！
        try {
            InfraredService.clearWaitGuideCount();
            Intent intent = new Intent(this, clazz);
            if (bundle != null)
                intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时同步电量
     */
    private void startTimingElectricity() {
        //Log.i(TAG, "startTimingElectricity");
        try {
            handler.postDelayed(timingElectricityRunnable, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeTimingElectricity() {
        //Log.i(TAG, "removeTimingElectricity");
        try {
            if(handler != null) {
                handler.removeCallbacks(timingElectricityRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 电池电量同步线程
     */
    Runnable timingElectricityRunnable = new Runnable() {
        @Override
        public void run() {
            if (handler != null) {
                getElectricity();
                handler.postDelayed(this, 60 * 1000);
            }
        }
    };

    /**
     * 获取电量
     */
    private void getElectricity() {
        try {
            if(txt_elec == null) {
                Log.i(TAG, "系统电量，txt_elec为空，返回" );
                return;
            }
            int electricity = SystemProperties.getInt("persist.service.ist.DianLiang", 0);
            Log.i(TAG, "系统电量是：" + electricity);
            txt_elec.setText(electricity + "%");
            findViewById(R.id.layout_elec).setBackgroundResource(R.drawable.electricity);
            if(img_elec == null) {
                Log.i(TAG, "系统电量，img_elec为空，返回" );
                return;
            }
            if (electricity >= 80 && electricity <= 100) {
                img_elec.setBackgroundResource(R.drawable.electricity100);
            } else if (electricity >= 50 && electricity < 80) {
                img_elec.setBackgroundResource(R.drawable.electricity50);
            } else if (electricity >= 30 && electricity < 50) {
                img_elec.setBackgroundResource(R.drawable.electricity30);
            } else {
                img_elec.setBackgroundResource(R.drawable.electricity20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 最小化音量图标的线程
     */
    Runnable minVoiceImageRunnable = new Runnable() {
        @Override
        public void run() {
            if (seekbar_liner.getVisibility() == View.VISIBLE) {
                voiceImage.setVisibility(View.VISIBLE);
                seekbar_liner.setVisibility(View.GONE);
            }
        }
    };

    /***
     * 若手机硬件存在menu按键 同样显示 overflow 图标
     * 私有 不可用，
     * 待修改状态
     */
    private void setMenuAlwaysShow() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 设置按钮部分的显示方式
     */
    private void setButtonGroup() {
        try {
            log.d("setButtonGroup");
            if (getButtonText() != null && getButtonText().length > 0) {
                footerButtonLayout = (LinearLayout) findViewById(R.id.frame_footer_button);
                footerButtonLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < getButtonText().length; i++) {
                    final int finalI = i;
                    Button button = new Button(this);
                    button.setBackgroundResource(R.drawable.style_button_default);
                    button.setTextColor(Color.BLACK);
                    button.setGravity(Gravity.CENTER);
                    button.setText(getButtonText()[i]);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FooterOnClick(finalI);
                        }
                    });
                    log.d(getApplication().getString(getButtonText()[i]));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i > 0) {
                        params.setMargins(100, 10, 10, 10);
                    } else {
                        params.setMargins(10, 10, 10, 10);
                    }

                    button.setLayoutParams(params);
                    footerButtonLayout.addView(button);
                }
            }
            //主体的样式
            if (getBody() > 0) {
                frameBody = (ViewGroup) LayoutInflater.from(this).inflate(getBody(), null);
                parent.addView(frameBody, paramsCenter);
            } else {
                findViewById(R.id.nopage).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通模式打开 Activity
     *
     * @param clazz  想打开的activity.class文件
     * @param bundle 想传递的Bundle
     */
    public void openOrdinaryActivity(Class<?> clazz, Bundle bundle) {
        try {
            Intent intent = new Intent(this, clazz);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 初始化主界面 主界面的修改
     * 当业务程序存在公共页面需要被其他页面引用时 可以新建一个Activity 继承
     * 然后复制公共库页面 activity_frame 在业务程序中 进行修改(其中已有的内容不许删除与修改，修改时请注意)
     * @return 返回 业务程序的公共主页面
     */
    public abstract int getMainView();

    /***
     * 初始化控制器实例，所有对象在此实例化
     *
     */
    public abstract void initController();

    /**
     * 初始化界面设置，包括数据填充等
     */
    public abstract void initView();

    /**
     * 设置主体页面
     */
    public abstract int getBody();

    /**
     * 设置按钮文本
     */
    public abstract int[] getButtonText();

    /***
     * 点击底部按钮栏点击
     * @param index
     */
    public abstract void FooterOnClick(int index);

    @Override
    protected void onStart() {
        try {
            super.onStart();
            log.d("onStart");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            log.d("onResume");
            //有需要号码达人的在业务里面自行设置
            AiuiController.getInstance().setSpecialAiuiListener(this);
            SceneController.getInstance().changeScene(this.getClass().getName());
            //电量同步
            startTimingElectricity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        try {
            super.onRestart();
            log.d("onRestart");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            log.d("onPause");
            removeTimingElectricity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            log.d("onStop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            log.d("onDestroy");
            if (log != null) {
                log = null;
            }
            ActivityController.getInstance().removeActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        try {
            if (!TimeUtil.getInstance().isMultipleClick()) {
                onRyClick(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(VerticalSeekBar vBar, int progress, boolean fromUser) {
        try {
            if (audioManager != null) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            checkVoice(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(VerticalSeekBar vBar) {

    }

    @Override
    public void onStopTrackingTouch(VerticalSeekBar vBar) {

    }

    public abstract void onRyClick(View view);

    public void pageAction(String action) {
        try {
            Log.i(TAG, "页面的动作：" + action);
            if (action.equals("max_voice")) {
                dealVoice("volume_plus");
            } else if (action.equals("min_voice")) {
                dealVoice("volume_minus");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**打开人脸识别**/
    public void startFace(){
        if(!FloatCamera.isOpenCamera){
            Intent mIntent = new Intent("startFaceDetect");
            mIntent.setClass(getApplicationContext(), FloatCamera.class);
            getApplicationContext().startService(mIntent);
        }
    }



    /**关闭人脸识别**/
    public void stopFace(){
        Intent mIntent = new Intent("exitFaceDetect");
        mIntent.setClass(getApplicationContext(), FloatCamera.class);
        getApplicationContext().startService(mIntent);
    }
}
