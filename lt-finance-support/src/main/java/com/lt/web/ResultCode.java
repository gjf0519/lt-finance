package com.lt.web;

public enum ResultCode {
    SUCCESS(200,"操作成功"),
    AUTH_401_1(401,"用户未注册"),
    AUTH_401_2(401,"用户名或密码错误"),
    AUTH_403_1(403,"用户未登录"),
    AUTH_403_2(403,"用户已作废"),
    AUTH_403_3(403,"用户权限不足"),
    FAIL(500,"服务器内部错误");

    private int code;
    private String val;

    ResultCode(int code,String val) {
        this.code = code;
        this.val = val;
    }

    public int getCode() {
        return code;
    }

    public String getVal() {
        return val;
    }

}
