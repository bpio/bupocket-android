package com.bupocket.enums;

public enum CoBuildDetailStatusEnum {
    //    1 初始化 ，2 资料已录入(共建中) ，3 共建成功(带发起人确认)，4 共建退出
    CO_BUILD_INIT("1", "初始化"),
    CO_BUILD_RUNING("2","进行中"),
    CO_BUILD_SUCCESS("3","已完成"),
    CO_BUILD_EXIT("4","已退出");


    private String code;
    private String msg;

    CoBuildDetailStatusEnum(String code, String msg) {
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
