package com.ist.nativepackage;

import android.util.Log;

import java.util.Calendar;

/**
 * 眼睛屏操作、同步
 */
public class EyesCtrl {
    private final String TAGEYE = "EyeCtrl";
    private final String DEV = "/dev/ttyUSB0";
    private static EyesCtrl eyesCtrl = null;
    private static boolean isThreadWorking = false;//同步眼睛的线程是否已启动
    private static int doWhat = -1;            // 执行什么表情
    private static int doLong = 0;        //执行多少s，单位：秒
    private static long lastEyesActionTime = 0;        // 最近一次执行眼睛动作的时间
    private static boolean isDoingEyesAction = false;    //是否处于执行表情: true 是，false 否

    private EyesCtrl() {

    }

    /**
     * 返回单例对象
     * @return
     */
    public static EyesCtrl getInstance() {
        if(eyesCtrl != null) {
            return eyesCtrl;
        }
        return new EyesCtrl();
    }
    /**
     * 定时同步眼睛，并检查眼睛动作超时情况
     */
    public synchronized void EyeWork() {
        if (isThreadWorking) {
            return;
        }
        isThreadWorking = true;
        Log.d(TAGEYE, "启动线程---------------------1");
        new Thread() {
            public void run() {
                try {
                    while (isThreadWorking) {
                        try {
                            if (isDoingEyesAction) {
                                long curTime = Calendar.getInstance().getTimeInMillis();
                                Log.d(TAGEYE, "curTime = " + curTime + "   lastEyesActionTime+doLong = " + (lastEyesActionTime + doLong * 1000));

                                //检查时间，超时了，则表示上次动作已经完成，执行默认动作
                                if (curTime > (lastEyesActionTime + doLong * 1000)) {
                                    Log.d(TAGEYE, "检查时间，上次动作已完成，执行同步眼睛屏");
                                    Eyes("1");
                                }
                            }  else {
                                Log.d(TAGEYE, "执行同步眼睛屏");
                                Eyes("1");
                            }

                            //等待一段时间
                            Thread.sleep(20 * 1000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isThreadWorking = false;
                    Log.d(TAGEYE, "EyeWork Thread Exit.----------------5");
                }
            }
        }.start();
    }


    // 眼睛执行动作
    public int Eyes(String cmd) {
        try {
            isDoingEyesAction = true;
            doWhat = 1;
            doLong = 10;
            lastEyesActionTime = Calendar.getInstance().getTimeInMillis();
            int ret = Eyes.eyeExecCmd(DEV, cmd);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
