package com.bupocket.enums;

public enum ScanTransactionTypeEnum {

    //node
    NODE_ADD_MARGIN("2","追加质押金"),
    NODE_VOTE("3","用户投票"),
    NODE_EXIT("6","节点退出"),

    //co-build
    CO_BUILD_PURCHASE("4","共建自购"),
    CO_BUILD_SUPPORT("5","共建支持"),
    CO_BUILD_EXIT("7","共建解散"),
    CO_BUILD_APPLY_KOL_EXIT("17","共建发起者申请退出kol"),
    CO_BUILD_APPLY_KOL("13","共建申请kol"),

    //committee
    COMMITTEE_APPLY("80","委员会审核申请"),
    COMMITTEE_EXIT("81","委员会审核退出"),



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
