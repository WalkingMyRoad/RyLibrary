package com.ist.rylibrary.base.module;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ist.nativepackage.EyesCtrl;
import com.ist.nativepackage.RR;
import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.ActivityController;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.LocalDataController;
import com.ist.rylibrary.base.entity.FinalQASemanticSlotsBean;
import com.ist.rylibrary.base.service.AiuiService;
import com.ist.rylibrary.base.util.TimeUtil;
import com.ist.rylibrary.base.util.ToolUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by minyuchun on 2017/3/21.
 */

public class BaseDormancyActivity extends BaseActivity{
    /**休眠页面展示图片*/
    protected ImageView imgDormancy;
    /**存储图片地址*/
    private String DORMANCY_IMAGE = File.separator+"dormancy";

    protected DisplayImageOptions options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = getOptions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AiuiController.getInstance().post(AiuiService.AIUI_TYPE_CLOSE);
        AiuiController.getInstance().setAutoWakeUp(false);
        try{
            EyesCtrl.getInstance().EyeWork();
        }catch (Exception e){
            e.printStackTrace();
        }
        initDormancyImage();
    }

    public DisplayImageOptions getOptions(){
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .considerExifParams(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeUtil.getInstance().stopTimer();
    }
    /**设置图片*/
    public void setImgDormancy(final String filepath){
        try{
            String standPath = "file://"+filepath;
            RyApplication.getImageLoader().displayImage(standPath, imgDormancy, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    switch (failReason.getType()){
                        case OUT_OF_MEMORY:
                            setImgDormancy(filepath);
                            break;
                    }
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**获取图片路径*/
    private List<String> getImageFile(String filePath){
        List<String> imgList = new ArrayList<>();
        try{
            List<String> fileList = ToolUtil.getInstance().getFiles(filePath);
            for (String name:fileList){
                if(name.endsWith(".png") || name.endsWith(".jpg")){
                    imgList.add(name);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return imgList;
    }


    @Override
    public int getMainView() {
        return 0;
    }

    @Override
    public void initController() {
        imgDormancy = (ImageView) findViewById(R.id.dormancy_base);
        imgDormancy.setOnClickListener(this);
    }

    @Override
    public void initView() {
    }

    /***
     * 设置休眠页面图片
     */
    private void initDormancyImage(){
        try{
            final File file = new File(LocalDataController.getInstance().getBASE_PATH_LOCAL_DATA()+DORMANCY_IMAGE);
            if(!file.exists()){
                file.mkdirs();
            }
            final List<String> fileImg = getImageFile(file.getPath());
            RyApplication.getLog().d("休眠图片的数目 "+fileImg);
            if (fileImg!=null && fileImg.size()>0){
                setImgDormancy(file.getPath() + File.separator + fileImg.get(0));
                if(fileImg.size() >1){
                    TimeUtil.getInstance().startTimer(new TimeUtil.TimerListener() {
                        @Override
                        public boolean complete() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RyApplication.getLog().d("设置休眠页面的图片 ");
                                    String path = file.getPath() + File.separator + fileImg.get(new Random().nextInt(fileImg.size()));
                                    setImgDormancy(path);
                                }
                            });
                            return false;
                        }
                        @Override
                        public boolean stop() {
                            return true;
                        }
                    },3000,3000);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getBody() {
        return R.layout.activity_dormancy_base;
    }

    @Override
    public int[] getButtonText() {
        return new int[0];
    }

    @Override
    public void FooterOnClick(int index) {

    }
    @Override
    public void onRyClick(View view) {

    }


    @Override
    public boolean numberResult(String text, String code, FinalQASemanticSlotsBean slotsBean) {
        return false;
    }

    @Override
    public boolean cmdResult(String text, String item, FinalQASemanticSlotsBean slotsBean) {
        return false;
    }
}
