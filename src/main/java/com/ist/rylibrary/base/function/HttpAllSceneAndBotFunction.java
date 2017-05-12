package com.ist.rylibrary.base.function;

import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.entity.AllSceneResultBean;
import com.ist.rylibrary.base.entity.MallDictsResultBean;
import com.ist.rylibrary.base.exception.ResponseException;

import io.reactivex.functions.Function;

/**
 * Created by minyuchun on 2017/4/7.
 */

public class HttpAllSceneAndBotFunction implements Function<AllSceneResultBean,AllSceneResultBean> {

    public String TAG = "HttpAllScene";

    @Override
    public AllSceneResultBean apply(AllSceneResultBean allSceneResultBean) throws Exception {
        if (allSceneResultBean.getRetcode()!=null && allSceneResultBean.getRetinfo()!=null){
            if (!allSceneResultBean.getRetcode().equals("0")){
                throw new ResponseException(allSceneResultBean.getRetinfo());
            }
            return allSceneResultBean;
        }else{
            throw new ResponseException(null);
        }
    }
}
