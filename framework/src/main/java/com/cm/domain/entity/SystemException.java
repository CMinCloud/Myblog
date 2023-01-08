package com.cm.domain.entity;


import com.cm.domain.enums.AppHttpCodeEnum;

/**
 * 该类用于自定义异常,在controller层抛出
 */
public class SystemException extends RuntimeException{

    private int code;

    private String msg;

//    封装异常信息为一个异常类对象
    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }



}
