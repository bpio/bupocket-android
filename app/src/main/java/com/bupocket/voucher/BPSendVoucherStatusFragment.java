package com.bupocket.voucher;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.activity.BPWebActivity;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.AdvertisingEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.BPWalletManageFragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bupocket.common.Constants.WeChat_APPID;
import static com.bupocket.common.Constants.XB_YOUPING_USERNAME;

public class BPSendVoucherStatusFragment extends BaseFragment {

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
    @BindView(R.id.llStatusFailed)
    LinearLayout llStatusFailed;
    @BindView(R.id.sendTransHash)
    TextView tvTransHash;
    @BindView(R.id.tvFromAddress)
    TextView tvFormAddress;


    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send_voucher_status, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        initListener();
        return root;
    }

    private void initListener() {

    }


    private void initData() {

        Drawable txStatusIconDrawable;
        String destAccAddr = getArguments().getString("destAccAddr");
        String tokenCode = getArguments().getString("tokenCode");
        String sendAmount = getArguments().getString("sendAmount");
        String txFee = getArguments().getString("txFee");
        String note = getArguments().getString("note");
        String sendTime = getArguments().getString("sendTime");
        Integer txStatus = Integer.parseInt(getArguments().getString("state"));
        String sendTokenStatus = getArguments().getString("sendTokenStatusKey");
        String txHash = getArguments().getString("txHash");
        String txStatusStr;

        llStatusFailed.setVisibility(View.GONE);
        if (txStatus.equals(TxStatusEnum.SUCCESS.getCode())) {
            llStatusFailed.setVisibility(View.VISIBLE);
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_send_success);
            mSendTokenStatusIcon.setImageDrawable(txStatusIconDrawable);

        } else if (txStatus.equals(ExceptionEnum.ERROR_151.getCode())) {
            llStatusFailed.setVisibility(View.VISIBLE);
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.tx_status_fail_txt3);
            mSendTokenStatusIcon.setImageDrawable(txStatusIconDrawable);
            mSendTokenStatusTv.setText(txStatusStr);
        } else {
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


    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
                getFragmentManager().popBackStack(BPVoucherDetailFragment.class.getSimpleName(), 1);
            }
        });
    }


}
