package com.bupocket.enums;

import com.bupocket.R;

public enum  VoteNodeEnum {

    VOTE("1","投","投票",R.drawable.shape_corner_green),
    CANCLE("2","撤","撤票",R.drawable.shape_corner_red);

    private String code;
    private String name1;
    private String name2;
    private int drawableRes;

    VoteNodeEnum(String code, String name1, String name2, int drawableRes) {
        this.code = code;
        this.name1 = name1;
        this.name2 = name2;
        this.drawableRes = drawableRes;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public String getCode() {
        return code;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }
}
