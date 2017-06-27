package com.cmcc.nativepackage;

import android.util.Log;

import com.cmcc.IDCardBean;
import com.cmcc.PrintInfoBean;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by minyuchun on 2017/6/12.
 * 应用的idcard操作
 */

public class PrintTool {

    private static PrintTool tool;

    public static PrintTool getInstance() {
        if (tool == null) {
            tool = new PrintTool();
        }
        return tool;
    }

    public PrintTool() {
    }

    public void print(Observer<Boolean> observable, final PrintInfoBean bean) {
        try {
            Observable.just("")
                    //可能涉及到IO操作，放在子线程
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .map(new Function<String, Boolean>() {
                        @Override
                        public Boolean apply(String s) throws Exception {
                            int ret = Printer.openPrinter(2, bean.getMAC(), null);
                            if (ret == 0) {
                                Log.i("Printer", "连接打印机成功");
                                int init = Printer.initialPrinter();
                                if (init == 0) {
                                    Log.i("Printer", "初始化成功,开始打印");
                                    Printer.setLineSpacingByDotPitch(10);
                                    Printer.setWordSpacingByDotPitch(1);
                                    if (bean.getHeadLine() != null && bean.getSubtitle()!=null) {
                                        Printer.setBold(1);
                                        Printer.setAlignType(1); //居中
                                        Printer.setZoonIn(4, 4);
                                        Printer.print(bean.getHeadLine()+"\r\n\r\n"+bean.getSubtitle()+"\r\n");
                                    }
                                    if (bean.getBody() != null) {
                                        if (bean.getBody().getName() != null) {
                                            Printer.print("\r\n");
                                            Printer.setBold(0);
                                            Printer.setAlignType(0); //左对齐
                                            Printer.setZoonIn(1, 1);
                                            Printer.setLeftMargin(30);
                                            Printer.print("业务名称:");
                                            Printer.print(bean.getBody().getName()
                                                    +"\r\n"+"等待人数:"+bean.getBody().getNumber()
                                                    +"\r\n"+"取号时间:"+bean.getBody().getTime()
                                                    +"\r\n"+bean.getBody().getRemind()
                                                    +"\r\n\r\n\r\n\r\n");
                                        }
                                    }
                                    Printer.closePrinter();
                                    return true;
                                } else {
                                    Printer.closePrinter();
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observable);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
