package com.bupocket.enums;

public enum BackupEnum {

    MY_IDENTITY("1", "my identity"),
    IDENTITY_WALLET("2", "identity wallet"),
    WALLET("3", "wallet"),
    ;

    private String code;
    private String msg;

    BackupEnum(String code, String msg) {
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
