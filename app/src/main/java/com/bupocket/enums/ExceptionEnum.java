package com.bupocket.enums;

public enum ExceptionEnum {
    SUCCESS("0","success"),
    VOTE_CLOSE("1029","vote close"),
    ADDRESS_ALREADY_EXISTED("100055","the contact is already existed")
    ;
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
