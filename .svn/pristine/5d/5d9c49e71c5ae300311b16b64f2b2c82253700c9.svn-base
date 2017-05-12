package com.ist.rylibrary.base.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ist.rylibrary.base.event.NetWorkMessageEvent;
import com.ist.rylibrary.base.service.NetWorkServer;
import com.ist.rylibrary.base.util.HttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.ist.rylibrary.base.service.NetWorkServer.NETWORK_TYPE_INVALID;

/**
 * Created by minyuchun on 2017/3/27.
 */

public class NetWorkController {
    /**网络控制实例*/
    private static NetWorkController mNetWorkController;
    /**上次记录的网络类型*/
    private int lastNetType;
    /**监听*/
    private NetWorkListener mNetWorkListener;

    public static NetWorkController getInstance(){
        if(mNetWorkController == null){
            mNetWorkController = new NetWorkController();
        }
        return mNetWorkController;
    }

    public void setNetWorkListener(NetWorkListener listener){
        mNetWorkListener = listener;
    }

    private NetWorkController(){

    }

    /**
     * 发送信息
     * @param type 网络类型
     * @param isNetWork 是否有网络
     * @param isPingServer 是否ping通服务器
     */
    public void post(int type,boolean isNetWork,boolean isPingServer){
        post(type,false,isNetWork,isPingServer);
    }
    /***
     * 发送信息
     * @param type  发送类型
     * @param isCustom 是否自定义
     * @param isNetWork  是否有网络
     * @param isPingServer  是否ping通服务器
     */
    public void post(int type,boolean isCustom,boolean isNetWork,boolean isPingServer){
        EventBus.getDefault().post(new NetWorkMessageEvent(type,isCustom,isNetWork,isPingServer));
    }

    /**
     * 回收方法
     * @param type  网络类型
     * @param isCustom  是否是自定义
     * @param isNetWork 是否有网络
     * @param isPingServer 是否ping通服务器
     */
    public void recovery(int type,boolean isCustom,boolean isNetWork,boolean isPingServer){
        if(mNetWorkListener!=null){
            mNetWorkListener.NetWorkMessage(type,isCustom,isNetWork,isPingServer);
        }
    }

    /***
     * 判断网络类型 并且返回
     * @param context
     * @return 是否是快速的网络
     */
    public boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
    public interface NetWorkListener{
        void NetWorkMessage(int netType,boolean isNetWork,boolean isCustom,boolean isPingServer);
    }

    /**
     * ping 服务器
     * @return
     */
    public boolean ping() {
        try {
            String pingUrlIp = SharedPreferencesController.getInstance().getHost();
            if(pingUrlIp!=null && !pingUrlIp.isEmpty()){
                Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + pingUrlIp);// ping网址3次
                // 读取ping的内容，可以不加
                InputStream input = p.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                StringBuffer stringBuffer = new StringBuffer();
                String content = "";
                while ((content = in.readLine()) != null) {
                    stringBuffer.append(content);
                }
                // ping的状态
                int status = p.waitFor();
                if (status == 0) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    /***
     * 获取放落类型 并且返回
     * @param context
     * @return  返回网络类型
     */
    public int getNetWorkType(Context context){
        int netWorkType = NetWorkServer.NETWORK_TYPE_ERROR;
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo=manager.getActiveNetworkInfo();
        if(netInfo == null ||  !manager.getBackgroundDataSetting()){//没有网络状态了
            netWorkType = NetWorkServer.NETWORK_TYPE_ERROR;
        }else{
            int type = netInfo.getType();
            if(type != lastNetType){
                if(netInfo.isConnected()){
                    if(type == ConnectivityManager.TYPE_WIFI){//wifi网络
                        netWorkType = NetWorkServer.NETWORK_TYPE_WIFI;
                    }else if(type == ConnectivityManager.TYPE_ETHERNET){//

                    }else if(type == ConnectivityManager.TYPE_MOBILE){
                        String proxyHost = android.net.Proxy.getDefaultHost();
                        netWorkType = TextUtils.isEmpty(proxyHost)
                                ? (NetWorkController.getInstance().isFastMobileNetwork(context)
                                ? NetWorkServer.NETWORK_TYPE_3G : NetWorkServer.NETWORK_TYPE_2G)
                                : NetWorkServer.NETWORK_TYPE_WAP;
                    }else {
                        netWorkType = NETWORK_TYPE_INVALID;
                    }
                }else{
                    netWorkType = NETWORK_TYPE_INVALID;
                }
                lastNetType = type;
            }else{
                netWorkType = NetWorkServer.NETWORK_TYPE_ERROR;
            }
        }
        return netWorkType;
    }
}
