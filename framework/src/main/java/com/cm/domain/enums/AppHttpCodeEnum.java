package com.cm.domain.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    // 登录
    NEED_LOGIN(401, "需要登录后操作"),
    NO_OPERATOR_AUTH(403, "无权限操作"),
    SYSTEM_ERROR(500, "出现错误"),
    USERNAME_EXIST(501, "用户名已存在"),
    PHONENUMBER_EXIST(502, "手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505, "用户名或密码错误"),
    CONTENT_NOT_NULL(506, "评论内容不能为空"),
    IMG_TYPE_ERROR(507, "图片类型错误"),
    IMG_UPLOAD_ERROR(508, "文件上传失败,可能是云存储已过期"),
    USERNAME_NOT_NULL(509, "用户名不能为空"),
    NICKNAME_NOT_NULL(510, "用户名不能为空"),
    PASSWORD_NOT_NULL(511, "用户名不能为空"),
    EMAIL_NOT_NULL(512, "邮箱不能为空"),
    NICKNAME_EXIST(513, "昵称已存在"),

    KEYWORDS_NOT_NULL(555, "关键字不能为空"),
    USER_NOT_EXIST(556,"该用户已注销"),
    REDUPLICATED_FOLLOW(557,"不能重复关注"),
    FOLLOWSELF(558,"不能关注自己哦~"),
    NO_FOLLOW(559,"暂无关注的博主~");


    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
