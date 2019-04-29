package com.bupocket.enums;

import com.bupocket.R;

public enum  VoteNodeEnum {

    VOTE("1",R.string.vote_txt,"投票",R.drawable.shape_corner_green),
    CANCLE("2",R.string.withdraw_vote_txt,"撤票",R.drawable.shape_corner_red);

    private String code;
    private int nameRes;
    private String name2;
    private int drawableRes;

    VoteNodeEnum(String code, int nameRes, String name2, int drawableRes) {
        this.code = code;
        this.nameRes = nameRes;
        this.name2 = name2;
        this.drawableRes = drawableRes;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public String getCode() {
        return code;
    }

    public int getNameRes() {
        return nameRes;
    }

    public String getName2() {
        return name2;
    }
}
