package com.bupocket.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.adaptor.RedPacketAdapter;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.http.api.RedPacketService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.BonusInfoBean;
import com.bupocket.model.RedPacketDetailModel;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.utils.WalletUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.Serializable;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private RedPacketAdapter adapter;
    private String bonusCode;
    private RedPacketDetailModel redPacketDetailModel;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_red_packet_home;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
        cancelRedDetailBtn.setVisibility(View.GONE);
    }

    private void initListView() {
        adapter = new RedPacketAdapter(mContext);
        redPacketLv.setAdapter(adapter);
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
        Bundle arguments = getArguments();
        if (arguments!=null) {
            bonusCode = arguments.getString(ConstantsType.BONUSCODE);
            redPacketDetailModel = (RedPacketDetailModel) arguments.getSerializable(ConstantsType.REDPACKETDETAILMODEL);

            BonusInfoBean bonusInfo = redPacketDetailModel.getBonusInfo();
            initOpenRedPacketView(bonusInfo);
        }
//        reqAllData();

    }

    private void reqAllData() {

        RedPacketService redPacketService = RetrofitFactory.getInstance().getRetrofit().create(RedPacketService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.BONUSCODE, "");
        map.put(ConstantsType.ADDRESS, WalletCurrentUtils.getWalletAddress(spHelper));
        redPacketService.queryRedPacketDetail(map).enqueue(new Callback<ApiResult<RedPacketDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<RedPacketDetailModel>> call, Response<ApiResult<RedPacketDetailModel>> response) {



            }

            @Override
            public void onFailure(Call<ApiResult<RedPacketDetailModel>> call, Throwable t) {

            }
        });
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
            redWalletAddressTv.setText(AddressUtil.anonymous(receiver));
        }

        redPacketDetailLL.setVisibility(View.VISIBLE);
    }
}
