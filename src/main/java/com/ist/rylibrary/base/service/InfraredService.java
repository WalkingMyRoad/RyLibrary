package com.ist.rylibrary.base.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.InfraredController;
import com.ist.rylibrary.base.controller.JiangJieController;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.controller.SharedPreferencesController;
import com.ist.rylibrary.base.controller.YinDaoController;
import com.ist.rylibrary.base.entity.MallDictsBean;
import com.ist.rylibrary.base.event.InfraredMessageEvent;
import com.ist.rylibrary.base.function.HttpMallDictsFunction;
import com.ist.rylibrary.base.inter.BaseHttpServiceInter;
import com.ist.rylibrary.base.listener.BaseAiuiListener;
import com.ist.rylibrary.base.listener.RyRRttsListener;
import com.ist.rylibrary.base.util.HttpUtil;
import com.ist.rylibrary.base.util.TimeUtil;
import com.ist.rylibrary.base.util.ToolUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by minyuchun on 2017/3/6.
 * 广播服务
 */

public class InfraredService extends Service {
    private String TAG = "InfraredService";

    /**
     * 缓存intent
     */
    private static Intent sIntent;

    /**
     * 超声波有人的计次
     */
    private static int personExistCount = 0;
    /**
     * 超声波无人的计次
     */
    private static int personNotExistCount = 0;

    /**
     * 面前引导频率：有人时，多少秒触发引导
     **/
    private static int guideFreqSecond = 60;
    /**等待面前引导的计次*/
    private static int waitGuideCount = 0;
    /**持续引导次数*/
    private static int continueGuideCount = 0;

    /**
     * 引导的音量
     **/
    private static int guideVolume = 0;

    /**
     * 吆喝频率：无人时，多少秒触发吆喝
     **/
    private static int shoutFreqSecond = 10 * 60;
    /**等待吆喝的计次*/
    private static int waitShoutCount = 0;
    /**
     * 吆喝的音量
     **/
    private static int shoutVolume = 0;

    /**
     * 全局变量
     **/
    private static RyApplication application;

    public AudioManager getmAudioManager() {
        return mAudioManager;
    }

    /**基础数据是否获取完成**/

    public static boolean isCompleteData=false;

    private Handler handler = new Handler();

    /**
     * 吆喝的开关 1 开 0 关
     **/
    private static String shout_switch = "1";

    private int lastVolum = 0;
    private AudioManager mAudioManager = null;



    /***
     * 打开service
     * @param context
     */
    public static void getInstance(Context context) {
        sIntent = new Intent(context, InfraredService.class);
        context.startService(sIntent);
        application = (RyApplication) context.getApplicationContext();
    }

    private void getBaseData(){
        String shoutFreqSecondStr=SharedPreferencesController.getInstance().getData("shoutFreqSecond");
        if(!shoutFreqSecondStr.equals("")){
            shoutFreqSecond=Integer.parseInt(shoutFreqSecondStr);
        }
        String   guideFreqSecondStr = SharedPreferencesController.getInstance().getData("guideFreqSecond");
        if(!guideFreqSecondStr.equals("")){
            guideFreqSecond=Integer.parseInt(guideFreqSecondStr);
        }
        String shoutVolumeStr=SharedPreferencesController.getInstance().getData("shoutVolume");
        if(!shoutVolumeStr.equals("")){
            shoutVolume=Integer.parseInt(shoutVolumeStr);
        }

        String guideVolumeStr=SharedPreferencesController.getInstance().getData("guideVolume");
        if(!guideVolumeStr.equals("")){
            guideVolume=Integer.parseInt(guideVolumeStr);
        }

        String shout_switchStr=SharedPreferencesController.getInstance().getData("shout_switch");
        if(!shout_switchStr.equals("")){
            shout_switch=shout_switchStr;
        }
    }

    /***
     * 关闭服务
     * @param context
     */
    public static void finishService(Context context) {
        if (sIntent != null) {
            context.stopService(sIntent);
            sIntent = null;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToolUtil.getInstance().loadEventBus(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getdata();
                handler.postDelayed(this, 30 * 1000 * 60);
            }
        }, 10*1000);


        //动态添加广播
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.IRDOWN");//红外有人
            filter.addAction("android.intent.action.IRUP");//红外无人
            registerReceiver(myInfraredReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mAudioManager = (AudioManager) RyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getdata() {
        String[] dicts = {"shout_freq_second", "guide_freq_second",
                "shout_volume", "guide_volume", "shout_switch","is_play_music",
                "is_konw_user","is_show_face_window","face++"};
        for (int i = 0; i < dicts.length; i++) {
            getMallDict(dicts[i], i);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("InfraredService","打开红外服务");
        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * 广播注册实例
     */
    public BroadcastReceiver myInfraredReceiver = new BroadcastReceiver() {
        @SuppressWarnings("unused")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TimeUtil.getInstance().isLessTimeInterval(500)) {
                if (action.equals("android.intent.action.IRDOWN")) {
                    Log.i(TAG, "--红外有人--");
                    personExistCount++;
                    waitGuideCount++;
                    waitShoutCount = 0;
                    personNotExistCount = 0;
                    playGuideVoice();
                    InfraredController.getInstance().post(true, true, personExistCount);
                } else if (action.equals("android.intent.action.IRUP")) {
                    Log.i(TAG, "--红外无人--");
                    personNotExistCount++;
                    waitShoutCount++;
                    personExistCount = 0;
                    waitGuideCount = 0;
                    continueGuideCount = 0;
                    playShoutVoice();
                    InfraredController.getInstance().post(true, false, personNotExistCount);
                }
            }
        }
    };

    /**
     * 播放吆喝语
     **/
    private void playShoutVoice() {
        try {
            if ("0".equals(shout_switch)) {
                return;
            }
        /*
        * 判断是否在引导、讲解
        * **/
            lastVolum = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            if (YinDaoController.getInstance().isInYindaoProcess()) {
                Log.i(TAG, "在引导过程中，不开启吆喝！");
                personNotExistCount = 0;
                waitShoutCount = 0;
                return;
            }
            if (JiangJieController.getInstance().isInJiangJieProcess()) {
                Log.i(TAG, "在讲解过程中，不开启吆喝！");
                personNotExistCount = 0;
                waitShoutCount = 0;
                return;
            }
            if(BaseAiuiListener.isOpenResultRawToWebSocket){
                Log.i(TAG, "要使用自定义的websocket定义");
                personNotExistCount = 0;
                waitShoutCount = 0;
                return;
            }

            Log.i(TAG, "当前音量是：" + lastVolum);
            if (waitShoutCount > 0 && shoutFreqSecond > 0
                    && waitShoutCount * 2 > shoutFreqSecond) {
                Log.i(TAG, "开始吆喝：" + waitShoutCount);
                waitShoutCount = 0;//清零
                if (shoutVolume != 0) {
                    setVolume(shoutVolume);
                }
                AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                    @Override
                    public boolean onComplete(String s) {

                        setVolume(lastVolum);
                        return true;
                    }
                });

                SceneController.getInstance().rrPeople("#cmd_shout_voice#", "","");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 播放引导内容
     **/
    private void playGuideVoice() {
        try {
            if (YinDaoController.getInstance().isInYindaoProcess()) {
                Log.i(TAG, "在引导过程中，不开启吆喝！");
                personExistCount = 0;
                waitGuideCount = 0;
                return;
            }
            if (JiangJieController.getInstance().isInJiangJieProcess()) {
                Log.i(TAG, "在讲解过程中，不开启吆喝！");
                personExistCount = 0;
                waitGuideCount = 0;
                return;
            }
            if(BaseAiuiListener.isOpenResultRawToWebSocket){
                Log.i(TAG, "要使用自定义的websocket定义");
                personNotExistCount = 0;
                waitShoutCount = 0;
                return;
            }
            if(continueGuideCount >= 2) {
                return;
            }
            //超声波2秒1次，waitGuideCount*2
            if (waitGuideCount > 0 && guideFreqSecond > 0
                    && waitGuideCount * 2 > guideFreqSecond) {
                Log.i(TAG, "开始面前引导：" + waitShoutCount  + ",连续引导次数："+continueGuideCount);
                waitGuideCount = 0;//清零
                continueGuideCount++;
                lastVolum = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

                if (guideVolume != 0) {
                    setVolume(guideVolume);
                }
                AiuiController.getInstance().setRyRRttsListener(new RyRRttsListener() {
                    @Override
                    public boolean onComplete(String s) {
                        setVolume(lastVolum);
                        return true;
                    }
                });
                Log.i(TAG, "触发面前引导：" + application.isChat());
                if (application.isChat()) {
                    SceneController.getInstance().rrPeople("#cmd_guide_voice#", "","");
                } else {
                    String guideText = application.getGuidText();
                    Log.i(TAG, "循环播放的内容是：" + guideText);
                    if (guideText != null && !guideText.equals("")) {
                        if (guideText.startsWith("#")) {
                            SceneController.getInstance().rrPeople(guideText, "","");
                        } else {
                            AiuiController.getInstance().startTTS(guideText);
                        }
                    } else {
                        Log.i(TAG, "循环引导的内容为空!");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ToolUtil.getInstance().relieveEventBus(this);
        //解除注册
        try {
            if (myInfraredReceiver != null) {
                unregisterReceiver(myInfraredReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * eventsbus 回调
     *
     * @param event 回调的类参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfraredMessageEvent event) {
        if (event != null) {
            try {
                if (event.isCustom()) {//是否自定义处理
                    InfraredController.getInstance().recovery(event.isCustom(), event.isPersonExist(), event.getFrequency());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void getMallDict(String type, final int tag) {
        BaseHttpServiceInter serviceInter = new HttpUtil().getMyBaseService();
        if (serviceInter != null) {
            serviceInter.getMallDicts(SharedPreferencesController.getInstance().getMailId()
                    ,SharedPreferencesController.getInstance().getRobotNumber(),type)
                    .map(new HttpMallDictsFunction<List<MallDictsBean>>())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<MallDictsBean>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }
                        @Override
                        public void onNext(List<MallDictsBean> value) {
                            Log.d("getMallMessage","参数 "+value);
                            if(tag==7){
                                isCompleteData=true;
                            }
                            if (value.size() > 0) {
                                MallDictsBean dictsBean = value.get(0);
                                if (tag == 0) {
                                    shoutFreqSecond = Integer.parseInt(dictsBean.getValue());
                                    SharedPreferencesController.getInstance().saveData("shoutFreqSecond",shoutFreqSecond);
                                } else if (tag == 1) {
                                    guideFreqSecond = Integer.parseInt(dictsBean.getValue());
                                    SharedPreferencesController.getInstance().saveData("guideFreqSecond",guideFreqSecond);
                                } else if (tag == 2) {
                                    shoutVolume = Integer.parseInt(dictsBean.getValue());
                                    SharedPreferencesController.getInstance().saveData("shoutVolume",shoutVolume);
                                } else if (tag == 3) {
                                    guideVolume = Integer.parseInt(dictsBean.getValue());
                                    SharedPreferencesController.getInstance().saveData("guideVolume",guideVolume);
                                } else if (tag == 4) {
                                    shout_switch = dictsBean.getValue();
                                    SharedPreferencesController.getInstance().saveData("shout_switch",shout_switch);
                                }else if(tag==5){
                                    String valueStr=dictsBean.getValue();
                                    SharedPreferencesController.getInstance().saveData("isPlayMusic",valueStr);
                                    if(valueStr.equals("0")){
                                        application.setPlayMusic(false);
                                    }else{
                                        application.setPlayMusic(true);
                                    }

                                }else if(tag==6){//是否要认识新老用户
                                    String valueStr=dictsBean.getValue();
                                    SharedPreferencesController.getInstance().saveData("isOpenFace",valueStr);
                                    if(valueStr.equals("0")){
                                        application.setOpenFace(false);
                                    }else{
                                        application.setOpenFace(true);
                                    }
                                }else if(tag==7){//是否要展示人脸识别的窗口
                                    String valueStr=dictsBean.getValue();
                                    SharedPreferencesController.getInstance().saveData("isHidePreview",valueStr);
                                    if(valueStr.equals("0")){
                                        application.setHidePreview(true);
                                    }else{
                                        application.setHidePreview(false);

                                    }
                                }else if(tag == 8){
                                    String valueStr  = dictsBean.getValue();
                                    try{
                                        JSONObject jsonObject = new JSONObject(valueStr);
                                        if(jsonObject.has("faceset_token")){
                                            SharedPreferencesController.getInstance().saveData("faceset_token",jsonObject.getString("faceset_token"));
                                            Log.d("getMallMessage",SharedPreferencesController.getInstance().getFacesetToken());
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    Log.d("getMallMessage",dictsBean.getValue());
                                }

                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 设置音量的大小
     *
     * @param volume
     */
    private void setVolume(int volume) {
        AudioManager mAudioManager = (AudioManager) RyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        Log.i(TAG, "系统的最大音量是：" + maxVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                volume, AudioManager.FLAG_PLAY_SOUND);
    }

    /**清零（等待面前引导的计次）*/
    public static void clearWaitGuideCount() {
        waitGuideCount = 0;
        continueGuideCount = 0;
    }

}
