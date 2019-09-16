package com.bupocket.voucher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;

public class BPAssetIssuerAdapter extends AbsViewHolderAdapter {


    public BPAssetIssuerAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void convert(BaseViewHolder holder, Object itemData) {

    }
}
