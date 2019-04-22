package com.bupocket.enums;

public enum ScanTransactionTypeEnum {
    NODE_VOTE("3","用户投票"),
    APPLY_CO_BUILD("4","共建自购"),
    NODE_AUDIT("8","委员会审核"),
    CO_BUILD_SUPPORT("5","共建支持"),
    ;

    private String code;
    private String name;

    private ScanTransactionTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
