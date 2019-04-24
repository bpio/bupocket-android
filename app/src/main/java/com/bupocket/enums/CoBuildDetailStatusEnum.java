package com.bupocket.enums;

import com.bupocket.R;

public enum CoBuildDetailStatusEnum {
    //    1 初始化 ，2 资料已录入(共建中) ，3 共建成功(带发起人确认)，4 共建退出
    CO_BUILD_RUNING("1", R.string.building),
    CO_BUILD_SUCCESS("2",R.string.build_success),
    CO_BUILD_FAILURE("3",R.string.build_failure),;


    private String code;
    private int msg;

    CoBuildDetailStatusEnum(String code, int msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public int getMsg() {
        return msg;
    }
}
