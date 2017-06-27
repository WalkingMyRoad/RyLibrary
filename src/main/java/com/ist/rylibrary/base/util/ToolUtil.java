package com.ist.rylibrary.base.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.controller.SharedPreferencesController;
import com.ist.rylibrary.base.listener.FaceAddErrorListener;
import com.ist.rylibrary.base.listener.FaceCompareListener;
import com.wewins.facelibrary.api.ApiConstants;
import com.wewins.facelibrary.api.NewApiBase;
import com.wewins.facelibrary.api.rr.RRBusinessApi;
import com.wewins.facelibrary.utils.FileUtil;
import com.wewins.facelibrary.utils.ImageUtil;
import com.wewins.facelibrary.utils.Person;

import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by minyuchun on 2017/3/16.
 * 工具类
 * getAppName() 获取app名称
 * getAppPackageName() 获取程序的包名
 * getAppVersionName() 获取程序版本名称
 * getAppVersionCode() 获取程序的版本编号
 */

public class ToolUtil {
    /**工具类实例*/
    private static ToolUtil sToolUtil;

    public static ToolUtil getInstance(){
        if(sToolUtil == null){
            sToolUtil = new ToolUtil();
        }
        return sToolUtil;
    }

    public ToolUtil(){
    }

    /**
     * 获取当前程序app的名称
     * @return
     */
    public String getAppName(){
        PackageManager pm = RyApplication.getContext().getPackageManager();
        return RyApplication.getContext().getApplicationInfo().loadLabel(pm).toString();
    }

    /**
     * 获取包名
     * @return
     */
    public String getAppPackageName(){
        String packageName = null;
        try{
            packageName = RyApplication.getContext().getPackageName();
        }catch (Exception e){
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * 获取当前 app 的版本名称
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public String getAppVersionName() throws PackageManager.NameNotFoundException {
        return RyApplication.getContext().getPackageManager().getPackageInfo(getAppPackageName(), 0).versionName;
    }
    /**
     * 获取当前aoo的版本号
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public int getAppVersionCode() throws PackageManager.NameNotFoundException {
        return RyApplication.getContext().getPackageManager().getPackageInfo(getAppPackageName(), 0).versionCode;
    }

    /***
     * 获取当前运行程序中最上层的 如果是当前程序 就返回activity的名称
     * 不是当前程序则 返回空字符串
     * @param context
     * @return
     */
    public String getTopActivity(Context context) {
        try{
            ActivityManager manager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE) ;
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
            if(runningTaskInfos != null){
                ComponentName cn = runningTaskInfos.get(0).topActivity;
                if(cn.getPackageName().equals(context.getPackageName())){
                    return cn.getClassName();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /***
     * 存储本地的xml文件
     * @param map  存储数据
     * @param filePath  文件存储全路径
     */
    public void saveXML(Map<Object,Object> map,String filePath) {
        try {
            if (filePath.endsWith(".xml")) {
                File file = new File(filePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(fileOutputStream,"utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "persons");//设置标签
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Log.i("ToolUtil", "map的key = " + entry.getKey() + ",value = " + entry.getValue());
                }
                serializer.endTag(null, "persons");//设置尾部标签
                serializer.endDocument();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取设备分辨路
     * @param activity  传入当前的acticity
     * @return 返回分辨率匹配
     */
    public String getDrawableDpi(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return getDpi(dm.densityDpi);
    }
    private String getDpi(int dpi){
        String drawableDpi = null;
        switch (dpi){
            case 120:
                drawableDpi = "ldpi";
                break;
            case 160:
                drawableDpi = "mdpi";
                break;
            case 240:
                drawableDpi = "hdpi";
                break;
            case 320:
                drawableDpi = "xhdpi";
                break;
            default:
                break;
        }
        return drawableDpi;
    }

    /***
     * 获取文件下子文件的文件名
     * @param pathStr 文件夹路径
     * @return 返回文件集合
     */
    public List<String> getFiles(String pathStr){
        List<String> mList = new ArrayList<String>();
        File file=new File(pathStr);
        if(file.exists()){
            File[] files=file.listFiles();
            for(int i=0;i<files.length;i++){
                String fileName=files[i].getName();
                mList.add(fileName);
            }
        }
        return mList;
    }


    /**
     * 将字符串写入到文本文件中
     * @param strContent  文件内容
     * @param filePath  文件路径
     * @param fileName  文件姓名
     */
    public void writeTxtToFile(String strContent, String filePath, String fileName) {
        try{
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdirs();
            }
            writeTxtToFile(strContent,filePath+fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 将字符串写入到文本文件中
     * @param strContent  文件内容
     * @param filePath  文件完整路径路径
     */
    public void writeTxtToFile(String strContent, String filePath) {
        // 生成文件夹之后，再生成文件，不然会出错
        FileWriter fw = null;
        try{
//            makeRootDirectory(filePath);
            File fileTxt = new File(filePath);
            if (!fileTxt.exists()) {
                fileTxt.createNewFile();
            }
            fw =new FileWriter(fileTxt,false);
            fw.write(strContent);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(fw!=null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            Log.i("ToolUtils","创建文件！");
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

        /**
         * 读取文件的内容
         * @param fileName 全路径文件名
         * @return
         * @throws IOException
         */
    public String readFileSdcardFile(String fileName) throws IOException {
        String resultStr = "";
        FileInputStream fis = null;
        try {
            File file = new File(fileName);
            if(file.exists()){
                Long fileLength = file.length();
                byte[] fileContent = new byte[fileLength.intValue()];
                fis = new FileInputStream(file);
                fis.read(fileContent);
                resultStr = new String(fileContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }

    /**
     * 在json 中读取字符串
     * @param jsonObject json
     * @param key  读取的字符串文件
     * @return  返回读取数据或者 空字符串
     */
    public String readStringJson(JSONObject jsonObject, String key){
        try{
            if(jsonObject.has(key)){
                return jsonObject.getString(key);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int readIntJson(JSONObject jsonObject, String key){
        try{
            if(jsonObject.has(key)){
                return jsonObject.getInt(key);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getDeviceId(){
        String deviceId = "";
//        TelephonyManager TelephonyMgr = (TelephonyManager) RyApplication.getContext().getSystemService(TELEPHONY_SERVICE);
//        return TelephonyMgr.getDeviceId();
        try{
            StringBuffer stringBuffer = new StringBuffer();
            deviceId = android.os.SystemProperties.get("persist.service.ist.MachineNo");
            while (deviceId.startsWith("0")){
                deviceId = deviceId.substring(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deviceId;
    }
    /**
     * 初始化(绑定)EventBus
     * @param subscriber 上下文
     */
    public void loadEventBus(Object subscriber){
        try{
            if(!EventBus.getDefault().isRegistered(subscriber)){
                EventBus.getDefault().register(subscriber);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 解除绑定EventBus
     * @param subscriber 上下文
     */
    public void relieveEventBus(Object subscriber){
        try{
            EventBus.getDefault().unregister(subscriber);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 获取当前程序上层activity 名称
     * @return
     */
    public String getThisPackActivityName(Context context) {
        try{
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
            if(runningTaskInfos != null){
                ComponentName cn = runningTaskInfos.get(0).topActivity;
                if(cn.getPackageName().equals(context.getPackageName())){
                    return cn.getClassName();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 打开明星脸-不隐藏说话条
     */
    public void startAPKStarFace(){
        startAPKStarFace(false);
    }

    /**
     * 打开明星脸
     * @param isCloseFloating 是否隐藏说话条
     */
    public void startAPKStarFace(boolean isCloseFloating) {
        try{
            ComponentName toActivity = new ComponentName("com.ist.starfaceframe", "com.ist.starfaceframe.MainActivity");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(toActivity);
            intent.putExtra("appName", getAppName());
            intent.putExtra("appVersionName", getAppVersionName());
            intent.putExtra("appVersionCode", getAppVersionCode());
            intent.putExtra("robotNo", SharedPreferencesController.getInstance().getRobotNumber());
            intent.putExtra("isCloseFloating",isCloseFloating);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            RyApplication.getContext().startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /***
     * 明星脸程序停止
     */
    public void stopAPKStarFace(){
        try{
            Intent intent=new Intent();
            intent.setAction("star_accept");
            intent.putExtra("information","star_close");
            RyApplication.getContext().sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String dealCnArr(String chineseNumber){
        String newNumber = "";
        try{
            StringBuffer stringCn = new StringBuffer();
            RyApplication.getLog().d("修改前的数字 "+chineseNumber);
            for(int i = 0; i < chineseNumber.length(); i++){
                char c = chineseNumber.charAt(i);
                String matchchar = null;
                for (int j = 0;j < cnArr.length;j++){
                    if(c == cnArr[j]){
                        if(j == (cnArr.length-1)){
                            matchchar = "2";
                        }else{
                            matchchar = String.valueOf(j);
                        }
                    }
                }
                if(matchchar!=null){
                    stringCn.append(matchchar);
                }else{
                    stringCn.append(c);
                }
            }
            newNumber = stringCn.toString();
            if(newNumber.startsWith("0") && newNumber.length()>1){
                newNumber = newNumber.substring(1,newNumber.length());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return newNumber;
    }
    private String dealChArr(String chineseNumber){
        String newNumber = "";
        try{
            StringBuffer stringCn = new StringBuffer();
            RyApplication.getLog().d("修改前的数字 "+chineseNumber);
            for(int i = 0; i < chineseNumber.length(); i++){
                char c = chineseNumber.charAt(i);
                String matchchar = null;
                for (int j = 0;j < chArr.length;j++){
                    if(c == chArr[j]){
                        if(j == (chArr.length-1)){
                            matchchar = "十";
                        }else{
                            matchchar = String.valueOf(j);
                        }
                    }
                }
                if(matchchar!=null){
                    stringCn.append(matchchar);
                }else{
                    stringCn.append(c);
                }
            }
            newNumber = stringCn.toString();
            if(newNumber.startsWith("0") && newNumber.length()>1){
                newNumber = newNumber.substring(1,newNumber.length());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return newNumber;
    }

    /***
     * 修改以后的单位
     * @param chineseNumber 处理过的中文数字
     * @return  纯数字
     */
    private String dealCoArr(String chineseNumber){
        String newNumber = "";
        //用于记录前期的数字
        StringBuffer str = new StringBuffer();
        //每次修改的数字列表
        List<Float> list = new ArrayList<>();
        try{
            float number = 0;
            for(int i = 0; i < chineseNumber.length(); i++){
                char c = chineseNumber.charAt(i);
                int matchchar = -1;
                for (int j = 0;j < coArr.length;j++){
                    if(c == coArr[j]){
                        matchchar = j;
                    }
                }
                if(matchchar!=-1){
                    if (str.length()>0){
                        RyApplication.getLog().d("有原始数据 "+str);
                        Float n = Float.valueOf(str.toString());
                        switch (matchchar){
                            case 0:
                                n = n * 10;
                                break;
                            case 1:
                                n = n * 100;
                                break;
                            case 2:
                                n = n * 1000;
                                break;
                            case 3:
                                n = n * 10000;
                                break;
                            case 4:
                                n = n * 100000000;
                                break;
                            case 5:
                                n = n * 1;
                                break;
                            case 6:
                                n = n * 1;
                                break;
                            case 7:
                                n = n * 1;
                                break;
                            case 8:
                                n = n * 0.1f;
                                break;
                            case 9:
                                n = n * 0.1f;
                                break;
                            case 10:
                                n = n * 0.01f;
                                break;
                            default:
                                break;
                        }
                        list.add(n);
                        str = new StringBuffer();
                    }else{
                        if (list.size()>0){
                            RyApplication.getLog().d("列表中有数据");
                            str = new StringBuffer(String.valueOf(list.get(list.size()-1)));
                            list.remove(list.size()-1);
                        }else{
                            RyApplication.getLog().d("列表中无数据");
                            str = new StringBuffer("1");
                        }
                        RyApplication.getLog().d(str.toString());
                        RyApplication.getLog().d(list.toString());
                        Float n = Float.valueOf(str.toString());
                        switch (matchchar){
                            case 0:
                                n = n * 10;
                                break;
                            case 1:
                                n = n * 100;
                                break;
                            case 2:
                                n = n * 1000;
                                break;
                            case 3:
                                n = n * 10000;
                                break;
                            case 4:
                                n = n * 100000000;
                                break;
                            case 5:
                                n = n * 1;
                                break;
                            case 6:
                                n = n * 1;
                                break;
                            case 7:
                                n = n * 1;
                                break;
                            case 8:
                                n = n * 0.1f;
                                break;
                            case 9:
                                n = n * 0.1f;
                                break;
                            case 10:
                                n = n * 0.01f;
                                break;
                            default:
                                break;
                        }
                        list.add(n);
                        str = new StringBuffer();
                    }
                }else{
                    str.append(c);
                }
            }
            if (str.length()>0){
                list.add(Float.valueOf(str.toString()));
            }
            for (Float i:list) {
                number +=i;
            }
            newNumber = String.valueOf(number);
        }catch (Exception e){
            e.printStackTrace();
        }
        return newNumber;
    }

    char[] cnArr = new char[]{'零','一','二','三','四','五','六','七','八','九','两'};
    char[] chArr = new char[]{'零','壹','贰','叁','肆','伍','陆','柒','捌','玖','拾'};
    char[] coArr = new char[]{'十','百','千','万','亿','整','块','元','角','毛','分'};

    public String chineseNumber2Int(String chineseNumber){
        String cnNumber = dealCnArr(chineseNumber);
        RyApplication.getLog().d("第一次修改后的数字 "+cnNumber);
        String chNumber = dealChArr(cnNumber);
        RyApplication.getLog().d("第二次修改后的数字 "+chNumber);
        String coNumber = dealCoArr(chNumber);
        RyApplication.getLog().d("第三次修改后的数字 "+coNumber);
        return coNumber;
    }

    /**
     * 新增人脸关系
     * @param idcardFile  用户人像文件路径
     * @param userId 用户姓名
     */
    public  void addFace(final String idcardFile,final String userId){
        addFace(idcardFile,userId,null);
    }

    /**
     * 新增人脸关系
     * @param idcardFile
     */
    public void addFace(final String idcardFile,final String userId,final  FaceAddErrorListener listener){
        if(idcardFile==null||idcardFile.equals("")){
             if(listener!=null){
                 listener.errorMessage("没有人脸头像！");
             }
            RyApplication.getLog().i("没有人脸头像！");
            return;
        }
        RyApplication.getLog().i("新增人脸关系》》》图片路径："+idcardFile+";用户姓名："+userId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    byte[] tmpImgData = ImageUtil.getScaledBitmapByteArray(idcardFile);
                    long startTime1 = System.currentTimeMillis();
                    RyApplication.getLog().i("开始上传人脸 "+startTime1);
                    JSONObject objDetect = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).detectReturnFaceId(tmpImgData);
                    //取得人脸数组
                    if(objDetect==null){
                        RyApplication.getLog().i("objDetect===null");
                        if(listener!=null){
                            listener.errorMessage("objDetect===null");
                        }
                        return;
                    }
                    long endTime1 = System.currentTimeMillis();
                    RyApplication.getLog().i("开始上传人脸 "+(endTime1-startTime1));
                    JSONObject faceJsonOjb = objDetect.getJSONArray("faces").getJSONObject(0);
                    RyApplication.getLog().i("faceJsonOjb=="+faceJsonOjb.toString());
                    String image_id=objDetect.getString("image_id");
                    String faceId = faceJsonOjb.getString("face_token");
                    String value = null;//性别
                    if(faceJsonOjb.has("attributes")){
                        JSONObject attributes =faceJsonOjb.getJSONObject("attributes");
                        if(attributes.has("gender")){
                            JSONObject gender = attributes.getJSONObject("gender");
                            if(gender.has("value")){
                                value = gender.getString("value");
                            }
                        }
                    }
                    RyApplication.getLog().i("faceId=="+faceId+";image_id=="+image_id);
                    if(listener!=null){
                        listener.onComplete(faceId,value);
                    }
//                    Thread.sleep(100);
                    //将Face Token保存到FaceSet中
                    long startTime2 = System.currentTimeMillis();
                    RyApplication.getLog().i("将Face Token保存到FaceSet中 "+startTime2);
                    JSONObject objAddFaceResult = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).facesetAddface(SharedPreferencesController.getInstance().getFacesetToken(), faceId);
                    if (objAddFaceResult.getInt("apiResult") != HttpStatus.SC_OK) {

                    }
//                    Thread.sleep(100);
                    //保存用户的个人信息
                    JSONObject objSetUserIdResult = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).setUserId(faceId, userId);
                    boolean  bResult = RRBusinessApi.getInstance().saveFaceToRRSvr("", faceId, image_id, null,"",value,"0");
                    long endTime2 = System.currentTimeMillis();
                    RyApplication.getLog().i("将Face Token保存到FaceSet中 "+(endTime2-startTime2));
                    Thread.sleep(3*1000);
//                    FileUtil.deleteFile(idcardFile);
                }catch (Exception e){
                    e.printStackTrace();
                    if(listener!=null){
                        listener.errorMessage("addFace某部分产生错误");
                    }
                    //已经成功的，就删除文件
                } finally {
                    FileUtil.deleteFile(idcardFile);
                }
            }
        }).start();

    }

    public void compareFace(final byte[] imgData1, final byte[] imgData2, final FaceCompareListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject objDetect = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).compareFace(imgData1,imgData2);
                    Log.d("ToolUtil","confidence = " + objDetect);
                    if(objDetect.has("confidence")){
                        Float confidence = Float.valueOf(objDetect.getString("confidence"));
                        Log.d("ToolUtil","confidence = "+confidence);
                        if(confidence>60){
                            listener.onResult(true);
                        }else{
                            listener.onResult(false);
                        }
                    }else{
                        listener.onResult(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void removeFace(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject objDetect = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).facesetGetdetail(SharedPreferencesController.getInstance().getFacesetToken());
                    if(objDetect.has("face_tokens")){
                        String face_tokens = objDetect.getString("face_tokens");
                        face_tokens = face_tokens.replaceAll("\"","");
                        face_tokens = face_tokens.replaceAll("\\[","");
                        face_tokens = face_tokens.replaceAll("\\]","");
                        RyApplication.getLog().d("获取faceset组中的face_token列表 "+face_tokens);
                        if(face_tokens!=null && face_tokens.length()>0){
                            JSONObject object = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).facesetRemoveface(SharedPreferencesController.getInstance().getFacesetToken(),face_tokens);
                            RyApplication.getLog().d("删除faceset中的列表 "+object.toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void creatFaceSet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject objDetect = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).createFaceset("f99646b32922eadae6003f8099abf7e3");
                    Log.d("ToolUtil",objDetect.getString("faceset_token"));
                    if (objDetect.has("faceset_token")){
                        Log.d("ToolUtil",objDetect.getString("faceset_token"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String  TAG = "ToolUtil";

    public void addPicture(Person person){
        new addPictrueTask().execute(person);
    }

    public class addPictrueTask extends AsyncTask<Person,String,Boolean> {
        @Override
        protected Boolean doInBackground(Person... persons) {
            Log.i(TAG,"图片开始上传");
            boolean bResult = false;
            try {
                if(persons!=null){
                    Log.i(TAG,"存在人物");
                    for (Person son:persons) {
                        Log.i(TAG,"遍历"+son);
                        byte[] tmpImgData = ImageUtil.getScaledBitmapByteArray(son.getImgPath());
                        JSONObject objDetect = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).detectReturnFaceId(tmpImgData);
                        Log.i(TAG,"接口返回  =="+objDetect);
                        if(objDetect != null){
                            //取得人脸数组
                            JSONObject faceJsonOjb = objDetect.getJSONArray("faces").getJSONObject(0);
                            String image_id=objDetect.getString("image_id");
                            String faceId = faceJsonOjb.getString("face_token");
                            Log.i(TAG,"face_token=="+faceId+";image_id=="+image_id);
                            //将Face Token保存到FaceSet中
                            Log.i(TAG,"将Face Token保存到FaceSet中 "+SharedPreferencesController.getInstance().getFacesetToken());
                            JSONObject objAddFaceResult = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).facesetAddface(SharedPreferencesController.getInstance().getFacesetToken(), faceId);
                            if (objAddFaceResult.getInt("apiResult") != HttpStatus.SC_OK) {
                                Log.i(TAG,"将Face Token保存到FaceSet中失败" + objAddFaceResult.getInt("apiResult"));
                            }
                            Log.i(TAG,"保存用户信息");
                            JSONObject objSetUserIdResult = ((NewApiBase) Class.forName("com.wewins.facelibrary.api.faceplusfreev3.FacePlusFreeV3Api").newInstance()).setUserId(faceId, son.getPersonName());
                            if (objSetUserIdResult.getInt("apiResult") != HttpStatus.SC_OK) {
                                Log.i(TAG,"保存用户信息失败");
                            }
                            Log.i(TAG,"保存用户信息成功");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            bResult = true;
            return bResult;
        }
    }

}
