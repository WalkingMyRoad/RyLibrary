package com.ist.rylibrary.base.util;

import android.os.Build;
import android.util.Log;

import com.ist.rylibrary.log4j_1.ConfigureLog4J;

import org.apache.log4j.Logger;


/**
 * 基础的日志管理类
 * 获取方式一：可以通过new BaseLogUtil(Class clazz) 获取
 * 获取方式二：可以通过getLocalLog(Class clazz) 获取
 *
 * 注意当版本高于22  使用系统的Log打印，还没有实现高版本下的日志本地记录
 *     当版本在22以下时候，使用的是log4j日志打印  日志的相关内容在 ConfigureLog4J中记录
 * Created by minyuchun on 2016/12/26.
 */

public class BaseLogUtil {
    /**日志输出的最小级别*/
    public static int LogCatType = Log.VERBOSE;
    /**是否在控制台打印出数据*/
    public static boolean isUseLogCat = true;
    /**输出日志的实例话log*/
    private Logger log = null;
    /**当前的className*/
    private String TAG="BaseLogUtil";

    public BaseLogUtil(Class clazz){
        try{
            if(Build.VERSION.SDK_INT<=22){
                //加载配置
                ConfigureLog4J configureLog4J=new ConfigureLog4J();
                configureLog4J.configure(isUseLogCat);
                //初始化 log
                log=Logger.getLogger(clazz);
            }else{
                TAG = clazz.getSimpleName();
            }
        }catch (Exception e){
            TAG = clazz.getSimpleName();
            e.printStackTrace();
        }
    }

    public static BaseLogUtil getLocalLog(Class clazz){
        return new BaseLogUtil(clazz);
    }


    public boolean d(String message){
        try{
            if(LogCatType < Log.DEBUG && log!=null) {
                if(Build.VERSION.SDK_INT<=22){
                    log.debug(message);
                    return true;
                }else{
                    Log.d(TAG,message);
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean i(String message){
        try{
            if(LogCatType < Log.INFO && log!=null){
                if(Build.VERSION.SDK_INT<=22){
                    log.info(message);
                    return true;
                }else{
                    Log.i(TAG,message);
                    return true;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean w(String message){
        try{
            if(LogCatType < Log.WARN && log!=null){
                if(Build.VERSION.SDK_INT<=22){
                    log.warn(message);
                    return true;
                }else{
                    Log.w(TAG,message);
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean e(String message){
        try {
            if(LogCatType < Log.ERROR && log!=null){
                if(Build.VERSION.SDK_INT<=22){
                    log.error(message);
                    return true;
                }else{
                    Log.e(TAG,message);
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
