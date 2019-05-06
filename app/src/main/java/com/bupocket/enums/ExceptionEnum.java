package com.bupocket.enums;

import com.bupocket.R;

public enum ExceptionEnum {
    SUCCESS("0","success", R.string.tx_status_success_txt),

    ERROR_151("151","vote close",R.string.tx_status_fail_txt3),
    ERROR_VOTE_CLOSE("1029","vote close",R.string.error_vote_close),
    ERROR_TIMEOUT("1006","the qr code does not exist or has expired",R.string.error_timeout),
    ERROR_NODE_1003("1003","test",R.string.error_1003),
    ERROR_NODE_1009("1009","",R.string.error_1009),
    ERROR_TRANSACTION_OTHER_1011( "1011","",R.string.error_1011),
    ERROR_ADDRESS_ALREADY_EXISTED("100055","",R.string.error_100055),

    ERROR_BUILD_1024("1024","",R.string.error_build_1024),
    ERROR_BUILD_1028("1028","copies is not available",R.string.error_build_1028),
    ERROR_BUILD_1031("1031","",R.string.error_1031),
    ERROR_BUILD_1032("1032","",R.string.error_1032),
    ERROR_BUILD_1033("1033","",R.string.error_1033),
    ERROR_BUILD_1034("1034","",R.string.error_1034),
    ERROR_BUILD_1035("1035","",R.string.error_1035),
    ERROR_BUILD_1036("1036","",R.string.error_1036),
    ERROR_BUILD_1038("1038","",R.string.error_1038),
    ;
    private final String code;
    private final String message;
    private final int msg;

    ExceptionEnum(String code, String message,int msg) {
        this.code = code;
        this.message = message;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    public int getMsg() {
        return msg;
    }

    //
    public static ExceptionEnum getByValue(String value) {
        for (ExceptionEnum code : values()) {
            if (value.equals(code.getCode())) {
                return code;
            }
        }
        return null;
    }

}
