package com.ist.rylibrary.base.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.util.ToolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by minyuchun on 2017/3/20.
 * SharedPreferences 本地存储数据;
 */

public class SharedPreferencesController{
    /**读取配置文件通过demo.xml*/
    public static final int ShareByDemo = 0;
    /**读取配置文件通过config.xml*/
    public static final int ShareByConfig = 1;
    /**本地数据控制器实例*/
    private static SharedPreferencesController sController;
    /**本地存储工具*/
    private SharedPreferences sp;
    /**本地存储实例*/
    private SharedPreferences.Editor mEditor;
    /**数据名称列表*/
    private static List<String> mapKeyList;

    private initSharedPreferenceListener mInitSharedPreferenceListener;

    public SharedPreferencesController(){
        try{
            sp =  RyApplication.getContext().getSharedPreferences(ToolUtil.getInstance().getAppPackageName(), Context.MODE_PRIVATE);
            mEditor = sp.edit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static SharedPreferencesController getInstance(){
        if(sController == null){
            sController = new SharedPreferencesController();
        }
        return sController;
    }

    public List<String> saveMap(Map<String,String> map){
        if(map!=null){
            mapKeyList = new ArrayList<>();
            Set<Map.Entry<String,String>> entries = map.entrySet();
            for (Map.Entry<String,String> entry:entries){
                mapKeyList.add(entry.getKey());
                saveData(entry.getKey(),entry.getValue());
            }
        }
        return mapKeyList;
    }


    public void saveData(String key,Object value){
        try{
            if(value instanceof Integer){
                mEditor.putInt(key,(Integer) value);
            }else if(value instanceof String){
                mEditor.putString(key,String.valueOf(value));
            }else if(value instanceof Boolean){
                mEditor.putBoolean(key,(Boolean)value);
            }else if(value instanceof Float){
                mEditor.putFloat(key,(Float)value);
            }else if(value instanceof Long){
                mEditor.putLong(key,(Long)value);
            }
            mEditor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getData(String key){
        String result = null;
        try{
            result = sp.getString(key, "");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public void initConfigSharedPreference(initSharedPreferenceListener listener){
        mInitSharedPreferenceListener = listener;
        new initSharedPreferenceDataTask().execute(
                LocalDataController.getInstance().getBASE_PATH_LOCAL_DATA());
    }

    private class initSharedPreferenceDataTask extends AsyncTask<String,String,List<String>> {
        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> result = null;
            if(strings!=null && strings.length>0){
                Map<String,String> demoMap= LocalDataController.getInstance().readConfigXML(strings[0]+LocalDataController.getInstance().getDEMO_XML());
                if(demoMap!=null){
                    RyApplication.getLog().d("Share demoxml缓存数据 "+strings.toString());
                    result = SharedPreferencesController.getInstance().saveMap(demoMap);
                }else{
                    Map<String,String> map= LocalDataController.getInstance().readConfigXML(strings[0]+LocalDataController.getInstance().getCONFIG_XML());
                    if(map!=null){
                        RyApplication.getLog().d("Share configxml缓存数据 "+strings.toString());
                        result = SharedPreferencesController.getInstance().saveMap(map);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if(strings!=null){
                RyApplication.getLog().d("存储是否成功以及存储的标签 "+strings.toString());
                if(mInitSharedPreferenceListener!=null){
                    if(strings.contains("mall_id") || strings.contains("robot_number")
                            || strings.contains("mall_number") || strings.contains("program_code")){
                        mInitSharedPreferenceListener.initData(ShareByDemo,true);
                    }else{
                        mInitSharedPreferenceListener.initData(ShareByConfig,true);
                    }
                }
            }

            super.onPostExecute(strings);
        }
    }

    public String getMailId(){
        return getData("mall_id");
    }

    public String getRobotNumber(){
        return getData("robot_number");
    }

    public String getMallNumber(){
        return getData("mall_number");
    }

    public String getProgramCode(){
        return getData("program_code");
    }

    public String getHost(){
        return getData("host");
    }

    public String getWebsocketHost(){
        return getData("websocket_host");
    }
    public String getWebsocketId(){
        return getData("websocket_id");
    }
    public String getAddress(){
        return getData("address");
    }

    public String isFormal(){
        return getData("isFormal");
    }

    public List<String> getMapKeyList() {
        return mapKeyList;
    }

    public interface initSharedPreferenceListener{
        boolean initData(int type,boolean isComplete);
    }
}
