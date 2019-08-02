package com.bupocket.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.bupocket.R;
import com.bupocket.base.BaseTransferFragment;

public class BPRedPacketHomeFragment extends BaseTransferFragment {



    @Override
    protected int getLayoutView() {
//        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getActivity().getWindow().setBackgroundDrawableResource(R.color.app_bg_color_gray);
        return R.layout.fragment_red_packet_home;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }
}
