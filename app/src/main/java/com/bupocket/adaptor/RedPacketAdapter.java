package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.LuckRedModel;

public class RedPacketAdapter extends AbsViewHolderAdapter<LuckRedModel> {


    public RedPacketAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_red_packet_detail_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, LuckRedModel itemData) {

    }
}
