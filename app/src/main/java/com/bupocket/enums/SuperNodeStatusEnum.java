package com.bupocket.enums;

public enum SuperNodeStatusEnum {

        SUCCESS("1","申请成功"),
        RUNING("2","退出中"),
        FAILED("3","已退出");



    SuperNodeStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
