package com.bupocket.enums;

import com.bupocket.R;

public enum BuildTypeEnum {

    SUPPORT("1", R.string.build_type_support),
    EXIT("2", R.string.build_type_exit);


    BuildTypeEnum(String code, int msgRes) {
        this.code = code;
        this.msgRes = msgRes;
    }

    private String code;

    private int msgRes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMsgRes() {
        return msgRes;
    }

    public void setMsgRes(int msgRes) {
        this.msgRes = msgRes;
    }
}
