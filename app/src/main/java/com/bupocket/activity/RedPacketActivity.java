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
import com.bupocket.fragment.BPAssetsHomeFragment;
import com.bupocket.http.api.RedPacketService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.BonusInfoBean;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.ShareUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.WalletCurrentUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                openRedPacket();
                break;
            case R.id.closeRedBtn:
                finish();
                break;
            case R.id.cancelRedDetailBtn:
                setResult(1);
                finish();
                break;
            case R.id.saveShareBtn:
                shareRedPacket();
                break;

        }
    }

    private void openRedPacket() {
        final RedPacketService redPacketService = RetrofitFactory.getInstance().getRetrofit().create(RedPacketService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.BONUSCODE, bonusCode);
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this, ConstantsType.BP_FILE_NAME);
        map.put(ConstantsType.ADDRESS, WalletCurrentUtils.getWalletAddress(spHelper));
        redPacketService.openRedPacket(map).enqueue(new Callback<ApiResult<BonusInfoBean>>() {
            @Override
            public void onResponse(Call<ApiResult<BonusInfoBean>> call, Response<ApiResult<BonusInfoBean>> response) {
                ApiResult<BonusInfoBean> body = response.body();
                if (body != null && !TextUtils.isEmpty(body.getErrCode())) {
                    if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                        BonusInfoBean data = body.getData();
                        initOpenRedPacketView(data);
                        BPAssetsHomeFragment.openStatus = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResult<BonusInfoBean>> call, Throwable t) {

            }
        });
    }

    private void initOpenRedPacketView(BonusInfoBean data) {
        Glide.with(this).load(data.getTopImage()).into(redTopIv);
        Glide.with(this).load(data.getBottomImage()).into(downloadQrIv);
        Glide.with(this).load(data.getIssuerPhoto()).into(redHeadIv);

        String issuerNick = data.getIssuerNick();
        if (!TextUtils.isEmpty(issuerNick)) {
            redNameIv.setText(issuerNick);
        }

        String amount = data.getAmount();
        if (!TextUtils.isEmpty(amount)) {
            redAmountTv.setText(amount);
        }

        String tokenSymbol = data.getTokenSymbol();
        if (!TextUtils.isEmpty(tokenSymbol)) {
            redTokenTypeTv.setText(tokenSymbol);
        }

        String receiver = data.getReceiver();
        if (!TextUtils.isEmpty(receiver)) {
            redWalletAddressTv.setText(getString(R.string.save_wallet) + "\n" + AddressUtil.anonymous(receiver));
        }

        redPacketFirstLL.setVisibility(View.GONE);
        redPacketDetailLL.setVisibility(View.VISIBLE);
    }


    private void shareRedPacket() {

        saveShareBtn.setVisibility(View.GONE);
        ShareUtils.shareImage(redPacketDetailLL, this);
        saveShareBtn.setVisibility(View.VISIBLE);
//        ShareUtils.saveImage("redPacket",redPacketDetailLL,this);
    }


}
