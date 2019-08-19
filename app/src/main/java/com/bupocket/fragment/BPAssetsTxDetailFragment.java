package com.bupocket.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.TxDetailSignatureAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.OutinTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.ToastUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BPAssetsTxDetailFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.txStatusIcon)
    QMUIRadiusImageView mTxStatusIcon;
    @BindView(R.id.sendAmountTv)
    TextView mSendAmountTv;
    @BindView(R.id.txStatusTv)
    TextView mTxStatusTv;
    @BindView(R.id.txFromAccAddrTv)
    TextView mTxFromAccAddrTv;
    @BindView(R.id.txToAccAddrTv)
    TextView mTxToAccAddrTv;
    @BindView(R.id.txDetailFeeTv)
    TextView mTxDetailFeeTv;
    @BindView(R.id.txDetailSendDateTv)
    TextView mTxDetailSendDateTv;
    @BindView(R.id.txDetailNoteTv)
    TextView mTxDetailNoteTv;
    @BindView(R.id.txDetailTXHashTv)
    TextView mTxDetailTXHashTv;
    @BindView(R.id.txDetailTxInfoSourceAddressTv)
    TextView mTxDetailTxInfoSourceAddressTv;
    @BindView(R.id.txDetailTxInfoDestAddressTv)
    TextView mTxDetailTxInfoDestAddressTv;
    @BindView(R.id.txDetailTxInfoAmountTv)
    TextView mTxDetailTxInfoAmountTv;
    @BindView(R.id.txDetailTxInfoTXFeeTv)
    TextView mTxDetailTxInfoTXFeeTv;
    @BindView(R.id.txDetailTxInfoNonceTv)
    TextView mTxDetailTxInfoNonceTv;
    @BindView(R.id.txDetailBlockInfoBlockHeightTv)
    TextView mTxDetailBlockInfoBlockHeightTv;
    @BindView(R.id.txDetailBlockInfoBlockHashTv)
    TextView mTxDetailBlockInfoBlockHashTv;
    @BindView(R.id.txDetailBlockInfoPrevBlockHashTv)
    TextView mTxDetailBlockInfoPrevBlockHashTv;
    @BindView(R.id.txDetailBlockInfoTXCountTv)
    TextView mTxDetailBlockInfoTXCountTv;
    @BindView(R.id.txDetailBlockInfoConsensusTimeTv)
    TextView mTxDetailBlockInfoConsensusTimeTv;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;
    @BindView(R.id.assetCodeTv)
    TextView mAssetCodeTv;
    @BindView(R.id.txSignatureTitleTv)
    TextView mTxSignatureTitleTv;

    @BindView(R.id.txDetailLl)
    LinearLayout mTxDetailLl;

    @BindView(R.id.txDetailSignatureListLl)
    LinearLayout txDetailSignatureListLl;

    TxDetailSignatureAdapter txDetailSignatureAdapter;

    private String txHash;
    private String outinType;
    private String assetCode;
    private String currentWalletAddress;
    private String optNo;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tx_detail, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initTxDetailView();
            }
        },10);

        return root;
    }

    private void initData(){
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        mEmptyView.show(true);
        txHash = getTxHash();
        outinType = getArguments().getString("outinType");
        assetCode = getArguments().getString("assetCode");
        currentWalletAddress = getArguments().getString("currentAccAddress");
        optNo = getArguments().getString("optNo");
        mAssetCodeTv.setText(assetCode);
    }


    private String getTxHash(){
        return getArguments().getString("txHash");
    }

    private void initTxDetailView() {
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("address",currentWalletAddress);
        paramsMap.put("optNo",optNo);
        Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByOptNo(paramsMap);
        call.enqueue(new Callback<ApiResult<TxDetailRespDto>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {


                ApiResult<TxDetailRespDto> respDto = response.body();

                if(null != respDto.getData()){

                    mTxDetailLl.setVisibility(View.VISIBLE);
                    TxDetailRespDto.TxInfoRespBoBean txInfoRespBoBean = respDto.getData().getTxInfoRespBo();
                    TxDetailRespDto.TxDeatilRespBoBean txDetailRespBoBean = respDto.getData().getTxDetailRespBo();
                    TxDetailRespDto.BlockInfoRespBoBean blockInfoRespBoBean = respDto.getData().getBlockInfoRespBo();

                    Drawable txStatusIconDrawable = null;
                    String txStatusStr = null;
                    if(txDetailRespBoBean.getStatus().equals(TxStatusEnum.SUCCESS.getCode())){
                        if(isAdded()){
                            txStatusIconDrawable = ContextCompat.getDrawable(Objects.requireNonNull(getContext()),R.mipmap.icon_send_success);
                            txStatusStr = getResources().getString(R.string.tx_status_success_txt);
                        }
                    }else{
                        if(isAdded()){
                            txStatusIconDrawable = ContextCompat.getDrawable(Objects.requireNonNull(getContext()),R.mipmap.icon_send_fail);
                            txStatusStr = getResources().getString(R.string.tx_status_fail_txt);
                        }
                    }
                    mTxStatusIcon.setImageDrawable(txStatusIconDrawable);
                    mTxStatusTv.setText(txStatusStr);

                    if (txInfoRespBoBean.getAmount().equals("0")) {
                        mSendAmountTv.setText(txInfoRespBoBean.getAmount());
                    }else{
                        mSendAmountTv.setText((OutinTypeEnum.IN.getCode().equals(outinType) ? mContext.getString(R.string.comm_in) :mContext.getString(R.string.comm_out)) + txInfoRespBoBean.getAmount());
                    }

                    mTxFromAccAddrTv.setText(txDetailRespBoBean.getSourceAddress());
                    mTxToAccAddrTv.setText(txDetailRespBoBean.getDestAddress());
                    mTxDetailFeeTv.setText(txDetailRespBoBean.getFee() + " BU");
                    mTxDetailSendDateTv.setText(TimeUtil.timeStamp2Date(txDetailRespBoBean.getApplyTimeDate()));
                    mTxDetailTXHashTv.setText(txInfoRespBoBean.getHash());
                    mTxDetailNoteTv.setText(txDetailRespBoBean.getTxMetadata());

                    mTxDetailTxInfoSourceAddressTv.setText(txInfoRespBoBean.getSourceAddress());
                    mTxDetailTxInfoDestAddressTv.setText(txInfoRespBoBean.getDestAddress());
                    mTxDetailTxInfoAmountTv.setText(CommonUtil.addSuffix(txInfoRespBoBean.getAmount(),assetCode));
                    mTxDetailTxInfoTXFeeTv.setText(txInfoRespBoBean.getFee() + " BU");
                    mTxDetailTxInfoNonceTv.setText(txInfoRespBoBean.getNonce() + "");

                    String signatureStr = txInfoRespBoBean.getSignatureStr();
                    if(signatureStr != null){
                        JSONArray signatureArr = JSON.parseArray(signatureStr);
                        JSONObject signatureObj = null;
                        List<TxDetailSignatureAdapter.Signature> signatures = new ArrayList<>();
                        TxDetailSignatureAdapter.Signature signature = null;
                        for (int i = 0; i < signatureArr.size(); i++) {
                            signatureObj = JSON.parseObject(signatureArr.getString(i));
                            signature = new TxDetailSignatureAdapter.Signature();
                            signature.setPublicKey(signatureObj.getString("publicKey"));
                            signature.setSignData(signatureObj.getString("signData"));
                            signatures.add(signature);
                        }

                        if(isAdded()){
                            loads(signatures);
                        }
                    }else {
                        mTxSignatureTitleTv.setVisibility(View.GONE);
                        txDetailSignatureListLl.setVisibility(View.GONE);
                    }

                    mTxDetailBlockInfoBlockHeightTv.setText(blockInfoRespBoBean.getSeq() + "");
                    mTxDetailBlockInfoBlockHashTv.setText(blockInfoRespBoBean.getHash());
                    mTxDetailBlockInfoPrevBlockHashTv.setText(blockInfoRespBoBean.getPreviousHash());
                    mTxDetailBlockInfoTXCountTv.setText(blockInfoRespBoBean.getTxCount() + "");
                    mTxDetailBlockInfoConsensusTimeTv.setText(TimeUtil.timeStamp2Date(blockInfoRespBoBean.getCloseTimeDate()));


                }else{

                    if (getActivity()!=null) {
                        ToastUtil.showToast(getActivity(),R.string.emptyView_mode_desc_fail_title, Toast.LENGTH_LONG);
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                t.printStackTrace();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                ToastUtil.showToast(getActivity(),R.string.network_error_msg,Toast.LENGTH_SHORT);
            }
        });
    }

    private void loads(List<TxDetailSignatureAdapter.Signature> signatures){

        for (TxDetailSignatureAdapter.Signature signature: signatures
             ) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(10, 10, 10, 30);
            layout.setLayoutParams(layoutParams);

            LinearLayout pkLayout = new LinearLayout(getContext());
            pkLayout.setOrientation(LinearLayout.VERTICAL);

            pkLayout.setBackgroundColor(0xFFF8F8F8);
            LinearLayout.LayoutParams LP_MM = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            pkLayout.setLayoutParams(LP_MM);


            TextView pkLabelTv = new TextView(getContext());
            pkLabelTv.setText("Public Key");
            pkLabelTv.setTextColor(0xFF888888);
            pkLabelTv.setPadding(20,10,10,20);

            pkLayout.addView(pkLabelTv);

            TextView pkVTv = new TextView(getContext());
            pkVTv.setTextIsSelectable(true);
            pkVTv.setText(signature.getPublicKey());
            pkVTv.setPadding(20,10,20,20);
            pkLayout.addView(pkVTv);



            LinearLayout skLayout = new LinearLayout(getContext());
            skLayout.setOrientation(LinearLayout.VERTICAL);
            skLayout.setBackgroundColor(0xFFF8F8F8);
            skLayout.setLayoutParams(LP_MM);


            TextView sdLabelTv = new TextView(getContext());
            sdLabelTv.setText("Signed Data");
            sdLabelTv.setPadding(20,10,10,20);
            sdLabelTv.setTextColor(0xFF888888);

            skLayout.addView(sdLabelTv);

            TextView sdVTv = new TextView(getContext());
            sdVTv.setTextIsSelectable(true);
            sdVTv.setText(signature.getSignData());
            sdVTv.setPadding(20,10,20,20);
            skLayout.addView(sdVTv);


            layout.addView(pkLayout);
            layout.addView(skLayout);

            txDetailSignatureListLl.addView(layout);
        }




    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.fragment_title_tx_detail_txt));
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }
}
