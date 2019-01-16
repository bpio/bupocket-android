package com.bupocket.wallet.enums;

public enum ExceptionEnum {
    BU_NOT_ENOUGH_FOR_PAYMENT("100", "bu not enough for payment"),
    SYS_ERR("10000","system exception"),
    INVALID_PASSWORD("10001","Invalid password"),
    FEE_NOT_ENOUGH("10002","Fee not enough"),
    BU_NOT_ENOUGH("10003","bu not enough"),
    IMPORT_KEYSTORE_VERSION_ERROR("20001","the keyStore version was wrong"),
    IMPORT_KEYSTORE_ADDRESS_ERROR("20002","the address in the keyStore was wrong, please check");

    private final String code;
    private final String message;

    private ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
