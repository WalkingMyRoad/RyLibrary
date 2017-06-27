package com.ist.rylibrary.face.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.face.activity.ConfigActivity;
import com.ist.rylibrary.face.activity.FloatCamera;
import com.ist.rylibrary.face.util.Constants;
import com.ist.rylibrary.face.util.MessageEvent;
import com.wewins.facelibrary.api.ApiBase;
import com.wewins.facelibrary.utils.FileUtil;
import com.wewins.facelibrary.utils.ImageUtil;

import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * 从业务程序发过来的广播通知在此类中处理
 */
public class DetectReceiver extends BroadcastReceiver {
    private static final String TAG = "DetectReceiver";

    private Context mContext = null;
    private RyApplication appData;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            appData = (RyApplication) context.getApplicationContext();
            mContext = context;
            String notifyType = intent.getStringExtra("NotifyType");
            String notifyData = intent.getStringExtra("NotifyData");
            String notifyTime = intent.getStringExtra("NotifyTime");
            Log.i(TAG, "接收到的消息");
            Log.i(TAG, "NotifyType = " + notifyType);
            Log.i(TAG, "NotifyTime = " + notifyTime);
            Log.i(TAG, "NotifyData = " + notifyData);
            if (notifyType != null && notifyType.equals("0003")) {
                // 图片对比
                try {
                    JSONObject jsonData = new JSONObject(notifyData);
                    String pic_1 = jsonData.getString("pic_1");
                    String pic_2 = jsonData.getString("pic_2");
                    if (pic_2 == null || pic_2.length() == 0) {
                        Log.i(TAG, pic_2 + "参数pic_2不能为空！");
                        sendBroad(notifyType, getReturnObj(false, "参数pic_2不能为空！").toString());
                        return;
                    }
                    if (!FileUtil.fileExists(pic_2)) {
                        Log.i(TAG, pic_2 + "文件(pic_2)不存在，不能比较");
                        sendBroad(notifyType, getReturnObj(false, "文件(pic_2)不存在，不能比较").toString());
                        return;
                    }

                    if (pic_1 != null && pic_1.length() > 0) {
                        //如果传递了Pic_1进来，则在这里直接比较
                        //faceCompare(notifyData);
                        new PictureCompareTask().execute(notifyData);
                    } else {
                        //如果没有传递pic_1，则需要在CameraActivity中去当前摄像头的图片进行比较。
                        EventBus.getDefault().post(new MessageEvent(notifyType, notifyData));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else if (notifyType != null && notifyType.equals("0004")) {
                // 上传用户头像
                Log.i(TAG, "上传用户头像");
                //sendMsgToMainActivity(notifyType, notifyData);
                new NewPersonTask().execute(notifyData);
            } else if (notifyType != null && notifyType.equals("0005")) {
                // 开启或停止人脸识别
                Log.i(TAG, "开启或停止识别服务");
                EventBus.getDefault().post(new MessageEvent(notifyType, notifyData));
                //sendMsgToMainActivity(notifyType, notifyData);
            } else if (notifyType != null && notifyType.equals("0006")) {
                // 配置
                Log.i(TAG, "显示参数配置窗口");
                showConfigWindows();
            } else if (notifyType != null && notifyType.equals("0007")) {
                Log.i(TAG, "退出识别主程序");
                Intent mIntent = new Intent("startFaceDetect");
                mIntent.setClass(context, FloatCamera.class);
                context.stopService(mIntent);
                EventBus.getDefault().post(new MessageEvent(notifyType, notifyData));
            }
        } else {
            Log.i(TAG, "参数为空，不处理");
        }
    }

    void sendBroad(String notifyType, String notifyData) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Intent mIntent = new Intent(Constants.BROADCAST_ACTION_NAME);
        mIntent.putExtra("NotifyType", notifyType);
        mIntent.putExtra("NotifyTime", format.format(new Date()));
        mIntent.putExtra("NotifyData", notifyData);
        Log.i(TAG, "准备发送广播，notifyType = " + notifyType + ", notifyData = " + notifyData);
        mContext.sendBroadcast(mIntent);
    }

    void showConfigWindows() {
        /*
        try {
			StandOutWindow.closeAll(mContext, CameraActivity.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			StandOutWindow.show(mContext, ConfigWindow.class, ConfigWindow.DEFAULT_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
        //Intent intent = new Intent(MainActivity.this, FPPersonListActivity.class);
        //startActivity(FPPersonListIntent);
        Intent newIntent = new Intent(mContext, ConfigActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(newIntent);
    }

    //图片对比
    private class PictureCompareTask extends AsyncTask<String, String, Boolean> {
        JSONObject resultJson = null;
        long globalStartMills;

        @Override
        protected void onPreExecute() {
            globalStartMills = System.currentTimeMillis();
            try {
                resultJson = new JSONObject();
                resultJson.put("result", false);// 默认失败
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean bResult = false;
            String pic_1 = "";
            String pic_2 = "";
            //判断输入参数是否合法
            try {
                JSONObject inputParamsJason = new JSONObject(params[0]);
                pic_1 = inputParamsJason.getString("pic_1");
                pic_2 = inputParamsJason.getString("pic_2");
                resultJson.put("pic_1", pic_1); //原样返回
                resultJson.put("pic_2", pic_2); //原样返回

                if (pic_1 == null || pic_1.length() == 0) {
                    publishProgress("pic_1不能为空！");
                    return bResult;
                }
                if (!FileUtil.fileExists(pic_1)) {
                    publishProgress("pic_1不存在！");
                    return bResult;
                }

                if (pic_2 == null || pic_2.length() == 0) {
                    publishProgress("pic_2不能为空！");
                    return bResult;
                }
                if (!FileUtil.fileExists(pic_2)) {
                    publishProgress("pic_2不存在！");
                    return bResult;
                }

                byte[] imgData1 = ImageUtil.getScaledBitmapByteArray(pic_1, 600);
                byte[] imgData2 = ImageUtil.getScaledBitmapByteArray(pic_2, 600);
                JSONObject compareJsonResult = ((ApiBase) Class.forName(appData.getmApiName()).newInstance())
                        .comparePicture(imgData1, imgData2);
                publishProgress(compareJsonResult.optString("apiResultDesc"));
                if (compareJsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
                    return bResult;
                }
                bResult = true;
                resultJson.put("result", bResult);
                resultJson.put("similarity", compareJsonResult.getDouble("similarity"));
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress("调用API接口recognitionCompare出错：" + e.getMessage().toString());
                return bResult;
            }
            publishProgress("图片对比完成");
            return bResult;
        }

        @Override
        protected void onProgressUpdate(String... progresses) {
            Log.i(TAG, "PictureCompareTask ProgressUpdate, " + progresses[0]);
            try {
                resultJson.put("remark", progresses[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Boolean executeResult) {
            try {
                //将耗时也传回去
                long timeConsuming = (System.currentTimeMillis() - globalStartMills);
                resultJson.put("timeConsuming", timeConsuming);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendBroad("0003", resultJson.toString());
            super.onPostExecute(executeResult);
        }
    }

    //新增Person
    private class NewPersonTask extends AsyncTask<String, String, Boolean> {
        String img_id = "";
        String face_Id = "";
        String person_id = "";
        String person_pic = "";
        String user_identify = "";
        String person_name = "";
        JSONObject resultJson = null;
        long globalStartMills;

        @Override
        protected void onPreExecute() {
            globalStartMills = System.currentTimeMillis();
            try {
                resultJson = new JSONObject();
                resultJson.put("result", false);// 默认失败
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean bResult = false;
            JSONObject inputParamsJason = null;

            //第一步，得到本地照片信息
            try {
                inputParamsJason = new JSONObject(params[0]);
                person_pic = inputParamsJason.getString("person_pic");
                user_identify = inputParamsJason.getString("user_identify");
                person_name = inputParamsJason.getString("person_name");
                resultJson.put("user_identify", user_identify); //原样返回
            } catch (Exception e) {
                e.printStackTrace();
                return bResult;
            }
            if (user_identify == null || user_identify.length() == 0) {
                publishProgress("user_identify不能为空！");
                return bResult;
            }
            if (person_pic == null || person_pic.length() == 0) {
                publishProgress("person_pic不能为空！");
                return bResult;
            }
            if (!FileUtil.fileExists(person_pic)) {
                publishProgress("person_pic不存在！");
                return bResult;
            }

            byte[] arrayImg = ImageUtil.getScaledBitmapByteArray(person_pic, 600);
            if (arrayImg == null || arrayImg.length == 0) {
                publishProgress("读取本地文件出错！");
                return bResult;
            }

            try {
                JSONObject newPersonResult = ((ApiBase) Class.forName(appData.getmApiName()).newInstance()).newPerson(person_name);
                if (newPersonResult.getInt("apiResult") != HttpStatus.SC_OK) {
                    publishProgress("newPerson出错, 错误消息 = " + newPersonResult.optString("apiResultDesc"));
                    return false;
                }
                person_id = newPersonResult.getString("person_id");
                publishProgress("person_id = " + person_id);
                resultJson.put("person_id", person_id);
                resultJson.put("remark", newPersonResult.optString("apiResultDesc"));
            } catch (Exception e) {
                Log.i(TAG, "新增Person出错");
                publishProgress("新增Person出错");
                e.printStackTrace();
                return false;
            }

            try {
                JSONObject newFaceResult = ((ApiBase) Class.forName(appData.getmApiName()).newInstance()).personNewFace(arrayImg, person_id);
                if (newFaceResult.getInt("apiResult") != HttpStatus.SC_OK) {
                    publishProgress("personNewFace出错, 错误消息 = " + newFaceResult.optString("apiResultDesc"));
                    return false;
                }
                String face_id = newFaceResult.getString("face_id");
                String img_id = newFaceResult.getString("img_id");
                publishProgress("face_id = " + face_id);
                resultJson.put("face_Id", face_Id);
                resultJson.put("img_id", img_id);

                publishProgress("Person添加Face完成");
            } catch (Exception e) {
                Log.i(TAG, "Person添加Face出错");
                publishProgress("Person添加Face出错");
                e.printStackTrace();
                return false;
            }

            //向RR服务器注册
            try {

                final Map<String, String> regParams = new HashMap<String, String>();
                regParams.put("personId", person_id);
                regParams.put("faceId", face_Id);
                regParams.put("imgId", img_id);

                JSONObject regJsonResult = RRApiServices.getInstance().apiPost("saveFaceToRRSvr", regParams, arrayImg);
                if (regJsonResult == null) {
                    publishProgress("服务器返回内容为空！");
                    return bResult;
                }
                if (regJsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
                    String errorMsg = regJsonResult.getString("apiResultDesc");
                    publishProgress("saveFaceToRRSvr, errorMsg = " + errorMsg);
                    return bResult;
                }
                int retcode = regJsonResult.getInt("retcode");
                publishProgress("RR注册返回结果，retcode = " + retcode);
                bResult = retcode == 0;
                resultJson.put("result", bResult);
            } catch (Exception e) {
                publishProgress("RR服务器注册出错，错误消息：" + e.getMessage().toString());
                e.printStackTrace();
                return bResult;
            }
            publishProgress("新增Person完成");
            return bResult;
        }

        @Override
        protected void onProgressUpdate(String... progresses) {
            Log.i(TAG, "NewPersonTask ProgressUpdate, " + progresses[0]);
            try {
                resultJson.put("remark", progresses[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Boolean executeResult) {
            try {
                //将耗时也传回去
                long timeConsuming = (System.currentTimeMillis() - globalStartMills);
                resultJson.put("timeConsuming", timeConsuming);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendBroad("0004", resultJson.toString());
            super.onPostExecute(executeResult);
        }
    }

    JSONObject getReturnObj(boolean result, String remark) {
        JSONObject returnJson = null;
        try {
            returnJson = new JSONObject();
            returnJson.put("result", result);
            returnJson.put("remark", remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJson;
    }

}
