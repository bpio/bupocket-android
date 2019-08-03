package com.bupocket.enums;

public enum RedPacketTypeEnum {
    CLOSE_RED_PACKET("0","close"),
    OPEN_RED_PACKET("100022","open");

    String code;
    String msg;

     RedPacketTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
