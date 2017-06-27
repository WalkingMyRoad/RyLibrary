package com.ist.rylibrary.base.controller;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.entity.AllSceneResultBean;
import com.ist.rylibrary.base.entity.CustomSceneBean;
import com.ist.rylibrary.base.entity.PageBean;
import com.ist.rylibrary.base.entity.Robotsbean;
import com.ist.rylibrary.base.entity.SceneBean;
import com.ist.rylibrary.base.entity.SceneQABean;
import com.ist.rylibrary.base.entity.TipsBean;
import com.ist.rylibrary.base.util.BaseLogUtil;
import com.ist.rylibrary.base.util.ToolUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by minyuchun on 2017/3/16.
 * 基础的本地数据控制器
 */
public class LocalDataController {
    /**存储数据的基础路径 目前为 sdcard下的包名文件*/
    private String BASE_PATH_LOCAL_DATA;
    /**基础配置文件的路径 的名称*/
    private String CONFIG_XML = File.separator+"config.xml";
    /**测试环境使用的demo xml 简易名称*/
    private String DEMO_XML = File.separator+"demo.xml";
    /**默认服务器文件写入的文件名称*/
    private String SERVER_DATA_TXT = File.separator + "data";//data文件
    private String GIT_IMAGE_VIEW = File.separator + "gif";//存放gif图片的文件
    private String FILE_NAME_SCENE_QAS = File.separator + "sceneQas.txt";//场景问答文件
    private String FILE_NAME_ROBOTS = File.separator + "robots.txt";//机器人信息文件
    private String FILE_NAME_SCENES = File.separator + "scenes.txt";//场景文件
    private String FILE_NAME_PAGES = File.separator +"pages.txt";//页面文件
    private String FILE_NAME_TIPS = File.separator +"tips.txt";//小贴士文件
    private String FILE_NAME_CUSTOM_SCENES = File.separator + "customScenes.txt";//场景问答邮件
    /**底盘的坐标信息**/
    private String FILE_NAME_POSITION= File.separator + "positionData.txt";//底盘坐标文件
    /**本地数据控制器实例*/
    private static LocalDataController sLocalDataController;
    /**构造函数*/
    public LocalDataController(){
        try{
            String packageName = ToolUtil.getInstance().getAppPackageName();
            BASE_PATH_LOCAL_DATA = Environment.getExternalStorageDirectory() + File.separator + packageName;
            File file = new File(BASE_PATH_LOCAL_DATA);
            if(!file.exists()){
                file.mkdirs();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 获取本地数据控制实例
     * @return
     */
    public static LocalDataController getInstance(){
        if(sLocalDataController == null){
            sLocalDataController = new LocalDataController();
        }
        return sLocalDataController;
    }

    /***
     * 读取指定路径下的 xml文件
     * @param filePath 文件指定路径,本地的全路径地址
     * @return  返回数据map值
     */
    public Map<String,String> readConfigXML(String filePath){
        Map<String,String> map = null;
        try {
            Log.i("ToolUtil ","文件路径 "+filePath);
            if(filePath.endsWith(".xml")){
                File file = new File(filePath);
                if(file.exists()){
                    map = new HashMap<String,String>();
                    FileInputStream inputStream = new FileInputStream(file);
                    // 获得pull解析器对象
                    XmlPullParser parser = Xml.newPullParser();
                    // 指定解析的文件和编码格式
                    parser.setInput(inputStream, "utf-8");
                    int eventType = parser.getEventType();
                    while (eventType!=XmlPullParser.END_DOCUMENT){
                        String tagName = parser.getName(); // 获得当前节点的名称
                        switch (eventType){
                            case XmlPullParser.START_TAG:  //若节点名称为开始节点
                                if(tagName.equals("config")){
                                }else{
                                    map.put(tagName,parser.nextText());
                                }
                                break;
                            case XmlPullParser.END_TAG:  //若节点名称为结束节点
                                break;
                            default:
                                break;
                        }
                        eventType = parser.next();
                    }
                }
            }
            return map;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /***
     * 本地文件存储
     * 调用全部情景获取接口成功时调用，将信息存储在 sdcard/包名/data/下的文件 填充数据
     * @param bean  传过来的场景对象
     */
    public boolean saveSceneData(final AllSceneResultBean bean){
        String path=getSERVER_DATA_TXT()+"/";
        Log.i("保存的路径","path=="+path);
        ToolUtil.makeRootDirectory(path);
        try{
            if(bean.getSceneQas()!=null && bean.getSceneQas().size()>0){// 场景问答
//                Log.i("旧版本场景问答保存地址：","getFILE_NAME_SCENE_QAS()="+getFILE_NAME_SCENE_QAS());
                ToolUtil.getInstance().writeTxtToFile(new Gson().toJson(bean.getSceneQas()),
                        getFILE_NAME_SCENE_QAS());
            }
            if(bean.getRobots()!=null){//机器人信息
                ToolUtil.getInstance().writeTxtToFile(new Gson().toJson(bean.getRobots() , Robotsbean.class),
                        getFILE_NAME_ROBOTS());
            }
            if(bean.getScenes()!=null && bean.getScenes().size()>0){// 场景信息
                ToolUtil.getInstance().writeTxtToFile(new Gson().toJson(bean.getScenes()),
                        getFILE_NAME_SCENES());
            }
            if(bean.getPages()!=null){// 页面信息
                ToolUtil.getInstance().writeTxtToFile(new Gson().toJson(bean.getPages()),
                        getFILE_NAME_PAGES());
            }
            if(bean.getCustomScenes()!=null && bean.getCustomScenes().size()>0){//自定义场景
                ToolUtil.getInstance().writeTxtToFile(new Gson().toJson(bean.getCustomScenes()),
                        getFILE_NAME_CUSTOM_SCENES());
            }
            if(bean.getTipsData()!=null && bean.getTipsData().size()>0){//小贴士信息
                ToolUtil.getInstance().writeTxtToFile(new Gson().toJson(bean.getTipsData()),
                        getFILE_NAME_TIPS());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 读取本地文件存储信息
     * 当获取全部情景信息接口失败时调用，读取下载在 sdcard/包名/data/下的文件 填充数据
     */
    public AllSceneResultBean readSceneData(){
        AllSceneResultBean sceneResultBean = new AllSceneResultBean();
        try{
            String jsonStr = ToolUtil.getInstance().readFileSdcardFile(getFILE_NAME_SCENE_QAS());
            List<SceneQABean> jaSceneQas = new Gson().fromJson(jsonStr, new TypeToken<List<SceneQABean>>(){}.getType());
            sceneResultBean.setSceneQas(jaSceneQas);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            String jsonStr = ToolUtil.getInstance().readFileSdcardFile(getFILE_NAME_ROBOTS());
            Robotsbean jsRobots = new Gson().fromJson(jsonStr, new TypeToken<Robotsbean>(){}.getType());
            sceneResultBean.setRobots(jsRobots);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            String jsonStr = ToolUtil.getInstance().readFileSdcardFile(getFILE_NAME_SCENES());
            List<SceneBean> jaScenes = new Gson().fromJson(jsonStr, new TypeToken<List<SceneBean>>(){}.getType());
            sceneResultBean.setScenes(jaScenes);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            String jsonStr = ToolUtil.getInstance().readFileSdcardFile(getFILE_NAME_PAGES());
            List<PageBean> jaPages = new Gson().fromJson(jsonStr, new TypeToken<List<PageBean>>(){}.getType());
            sceneResultBean.setPages(jaPages);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            String jsonStr = ToolUtil.getInstance().readFileSdcardFile(getFILE_NAME_CUSTOM_SCENES());
            List<CustomSceneBean> jaCustomScenes = new Gson().fromJson(jsonStr, new TypeToken<List<CustomSceneBean>>(){}.getType());
            sceneResultBean.setCustomScenes(jaCustomScenes);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            String jsonStr = ToolUtil.getInstance().readFileSdcardFile(getFILE_NAME_TIPS());
            List<TipsBean> tipsBeen = new Gson().fromJson(jsonStr, new TypeToken<List<TipsBean>>(){}.getType());
            sceneResultBean.setTipsData(tipsBeen);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sceneResultBean;
    }


    /***
     *
     * @return
     */
    public String getSERVER_DATA_TXT() {
        return BASE_PATH_LOCAL_DATA+SERVER_DATA_TXT;
    }

    public String getGIT_IMAGE_VIEW() {
        try{
            String path = BASE_PATH_LOCAL_DATA+GIT_IMAGE_VIEW;
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return BASE_PATH_LOCAL_DATA+GIT_IMAGE_VIEW;
    }

    /**
     * 获取基础的sd卡配置路径
     * @return
     */
    public String getBASE_PATH_LOCAL_DATA() {
        return BASE_PATH_LOCAL_DATA;
    }
    /***
     * 获取场景问答数据的存储文件的文件名
     * @return ／文件名
     */
    public String getFILE_NAME_SCENE_QAS() {
        return getSERVER_DATA_TXT()+FILE_NAME_SCENE_QAS;
    }
    /***
     * 获取机器人信息数据的存储文件的文件名
     * @return ／文件名
     */
    public String getFILE_NAME_ROBOTS() {
        return getSERVER_DATA_TXT()+FILE_NAME_ROBOTS;
    }
    /***
     * 获取场景数据的存储文件的文件名
     * @return ／文件名
     */
    public String getFILE_NAME_SCENES() {
        return getSERVER_DATA_TXT()+FILE_NAME_SCENES;
    }
    /***
     * 获取页面数据的存储文件的文件名
     * @return ／文件名
     */
    public String getFILE_NAME_PAGES() {
        return getSERVER_DATA_TXT()+FILE_NAME_PAGES;
    }
    /***
     * 获取小贴士存储文件的文件名
     * @return ／文件名
     */
    public String getFILE_NAME_TIPS() {
        return getSERVER_DATA_TXT()+FILE_NAME_TIPS;
    }
    /***
     * 获取自定义场景文件的文件名
     * @return  ／文件名
     */
    public String getFILE_NAME_CUSTOM_SCENES() {
        return getSERVER_DATA_TXT()+FILE_NAME_CUSTOM_SCENES;
    }
    /***
     * 获取点位存储文件的文件名
     * @return ／文件名
     */
    public String getFILE_NAME_POSITION() {
        return FILE_NAME_POSITION;
    }

    public String getCONFIG_XML() {
        return CONFIG_XML;
    }

    public void setCONFIG_XML(String CONFIG_XML) {
        this.CONFIG_XML = CONFIG_XML;
    }

    public String getDEMO_XML() {
        return DEMO_XML;
    }

    public void setDEMO_XML(String DEMO_XML) {
        this.DEMO_XML = DEMO_XML;
    }
}
