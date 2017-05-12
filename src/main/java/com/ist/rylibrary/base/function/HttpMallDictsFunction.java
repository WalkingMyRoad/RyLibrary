package com.ist.rylibrary.base.function;

import com.ist.rylibrary.base.entity.MallDictsResultBean;
import com.ist.rylibrary.base.exception.ResponseException;

import io.reactivex.functions.Function;

/**
 * Created by minyuchun on 2017/4/7.
 */

public class HttpMallDictsFunction<T> implements Function<MallDictsResultBean<T>,T> {
    @Override
    public T apply(MallDictsResultBean<T> tMallDictsResultBean) throws Exception {
        if (tMallDictsResultBean.getRetcode()!=null && tMallDictsResultBean.getRetinfo()!=null && tMallDictsResultBean.getMallDicts()!=null){
            if (!tMallDictsResultBean.getRetcode().equals("0")){
                throw new ResponseException(tMallDictsResultBean.getRetinfo());
            }
            return tMallDictsResultBean.getMallDicts();
        }else{
            throw new ResponseException(null);
        }
    }
}
