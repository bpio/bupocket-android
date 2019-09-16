package com.bupocket.enums;

import com.bupocket.fragment.BPAssetsHomeFragment;
import com.bupocket.voucher.BPVoucherHomeFragment;

public enum VoucherStatusEnum {
    ASSETS_HOME_FRAGMENT("0", BPAssetsHomeFragment.class.getSimpleName()),
    VOUCHER_HOME_FRAGMENT("1", BPVoucherHomeFragment.class.getSimpleName())
    ;

    private String code;
    private String name;

    private VoucherStatusEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
