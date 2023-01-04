package com.cm.domain.enums;

//  统一错误码

/**
 * 控制登录信息错误码
 */
public enum ErrorCodeEnum {

    PARAM_ERROR(10001, "参数有误"),
    ACCOUNT_PWD_NOT_EXIST(10002, "用户名或密码不存在"),
    TOKEN_ERROR(10003,"TOKEN不合法"),
    NO_PERMISSION(70001, "无访问权限"),
    SESSION_TIME_OUT(90001,"会话超时"),
    NO_LOGIN(90002, "未登录"),;

    private int code;   //错误代码
    private String msg; //错误信息

    ErrorCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
