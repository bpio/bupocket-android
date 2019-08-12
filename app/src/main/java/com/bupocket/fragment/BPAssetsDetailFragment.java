package com.bupocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.adaptor.MyTokenTxAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.database.greendao.TokenTxInfoDao;
import com.bupocket.enums.OutinTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetMyTxsRespDto;
import com.bupocket.model.TokenTxInfo;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.ToastUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bumo.encryption.key.PublicKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BPAssetsDetailFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.headIconIv)
    QMUIRadiusImageView mAssetIconIv;
    @BindView(R.id.amountTv)
    TextView mAmountTv;
    @BindView(R.id.assetAmountTv)
    TextView mAssetAmountTv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;
    @BindView(R.id.recentlyTxRecordEmptyLL)
    LinearLayout mRecentlyTxRecordEmptyLL;
    @BindView(R.id.myTokenTxLv)
    ListView mMyTokenTxLv;
    @BindView(R.id.walletScanBtn)
    Button mWalletScanBtn;
    @BindView(R.id.walletSendBtn)
    Button mWalletSendBtn;
    @BindView(R.id.myTokenTxTitleTv)
    TextView mMyTokenTxTitleTv;
    @BindView(R.id.reloadBtn)
    QMUIRoundButton copyCommandBtn;
    @BindView(R.id.loadFailedLL)
    LinearLayout llLoadFailed;

    protected SharedPreferencesHelper sharedPreferencesHelper;

    private String assetCode;
    private Map<String, TokenTxInfo> tokenTxInfoMap = new HashMap<>();
    private List<TokenTxInfo> tokenTxInfoList = new ArrayList<>();
    private MyTokenTxAdapter myTokenTxAdapter;
    private GetMyTxsRespDto.PageBean page;
    private String pageSize = "10";
    private Integer pageStart = 1;
    private String currentWalletAddress;
    private String issuer;
    private String decimals;
    private String tokenBalance = "~";
    private String assetAmount = "~";
    private String tokenType;
    private String currencyType;
    private Unbinder bind;
    private Call<ApiResult<GetMyTxsRespDto>> callTxService;
    private TokenTxInfoDao tokenTxInfoDao;

    @Override
    protected View onCreateView() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_detail, null);
        bind = ButterKnife.bind(this, root);
        tokenTxInfoDao = mApplication.getDaoSession().getTokenTxInfoDao();
        initTopBar();
        initTxListView();
        setListener();
        initData();
        return root;
    }

    private void setListener() {
        mWalletScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startFragment(new BPCollectionFragment());
            }
        });

        mWalletSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("tokenType", tokenType);
                argz.putString("tokenCode", assetCode);
                argz.putString("tokenDecimals", decimals);
                argz.putString("tokenIssuer", issuer);
                argz.putString("tokenBalance", tokenBalance);
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        });

        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
    }

    private void initTxListView() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshlayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
//                        initData();
                    }
                }, 500);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (myTokenTxAdapter == null) {
                            refreshlayout.finishRefresh();
                            return;
                        }

                        loadMoreData();
                        refreshlayout.finishLoadMore(500);

                        if (!page.isNextFlag()) {
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    }
                }, 500);
            }
        });
    }

    private void refreshData() {
        pageStart = 1;
        tokenTxInfoMap.clear();
        tokenTxInfoList.clear();
        loadMyTxList();
    }

    private void loadMoreData() {
        if (page.isNextFlag()) {
            pageStart++;
            loadMyTxList();
        }
    }

    private void loadMyTxList() {
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tokenType", tokenType);
        paramsMap.put("assetCode", assetCode);
        paramsMap.put("issuer", issuer);
        paramsMap.put("address", currentWalletAddress);
        paramsMap.put("startPage", pageStart);
        paramsMap.put("pageSize", pageSize);
        paramsMap.put("currencyType", currencyType);
        callTxService = txService.getMyTxs(paramsMap);
        callTxService.enqueue(new Callback<ApiResult<GetMyTxsRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetMyTxsRespDto>> call, Response<ApiResult<GetMyTxsRespDto>> response) {
                ApiResult<GetMyTxsRespDto> respDto = response.body();
                if (respDto != null && isAdded()) {
                    handleMyTxs(respDto.getData());
                    if (respDto.getData().getTxRecord().size() == 0) {
                        mRecentlyTxRecordEmptyLL.setVisibility(View.VISIBLE);
                    } else {
                        mRecentlyTxRecordEmptyLL.setVisibility(View.GONE);
                    }

                } else {
                    mRecentlyTxRecordEmptyLL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetMyTxsRespDto>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                t.printStackTrace();
                if (isAdded()) {
                    if (myTokenTxAdapter!=null&&myTokenTxAdapter.getCount()>0) {
                        ToastUtil.showToast(getActivity(),R.string.network_error_msg,Toast.LENGTH_SHORT);
                        return;
                    }
                    llLoadFailed.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void handleMyTxs(GetMyTxsRespDto getMyTxsRespDto) {

        if (getMyTxsRespDto != null) {
            page = getMyTxsRespDto.getPage();
            tokenBalance = getMyTxsRespDto.getAssetData().getBalance();
            assetAmount = getMyTxsRespDto.getAssetData().getTotalAmount();
            mAmountTv.setText(CommonUtil.rvZeroAndDot(tokenBalance) + " " + assetCode);
            if ("~".equals(assetAmount)) {
                mAssetAmountTv.setText(assetAmount);
            } else {
                mAssetAmountTv.setText(CommonUtil.addCurrencySymbol(assetAmount, currencyType));
            }
            if (getMyTxsRespDto.getTxRecord() == null || getMyTxsRespDto.getTxRecord().size() == 0) {
                showOrHideNoRecord(true);
                return;
            } else {
                showOrHideNoRecord(false);
                mEmptyView.show(null, null);
            }

            refreshLayout.setEnableLoadMore(true);

            for (GetMyTxsRespDto.TxRecordBean obj : getMyTxsRespDto.getTxRecord()) {

                if (obj==null) {
                    return;
                }
                String txAccountAddress = AddressUtil.anonymous((OutinTypeEnum.OUT.getCode().equals(obj.getOutinType())) ? obj.getToAddress() : obj.getFromAddress());
                String amountStr = null;
                String txStartStr = null;
                if (obj.getAmount().equals("0")) {
                    amountStr = obj.getAmount();
                } else {
                    if (OutinTypeEnum.OUT.getCode().equals(obj.getOutinType())) {
                        amountStr =getString(R.string.comm_out) + obj.getAmount();
                    } else {
                        amountStr = getString(R.string.comm_in) + obj.getAmount();
                    }
                }


                if (TxStatusEnum.SUCCESS.getCode().equals(obj.getTxStatus())) {
                    txStartStr = TxStatusEnum.SUCCESS.getName();
                } else {
                    txStartStr = TxStatusEnum.FAIL.getName();
                }
                long optNo = obj.getOptNo();

                if (!tokenTxInfoMap.containsKey(String.valueOf(obj.getOptNo()))) {
                    TokenTxInfo tokenTxInfo = new TokenTxInfo(txAccountAddress, TimeUtil.getDateDiff(obj.getTxTime(), getContext()), amountStr, txStartStr, String.valueOf(optNo));
                    tokenTxInfo.setTxHash(obj.getTxHash());
                    tokenTxInfo.setOutinType(obj.getOutinType());
                    tokenTxInfoMap.put(String.valueOf(obj.getOptNo()), tokenTxInfo);
                    tokenTxInfoList.add(tokenTxInfo);
                }
            }

        } else {
            mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

        if (pageStart==1) {
            myTokenTxAdapter = new MyTokenTxAdapter(tokenTxInfoList, getContext());
            myTokenTxAdapter.setPage(getMyTxsRespDto.getPage());
            mMyTokenTxLv.setAdapter(myTokenTxAdapter);
            tokenTxInfoDao.deleteAll();
            tokenTxInfoDao.insertInTx(tokenTxInfoList);

        } else {
            myTokenTxAdapter.loadMore(getMyTxsRespDto.getTxRecord(), tokenTxInfoMap);
        }

    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress)) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        currencyType = sharedPreferencesHelper.getSharedPreference("currencyType", "CNY").toString();

        Bundle bundle = getArguments();
        assetCode = bundle.getString("assetCode");
        issuer = bundle.getString("issuer");
        decimals = bundle.getString("decimals");
        tokenBalance = bundle.getString("amount");
        tokenType = bundle.getString("tokenType");
        String assetAmount = bundle.getString("assetAmount");
        mAmountTv.setText(CommonUtil.rvZeroAndDot(tokenBalance) + " " + assetCode);
        if ("~".equals(tokenBalance)) {
            mAssetAmountTv.setText(assetAmount);
        } else {
            mAssetAmountTv.setText(CommonUtil.addCurrencySymbol(assetAmount, currencyType));
        }
        if (!CommonUtil.isNull(bundle.getString("icon"))) {
            try {
                mAssetIconIv.setImageBitmap(CommonUtil.base64ToBitmap(bundle.getString("icon")));
            } catch (IllegalArgumentException e) {
                mAssetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
            }

        } else {
            mAssetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
        }


        showOrHideNoRecord(false);
        mAssetAmountTv.postDelayed(new Runnable() {
            @Override
            public void run() {

                List<TokenTxInfo> tokenTxInfos = tokenTxInfoDao.loadAll();
                if (tokenTxInfos!=null&&tokenTxInfos.size()>0) {
                    myTokenTxAdapter = new MyTokenTxAdapter(tokenTxInfos, getContext());
                    mMyTokenTxLv.setAdapter(myTokenTxAdapter);

                }else{
                    mEmptyView.show(true);
                }
                LogUtils.e("tokenTxInfos:    "+tokenTxInfos.size());
                refreshData();


                mMyTokenTxLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (myTokenTxAdapter==null) {
                            return;
                        }

                        TokenTxInfo currentItem = (TokenTxInfo) myTokenTxAdapter.getItem(position);
                        Bundle argz = new Bundle();
                        argz.putString("txHash", currentItem.getTxHash());
                        argz.putString("outinType", currentItem.getOutinType());
                        argz.putString("assetCode", assetCode);
                        argz.putString("currentAccAddress", currentWalletAddress);
                        argz.putString("optNo", currentItem.getOptNo());
                        BPAssetsTxDetailFragment bpAssetsTxDetailFragment = new BPAssetsTxDetailFragment();
                        bpAssetsTxDetailFragment.setArguments(argz);
                        startFragment(bpAssetsTxDetailFragment);
                    }
                });
            }
        },10);

    }

    private void showOrHideNoRecord(boolean showFlag) {
        if (showFlag) {
            mRecentlyTxRecordEmptyLL.setVisibility(View.VISIBLE);
            mMyTokenTxLv.setVisibility(View.GONE);
            mMyTokenTxTitleTv.setVisibility(View.GONE);
        } else {
            mRecentlyTxRecordEmptyLL.setVisibility(View.GONE);
            mMyTokenTxLv.setVisibility(View.VISIBLE);
            mMyTokenTxTitleTv.setVisibility(View.VISIBLE);
        }
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(assetCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
            } else if (!PublicKey.isAddressValid(result.getContents())) {

                DialogUtils.showMessageNoTitleDialog(mContext,R.string.error_qr_message_txt_2);

            } else {
                Bundle argz = new Bundle();
                argz.putString("destAddress", result.getContents());
                argz.putString("tokenCode", assetCode);
                argz.putString("tokenDecimals", decimals);
                argz.putString("tokenIssuer", issuer);
                argz.putString("tokenBalance", tokenBalance);
                argz.putString("tokenType", tokenType);
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


}
