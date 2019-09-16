package com.bupocket.enums;

import com.bupocket.R;

import java.io.PipedReader;

public enum WalletHeadResEnum {


    ONEHEAD(0, R.mipmap.ic_normal_head);

    private int code;

    private int headIconRes;


    WalletHeadResEnum(int code, int headIconRes) {
        this.code = code;
        this.headIconRes = headIconRes;
    }
}
