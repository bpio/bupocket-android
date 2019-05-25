package com.bupocket.enums;

public enum AdvertisingEnum {
    APP("1"),
    H5("2");


    AdvertisingEnum(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
