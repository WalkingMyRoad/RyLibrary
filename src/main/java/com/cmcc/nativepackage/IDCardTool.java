package com.cmcc.nativepackage;

import android.util.Log;

import com.cmcc.IDCardBean;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by minyuchun on 2017/6/12.
 * 应用的idcard操作
 */

public class IDCardTool {

    private static IDCardTool tool;

    public static IDCardTool getInstance(){
        if(tool == null){
            tool = new IDCardTool();
        }
        return tool;
    }

    public IDCardTool(){
    }

    public void readIdcard(Observer<IDCardBean> observable){
        try{
            Observable.interval(2, TimeUnit.SECONDS,Schedulers.trampoline())
                    //可能涉及到IO操作，放在子线程
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map(new Function<Long, IDCardBean>() {
                        @Override
                        public IDCardBean apply(Long aLong) throws Exception {
                            Log.d("IDCardTool","Thread "+Thread.currentThread().getName());
                            IDCardBean bean = new IDCardBean();
                            int ret = IDCard.openIDCard(2, "", "");//打开身份证读取串口
                            bean.setOpenResult(ret);
                            if(ret == 0){
                                String[] idCardInfo = new String[9];//用于读取身份证信息的数组
                                byte[] img = new byte[60000];//身份证照片的字节
                                int result = IDCard.getIdCardInfo(idCardInfo, img);//读取身份证信息的返回值
                                bean.setReadResult(result);
                                if(result == 0){
                                    Log.d("MyTest"," -- "+idCardInfo);
                                    bean.setName(idCardInfo[0]);
                                    bean.setSex(idCardInfo[1]);
                                    bean.setNation(idCardInfo[2]);
                                    bean.setBirthday(idCardInfo[3]);
                                    bean.setAddress(idCardInfo[4]);
                                    bean.setIDCard_No(idCardInfo[5]);
                                    bean.setIssue(idCardInfo[6]);
                                    bean.setEnd(idCardInfo[7]);
                                    bean.setImg(idCardInfo[8]);
                                }
                            }
                            IDCard.closeIDCard();
                            return bean;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
