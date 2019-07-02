package com.bupocket.voucher;

import android.os.Bundle;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.voucher.model.VoucherDetailModel;

import java.io.Serializable;

public class BPVoucherDetailFragment extends AbsBaseFragment {

    private VoucherDetailModel voucherDetailModel;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        Bundle arguments = getArguments();
        if (arguments!=null) {
            voucherDetailModel = (VoucherDetailModel) arguments.getSerializable(ConstantsType.VOUCHER_DETAIL);
        }
    }

    @Override
    protected void setListeners() {

    }
}
