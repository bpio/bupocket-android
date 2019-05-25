package com.bupocket.enums;

import com.bupocket.R;

public enum SuperNodeStatusEnum {

        SUCCESS("1", R.string.super_status_success),
        RUNNING("2",R.string.super_status_running),
        FAILED("3",R.string.super_status_failed);


    SuperNodeStatusEnum(String code, int nameRes) {
        this.code = code;
        this.nameRes = nameRes;
    }

    private String code;

    private int nameRes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }
}
