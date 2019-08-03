package com.bupocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.RedPacketTypeEnum;
import com.bupocket.model.BonusInfoBean;
import com.bupocket.utils.ShareUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RedPacketActivity extends Activity {


    @BindView(R.id.noOpenRedBgIv)
    ImageView noOpenRedBgIv;
    @BindView(R.id.redPacketDetailLL)
    LinearLayout redPacketDetailLL;
    @BindView(R.id.saveShareBtn)
    Button saveShareBtn;
    @BindView(R.id.openRedPacketBtn)
    Button openRedPacketBtn;
    @BindView(R.id.closeRedBtn)
    ImageView closeRedBtn;
    @BindView(R.id.redPacketFirstLL)
    LinearLayout redPacketFirstLL;
    @BindView(R.id.redAmountTv)
    TextView redAmountTv;
    @BindView(R.id.downloadQrIv)
    ImageView downloadQrIv;
    @BindView(R.id.redTopIv)
    ImageView redTopIv;
    @BindView(R.id.cancelRedDetailBtn)
    ImageView cancelRedDetailBtn;
    @BindView(R.id.redHeadIv)
    RoundedImageView redHeadIv;
    @BindView(R.id.redNameIv)
    TextView redNameIv;
    @BindView(R.id.redTokenTypeTv)
    TextView redTokenTypeTv;
    @BindView(R.id.redWalletAddressTv)
    TextView redWalletAddressTv;

    private Unbinder bind;
    private String bonusCode;
    private BonusInfoBean bonusInfoBean;
    private boolean openStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packet_home);
        bind = ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            String openStatus = intent.getStringExtra(ConstantsType.REDOPENSTATUS);
            if (!TextUtils.isEmpty(openStatus)) {


                if (openStatus.equals(RedPacketTypeEnum.CLOSE_RED_PACKET.getCode())) {
                    this.openStatus = false;
                    bonusCode = intent.getStringExtra(ConstantsType.BONUSCODE);
                    Bundle extras = intent.getExtras();
                    bonusInfoBean = ((BonusInfoBean) extras.getSerializable(ConstantsType.BONUSINFOBEAN));

                    setNoOpenRedPacketView(bonusInfoBean);
                }

//                else if (openStatus.equals(RedPacketTypeEnum.OPEN_RED_PACKET.getCode())) {
//                    this.openStatus = true;
//
//
//                }

            }

        }


    }

    private void setNoOpenRedPacketView(BonusInfoBean bonusInfoBean) {
        Glide.with(this).load(bonusInfoBean.getTopImage()).into(noOpenRedBgIv);
    }

    private void initView() {
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    @OnClick({R.id.openRedPacketBtn, R.id.closeRedBtn, R.id.saveShareBtn, R.id.cancelRedDetailBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.openRedPacketBtn:
                redPacketFirstLL.setVisibility(View.GONE);
                redPacketDetailLL.setVisibility(View.VISIBLE);
                break;
            case R.id.closeRedBtn:
            case R.id.cancelRedDetailBtn:
                finish();
                break;
            case R.id.saveShareBtn:
                shareRedPacket();
                break;

        }
    }


    private void shareRedPacket() {
        ShareUtils.shareImage(redPacketDetailLL, this);
//        ShareUtils.saveImage("redPacket",redPacketDetailLL,this);
    }
}
