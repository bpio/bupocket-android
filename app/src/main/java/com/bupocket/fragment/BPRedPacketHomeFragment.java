package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseTransferFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPRedPacketHomeFragment extends BaseTransferFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;


    @BindView(R.id.redTopIv)
    ImageView redTopIv;
    @BindView(R.id.cancelRedDetailBtn)
    ImageView cancelRedDetailBtn;
    @BindView(R.id.redHeadIv)
    RoundedImageView redHeadIv;
    @BindView(R.id.redNameIv)
    TextView redNameIv;
    @BindView(R.id.redAmountTv)
    TextView redAmountTv;
    @BindView(R.id.redTokenTypeTv)
    TextView redTokenTypeTv;
    @BindView(R.id.redWalletAddressTv)
    TextView redWalletAddressTv;
    @BindView(R.id.downloadQrIv)
    ImageView downloadQrIv;
    @BindView(R.id.saveShareBtn)
    Button saveShareBtn;
    @BindView(R.id.redPacketDetailLL)
    LinearLayout redPacketDetailLL;
    @BindView(R.id.redPacketLv)
    ListView redPacketLv;
    @BindView(R.id.redPacketDetailTitleTv)
    TextView redPacketDetailTitleTv;
    @BindView(R.id.redPacketDetailTv)
    TextView redPacketDetailTv;


    Unbinder unbinder;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_red_packet_home;
    }

    @Override
    protected void initView() {
        initTopBar();
        cancelRedDetailBtn.setVisibility(View.GONE);
    }

    private void initTopBar() {
        topbar.setTitle(R.string.red_success_title);
        topbar.setBackgroundDividerEnabled(false);
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
