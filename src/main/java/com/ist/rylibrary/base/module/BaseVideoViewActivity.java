package com.ist.rylibrary.base.module;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ist.rylibrary.R;
import com.ist.rylibrary.base.controller.SceneController;
import com.ist.rylibrary.base.entity.FinalQASemanticSlotsBean;


/**
 * Created by maxy
 * on 2017/5/12.
 * 视频播放
 */
public class BaseVideoViewActivity extends BaseActivity{
    private  String TAG="BaseVideoViewActivity";
    private VideoView videoView;
    private MediaController mMediaController;
    private  Activity activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
    }

    @Override
    public int getMainView() {
        return 0;
    }


    @Override
    public void initController() {
        videoView=(VideoView)findViewById(R.id.videoView);
    }

    @Override
    public void initView() {

    }

    @Override
    public int getBody() {
        return R.layout.layou_video_view;
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
    protected void onResume() {
        super.onResume();
        try {
            Bundle bundle=this.getIntent().getExtras();
            if(bundle!=null){
                String path=bundle.getString("path");
                playVideo(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       stopVideoView();
    }

    /**
     * 停止视频的播放
     * */
    public  void stopVideoView(){
        if(videoView!=null){
            Log.i(TAG,"停止播放");
            videoView.pause();
        }
    }

    public  void playVideo(final  String path){
        try{
            Uri uri = Uri.parse(path);
            videoView.setVisibility(View.VISIBLE);
            Log.i(TAG,"视频的地址："+uri.toString());
//            mMediaController = new MediaController(this);
//            videoView.setMediaController(mMediaController);
//            mMediaController.setVisibility(View.VISIBLE);
//            mMediaController.setMediaPlayer(videoView);

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.i(TAG,"立体式回复》》》视频出错，结束");
                    SceneController.getInstance().isVideoCompleted = true;
                    return false;
                }
            });
            videoView.setVideoURI(uri);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG,"立体式回复》》》视频结束");
//                    videoView.setVisibility(View.GONE);
                    SceneController.getInstance().isVideoCompleted = true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

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
