package com.bupocket.enums;

import com.bupocket.R;

public enum NodeStatusEnum {

    RUNING("0",R.string.tx_status_runing, R.color.app_txt_color_red_6),
    SUCCESS("1",R.string.tx_status_success_txt,R.color.app_color_green),
    FAILURE("2",R.string.tx_status_fail_txt, R.color.app_txt_color_red_4);


    private String code;
    private int name;
    private int color;

    private NodeStatusEnum(String code, int name, int  colorRes) {
        this.code = code;
        this.name = name;
        this.color=colorRes;
    }

    public int getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getColor() {
        return color;
    }


}
