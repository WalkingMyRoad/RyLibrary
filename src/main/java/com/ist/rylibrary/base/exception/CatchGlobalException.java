package com.ist.rylibrary.base.exception;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.util.ToolUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minyuchun on 2017/3/31.
 * 获取系统异常处理类
 * 需要在 自定义的 Application中的onCreate中调用 CatchGlobalException.getInstance(); 实现效果
 */

public class CatchGlobalException implements Thread.UncaughtExceptionHandler{
    /**系统默认的UncaughtException处理类*/
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**实例*/
    private static CatchGlobalException sCatchGlobalException;
    /**用来存储设备信息和异常信息*/
    private Map<String, String> infos = new HashMap<>();

    /**
     * 获取全局捕获异常类的实例
     * @return 返回实例
     */
    public static CatchGlobalException getInstance(){
        if(sCatchGlobalException == null){
            sCatchGlobalException = new CatchGlobalException();
        }
        return sCatchGlobalException;
    }

    /**
     * 初始化
     */
    private CatchGlobalException(){
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 异常处理类
     * @param thread 当前线程
     * @param throwable 错误
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try{
            if (!handleException(throwable) && mDefaultHandler != null) {
                //如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, throwable);
            } else {
                try {
                    throwable.printStackTrace();
                    RyApplication.getLog().d(throwable.getMessage());
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    RyApplication.getLog().d("CatchGlobalExceptionError "+e);
                }
                //退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex 错误信息
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo();
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(RyApplication.getContext(), "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        //保存日志文件
//        saveCatchInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    public void collectDeviceInfo() {
        try {
            infos.put("versionName", ToolUtil.getInstance().getAppVersionName());
            infos.put("versionName", ToolUtil.getInstance().getAppVersionCode()+"");
        } catch (PackageManager.NameNotFoundException e) {
            RyApplication.getLog().d("an error occured when collect package info = "+e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                RyApplication.getLog().d("CatchGlobalException 错误:  "+field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                RyApplication.getLog().e("an error occured when collect crash info"+ e);
            }
        }
    }

}
