package com.bupocket.wallet.enums;

public enum BUChainExceptionEnum {
    ERRCODE_FEE_NOT_ENOUGH(111,"Fee not enough"),
    ERRCODE_ACCOUNT_LOW_RESERVE(100,"account low reserve"),
    ERRCODE_OTHER(152,"152");

    private final Integer code;
    private final String message;

    private BUChainExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
