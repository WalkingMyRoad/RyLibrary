package com.ist.rylibrary.base.function;

import com.ist.rylibrary.base.entity.DefaultResultBean;
import com.ist.rylibrary.base.entity.MallDictsResultBean;
import com.ist.rylibrary.base.exception.ResponseException;

import io.reactivex.functions.Function;

/**
 * Created by minyuchun on 2017/4/7.
 */

public class HttpDefaultFunction implements Function<DefaultResultBean,DefaultResultBean> {

    @Override
    public DefaultResultBean apply(DefaultResultBean defaultResultBean) throws Exception {
        if (defaultResultBean.getRetcode()!=null && defaultResultBean.getRetinfo()!=null){
            if (!defaultResultBean.getRetcode().equals("0")){
                throw new ResponseException(defaultResultBean.getRetinfo());
            }
            return defaultResultBean;
        }else{
            throw new ResponseException(null);
        }
    }
}
