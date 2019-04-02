package com.bupocket.enums;

import com.bupocket.R;

public enum NodePlanMoreEnum {
    CANCLE_VOTE("撤销投票", R.mipmap.cancle_vote),
    VOTING_RECORD("投票记录",R.mipmap.voting_record),
    ;

    private int code;
    private String name;

    private NodePlanMoreEnum(String name,int imageRes){
        this.code = imageRes;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
