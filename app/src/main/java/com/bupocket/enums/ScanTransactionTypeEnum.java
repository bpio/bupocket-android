package com.bupocket.enums;

public enum ScanTransactionTypeEnum {
    NODE_VOTE("3","用户投票"),
    NODE_AUDIT("8","委员会审核"),
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
