package com.bupocket.enums;

public enum SuperNodeTypeEnum {
    VALIDATOR("1","记账节点"),
    ECOLOGICAL("2","生态节点"),
    ;

    private String code;
    private String name;

    private SuperNodeTypeEnum(String code, String name){
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
