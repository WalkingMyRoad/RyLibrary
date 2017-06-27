package com.ist.rylibrary.base.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.AiuiController;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.entity.FinalQASemanticSlotsBean;
import com.ist.rylibrary.base.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by maxy
 * on 2017/5/26.
 * 图片
 */
public class BaseImageViewActivity extends BaseActivity{
    private ImageView image_view;
    protected DisplayImageOptions options;
    private String path = "";
    private boolean isBad = false;  //图片是否损坏

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        options = getOptions();

    }
    public DisplayImageOptions getOptions(){
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .considerExifParams(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    /**设置图片*/
    public void setImg(final String filepath){
        if(image_view==null){
            image_view.findViewById(R.id.image_view);
        }
        try{
            String standPath =filepath;
            log.i("图片的路径是："+standPath);
            if(standPath.indexOf("/") > -1){
                String[] data = standPath.split("/");
                if(data != null && data.length >0){
                    path = data[data.length-1];
                    Log.i("wjz","path="+ path);
                    if(isExit(path)&& !isBad){
                        standPath = "file://" + Environment.getExternalStorageDirectory()+File.separator + "imageloader/pic/" + path;
                        Log.i("wjz","图片的路径="+ standPath);
                    }
                }
            }
            RyApplication.getImageLoader().displayImage(standPath, image_view, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        isBad = true;
                        switch (failReason.getType()){
                            case OUT_OF_MEMORY:
                                setImg(filepath);
                                break;
                        }
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        isBad = false;
                        try {
                            if(!path.equals("")&&!isExit(path)){
                                saveBitmap(bitmap, path);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isExit(String bitName){
        boolean isExit = false;
        String path = Environment.getExternalStorageDirectory()+File.separator + "imageloader/pic/";
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        File[] files = file.listFiles();
        if(files != null &&files.length > 0){
            for(int i= 0;i < files.length;i++){
                if(bitName.equals(files[i].getName())){
                    isExit = true;
                    break;
                }else {
                    isExit =  false;
                }
            }
        }else {
            isExit = false;
        }
        return isExit;
    }
    private void saveBitmap(final Bitmap bitmap, final String bitName) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory()+File.separator + "imageloader/pic/";
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                File[] files = file.listFiles();
                if(files != null &&files.length >=200){  //图片超过200张，内存占用大概是100M左右删除以前图片
                    for(int i= 0;i < files.length;i++){
                        files[i].delete();
                    }
                }
                File file1 = new File(file,bitName);
                try{
                    FileOutputStream out = new FileOutputStream(file1);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //从内存缓冲中取图片
    public  Bitmap getCacheImage(String uri){//这里的uri一般就是图片网址
        log.i("开始从内存中取图片");
        List<String> memCacheKeyNameList = MemoryCacheUtils.findCacheKeysForImageUri(uri,RyApplication.getImageLoader().getMemoryCache());
        if(memCacheKeyNameList != null && memCacheKeyNameList.size() > 0){
            log.i("内存中有图片=="+RyApplication.getImageLoader().getMemoryCache().get(memCacheKeyNameList.get(0)));
            return RyApplication.getImageLoader().getMemoryCache().get(memCacheKeyNameList.get(0));
        }
        return null;
    }

    //从本地缓冲中取图片
    public  Bitmap getDiscCacheImage(String uri){//这里的uri一般就是图片网址
        log.i("开始从本地中取图片");
        File file = DiskCacheUtils.findInCache(uri,RyApplication.getImageLoader().getDiskCache());
        try {
            if(!file.exists()){
                return null;
            }
            String path= file.getPath();
            log.i("本地有图片=="+path);
            return BitmapFactory.decodeFile(path);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent==null){
                log.i("intent==null");
                return;
            }
            String action = intent.getAction();
            if (action.equals("com.ist.rr.showImage")) {
                log.i("接收到图片展示的广播啦！");
                Bundle bundle=intent.getExtras();
                if(bundle!=null){
                    String imagePath=bundle.getString("imagePath");
                    log.i("imagePath=="+imagePath);
                    setImg(imagePath);
                }else{
                    log.i("没有获取到图片！");
                }
            }
        }
    };
    @Override
    public int getMainView() {
        return 0;
    }

    @Override
    public void initController() {
        image_view=(ImageView)findViewById(R.id.image_view);
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public int getBody() {
        return R.layout.acivity_image_view;
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


    @Override
    protected void onResume() {
        super.onResume();
        AiuiController.getInstance().setSpecialAiuiListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ist.rr.showImage");
        registerReceiver(receiver, intentFilter);
        try{
            Bundle bundle=this.getIntent().getExtras();
            if(bundle!=null){
                String imagePath=bundle.getString("imagePath");
                log.i("imagePath=="+imagePath);
                setImg(imagePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
