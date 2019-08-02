package com.bupocket.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bupocket.R;
import com.bupocket.utils.ShareUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RedPacketActivity extends Activity {


    @BindView(R.id.redPacketLL)
    LinearLayout redPacketLL;
    @BindView(R.id.saveShareBtn)
    Button saveShareBtn;

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_red_packet_home);
        bind = ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.saveShareBtn)
    public void onViewClicked() {
        shareRedPacket();
    }

    private void shareRedPacket() {
        ShareUtils.shareImage(redPacketLL, this);
//        ShareUtils.saveImage("redPacket",redPacketLL,this);
    }


}
