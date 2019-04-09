package com.bupocket.enums;

import com.bupocket.R;

public enum NodeStatusEnum {

    RUNING("0","处理中", R.color.app_txt_color_red_6),
    SUCCESS("1","成功",R.color.app_color_green),
    FAILURE("2","失败", R.color.app_txt_color_red_4);


    private String code;
    private String name;
    private int color;

    private NodeStatusEnum(String code, String name, int  colorRes) {
        this.code = code;
        this.name = name;
        this.color=colorRes;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getColor() {
        return color;
    }


}
