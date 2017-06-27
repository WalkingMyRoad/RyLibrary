package com.ist.rylibrary.face.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;


import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.controller.SharedPreferencesController;
import com.ist.rylibrary.base.module.BaseActivity;
import com.ist.rylibrary.base.util.BaseLogUtil;
import com.ist.rylibrary.face.services.GoogleFaceDetect;
import com.ist.rylibrary.face.ui.FaceView;
import com.ist.rylibrary.face.util.Constants;
import com.ist.rylibrary.face.util.MessageEvent;
import com.ist.rylibrary.listener.FaceResultListener;
import com.wewins.facelibrary.api.ApiBase;
import com.wewins.facelibrary.api.NewApiBase;
import com.wewins.facelibrary.api.rr.RRBusinessApi;
import com.wewins.facelibrary.utils.CamParaUtil;
import com.wewins.facelibrary.utils.FileUtil;
import com.wewins.facelibrary.utils.ImageUtil;
import com.wewins.facelibrary.utils.SequenceUtil;

import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
修改记录
    2016-07-12  YangWY  添加配置项（是否保留人脸图片），不保存的话，每次从摄像头采集的头像将在处理完成后删除。
    2016-07-12  YangWY  完善日志输出，方便查找问题
 */

//预览界面FloatCamera
public class FloatCamera extends Service implements SurfaceHolder.Callback {
    private static final String TAG = "FloatCamera";
    private static FaceResultListener faceResultListener;
    /**主程序的布局*/
    private WindowManager wm;
    /**配置*/
    private WindowManager.LayoutParams wmParams;
    /**预览界面*/
    private SurfaceView mSurfaceView;
    /**识别的框*/
    private FaceView faceView;
    private SurfaceHolder mHolder;
    /**相机*/
    private Camera mCamera;
    /**界面*/
    private View mLayoutView;
    private RyApplication appData;
    private MainHandler mMainHandler = null;
    private GoogleFaceDetect googleFaceDetect = null;
    private int faceStatus = 0; // 0-未检测到 1-检测到人脸 2-正在处理
    private boolean googleDetectRunning = false; // Google识别是否在运行
    private boolean needComparePicture = false; // 是否需要进行图片比较
    private String comparePictureDst = ""; // 图片比较对象
    private String serialNo = ""; //当前处理流程的流水号
    private long startTime = 0;//人像查找时间
    private long endTime = 0;//结束时间时时获取
    private long intervalTime = 5 * 1000;//间隔时间
    /**照相机是否正在开启的状态*/
    public static boolean isOpenCamera =false;
    private BaseLogUtil log = new BaseLogUtil(this.getClass());
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
//                        Toast.makeText(RyApplication.getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log.i("onCreate");
        EventBus.getDefault().register(this);
        appData = (RyApplication) getApplicationContext();
        if (!checkCameraHardware(getApplicationContext())) {
            log.e("没有摄像头!");
            System.exit(0);
        }
        initLayout();
        initWMParams();
        mMainHandler = new MainHandler();
        googleFaceDetect = new GoogleFaceDetect(getApplicationContext(), mMainHandler);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销广播
        EventBus.getDefault().unregister(this);//反注册
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (!appData.isOpenFace()) {
            log.i("不开启人脸识别！");
            return START_STICKY;
        }
        if (intent != null) {
            String action = intent.getAction();
            if ("startFaceDetect".equals(action)) {//开启相机预览
                startFaceDetect();
            } else if ("exitFaceDetect".equals(action)) {//关闭相机预览
                exitFaceDetect();
            }
        }
        return START_STICKY;
    }

    /***
     * 设置拍照初始化界面
     */
    void initLayout() {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mLayoutView = (ViewGroup) View.inflate(getApplicationContext(), R.layout.activity_camera, null);
        mSurfaceView = (SurfaceView) mLayoutView.findViewById(R.id.surfaceView1);
        faceView = (FaceView) mLayoutView.findViewById(R.id.face_view);
    }

    /***
     * 初始化显示窗口的参数
     */
    void initWMParams() {
        wmParams = appData.getMywmParams();
        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		/*
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 */
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        // 设置悬浮窗的长、宽、位置等属性
        MySetWMParams();
    }

    //更新悬浮窗的位置和大小
    void updateViewPosition() {
        try{
            MySetWMParams();
            wm.updateViewLayout(mLayoutView, wmParams);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 设置相机悬浮窗的大小属性,并设置相机的界面
     * */
    void MySetWMParams(){
        try{
            wmParams.width = 600;
            wmParams.height = 400;
            wmParams.screenOrientation = appData.getScreenOrientation();
            wmParams.gravity = Gravity.RIGHT | Gravity.TOP; // 调整悬浮窗口至右上角
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***
     * 开启相机识别
     */
    public void startFaceDetect() {
        log.i("startFaceDetect 开始相机识别 , 检查相机开启状态 "+isOpenCamera);
        if(!isOpenCamera){
            faceStatus = 0;
            isOpenCamera = true;
            appData.setFaceText("");
            SceneController.getInstance().isAddUserFace = false;
            SceneController.getInstance().isAskUserName = false;
            SceneController.getInstance().lastIflySemantic = "";
            SceneController.getInstance().lastQAnswer = null;
            //开启相机预览功能
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);//这一句话开启识别功能
            try{
                wm.addView(mLayoutView, wmParams);
            }catch (Exception e){
                e.printStackTrace();
                updateViewPosition();
            }
        }
    }

    /***
     * 停止相机识别
     */
    public void stopFaceDetect() {
        Log.i(TAG, "stopFaceDetect");
        stopGoogleFaceDetect();
        try{
            wm.removeView(mLayoutView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***
     * 退出相机识别
     */
    public void exitFaceDetect() {
        Log.i(TAG, "exitFaceDetect");
        try{
            if (mCamera != null) {
                try {
                    stopGoogleFaceDetect();
                    mCamera.setPreviewCallback(null);
                    mCamera.autoFocus(null);
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if (wm != null) {
                wm.removeView(mLayoutView);
                sendOutBroad("0005", "stoped");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        isOpenCamera = false;
    }

    /**
     * 广播事件处理
     */
    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        String notifyType = event.getNotifyType();
        String notifyData = event.getNotifyData();
        JSONObject jsonInputParams = null;
        try {
            jsonInputParams = new JSONObject(notifyData);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (notifyType != null && notifyType.equals("0003")) {
            // 图片对比 //faceCompare(notifyData);
            try {
                comparePictureDst = jsonInputParams.getString("pic_2");
                Log.i(TAG, "comparePicture, Dst = " + comparePictureDst);
                if (!FileUtil.fileExists(comparePictureDst)) {
                    Log.i(TAG, comparePictureDst + "文件不存在，比对失败");
                    return;
                }
                needComparePicture = true;
                Log.i(TAG, "准备对比");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (notifyType != null && notifyType.equals("0004")) {
            // 上传用户头像
            // Log.i(TAG, "上传用户头像");
        } else if (notifyType != null && notifyType.equals("0005")) {
            // 开启或停止人脸识别
            try {
                String action_name = jsonInputParams.getString("action_name");
                if (action_name != null && action_name.equals("start")) {
                    startFaceDetect();
                } else if (action_name != null && action_name.equals("stop")) {
                    exitFaceDetect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (notifyType != null && notifyType.equals("0007")) {
            //退出Service;
            exitFaceDetect();
            stopSelf();
        }
    }

    /*
     * 对外部应用程序发送广播 notifyType 通知类型 00-心跳通知 01-本地人脸识别成功 02-在线识别成功 notifyData 数据
     */
    void sendOutBroad(String notifyType, String notifyData) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Intent mIntent = new Intent(Constants.BROADCAST_ACTION_NAME);
        mIntent.putExtra("NotifyType", notifyType);
        mIntent.putExtra("NotifyTime", format.format(new Date()));
        mIntent.putExtra("NotifyData", notifyData);
        Log.i(TAG, "准备发送广播，notifyType = " + notifyType + ", notifyData = " + notifyData);
        getApplicationContext().sendBroadcast(mIntent);
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.CAMERA_HAS_STARTED_PREVIEW: // 开始检测
                    startGoogleFaceDetect();
                    break;
                case Constants.FACE_DETECTED:
                    endTime = System.currentTimeMillis();
                    Log.d(TAG,"startTime = "+startTime+",endTime = "+endTime+"，"+(endTime-startTime));
                    if ((endTime-startTime)<intervalTime){
                        Face[] faces = (Face[]) msg.obj;
                        if (faces.length > 0) {
                            Log.i(TAG, "Google检测到人脸");
                            for (int iFace = 0; iFace < faces.length; iFace++) {
                                Log.i(TAG, "face idx = " + iFace + ",  score = " + faces[iFace].score);
                            }
                            if (faceStatus == 0) {
                                faceStatus = 1;
                                sendOutBroad("0001", "本地识别出" + faces.length + "张人脸"); // 将识别结果通过广播发送出去。
                            } else {
                                Log.i(TAG, "当前非空闲状态，不进入处理流程");
                            }
                        } else {
                            sendOutBroad("0000", "心跳包");
                            Log.i(TAG, "Google没有检测到人脸");
                            faceStatus = 0;
                        }
                    }else{
                        if(appData.getFaceResultListener()!=null){
                            if(appData.getFaceResultListener().dealFace(true,null)){
                                appData.setFaceResultListener(null);
                            }
                        }
                    }
                    break;
                case Constants.FACE_DETECT_SUCC:
                    String identifyResult = (String) msg.obj;
                    sendOutBroad("0002", identifyResult); // 将识别结果通过广播发送出去。
                    faceStatus = 0; // 修改状态
                    break;
                case Constants.PICTURE_COMPARE_SUCC:
                    String compareResult = (String) msg.obj;
                    sendOutBroad("0003", compareResult); // 将识别结果通过广播发送出去。
                    break;
                case Constants.PICTURE_COMPARE_PROCESS:
                    String notifyResult = (String) msg.obj;
                    sendOutBroad("0006", notifyResult); // 将识别通知通过广播发送出去。
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public void startGoogleFaceDetect() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            if (params.getMaxNumDetectedFaces() > 0) {
                try {
                    mCamera.setFaceDetectionListener(googleFaceDetect);
                    mCamera.startFaceDetection();
                } catch (Exception e) {
                    Log.e(TAG, "startGoogleFaceDetect error");
                }
                googleDetectRunning = true;
                if (faceView != null) {
                    faceView.clearFaces();
                }
            } else {
                Log.e(TAG, "硬件不支持Google人脸识别");
            }
        } else {
            Log.e(TAG, "mCamera is null!");
        }
    }

    public void stopGoogleFaceDetect() {
        try {
            if (mCamera == null) {
                Log.i(TAG, "mCamera==NULL");
                return;
            }
            googleDetectRunning = false;
            mCamera.setFaceDetectionListener(null);
            mCamera.stopFaceDetection();
            if (faceView != null) {
                faceView.clearFaces();
            }
        } catch (Exception e) {
            Log.e(TAG, "stopGoogleFaceDetects error");
        }
    }

    public int FindFrontCamera() {
        if (Build.VERSION.SDK_INT >= 9) {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras(); // get cameras number
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return camIdx;
                }
            }
        }
        return -1;
    }

    public int FindBackCamera() {
        try {
            if (Build.VERSION.SDK_INT >= 9) {
                int cameraCount = 0;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras(); // get cameras number
                for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                    Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        return camIdx;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated "+",画面开始时间 "+System.currentTimeMillis());
//        updateViewPosition();
        startTime = System.currentTimeMillis();
        if (mCamera == null) {
            int CammeraIndex = -1;//默认设置
            CammeraIndex = FindFrontCamera();
            Log.i(TAG, "首先使用前置摄像头 " + CammeraIndex);
            if (CammeraIndex == -1) {
                CammeraIndex = FindBackCamera();
            }
            if (CammeraIndex == -1) {
                Toast.makeText(RyApplication.getContext(), "摄像头打开失败啦！", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                mCamera = Camera.open(appData.getDefaultCamera());
                mCamera.setPreviewDisplay(holder);// 设置显示面板控制器
                previewCallBack pre = new previewCallBack();//建立预览回调对象
                mCamera.setPreviewCallback(pre); //设置预览回调对象
            } catch (Exception e) {
                e.printStackTrace();
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
                Toast.makeText(RyApplication.getContext(), "摄像头打开失败", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                //打印硬件支持的保存/预览图片的长宽
                CamParaUtil.getInstance().printSupportPictureSize(parameters);
                CamParaUtil.getInstance().printSupportPreviewSize(parameters);

//                parameters.setPictureSize(appData.getPictureSizeWidth(), appData.getPictureSizeHeight());
//                parameters.setPreviewSize(appData.getPreviewSizeWidth(), appData.getPreviewSizeHeight());

                parameters.setPictureSize(352, 288);
                parameters.setPreviewSize(352, 288);

                mCamera.setDisplayOrientation(appData.getPreviewRotate());
                //打印硬件支持的相机功能，比如自动对焦
                CamParaUtil.getInstance().printSupportFocusMode(parameters);
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains("continuous-picture")) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                mCamera.setParameters(parameters);
                parameters = mCamera.getParameters(); //重新get一次
                Log.d(TAG, "最终设置:PreviewSize--With = " + parameters.getPreviewSize().width + "Height = " + parameters.getPreviewSize().height);
                holder.setKeepScreenOn(true);
                /* 打开预览画面 */
                mCamera.startPreview();
                mMainHandler.sendEmptyMessageDelayed(Constants.CAMERA_HAS_STARTED_PREVIEW, 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try{
            Log.i(TAG, "surfaceDestroyed");
            if (mCamera != null) {
                try {
                    stopGoogleFaceDetect();
                    mCamera.setPreviewCallback(null);
                    mCamera.autoFocus(null);
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(wm!=null){
                wm.removeView(mLayoutView);
            }
            isOpenCamera = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 检测摄像头是否存在的私有方法
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 摄像头存在
            return true;
        } else {
            // 摄像头不存在
            return false;
        }
    }

    // 每次cam采集到新图像时调用的回调方法，前提是必须开启预览
    class previewCallBack implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            final byte[] mData = data;
            if (needComparePicture) {
                Log.i(TAG, "PreviewCallback, 进入图片比较流程");
                needComparePicture = false;
                //图片比较
                Thread compareThread = new Thread() {
                    @Override
                    public void run() {
                        serialNo = SequenceUtil.makeSerialNo();
                        Log.i(TAG, "compareThread, serialNo = " + serialNo);
                        long startMills;
                        long globalStartMills = System.currentTimeMillis();
                        Bitmap srcBitmap = null;
                        byte[] imgData1 = null;
                        Log.i(TAG, "compareThread, serialNo = " + serialNo + ", 启动线程准备处理图像");
                        Size size = mCamera.getParameters().getPreviewSize();
                        String localFileName = "";
                        try {
                            startMills = System.currentTimeMillis();
                            YuvImage image = new YuvImage(mData, ImageFormat.NV21, size.width, size.height, null);
                            if (image != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                                srcBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                                Log.d(TAG,"图片的大小"+stream.size());
                                if (appData.getPictureRotate() == 0) {
                                    imgData1 = stream.toByteArray();
                                    stream.close();
                                    localFileName = FileUtil.saveBitmap(serialNo, srcBitmap);
                                } else {//图片需要旋转
                                    Log.i(TAG, "compareThread, serialNo = " + serialNo + ", 保存的图片需要旋转");
                                    ByteArrayOutputStream newStream = new ByteArrayOutputStream();
                                    Matrix matrix = new Matrix();
                                    //图片旋转方向
                                    matrix.postRotate(appData.getPictureRotate());
                                    Bitmap newBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix,
                                            false);
                                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, newStream);
                                    imgData1 = newStream.toByteArray();
                                    newStream.close();
                                    localFileName = FileUtil.saveBitmap(serialNo, newBitmap);
                                }
                                Log.i(TAG, "compareThread, serialNo = " + serialNo + ", 文件保存完成, 耗时 = " + (System.currentTimeMillis() - startMills));
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "compareThread, serialNo = " + serialNo + ", 处理图像出现错误: " + ex.getMessage());
                            return;
                        }
                        //这里送个消息出去，表示正在处理
                        JSONObject notifyJson = new JSONObject();
                        try {
                            notifyJson.put("result", true);
                            notifyJson.put("remark", "识别进行中");
                            Message m1 = mMainHandler.obtainMessage();
                            m1.what = Constants.PICTURE_COMPARE_PROCESS;
                            m1.obj = notifyJson.toString();
                            m1.sendToTarget();
                        } catch (Exception e) {

                        }

                        try {
                            byte[] imgData2 = ImageUtil.getScaledBitmapByteArray(comparePictureDst, 600);
                            JSONObject compareJsonResult = ((ApiBase) Class.forName(appData.getmApiName()).newInstance()).comparePicture(imgData1,
                                    imgData2);
                            Log.i(TAG, "compareThread, serialNo = " + serialNo + ", jsonResult = " + compareJsonResult.toString());
                            compareJsonResult.put("result", compareJsonResult.getInt("apiResult") == HttpStatus.SC_OK);
                            compareJsonResult.put("remark", compareJsonResult.getString("apiResultDesc"));
                            long timeConsuming = (System.currentTimeMillis() - globalStartMills);
                            compareJsonResult.put("pic_1", localFileName);
                            compareJsonResult.put("pic_2", comparePictureDst);
                            compareJsonResult.put("tim 　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　   eConsuming", timeConsuming); //将耗时也传回去
                            Message m = mMainHandler.obtainMessage();
                            m.what = Constants.PICTURE_COMPARE_SUCC;
                            m.obj = compareJsonResult.toString();
                            m.sendToTarget();

                            if (!appData.getKeepCameraPicture()) {
                                //处理完成后删除本地的图片;
                                FileUtil.fileDelete(serialNo, localFileName);
                            }
                            Log.i(TAG, "compareThread, serialNo = " + serialNo + ",  处理总耗时 = " + (System.currentTimeMillis() - globalStartMills));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "compareThread, serialNo = " + serialNo + ", 处理图像出现错误: " + e.getMessage());
                        }
                    }
                };
                compareThread.start();
            } else if (faceStatus == 1 && googleDetectRunning) {
                Log.i(TAG, "PreviewCallback, 进入人脸识别流程");
                Thread identifyThread = new Thread() {
                    @Override
                    public void run() {
                        serialNo = SequenceUtil.makeSerialNo();
                        Log.i(TAG, "identifyThread, serialNo = " + serialNo);
                        long startMills;
                        long globalStartMills = System.currentTimeMillis();
                        Bitmap srcBitmap = null;
                        byte[] arrayImg = null;
                        Log.i(TAG, "identifyThread, serialNo = " + serialNo + ", 启动线程准备处理图像");
                        faceStatus = 2;
                        Size size = mCamera.getParameters().getPreviewSize();
                        String localFileName = "";
                        try {
                            startMills = System.currentTimeMillis();
                            YuvImage image = new YuvImage(mData, ImageFormat.NV21, size.width, size.height, null);
                            if (image != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                                srcBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                                Log.d(TAG,"图片大小 " +stream.toByteArray().length);
                                if (appData.getPictureRotate() == 0) {
                                    arrayImg = stream.toByteArray();
                                    stream.close();
                                    localFileName = FileUtil.saveBitmap(serialNo, srcBitmap);
                                } else {//图片需要旋转
                                    Log.i(TAG, "identifyThread, serialNo = " + serialNo + ", 保存的图片需要旋转");
                                    ByteArrayOutputStream newStream = new ByteArrayOutputStream();
                                    Matrix matrix = new Matrix();
                                    //图片旋转方向
                                    matrix.postRotate(appData.getPictureRotate());
                                    Bitmap newBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix,
                                            false);
                                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, newStream);
                                    arrayImg = newStream.toByteArray();
                                    newStream.close();
                                    localFileName = FileUtil.saveBitmap(serialNo, newBitmap);
                                }
                                Log.i(TAG, "identifyThread, serialNo = " + serialNo + ", 文件保存完成, 耗时 = " + (System.currentTimeMillis() - startMills)+"，文件地址 = "+localFileName);
                            } else {
                                Log.i(TAG, "identifyThread, serialNo = " + serialNo + ", 无法采集到图像，处理失败！");
                                return;
                            }
                        } catch (Exception ex) {
                            Log.i(TAG, "identifyThread, serialNo = " + serialNo + ", 处理图像出现错误: " + ex.getMessage());
                            return;
                        }
                        if (!googleDetectRunning)
                            return;
                        try {
                            startMills = System.currentTimeMillis();
                            Log.i(TAG, "开始人脸识别！！！！");
                            JSONObject jsonResult = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).search(SharedPreferencesController.getInstance().getFacesetToken(), arrayImg);
                            log.i("查询相似的人脸接口jsonResult=" + jsonResult);
                            //将缓存的人脸图片保存到缓存中
                            jsonResult.put("localFileName", localFileName);
                            if(appData.getPerson()!=null){
                                appData.getPerson().setFacePath(localFileName);
                            }
                            if (!googleDetectRunning || jsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
                                log.i("在线识别相似人脸的接口返回失败或没有识别结果在规定时间段内重置标志位");
                                FaceResultListener faceResultListener = appData.getFaceResultListener();
                                if (faceResultListener != null) {
                                    if (faceResultListener.dealFace(true, null)) {
                                        appData.setFaceResultListener(null);
                                    }
                                    stopFaceDetect();
                                }
                            }else{
                                appData.setFaceText(jsonResult.toString());
                                int faceCount = jsonResult.getJSONArray("faces").length();
                                log.i("识别到相似的人脸数目 = " + faceCount);
                                if (faceCount > 0) {
                                    //返回将识别结果
                                    long timeConsuming = (System.currentTimeMillis() - globalStartMills);
                                    jsonResult.put("localFileName", localFileName);
                                    jsonResult.put("timeConsuming", timeConsuming); //将耗时也传回去
                                    //遍历返回结果
                                    if (jsonResult.has("results")) {
                                        List<String> confidenceList = new ArrayList<>();
                                        JSONArray resultsJsonArray = jsonResult.getJSONArray("results");
                                        for (int i = 0; i < resultsJsonArray.length(); i++) {
                                            JSONObject jsonObject = resultsJsonArray.getJSONObject(i);
//                                            String face_token = jsonObject.getString("face_token");//唯一标示
//                                            String user_id = jsonObject.getString("user_id");//用户姓名
                                            String confidence = jsonObject.getString("confidence");//相识度
                                            confidenceList.add(confidence);
                                        }
                                        String maxConf = Collections.max(confidenceList);
                                        double maxConfDouble = Double.parseDouble(maxConf);
                                        Log.i(TAG, "最大的相似度是：" + maxConf);
                                        if (maxConfDouble > 70) {
                                            if(appData.getPerson()!=null) {
                                                appData.getPerson().setNewPerson(false);
                                            }
                                            for (int i = 0; i < resultsJsonArray.length(); i++) {
                                                JSONObject jsonObject = resultsJsonArray.getJSONObject(i);
                                                String face_token = jsonObject.getString("face_token");//唯一标示
                                                String user_id = jsonObject.getString("user_id");//用户姓名
                                                String confidence = jsonObject.getString("confidence");//相识度
                                                if (confidence.equals(maxConf)) {
                                                    Log.i(TAG, "找到的最相识的人是：" + user_id);
                                                    //将人的名字缓存进入缓存中
                                                    if(appData.getPerson()!=null){
                                                        appData.getPerson().setName(user_id);
                                                        appData.getPerson().setId(face_token);
                                                    }
                                                    boolean  bResult = RRBusinessApi.getInstance().getPerson(face_token,appData.getPerson());
                                                    if(bResult){
                                                        FaceResultListener faceResultListener = appData.getFaceResultListener();
                                                        if (faceResultListener != null) {
                                                            if (faceResultListener.dealFace(false, jsonResult.toString())) {
                                                                appData.setFaceResultListener(null);
                                                            }
                                                            stopFaceDetect();
                                                        }
                                                    }else{
                                                        faceStatus = 0;
                                                    }
//                                            JSONObject analyzejsonResult = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).faceAnalyze(face_token);
//                                            Log.i(TAG, "analyzejsonResult===：" + analyzejsonResult);
                                                    break;
                                                }
                                            }
                                        } else {
                                            FaceResultListener faceResultListener = appData.getFaceResultListener();
                                            Log.i(TAG, "不认识用户返回信息：" + (faceResultListener!=null));
                                            if (faceResultListener != null) {
                                                if (faceResultListener.dealFace(true, null)) {
                                                    appData.setFaceResultListener(null);
                                                }
                                                stopFaceDetect();
                                            }
                                        }
                                    } else {
                                        faceStatus = 0;
                                        Log.i(TAG, "没有返回人脸信息，不认识!");
                                    }
                                    //没有特别的作用目前，只是将结果广播出去
                                    Message m = mMainHandler.obtainMessage();
                                    m.what = Constants.FACE_DETECT_SUCC;
                                    m.obj = jsonResult.toString();
                                    m.sendToTarget();
                                } else {
                                    log.i("识别结果中没有返回人脸");
                                    long timeConsuming = (System.currentTimeMillis() - globalStartMills);
                                    jsonResult.put("localFileName", localFileName);
                                    jsonResult.put("timeConsuming", timeConsuming); //将耗时也传回去
                                    jsonResult.put("reultType", "1");
                                    faceStatus = 0;
                                    Message m = mMainHandler.obtainMessage();
                                    m.what = Constants.FACE_DETECT_SUCC;
                                    m.obj = jsonResult.toString();
                                    m.sendToTarget();
                                }
//                            if (!appData.getKeepCameraPicture()) {
//                                //处理完成后删除本地的图片;
//                                FileUtil.fileDelete(serialNo, localFileName);
//                            }
                                Log.i(TAG, "identifyThread, serialNo = " + serialNo + ", 处理总耗时 = " + (System.currentTimeMillis() - globalStartMills));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "identifyThread, serialNo = " + serialNo + ", 处理图像出现错误: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                };
                identifyThread.start();
            }
        }
    }

}
