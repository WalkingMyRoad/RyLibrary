package com.ist.rylibrary.base.exception;

/**
 * Created by minyuchun on 2017/4/6.
 * 自定义异常
 */

public class ResponseException extends Exception{
    public ResponseException(String errorMessage){
        super(errorMessage);
    }
}
