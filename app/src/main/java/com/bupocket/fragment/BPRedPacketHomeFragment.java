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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.adaptor.RedPacketAdapter;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.RedPacketService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.BonusInfoBean;
import com.bupocket.model.LuckRedModel;
import com.bupocket.model.RedPacketDetailModel;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.ShareUtils;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.WalletCurrentUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.HashMap;
import java.util.List;

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
    @BindView(R.id.luckTitleTv)
    TextView luckTitleTv;
    @BindView(R.id.shareRedPacketLL)
    LinearLayout shareRedPacketLL;


    Unbinder unbinder;
    private RedPacketAdapter adapter;
    private String bonusCode;
    private RedPacketDetailModel redPacketDetailModel;
    int measuredHeight;
    private static int SCROLL_TIME = 1500;
    private int LUCE_NUM = 40;
    private int luckIndex;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_red_packet_home;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();

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
        cancelRedDetailBtn.setVisibility(View.GONE);
        Bundle arguments = getArguments();
        if (arguments != null) {
            bonusCode = arguments.getString(ConstantsType.BONUSCODE);
            redPacketDetailModel = (RedPacketDetailModel) arguments.getSerializable(ConstantsType.REDPACKETDETAILMODEL);

            BonusInfoBean bonusInfo = redPacketDetailModel.getBonusInfo();
            initOpenRedPacketView(bonusInfo);

            List<LuckRedModel> data = redPacketDetailModel.getLatelyData().getData();
            adapter.setNewData(data);
            luckTitleTv.setText(redPacketDetailModel.getLatelyData().getLabel());
//            LUCE_NUM = data.size();

            redPacketDetailTitleTv.setText(redPacketDetailModel.getActivityRules().getLabel());
            String ruleData = redPacketDetailModel.getActivityRules().getData();

            if (!TextUtils.isEmpty(ruleData)) {
                String[] split = ruleData.split("；");
                ruleData="";
                for (int i = 0; i < split.length; i++) {
                    if (i==split.length-1){
                        ruleData=ruleData+split[i];
                    }else{
                        ruleData=ruleData+split[i]+"；\n";
                    }

                }
                redPacketDetailTv.setText(ruleData);
            }

        }


    }


    @Override
    public void onResume() {
        super.onResume();

        redPacketLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                luckIndex = 0;
                if (adapter == null || redPacketLv == null) {
                    return;
                }
                View view = adapter.getView(0, null, redPacketLv);
                view.measure(0, 0);
                measuredHeight = view.getMeasuredHeight();

                ViewGroup.LayoutParams layoutParams = redPacketLv.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
                layoutParams.height =measuredHeight*5 ;
                redPacketLv.setLayoutParams(layoutParams);
                scrollLuckListView();
            }
        }, 2000);

    }

    private void scrollLuckListView() {
        if (adapter.getData() != null) {
            if (adapter.getData().size() > 0) {
                Runnable redPacketRunnable = new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(SCROLL_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (redPacketLv != null) {

                                redPacketLv.smoothScrollBy(measuredHeight, 500);
                            }
                            ++luckIndex;
                            if (luckIndex == LUCE_NUM) {
                                luckIndex = 40;
                                reqAllData();
                            }
                        }
                    }
                };
                ThreadManager.getInstance().execute(redPacketRunnable);
            }
        }
    }

    private void reqAllData() {

        RedPacketService redPacketService = RetrofitFactory.getInstance().getRetrofit().create(RedPacketService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.BONUSCODE, bonusCode);
        map.put(ConstantsType.ADDRESS, WalletCurrentUtils.getWalletAddress(spHelper));
        redPacketService.queryRedPacketDetail(map).enqueue(new Callback<ApiResult<RedPacketDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<RedPacketDetailModel>> call, Response<ApiResult<RedPacketDetailModel>> response) {
                ApiResult<RedPacketDetailModel> body = response.body();
                if (body==null) {
                    return;
                }
                if (body.getErrCode().equals(ExceptionEnum.SUCCESS.getCode())) {
                    if (body.getData() != null) {

                        List<LuckRedModel> data = body.getData().getLatelyData().getData();
                        if (data != null) {
                            adapter.setNewData(data);
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<ApiResult<RedPacketDetailModel>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void setListeners() {
        saveShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareImage(shareRedPacketLL, getActivity());
            }
        });
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
        Glide.with(this).load(data.getTopImage())
                .error(R.mipmap.ic_red_packet_empty)
                .into(redTopIv);
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
            redWalletAddressTv.setText(getString(R.string.save_wallet_1) + "\n(" + AddressUtil.anonymous(receiver)+")");
        }

        redPacketDetailLL.setVisibility(View.VISIBLE);
    }
}
