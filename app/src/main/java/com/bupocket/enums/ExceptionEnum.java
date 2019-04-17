package com.bupocket.enums;

import com.bupocket.R;

public enum ExceptionEnum {
    SUCCESS("0","success", R.string.req_success),
    ERROR_VOTE_CLOSE("1029","vote close",R.string.error_vote_close),
    ERROR_TIMEOUT("1006","request timeout",R.string.error_timeout),
    ERROR_1003("1003","test",R.string.error_1003),
    ERROR_1009("1009","",R.string.error_1009),
    ERROR_1011( "1011","",R.string.error_1011),
    ERROR_ADDRESS_ALREADY_EXISTED("100055","",R.string.error_100055);
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
