package com.bupocket.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.AdvertisingEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.AdvertisingService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.AdModel;
import com.bupocket.model.AdvertisingModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.TimeUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bupocket.common.Constants.WeChat_APPID;
import static com.bupocket.common.Constants.XB_YOUPING_USERNAME;

public class BPSendStatusFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.targetAddr)
    TextView targetAddrTv;

    @BindView(R.id.sendAmount)
    TextView sendAmountTv;

    @BindView(R.id.sendFee)
    TextView sendFeeTv;

    @BindView(R.id.sendNote)
    TextView sendNoteTv;

    @BindView(R.id.sendTimeTv)
    TextView mSendTimeTv;

    @BindView(R.id.sendTokenStatusIcon)
    QMUIRadiusImageView mSendTokenStatusIcon;

    @BindView(R.id.sendTokenStatusTv)
    TextView mSendTokenStatusTv;

    @BindView(R.id.llTransactionSuccess)
    LinearLayout llTransactionSuccess;

    @BindView(R.id.llStatusFailed)
    LinearLayout llStatusFailed;

    @BindView(R.id.ivTransSuccess)
    ImageView ivTransSuccess;


    @BindView(R.id.sendTransHash)
    TextView  tvTransHash;

    @BindView(R.id.tvFromAddress)
    TextView tvFormAddress;

    private AdModel ad;
    private String fragmentTag;


    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send_status, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        initListener();
        return root;
    }

    private void initListener() {
        ivTransSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad!=null) {
                    if (AdvertisingEnum.APP.getCode().equals(ad.getType())) {
                        CommonUtil.goWeChat(getContext(),WeChat_APPID,XB_YOUPING_USERNAME);
                    }
                }
            }
        });
    }


    private void initData() {

        Drawable txStatusIconDrawable;
        String destAccAddr = getArguments().getString("destAccAddr");
        String tokenCode = getArguments().getString("tokenCode");
        String sendAmount = getArguments().getString("sendAmount") + " " + tokenCode;
        String txFee = getArguments().getString("txFee") + " BU";
        String note = getArguments().getString("note");
        String sendTime = getArguments().getString("sendTime");
        Integer txStatus = Integer.parseInt(getArguments().getString("state"));
        String sendTokenStatus = getArguments().getString("sendTokenStatusKey");
        String txHash = getArguments().getString("txHash");

        fragmentTag = getArguments().getString("fragmentTag");

        String txStatusStr;
        llTransactionSuccess.setVisibility(View.GONE);
        llStatusFailed.setVisibility(View.GONE);
        if (txStatus.equals(TxStatusEnum.SUCCESS.getCode())) {
            if (TextUtils.isEmpty(sendTokenStatus)) {
                llTransactionSuccess.setVisibility(View.VISIBLE);
                mTopBar.setTitle(R.string.transaction_success_title);
            } else {
                llStatusFailed.setVisibility(View.VISIBLE);
                txStatusIconDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_send_success);
                txStatusStr = getResources().getString(R.string.tx_status_success_txt);
                mSendTokenStatusIcon.setImageDrawable(txStatusIconDrawable);
                mSendTokenStatusTv.setText(txStatusStr);
            }

        } else if (txStatus.equals(ExceptionEnum.ERROR_151.getCode())){
            llStatusFailed.setVisibility(View.VISIBLE);
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.tx_status_fail_txt3);
            mSendTokenStatusIcon.setImageDrawable(txStatusIconDrawable);
            mSendTokenStatusTv.setText(txStatusStr);
        }else{
            llStatusFailed.setVisibility(View.VISIBLE);
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.tx_status_fail_txt);
            mSendTokenStatusIcon.setImageDrawable(txStatusIconDrawable);
            mSendTokenStatusTv.setText(txStatusStr);

        }

        tvFormAddress.setText(getWalletAddress());
        targetAddrTv.setText(destAccAddr);
        sendAmountTv.setText(sendAmount);
        sendFeeTv.setText(txFee);
        sendNoteTv.setText(note);
        tvTransHash.setText(txHash);
        mSendTimeTv.setText(TimeUtil.timeStamp2Date(sendTime));




        getAdInfo();
    }

    private void getAdInfo() {

        AdvertisingService adService = RetrofitFactory.getInstance().getRetrofit().create(AdvertisingService.class);
        adService.getAdInfo().enqueue(new Callback<ApiResult<AdvertisingModel>>() {
            @Override
            public void onResponse(Call<ApiResult<AdvertisingModel>> call, Response<ApiResult<AdvertisingModel>> response) {
                ApiResult<AdvertisingModel> body = response.body();
                if (body==null) {
                    return;
                }
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    ad = body.getData().getAd();

                    if (ad ==null) {
                        return;
                    }

                    Glide.with(mContext)
                            .load(ad.getImageUrl())
                            .into(ivTransSuccess);

                }

            }

            @Override
            public void onFailure(Call<ApiResult<AdvertisingModel>> call, Throwable t) {

                LogUtils.e(""+t.getMessage());
            }
        });
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    popBackStack();
            }
        });
    }

    @Override
    public void popBackStack() {
        if (!TextUtils.isEmpty(fragmentTag)&&fragmentTag.equals(HomeFragment.class.getSimpleName())) {
            startFragmentAndDestroyCurrent(new HomeFragment());
        }else {
            super.popBackStack();

        }

    }

    public void onBackPressed() {
        startFragmentAndDestroyCurrent(new HomeFragment());
    }

}
