package com.ist.rylibrary.face.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;

import com.wewins.facelibrary.api.ApiConstants;

import java.util.List;

//修改配置参数
public class ConfigActivity extends Activity {
    final static String TAG = "ConfigActivity";
    final static String[] mRotate = {"0", "90", "180", "270"};
    RyApplication appData;
    RadioGroup radioGroupCamera;
    RadioButton rb_defaultCamera_0;
    RadioButton rb_defaultCamera_1;
    RadioButton rb_screenOrientation_portrait;
    RadioButton rb_screenOrientation_landscape;
    CheckBox cbHidePreview;
    Spinner spinnerPreviewSize;
    Spinner spinnerPreviewRotate;
    Spinner spinnerPictureSize;
    Spinner spinnerPictureRotate;
    CheckBox cbKeepCameraPicture;
    RadioButton rb_api_faceplus_business;
    RadioButton rb_api_faceplus_free;
    RadioButton rb_api_nj_robot;
    Button button_save;
    ArrayAdapter previewSizeAdapter;
    ArrayAdapter pictureSizeAdapter;
    ArrayAdapter previewRotateAdapter;
    ArrayAdapter pictureRotateAdapter;
    List<Size> previewSizesList;
    List<Size> pictureSizesList;
    String[] mPreviewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        appData = (RyApplication) getApplicationContext();
        initView();
    }

    void initView() {
        rb_defaultCamera_0 = (RadioButton) findViewById(R.id.rb_defaultCamera_0);
        rb_defaultCamera_0.setChecked(appData.getDefaultCamera() == 0 ? true : false);

        rb_defaultCamera_1 = (RadioButton) findViewById(R.id.rb_defaultCamera_1);
        rb_defaultCamera_1.setChecked(appData.getDefaultCamera() == 1 ? true : false);
        //默认摄像头变更时，需要重新加载摄像头支持的预览和图片大小
        radioGroupCamera = (RadioGroup) findViewById(R.id.radioGroupCamera);
        radioGroupCamera.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb_defaultCamera_0.getId()) {
                    loadCameraParams(Camera.CameraInfo.CAMERA_FACING_BACK);
                } else if (checkedId == rb_defaultCamera_1.getId()) {
                    loadCameraParams(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }
            }
        });

        rb_screenOrientation_portrait = (RadioButton) findViewById(R.id.rb_screenOrientation_portrait);
        rb_screenOrientation_portrait.setChecked(appData.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? true : false);

        rb_screenOrientation_landscape = (RadioButton) findViewById(R.id.rb_screenOrientation_landscape);
        rb_screenOrientation_landscape.setChecked(appData.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false);

        spinnerPreviewSize = (Spinner) findViewById(R.id.spinnerPreviewSize);

        spinnerPreviewRotate = (Spinner) findViewById(R.id.spinnerPreviewRotate);
        previewRotateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mRotate);
        spinnerPreviewRotate.setAdapter(previewRotateAdapter);
        spinnerPreviewRotate.setSelection(previewRotateAdapter.getPosition(String.valueOf(appData.getPreviewRotate())), true);

        cbHidePreview = (CheckBox) findViewById(R.id.cbHidePreview);
        cbHidePreview.setChecked(appData.getHidePreview());


        spinnerPictureSize = (Spinner) findViewById(R.id.spinnerPictureSize);

        spinnerPictureRotate = (Spinner) findViewById(R.id.spinnerPictureRotate);
        pictureRotateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mRotate);
        spinnerPictureRotate.setAdapter(pictureRotateAdapter);
        spinnerPictureRotate.setSelection(pictureRotateAdapter.getPosition(String.valueOf(appData.getPictureRotate())), true);

        cbKeepCameraPicture = (CheckBox) findViewById(R.id.cbKeepCameraPicture);
        cbKeepCameraPicture.setChecked(appData.getKeepCameraPicture());

        rb_api_faceplus_business = (RadioButton) findViewById(R.id.rb_api_faceplus_business);
        rb_api_faceplus_business.setChecked(appData.getmApiName().equals(ApiConstants.API_FACEPLUS_BUSINESS) ? true : false);

        rb_api_faceplus_free = (RadioButton) findViewById(R.id.rb_api_faceplus_free);
        rb_api_faceplus_free.setChecked(appData.getmApiName().equals(ApiConstants.API_FACEPLUS_FREE) ? true : false);

        rb_api_nj_robot = (RadioButton) findViewById(R.id.rb_api_nj_robot);
        rb_api_nj_robot.setChecked(appData.getmApiName().equals(ApiConstants.API_NJ_ROBOT) ? true : false);

        button_save = (Button) findViewById(R.id.button_save);
        button_save.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                saveConfig();
            }
        });
    }

    //加载预览大小
    void loadCameraParams(int cameraId) {
        Camera mCamera = Camera.open(cameraId);
        try {
            if (mCamera != null) {
                Camera.Parameters parameters = mCamera.getParameters();
                //预览
                int currPreviewIdx = 0;
                previewSizesList = parameters.getSupportedPreviewSizes();
                mPreviewArray = new String[previewSizesList.size()];
                for (int iSize = 0; iSize < previewSizesList.size(); iSize++) {
                    Size size = previewSizesList.get(iSize);
                    mPreviewArray[iSize] = size.width + "x" + size.height;
                    if (appData.getPreviewSizeWidth() == size.width && appData.getPreviewSizeHeight() == size.height)
                        currPreviewIdx = iSize;
                }
                previewSizeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mPreviewArray);
                spinnerPreviewSize.setAdapter(previewSizeAdapter);
                spinnerPreviewSize.setSelection(currPreviewIdx, true);

                //图片
                int currPictureIdx = 0;
                pictureSizesList = parameters.getSupportedPictureSizes();
                String[] mPictureArray = new String[pictureSizesList.size()];
                for (int iSize = 0; iSize < pictureSizesList.size(); iSize++) {
                    Size size = pictureSizesList.get(iSize);
                    mPictureArray[iSize] = size.width + "x" + size.height;
                    if (appData.getPictureSizeWidth() == size.width && appData.getPictureSizeHeight() == size.height)
                        currPictureIdx = iSize;
                }
                pictureSizeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mPictureArray);
                spinnerPictureSize.setAdapter(pictureSizeAdapter);
                spinnerPictureSize.setSelection(currPictureIdx, true);
            } else {
                Toast.makeText(ConfigActivity.this, "无法打开摄像头！", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    @Override
    protected void onResume() {
        loadCameraParams(appData.getDefaultCamera());
        super.onResume();
    }

    //保存修改后的配置
    void saveConfig() {
        int iTemp = 0;
        //摄像头
        appData.setDefaultCamera(rb_defaultCamera_0.isChecked() ? Camera.CameraInfo.CAMERA_FACING_BACK
                : Camera.CameraInfo.CAMERA_FACING_FRONT);

        //屏幕方向
        appData.setScreenOrientation(rb_screenOrientation_portrait.isChecked() ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //预览窗口大小
        Size previewSize = previewSizesList.get(spinnerPreviewSize.getSelectedItemPosition());
        appData.setPreviewSizeWidth(previewSize.width);
        appData.setPreviewSizeHeight(previewSize.height);

        //预览窗口翻转
        appData.setPreviewRotate(Integer.valueOf(mRotate[spinnerPreviewRotate.getSelectedItemPosition()]));

        //预览窗口隐藏
        appData.setHidePreview(cbHidePreview.isChecked());

        //保存图片大小
        Size pictureSize = pictureSizesList.get(spinnerPictureSize.getSelectedItemPosition());
        appData.setPictureSizeWidth(pictureSize.width);
        appData.setPictureSizeHeight(pictureSize.height);

        //保存图片翻转
        appData.setPictureRotate(Integer.valueOf(mRotate[spinnerPictureRotate.getSelectedItemPosition()]));

        //保留采集的图像
        appData.setKeepCameraPicture(cbKeepCameraPicture.isChecked());


        if (rb_api_faceplus_business.isChecked()) {
            appData.setApiName(ApiConstants.API_FACEPLUS_BUSINESS);
        } else if (rb_api_faceplus_free.isChecked()) {
            appData.setApiName(ApiConstants.API_FACEPLUS_FREE);
        } else if (rb_api_nj_robot.isChecked()) {
            appData.setApiName(ApiConstants.API_NJ_ROBOT);
        }

        Toast.makeText(ConfigActivity.this, "完成参数修改，需要重启应用才能生效!", Toast.LENGTH_LONG).show();
    }
}
