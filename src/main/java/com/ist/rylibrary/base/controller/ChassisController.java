package com.ist.rylibrary.base.controller;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.event.ChassisMessageEvent;
import com.ist.rylibrary.base.util.BaseLogUtil;
import com.ist.rylibrary.base.util.TimeUtil;
import com.ist.rylibrary.base.util.ToolUtil;
import com.wewins.robot.Dipan;
import com.wewins.robot.DipanGs;
import com.wewins.robot.Human;
import com.wewins.robot.HumanListener;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import retrofit2.Call;

/**
 * Created by minyuchun on 2017/3/27.
 */

public class ChassisController {

    private static ChassisController mChassisController;

    public static String CHASSIS_TYPE_WALK = "walk";
    public static String CHASSIS_TYPE_NAVIGATE = "navigate";
    private Human human;

    private ChassisListener mChassisListener;

    private String pointMessage = null;

    private String TAG = "ChassisController";
    /**日志*/
    private BaseLogUtil log = null;
    /**用户底盘walk方法的运用记录*/
    private String walks = null;
    /**用户记录当前点位信息*/
    private String nowWhere;

    public static ChassisController getInstance(){
        if(mChassisController == null){
            mChassisController = new ChassisController();
        }
        return mChassisController;
    }

    public ChassisController(){
        human = new Human();
        log = new BaseLogUtil(this.getClass());
    }



    /**
     * 发送默认的地盘消息
     * @param message  消息内容
     */
    public void post(String message){
        post(false,message,CHASSIS_TYPE_NAVIGATE);
    }
    /**
     *  发送自定义默认的地盘消息
     * @param isCustom 是否自定义处理
     * @param message 消息内容
     */
    public void post(boolean isCustom,String message){
        post(isCustom,message,CHASSIS_TYPE_NAVIGATE);
    }
    /***
     * 发送地盘消息
     * @param isCustom 是否自定义处理
     * @param message 发送的地盘内容
     * @param type 发送底盘动作的类型
     */
    public void post(boolean isCustom,String message,String type){
        log.d("底盘动作处理 "+isCustom+","+message+","+type);
        EventBus.getDefault().post(new ChassisMessageEvent(isCustom,message,type));
    }

    /**
     * 自定义消息回收处理
     * @param isCustom  是否自定义
     * @param message  消息内容
     */
    public void recovery(boolean isCustom,String message){
        if(mChassisListener!=null){
            mChassisListener.ChassisMessage(isCustom,message);
        }
    }

    /**
     * 设置地盘监听
     * @param listener 新的监听
     */
    public void setChassisListener(ChassisListener listener) {
        this.mChassisListener = listener;
    }

    /***
     * 地盘监听
     */
    public interface ChassisListener{
        void ChassisMessage(boolean isCustom,String message);
    }

    public Human getHuman() {
        return human;
    }

    public void defaultHandling(String message){
        pointMessage = message;
        if(pointMessage.contains("_yindao")){
            String point = pointMessage.split("_")[0];
            human.navigate(point,1000);
        }
    }

    public void connect(final Context context){
        human.init(new HumanListener() {
            @Override
            public void onWalkResult(int result) {
                Log.i(TAG,"onWalkResult numberResult == "+result);
                if(result == 0){
                    if(walks!=null){
                        walk(walks);
                    }
                }
            }

            @Override
            public void onCtrlResult(int result) {
                Log.i(TAG,"onCtrlResult numberResult == "+result);
            }

            @Override
            public void onNavigateResult(int result, String where) {
                Log.i(TAG,"onNavigateResult numberResult == "+result + "where == "+where);
                if (result == Dipan.TOO_CLOSE_TO_OBSTACLES){
                    Log.i(TAG,"距离障碍物太近了 ");
                    if (!TimeUtil.getInstance().isChassisObstacle()){
                        Log.i(TAG,"播放距离太近的语音 ");
                        AiuiController.getInstance().post("请让一下");
                    }
                }else if(result == Dipan.REACHED || result == Dipan.UNREACHED){
                    Log.i(TAG,"到达目的地 或者 福建 "+result);
                    nowWhere = where;
                    if (JiangJieController.getInstance().getJiangjieChassisListener()!=null){
                        if(JiangJieController.getInstance().getJiangjieChassisListener().onChassisComplete(true,where)){
                            JiangJieController.getInstance().setJiangjieChassisListener(null);
                        }
                    }
                    if(YinDaoController.getInstance().getYinDaoChassisListener()!=null){
                        if(YinDaoController.getInstance().getYinDaoChassisListener().onChassisComplete(true,where)){
                            YinDaoController.getInstance().setYinDaoChassisListener(null);
                        }
                    }
                }else if(result == Dipan.PLANNING){
                    Log.i(TAG,"正在规划路径 ");
                }else if(result == Dipan.HEADING){
                    Log.i(TAG,"正在前往目的地 ");
                }else if(result == Dipan.TIME_OUT){
                    Log.i(TAG,"超时 ");
                }else if(result == Dipan.UNREACHABLE){
                    Log.i(TAG,"不能到达目的地 ");
                    if (JiangJieController.getInstance().getJiangjieChassisListener()!=null){
                        if(JiangJieController.getInstance().getJiangjieChassisListener().onChassisComplete(false,where)){
                            JiangJieController.getInstance().setJiangjieChassisListener(null);
                        }
                    }
                    if(YinDaoController.getInstance().getYinDaoChassisListener()!=null){
                        if(YinDaoController.getInstance().getYinDaoChassisListener().onChassisComplete(false,where)){
                            YinDaoController.getInstance().setYinDaoChassisListener(null);
                        }
                    }
                }else if(result == Dipan.GOAL_NOT_SAFE){
                    Log.i(TAG,"目的地有障碍物 ");
                }else if(result == Dipan.LOCALIZATION_FAILED){
                    Log.i(TAG,"定位失败 ");
                }
            }

            @Override
            public void onLocation(int x, int y, int jd, int xSpeed, int rotSpeed, int stat) {
//                Log.i(TAG,"onLocation numberResult == "+numberResult + "where == "+where);
            }

            @Override
            public void onError(int type, String info) {
                Log.i(TAG,"onError type == "+type + "info == "+info);
                if(type == Dipan.E_BTNSTOP){
                    if(YinDaoController.getInstance().isInYindaoProcess()){
                        YinDaoController.getInstance().YindaoEnd();
                        AiuiController.getInstance().post("不好意思，暂时无法移动");
                    }
                }
                if (JiangJieController.getInstance().getJiangjieChassisListener()!=null){
                    if(JiangJieController.getInstance().getJiangjieChassisListener().onError()){
                        JiangJieController.getInstance().setJiangjieChassisListener(null);
                    }
                }
                if(YinDaoController.getInstance().getYinDaoChassisListener()!=null){
                    if(YinDaoController.getInstance().getYinDaoChassisListener().onError()){
                        YinDaoController.getInstance().setYinDaoChassisListener(null);
                    }
                }
            }
        }, context);
    }

    /**
     * 引导处理
     * @param where  到达的点位
     */
    private void dealYindao(String where){
        if (pointMessage.indexOf(where)>-1){
            RyApplication.getLog().d("到达点位 "+where+" 说话 内容 "+pointMessage);
            String[] pointSplit = pointMessage.split("_");
            String instructions = "";
            if(pointSplit.length == 3){
                instructions = pointSplit[0]+"_"+pointSplit[1];
                String answer = SceneController.getInstance().rrPeople(instructions,null,"");
                if(pointSplit[2].equals("true")){
                    AiuiController.getInstance().post(true,answer);
                }else {
                    AiuiController.getInstance().post(answer);
                }
            }else{
                String answer = SceneController.getInstance().rrPeople(pointMessage,null,"");
                AiuiController.getInstance().post(answer);
            }
        }
    }


    public void walk(String continuityAction){
        if (continuityAction.contains("_")){
            log.d("底盘移动的动作指令 "+continuityAction);
            String[] action = continuityAction.split("_");
            if(action!=null){
                try{
                    int x = Integer.parseInt(action[2]);
                    if(human.getDipanType().equals("南江底盘") || human.getDipanType().equals("EAI底盘")){
                        if(x>180){
                            walks = action[0]+"_"+action[1]+"_"+(x-180);
                            log.d("底盘移动的动作指令 = "+human.walk(action[0],action[1],180));
                        }else{
                            walks = null;
                            log.d("底盘移动的动作指令 = "+human.walk(action[0],action[1],x));
                        }
                    }else if(human.getDipanType().equals("高仙底盘")){
                        log.d("底盘移动的动作指令 = "+human.walk(action[0],action[1],x));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    public String getNowWhere() {
        return nowWhere;
    }

    public String getLocalIp(Context context){
        String ret = null;
        try {
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            if(ipAddress==0){

                try {
                    String ipAddress1=null;
                    for (Enumeration<NetworkInterface> en = NetworkInterface
                            .getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf
                                .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                ipAddress1 = inetAddress.getHostAddress().toString();
                                if(!ipAddress1.contains("::"))
                                    return inetAddress.getHostAddress().toString();
                            }else
                                continue;
                        }
                    }
                } catch (SocketException ex) {
                    Log.e(TAG, ex.toString());
                }
                return "192.168.0.1";
            }
            ret = ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                    +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
            Log.d(TAG, "getlocalip: ip======="+ret);
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "Exception: e="+e.toString());
        }
        return ret;
    }



}
