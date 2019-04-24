package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.AdvertisingEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.AdvertisingService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.AdModel;
import com.bupocket.model.AdvertisingModel;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bupocket.common.Constants.WeChat_APPID;
import static com.bupocket.common.Constants.XB_YOUPING_USERNAME;

public class BPTransactionTimeoutFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    Unbinder unbinder;
    @BindView(R.id.ivTimeoutCopy)
    ImageView ivTimeoutCopy;
    @BindView(R.id.tvTimeoutHash)
    TextView tvTimeoutHash;
    @BindView(R.id.tvTransactionInfo)
    TextView tvTransactionInfo;
    @BindView(R.id.ivTimeout)
    ImageView ivTimeout;


    private String txHash;
    private AdModel ad;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_transaction_timeout, null);
        unbinder = ButterKnife.bind(this, view);
        init();
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        ivTimeout.setOnClickListener(new View.OnClickListener() {
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
        getAdInfo();
    }

    private void init() {

        txHash = getArguments().getString("txHash", "");
        tvTimeoutHash.setText(AddressUtil.anonymous(txHash, 8));
        tvTransactionInfo.setText(Html.fromHtml(getString(R.string.transaction_timeout_info)));
        initTopBar();
    }

    private void initTopBar() {

        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.transaction_timeout_title);
    }


    @OnClick(R.id.ivTimeoutCopy)
    public void onViewClicked() {

        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", txHash);
        cm.setPrimaryClip(mClipData);
        final QMUITipDialog copySuccessDiglog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(getString(R.string.qr_copy_success_message))
                .create();
        copySuccessDiglog.show();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                copySuccessDiglog.dismiss();
            }
        }, 1000);
    }


    private void getAdInfo() {

        AdvertisingService adService = RetrofitFactory.getInstance().getRetrofit().create(AdvertisingService.class);
        adService.getAdInfo().enqueue(new Callback<ApiResult<AdvertisingModel>>() {
            @Override
            public void onResponse(Call<ApiResult<AdvertisingModel>> call, Response<ApiResult<AdvertisingModel>> response) {
                ApiResult<AdvertisingModel> body = response.body();
                if (body == null) {
                    return;
                }
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    ad = body.getData().getAd();

                    if (ad == null) {
                        return;
                    }

                    Glide.with(mContext)
                            .load(ad.getImageUrl())
                            .into(ivTimeout);

                }

            }

            @Override
            public void onFailure(Call<ApiResult<AdvertisingModel>> call, Throwable t) {

                LogUtils.e("" + t.getMessage());
            }
        });
    }

}
