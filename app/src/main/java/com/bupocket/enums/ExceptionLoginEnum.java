package com.bupocket.enums;

import com.bupocket.R;

public enum ExceptionLoginEnum {


    SUCCESS("0","success", R.string.tx_status_success_txt),
    ERROR_VOTE_CLOSE("600001","vote close",R.string.scan_qr_login_management_system_not_bound_txt),
    ERROR_TIMEOUT("600000","the qr code does not exist or has expired",R.string.error_timeout),

    ;
    private final String code;
    private final String message;
    private final int msg;

    ExceptionLoginEnum(String code, String message, int msg) {
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
    public static ExceptionLoginEnum getByValue(String value) {
        for (ExceptionLoginEnum code : values()) {
            if (value.equals(code.getCode())) {
                return code;
            }
        }
        return null;
    }

}
