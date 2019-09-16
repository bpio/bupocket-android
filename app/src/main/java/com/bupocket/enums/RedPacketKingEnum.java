package com.bupocket.enums;

public enum RedPacketKingEnum {
    YES("1","YES"),
    NO("0","NO");

    String code;
    String msg;

     RedPacketKingEnum(String code, String msg) {
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
