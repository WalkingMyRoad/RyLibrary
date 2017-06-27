package com.ist.rylibrary.base.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;

import com.ist.nativepackage.EyesCtrl;
import com.ist.nativepackage.RR;
import com.ist.rylibrary.base.controller.HttpController;
import com.ist.rylibrary.base.controller.JiangJieController;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.controller.SharedPreferencesController;
import com.ist.rylibrary.base.entity.AllInfoByRobotIdBean;
import com.ist.rylibrary.base.entity.AllSceneResultBean;
import com.ist.rylibrary.base.entity.MallDictsBean;
import com.ist.rylibrary.base.entity.PersonInformationBean;
import com.ist.rylibrary.base.entity.SceneQABean;
import com.ist.rylibrary.base.exception.CatchGlobalException;
import com.ist.rylibrary.base.util.BaseLogUtil;
import com.ist.rylibrary.listener.FaceResultListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.renying.m4.AiuiObj;
import com.wewins.facelibrary.api.ApiConstants;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by minyuchun on 2017/3/16.
 * 自定义的Application 业务程序可以集成 RyApplication 然后在AndroidManifest.xml中的application 标签中的 android:name=".base.xxx"添加
 */

public abstract class RyApplication extends Application{
    private String TAG="RyApplication";
    /**全局上下文
     * 可以通过 RyApplication.getContext() 获取
     * */
    private static Context context;
    /**全局日志
     * 可以通过 RyApplication.getLog() 获取
     * */
    private static BaseLogUtil log;
    /**图片加载器
     * */
    protected static ImageLoader imageLoader;
     /**是否是聊天**/
    protected boolean isChat=true;
    /**引导的内容**/
    protected String guidText="";
    /**是否播放音乐**/
    private boolean isPlayMusic=true;

    /***
     * AIUI是否在工作**/
    private boolean isAiuiWorkIng;


    /**AIUI是否打开**/
    private boolean isAiuiOpen=false;

    /**人物信息*/
    private PersonInformationBean mPerson;

    /**是否打开人脸识别**/
    private boolean isOpenFace=false;
    private String faceText;
    private FaceResultListener faceResultListener=null;

    public FaceResultListener getFaceResultListener() {
        return faceResultListener;
    }

    public void setFaceResultListener(FaceResultListener faceResultListener) {
        this.faceResultListener = faceResultListener;
    }

    public String getFaceText() {
        return faceText;
    }

    public void setFaceText(String faceText) {
        this.faceText = faceText;
    }

    public boolean isOpenFace() {
        String isOpenFaceStr=SharedPreferencesController.getInstance().getData("isOpenFace");
        if(isOpenFaceStr.equals("0")){
            isOpenFace=false;
        }else{
            isOpenFace=true;
        }
        return isOpenFace;
    }

    public void setOpenFace(boolean openFace) {
        isOpenFace = openFace;
    }

    private int mPictureSizeWidth; //保存图片的宽度
    private int mPictureSizeHeight;//保存图片的高度
    private int mPictureRotate; //保存图片的旋转角度，参数值限定在0/90/180/270
    private int mPreviewSizeWidth; //预览图片的宽度
    private int mPreviewSizeHeight;//预览图片的高度
    private int mPreviewRotate; //预览图片的旋转角度，参数值限定在0/90/180/270

    private boolean mCameraMute; //拍照时是否静音
    private int mDefaultCamera; //默认开启的摄像头
    private int mScreenOrientation; //屏幕方向 横屏竖屏
    private int mDetectInterval; //摄像头识别间隔时间，单位为毫秒

    private String mApiName;
    /**开关需求集合
     * switchs[0] 是否启用新老用户识别的人像接口
     * */
    private boolean[] switchs ={true};
    /**是否需要新老用户识别的流程*/
    public boolean getDistinguishSwitch(){
        if(switchs.length>0){
            return switchs[0];
        }else{
            return false;
        }
    }

    public PersonInformationBean getPerson() {
        return mPerson;
    }

    public void setPerson(PersonInformationBean person) {
        mPerson = person;
    }

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }

    //读取参数
    private String getConfigValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("CameraConfig", Activity.MODE_PRIVATE);
        String sValue = sharedPreferences.getString(key, "");
        return sValue;
    }

    //写入参数
    private void SetConfigValue(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("CameraConfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        Log.i(TAG, "SetConfigValue, key = " + key + ", value = " + value);
    }

    public int getPictureSizeWidth() {
        String sTemp = getConfigValue("pictureSizeWidth");
        try {
//            mPictureSizeWidth = Integer.parseInt(sTemp);
            mPictureSizeWidth = 160;
        } catch (Exception e) {
//            mPictureSizeWidth = 640;
            SetConfigValue("pictureSizeWidth", String.valueOf(mPictureSizeWidth));
            //e.printStackTrace();
        }
        return mPictureSizeWidth;
    }

    public void setPictureSizeWidth(int pictureSizeWidth) {
        this.mPictureSizeWidth = pictureSizeWidth;
        SetConfigValue("pictureSizeWidth", String.valueOf(pictureSizeWidth));
    }

    public int getPictureSizeHeight() {
        String sTemp = getConfigValue("pictureSizeHeight");
        try {
//            mPictureSizeHeight = Integer.parseInt(sTemp);
            mPictureSizeHeight = 120;
        } catch (Exception e) {
            mPictureSizeHeight = 480;
//            mPictureSizeHeight = 120;
            SetConfigValue("pictureSizeHeight", String.valueOf(mPictureSizeHeight));
            //e.printStackTrace();
        }
        return mPictureSizeHeight;
    }

    public void setPictureSizeHeight(int pictureSizeHeight) {
        this.mPictureSizeHeight = pictureSizeHeight;
        SetConfigValue("pictureSizeHeight", String.valueOf(pictureSizeHeight));
    }

    public int getPictureRotate() {
        String sTemp = getConfigValue("pictureRotate");
        try {
            mPictureRotate = Integer.parseInt(sTemp);
        } catch (Exception e) {
            mPictureRotate = 0;
            SetConfigValue("pictureRotate", String.valueOf(mPictureRotate));
            //e.printStackTrace();
        }
        return mPictureRotate;
    }

    public void setPictureRotate(int pictureRotate) {
        this.mPictureRotate = pictureRotate;
        SetConfigValue("pictureRotate", String.valueOf(pictureRotate));
    }

    public int getPreviewSizeWidth() {
        String sTemp = getConfigValue("previewSizeWidth");
        try {
            mPreviewSizeWidth = Integer.parseInt(sTemp);
        } catch (Exception e) {
            mPreviewSizeWidth = 640;
            SetConfigValue("previewSizeWidth", String.valueOf(mPreviewSizeWidth));
            //e.printStackTrace();
        }
        return mPreviewSizeWidth;
    }

    public void setPreviewSizeWidth(int previewSizeWidth) {
        this.mPreviewSizeWidth = previewSizeWidth;
        SetConfigValue("previewSizeWidth", String.valueOf(previewSizeWidth));
    }

    public int getPreviewSizeHeight() {
        String sTemp = getConfigValue("previewSizeHeight");
        try {
            mPreviewSizeHeight = Integer.parseInt(sTemp);
        } catch (Exception e) {
            mPreviewSizeHeight = 480;
            SetConfigValue("previewSizeHeight", String.valueOf(mPreviewSizeHeight));
            //e.printStackTrace();
        }
        return mPreviewSizeHeight;
    }

    public void setPreviewSizeHeight(int previewSizeHeight) {
        this.mPreviewSizeHeight = previewSizeHeight;
        SetConfigValue("previewSizeHeight", String.valueOf(previewSizeHeight));
    }

    public int getPreviewRotate() {
        String sTemp = getConfigValue("previewRotate");
        try {
            mPreviewRotate = Integer.parseInt(sTemp);
        } catch (Exception e) {
            mPreviewRotate = 0;
            SetConfigValue("previewRotate", String.valueOf(mPreviewRotate));
            //e.printStackTrace();
        }
        return mPreviewRotate;
    }

    public void setPreviewRotate(int previewRotate) {
        this.mPreviewRotate = previewRotate;
        SetConfigValue("previewRotate", String.valueOf(previewRotate));
    }

    public boolean getCameraMute() {
        String sTemp = getConfigValue("cameraMute");
        if (sTemp == null || sTemp.length() == 0) {
            SetConfigValue("cameraMute", "0");
            mCameraMute = true;
        } else {
            mCameraMute = sTemp.equals("1");
        }
        return mCameraMute;
    }

    public int getDefaultCamera() {
        String sTemp = getConfigValue("defaultCamera");
        try {
            mDefaultCamera = Integer.parseInt(sTemp);
        } catch (Exception e) {
            //e.printStackTrace();
            mDefaultCamera = 0;
            SetConfigValue("defaultCamera", String.valueOf(mDefaultCamera));
        }
        return mDefaultCamera;
    }

    public void setDefaultCamera(int cameraId) {
        mDefaultCamera = cameraId;
        SetConfigValue("defaultCamera", String.valueOf(cameraId));
    }

    public int getScreenOrientation() {
        String sTemp = getConfigValue("screenOrientation");
        try {
            mScreenOrientation = Integer.parseInt(sTemp);
        } catch (Exception e) {
            //e.printStackTrace();
            mScreenOrientation = 0;
            SetConfigValue("screenOrientation", String.valueOf(mScreenOrientation));
        }
        return mScreenOrientation;
    }

    public void setScreenOrientation(int screenOrientation) {
        mScreenOrientation = screenOrientation;
        SetConfigValue("screenOrientation", String.valueOf(screenOrientation));
    }

    public int getDetectInterval() {
        String sTemp = getConfigValue("detectInterval");
        try {
            mDetectInterval = Integer.parseInt(sTemp);
        } catch (Exception e) {
            mDetectInterval = 10000;
            SetConfigValue("detectInterval", String.valueOf(mDetectInterval));
            //e.printStackTrace();
        }
        return mDetectInterval;
    }

    public String getmApiName() {
        String sTemp = getConfigValue("ApiName");
        if (sTemp == null || sTemp.length() == 0) {
            mApiName = ApiConstants.API_FACEPLUS_FREE;
            SetConfigValue("ApiName", mApiName);
        } else
            mApiName = sTemp;
        return mApiName;
    }

    public void setApiName(String apiName) {
        this.mApiName = apiName;
        SetConfigValue("ApiName", mApiName);
    }

  private boolean hidePreview=true;
    //是否隐藏预览窗口
    public boolean getHidePreview() {
        String isHidePreviewStr=SharedPreferencesController.getInstance().getData("isHidePreview");
        if(isHidePreviewStr.equals("0")){
            hidePreview=true;
        }else{
            hidePreview=false;

        }
        return hidePreview;
    }

    public void setHidePreview(boolean hidePreview) {
       this.hidePreview = hidePreview;

    }

    //是否保留摄像头的采集图像
    public boolean getKeepCameraPicture() {
        return getConfigValue("keepCameraPicture").equals("1");
    }

    public void setKeepCameraPicture(boolean keepCameraPicture) {
        SetConfigValue("keepCameraPicture", true == keepCameraPicture ? "1" : "0");
    }

    public boolean isAiuiOpen() {
        return isAiuiOpen;
    }

    public void setAiuiOpen(boolean aiuiOpen) {
        isAiuiOpen = aiuiOpen;
    }

    public boolean isAiuiWorkIng() {
        return isAiuiWorkIng;
    }

    public void setAiuiWorkIng(boolean aiuiWorkIng) {
        isAiuiWorkIng = aiuiWorkIng;
    }

    public boolean isPlayMusic() {
        String valueStr=SharedPreferencesController.getInstance().getData("isPlayMusic");
        if(valueStr.equals("0")){
            isPlayMusic=false;
        }else{
            isPlayMusic=true;
        }
        return isPlayMusic;
    }

    public void setPlayMusic(boolean playMusic) {
        isPlayMusic = playMusic;
    }

    public String getGuidText() {
        return guidText;
    }

    public void setGuidText(String guidText) {
        this.guidText = guidText;
    }

    public boolean isChat() {
        return isChat;
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try{
            context = getApplicationContext();
            log = new BaseLogUtil(context.getClass());
            SceneController.getInstance().setCompleteData(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            SceneController.getInstance().readSceneData();//先读取本地的缓存数据,然后偷偷的下载数据后替换
            //截取错误日志
//            CatchGlobalException.getInstance();
            //初始化配置文件
            initSharedPreference();
            //初始化图片加载器
            initLoader();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            //硬件串口初始化
            RR.InitLock();
           // 眼睛屏同步
            EyesCtrl.getInstance().EyeWork();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 图片加载器初始化
     */
    private void initLoader(){
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .memoryCache(new WeakMemoryCache())
                .memoryCache(new UsingFreqLimitedMemoryCache(20000)) //如果缓存的图片总量超过限定值，先删除使用频率最小的bitmap
                // 线程池内加载的数量
                .threadPoolSize(3)
                // 线程优先级
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .discCacheSize(2 * 1024 * 1024)
                .discCacheFileCount(1000)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new BaseImageDownloader(context, 3 * 1000, 3 * 1000))
                .writeDebugLogs() // Remove // app
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * 读取配置文件数据并保存
     */
    private void initSharedPreference(){
        //初始化本地数据实例
        SharedPreferencesController.getInstance().initConfigSharedPreference(
                new SharedPreferencesController.initSharedPreferenceListener() {
                    @Override
                    public boolean initData(int type, boolean isComplete) {
                        if(isComplete){
                            if(type == SharedPreferencesController.ShareByDemo){
                                log.d("存在demo.xml配置或者config.xml日志完全直接使用缓存内容读取数据");
                                getAllSceneAndBot();
                                getMallDict();
                            }else if(type == SharedPreferencesController.ShareByConfig){
                                log.d("只存在config.xml配置先要通过机器人id读取配置信息");
                                if(setProgramCode()!=null && !setProgramCode().isEmpty()){
                                    SharedPreferencesController.getInstance().saveData("program_code",setProgramCode());
                                }
                                getAllInfoByRobotId();
                            }
                        }else {
                            log.d("isComplete==="+isComplete);
                        }
                        return false;
                    }
                }
        );
    }

    public static Context getContext(){
        return context;
    }

    public static BaseLogUtil getLog() {
        return log;
    }

    public void getAllInfoByRobotId(){
        log.d("根据机器人编号获取网店的相信信息");
        HttpController.getInstance().getAllInfoByRobotId(new Observer<List<AllInfoByRobotIdBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(List<AllInfoByRobotIdBean> value) {
                log.d("接口获得机器人编号数据成功 "+value.toString());
                if(value.size()>0){
                    AllInfoByRobotIdBean bean = value.get(0);
                    if(bean.getRobotInfo()!=null && bean.getMallInfo()!=null){
                        SharedPreferencesController.getInstance().saveData("robot_number",bean.getRobotInfo().getRobotNo());
                        SharedPreferencesController.getInstance().saveData("mall_id",bean.getMallInfo().getMallId());
                        SharedPreferencesController.getInstance().saveData("mall_number",bean.getMallInfo().getMallNo());
                        getAllSceneAndBot();
                        getMallDict();
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                log.d("接口获得机器人编号数据失败 "+e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        },setRobotNo());
    }


    public void getAllSceneAndBot(){
        log.d("获取所有场景数据");
        HttpController.getInstance().getAllSceneAndBot(new Observer<AllSceneResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AllSceneResultBean value) {
                log.d("接口获得场景列表数据成功 ");
                SceneController.getInstance().saveSceneData(value);
            }

            @Override
            public void onError(Throwable e) {
                log.d("接口获取场景列表数据失败 "+e.getClass().getSimpleName());
            }

            @Override
            public void onComplete() {

            }
        },true);
    }

    public void getMallDict(){
        log.d("获取字典表数据");
        HttpController.getInstance().getMallDict(new Observer<List<MallDictsBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<MallDictsBean> value) {
                log.d("接口获取字典表数据成功,数据的长度 "+value.toString());
                if(value.size()>0){
                    SharedPreferencesController.getInstance().saveData("jiangjie",value.get(0).getValue());
                    JiangJieController.getInstance().initJiangJieData(value.get(0).getValue());
                }
            }

            @Override
            public void onError(Throwable e) {
                log.d("接口获取字典表数据失败 "+e.getClass().getSimpleName());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 程序编码
     * @return 返回程序编码
     */
    public abstract String setProgramCode();

    /***
     * 机器人编号
     * 用于 通过机器人编号获取信息的接口，当使用机器人编号与数据库中不同时，可以通过此方法设置
     * 如 我想再 22号机器人上运行程序 获得 6号机器人上的版本数据
     * 当传null  则读取机器人编号传参数
     * @return 机器人编号
     */
    public abstract String setRobotNo();
}
