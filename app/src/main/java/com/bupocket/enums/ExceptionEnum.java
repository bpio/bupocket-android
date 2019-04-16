package com.bupocket.enums;

public enum ExceptionEnum {
    SUCCESS("0","success"),
    VOTE_CLOSE("1029","vote close"),
    NODE_TIMEOUT("1006","请求已超时"),
    EEEOR_1003("1003","该待绑定的物理地址已被占用，请更换后再申请"),
    ERROR_1009("1009"," 该钱包账户已申请过，请更换后再进行申请"),
    ERROR_1011( "1011","对应的提示「检测到有尚未提交的操作，请稍后再重试"),
    ADDRESS_ALREADY_EXISTED("100055","the contact is already existed")
    ;
    private final String code;
    private final String message;

    private ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
