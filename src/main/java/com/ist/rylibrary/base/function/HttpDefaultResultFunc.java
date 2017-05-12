package com.ist.rylibrary.base.function;

import com.ist.rylibrary.base.entity.HttpDefauleResultBean;
import com.ist.rylibrary.base.exception.ResponseException;

import io.reactivex.functions.Function;

/**
 * Created by minyuchun on 2017/4/6.
 * 来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */

public class HttpDefaultResultFunc<T> implements Function<HttpDefauleResultBean<T>,T> {
    @Override
    public T apply(HttpDefauleResultBean<T> tHttpDefauleResultBean) throws Exception {
        if (tHttpDefauleResultBean.getRetcode()!=null && tHttpDefauleResultBean.getRetinfo()!=null && tHttpDefauleResultBean.getData()!=null){
            if (!tHttpDefauleResultBean.getRetcode().equals("0")){
                throw new ResponseException(tHttpDefauleResultBean.getRetinfo());
            }
            return tHttpDefauleResultBean.getData();
        }else{
            throw new ResponseException(null);
        }
    }
}
