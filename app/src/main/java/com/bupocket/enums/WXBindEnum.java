package com.bupocket.enums;

public enum WXBindEnum {

    BIND_WECHAT("1","bind"),
    UNBIND_WECHAT("0","unbind");

    private String code;
    private String msg;


    WXBindEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
