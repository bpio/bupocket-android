package com.bupocket.fragment;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.bupocket.BPMainActivity;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.adaptor.TokensAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.SingatureListener;
import com.bupocket.enums.BackupTipsStateEnum;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CurrencyTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.ExceptionLoginEnum;
import com.bupocket.enums.MnemonicWordBackupStateEnum;
import com.bupocket.enums.ScanTransactionTypeEnum;
import com.bupocket.enums.TokenActionTypeEnum;
import com.bupocket.fragment.components.AssetsListView;
import com.bupocket.http.api.NodePlanManagementSystemService;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetQRContentDto;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.http.api.dto.resp.UserScanQrLoginDto;
import com.bupocket.model.TransConfirmModel;
import com.bupocket.model.UDCBUModel;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.utils.WalletUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import io.bumo.encryption.key.PublicKey;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BPAssetsHomeFragment extends BaseFragment {

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.assetsHomeEmptyView)
    QMUIEmptyView mAssetsHomeEmptyView;
    @BindView(R.id.tokenListLv)
    AssetsListView mTokenListLv;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.totalAssetsValueTv)
    TextView mTotalAssetsValueTv;
    @BindView(R.id.assetsSv)
    ScrollView assetsSv;
    @BindView(R.id.currencyTypeTv)
    TextView mCurrencyTypeTv;
    @BindView(R.id.currentTestNetTipsTv)
    TextView mCurrentTestNetTipsTv;
    @BindView(R.id.assetLinearLayout)
    LinearLayout mAssetLinearLayout;
    @BindView(R.id.homeScanLl)
    LinearLayout mHomeScanLl;
    @BindView(R.id.receiveLl)
    LinearLayout mReceiveLl;
    @BindView(R.id.addTokenLl)
    LinearLayout mAddTokenLl;
    @BindView(R.id.immediatelyBackupBtn)
    QMUIRoundButton mImmediatelyBackupBtn;
    @BindView(R.id.notBackupBtn)
    QMUIRoundButton mNotBackupBtn;
    @BindView(R.id.safetyTipsLl)
    LinearLayout mSafetyTipsLl;
    @BindView(R.id.manageWalletBtn)
    ImageView mManageWalletBtn;
    @BindView(R.id.currentWalletNameTv)
    TextView mCurrentWalletNameTv;
    @BindView(R.id.ivAssetsInfo)
    ImageView ivAssetsInfo;

    protected SharedPreferencesHelper sharedPreferencesHelper;
    private TokensAdapter mTokensAdapter;
    private String currentWalletAddress;
    private String currentWalletName;
    private String currentAccNick;
    private String currentIdentityWalletAddress;
    private MaterialHeader mMaterialHeader;
    List<GetTokensRespDto.TokenListBean> mLocalTokenList = new ArrayList<>();

    private String tokenBalance;
    private String currencyType;
    private Integer bumoNodeType;
    private String localTokenListSharedPreferenceKey;

    private String txHash;
    private Boolean whetherIdentityWallet = false;

    private TxDetailRespDto.TxDeatilRespBoBean txDetailRespBoBean;

    private Double scanTxFee;
    private String expiryTime;
    private View faildlayout;
    List<GetTokensRespDto.TokenListBean> mTokenList;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_home, null);
        ButterKnife.bind(this, root);
        initView();
        initData();
        initWalletInfoView();
        setListeners();
        backupState();
        initPermission();
        return root;
    }

    private void initPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        ArrayList<String> needRequestPermissions = ((BPMainActivity) getActivity()).getNeedRequestPermissions(permissions);
        if (needRequestPermissions.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(), needRequestPermissions.toArray(new String[permissions.size()]), BPMainActivity.BASE_REQUEST_CODE);

        }

    }

    private void initView() {
        faildlayout = LayoutInflater.from(mContext).inflate(R.layout.view_load_failed, null);
        faildlayout.findViewById(R.id.copyCommandBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefreshAnimationOnly();
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshLayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                    }
                }, 400);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initBackground();
    }

    private void setListeners() {
        mHomeScanLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        mReceiveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountAddressView();
            }
        });
        mAddTokenLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAssetsAddFragment());
            }
        });
        mImmediatelyBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName", currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });
        mNotBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSafetyTipsLl.setVisibility(View.GONE);
                sharedPreferencesHelper.put("backupTipsState", BackupTipsStateEnum.HIDE.getCode());
            }
        });
        mManageWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPWalletsHomeFragment());
            }
        });

        ivAssetsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletUtils.showWalletPopup(mContext,getString(R.string.wallet_bu_info),v);
            }
        });
    }

    private void backupState() {
        String backupState = sharedPreferencesHelper.getSharedPreference("mnemonicWordBackupState", "").toString();
        String backupTipsState = sharedPreferencesHelper.getSharedPreference("backupTipsState", "").toString();
        if (MnemonicWordBackupStateEnum.ALREADYBACKUP.getCode().equals(backupState) || BackupTipsStateEnum.HIDE.getCode().equals(backupTipsState)) {
            mSafetyTipsLl.setVisibility(View.GONE);
        }
    }

    private void showAccountAddressView() {
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_show_address, null));
        TextView accountAddressTv = qmuiBottomSheet.findViewById(R.id.printAccAddressTv);
        accountAddressTv.setText(currentWalletAddress);

        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(currentWalletAddress, 356, 356);
        ImageView mImageView = qmuiBottomSheet.findViewById(R.id.qr_pocket_address_image);
        mImageView.setImageBitmap(mBitmap);

        qmuiBottomSheet.findViewById(R.id.addressCopyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", currentWalletAddress);
                cm.setPrimaryClip(mClipData);
                final QMUITipDialog copySuccessDiglog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                        .setTipWord(copySuccessMessage)
                        .create();
                copySuccessDiglog.show();
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        copySuccessDiglog.dismiss();
                    }
                }, 1500);
            }
        });
        qmuiBottomSheet.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.show();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mTotalAssetsValueTv.setText("≈" + msg.getData().get("assetValuation").toString());
        }

        ;
    };

    private void initWalletInfoView() {
        String shortCurrentAccAddress = AddressUtil.anonymous(currentWalletAddress);
    }

    private void initTokensView() {
        mMaterialHeader = (MaterialHeader) refreshLayout.getRefreshHeader();


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshlayout.finishRefresh();
                        refreshlayout.setNoMoreData(false);
                    }
                }, 400);

            }
        });
        mCurrencyTypeTv.setText(getCurrencyTypeSymbol(currencyType));
    }

    private String getCurrencyTypeSymbol(String currencyType) {
        for (CurrencyTypeEnum currencyTypeEnum : CurrencyTypeEnum.values()) {
            if (currencyTypeEnum.getName().equals(currencyType)) {
                return currencyTypeEnum.getSymbol();
            }
        }
        return null;
    }

    private void loadAssetList() {

        tokenBalance = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "tokenBalance", "0").toString();
        Runnable getBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                tokenBalance = Wallet.getInstance().getAccountBUBalance(currentWalletAddress);
                if (!CommonUtil.isNull(tokenBalance)) {
                    sharedPreferencesHelper.put(currentWalletAddress + "tokenBalance", tokenBalance);
                }
            }
        };
        new Thread(getBalanceRunnable).start();


        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        if (BumoNodeEnum.TEST.getCode() == bumoNodeType) {
            localTokenListSharedPreferenceKey = BumoNodeEnum.TEST.getLocalTokenListSharedPreferenceKey();
        } else if (BumoNodeEnum.MAIN.getCode() == bumoNodeType) {
            localTokenListSharedPreferenceKey = BumoNodeEnum.MAIN.getLocalTokenListSharedPreferenceKey();
        }
        GetTokensRespDto getTokensRespDto = JSON.parseObject(sharedPreferencesHelper.getSharedPreference(localTokenListSharedPreferenceKey, "").toString(), GetTokensRespDto.class);
        if (getTokensRespDto != null) {
            mLocalTokenList = getTokensRespDto.getTokenList();
        }
        String currencyType = sharedPreferencesHelper.getSharedPreference("currencyType", "CNY").toString();
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("address", currentWalletAddress);
        parmasMap.put("currencyType", currencyType);
        parmasMap.put("tokenList", mLocalTokenList);
        Call<ApiResult<GetTokensRespDto>> call = tokenService.getTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokensRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokensRespDto>> call, Response<ApiResult<GetTokensRespDto>> response) {
                mAssetsHomeEmptyView.show(null, null);
                ApiResult<GetTokensRespDto> respDtoApiResult = response.body();

                if (respDtoApiResult != null) {
                    sharedPreferencesHelper.put(currentWalletAddress + "tokensInfoCache", JSON.toJSONString(respDtoApiResult.getData()));
                    if (isAdded()) {
                        handleTokens(respDtoApiResult.getData());
                    }
                } else {
                    mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetTokensRespDto>> call, Throwable t) {
                t.printStackTrace();
                if (isAdded() && mTokenList.size() == 0) {
                    mAssetsHomeEmptyView.removeAllViews();
                    mAssetsHomeEmptyView.addView(faildlayout);
                }
            }
        });
    }

    private void handleTokens(GetTokensRespDto tokensRespDto) {
        if (tokensRespDto != null) {
            if (tokensRespDto.getTokenList() == null || tokensRespDto.getTokenList().size() == 0) {
                mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                return;
            } else {
                mAssetsHomeEmptyView.show("", "");
            }

            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putString("assetValuation", tokensRespDto.getTotalAmount());
            msg.setData(data);
            handler.sendMessage(msg);
            assetsSv.smoothScrollTo(0, 0);
            mTokenList = tokensRespDto.getTokenList();

        } else {
            mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

        mTokensAdapter = new TokensAdapter(mTokenList, getContext());
        mTokenListLv.setAdapter(mTokensAdapter);
        mTokenListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle argz = new Bundle();
                GetTokensRespDto.TokenListBean tokenInfo = (GetTokensRespDto.TokenListBean) mTokensAdapter.getItem(position);
                argz.putString("assetCode", tokenInfo.getAssetCode());
                argz.putString("icon", tokenInfo.getIcon());
                argz.putString("amount", tokenInfo.getAmount());
                argz.putString("price", tokenInfo.getPrice());
                argz.putString("issuer", tokenInfo.getIssuer());
                argz.putString("decimals", tokenInfo.getDecimals() + "");
                argz.putString("tokenType", tokenInfo.getType() + "");
                argz.putString("assetAmount", tokenInfo.getAssetAmount());
                BPAssetsDetailFragment bpAssetsDetailFragment = new BPAssetsDetailFragment();
                bpAssetsDetailFragment.setArguments(argz);
                startFragment(bpAssetsDetailFragment);
            }
        });

    }

    private void initData() {
        mTokenList = new ArrayList<>();
        QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
//        QMUIStatusBarHelper.translucent(getActivity());
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString())) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
            currentWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", "Wallet-1").toString();
            whetherIdentityWallet = true;
        } else {
            currentWalletName = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "-walletName", "").toString();
        }
        currencyType = sharedPreferencesHelper.getSharedPreference("currencyType", "CNY").toString();
        bumoNodeType = sharedPreferencesHelper.getInt("bumoNode", Constants.DEFAULT_BUMO_NODE);
        GetTokensRespDto tokensCache = JSON.parseObject(sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "tokensInfoCache", "").toString(), GetTokensRespDto.class);
        if (tokensCache != null) {
            handleTokens(tokensCache);
        }
//        initBackground();
        initTokensView();
        refreshLayout.autoRefresh();
    }

    private void initBackground() {
        if (SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()) {
            mCurrentTestNetTipsTv.setText(getString(R.string.current_test_message_txt));
            mAssetLinearLayout.setBackgroundResource(R.mipmap.ic_asset_home_bg_test_net);
        }
        mCurrentWalletNameTv.setText(currentWalletName);
    }

    private void refreshData() {
        loadAssetList();
    }

    private void startScan() {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getResources().getString(R.string.wallet_scan_notice));
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            if (Constants.REQUEST_IMAGE == resultCode) {
                if (null != data) {
                    String resultFromBitmap = data.getStringExtra("resultFromBitmap");
                    handleResult(resultFromBitmap);
                }
            } else {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                String resultContent = result.getContents();
                handleResult(resultContent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showTransactionConfirmView(final GetQRContentDto contentDto) {
        String destAddress = contentDto.getDestAddress();
        String transactionAmount = contentDto.getAmount();
        String transactionDetail = contentDto.getQrRemark();
        String transactionParams = contentDto.getScript();
        String transactionType = contentDto.getType();
        String accountTag = contentDto.getAccountTag();


        switch (LocaleUtil.getLanguageStatus()) {
            case 1:
                accountTag = contentDto.getAccountTagEn();
                transactionDetail = contentDto.getQrRemarkEn();
                break;
        }
        if (ScanTransactionTypeEnum.ADD_MARGIN.getCode().equals(transactionType)) {
            scanTxFee = Constants.ADD_MARGIN;
        } else if (ScanTransactionTypeEnum.NODE_VOTE.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_VOTE_FEE;
        } else if (ScanTransactionTypeEnum.CO_BUILD_APPLY.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_AUDIT_FEE;
        } else if (ScanTransactionTypeEnum.APPLY_CO_BUILD.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_CO_BUILD_FEE;
        } else if (ScanTransactionTypeEnum.CO_BUILD_SUPPORT.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_CO_BUILD_SUPPORT;
        } else if (ScanTransactionTypeEnum.CO_BUILD_APPLY.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.CO_BUILD_APPLY_KOL.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.COMMITTEE_EXIT.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_CO_BUILD_SUPPORT;
        } else if (ScanTransactionTypeEnum.COMMITTEE_APPLY.getCode().equals(transactionType)) {
            scanTxFee = Constants.COMMITTEE_APPLY;
        } else if (ScanTransactionTypeEnum.COMMITTEE_EXIT.getCode().equals(transactionType)) {
            scanTxFee = Constants.COMMITTEE_APPLY;
        } else if (ScanTransactionTypeEnum.NODE_EXIT.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_EXIT;
        } else {
            scanTxFee = Constants.MIN_FEE;
        }

        TransferUtils.confirmTxSheet(mContext, getWalletAddress(), destAddress,
                accountTag, transactionAmount, scanTxFee,
                transactionDetail, transactionParams, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {
                        confirmTransaction(contentDto);
                    }
                });
    }

    private void confirmTransaction(final GetQRContentDto contentDto) {


        final String qrCodeSessionID = contentDto.getQrcodeSessionId();
        final String script = contentDto.getScript();
        final String amount = contentDto.getAmount();
        final String contractAddress = contentDto.getDestAddress();
        final String transactionType = contentDto.getType();
        final String transMetaData = contentDto.getQrRemarkEn();



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final TransactionBuildBlobResponse buildBlobResponse;
                    if (ScanTransactionTypeEnum.APPLY_CO_BUILD.getCode().equals(transactionType)) {
                        // handle script

                        org.json.JSONObject jsonObj = new org.json.JSONObject(script);
                        String input = jsonObj.getString("input");
                        String payload = jsonObj.getString("payload");
                        buildBlobResponse = Wallet.getInstance().applyCoBuildBlob(currentWalletAddress, String.valueOf(Double.parseDouble(amount) + Constants.NODE_CO_BUILD_AMOUNT_FEE), input.toString(), payload, Constants.NODE_CO_BUILD_FEE, transMetaData);
                    } else {
                        buildBlobResponse = Wallet.getInstance().buildBlob(amount, script, currentWalletAddress, String.valueOf(scanTxFee), contractAddress, transMetaData);
                    }
                    final String txHash = buildBlobResponse.getResult().getHash();


                    if (TextUtils.isEmpty(tokenBalance) || (Double.valueOf(tokenBalance) < Double.valueOf(amount))) {
                       ToastUtil.showToast(getActivity(), getString(R.string.send_tx_bu_not_enough), Toast.LENGTH_SHORT);
                        return;
                    }

                    getSignatureInfo(new SingatureListener() {
                        @Override
                        public void success(final String privateKey) {


                            final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                    .setTipWord(getResources().getString(R.string.send_tx_verify))
                                    .create();
                            txSendingTipDialog.show();

                            NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                            Call<ApiResult<TransConfirmModel>> call;
                            Map<String, Object> paramsMap = new HashMap<>();
                            paramsMap.put("qrcodeSessionId", qrCodeSessionID);
                            paramsMap.put("hash", txHash);
                            paramsMap.put("initiatorAddress", currentWalletAddress);
                            call = nodePlanService.confirmTransaction(paramsMap);
                            call.enqueue(new Callback<ApiResult<TransConfirmModel>>() {
                                @Override
                                public void onResponse(Call<ApiResult<TransConfirmModel>> call, Response<ApiResult<TransConfirmModel>> response) {
                                    txSendingTipDialog.dismiss();
                                    ApiResult<TransConfirmModel> respDto = response.body();
                                    if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                        expiryTime = respDto.getData().getExpiryTime();
                                        submitTransactionBase(privateKey, buildBlobResponse);
                                    } else {
                                        if (ExceptionEnum.ERROR_TRANSACTION_OTHER_1011.getCode().equals(respDto.getErrCode())) {
                                            expiryTime = respDto.getData().getExpiryTime();
                                            CommonUtil.setExpiryTime(expiryTime, mContext);

                                        } else {
                                            CommonUtil.showMessageDialog(getContext(), respDto.getMsg(), respDto.getErrCode());
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResult<TransConfirmModel>> call, Throwable t) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    txSendingTipDialog.dismiss();
                                }
                            });

                        }
                    });


                } catch (WalletException e) {
                    if (e.getErrCode().equals(com.bupocket.wallet.enums.ExceptionEnum.ADDRESS_NOT_EXIST.getCode())) {
                        ToastUtil.showToast(getActivity(), getString(R.string.address_not_exist), Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {


                }
            }
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("address:\t" + getWalletAddress());

    }


    private void handleResult(String resultContent) {
        if (null == resultContent) {
            Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
        } else {
            if (!PublicKey.isAddressValid(resultContent)) {
                LogUtils.e("resultContent:\t" + resultContent);
                if (CommonUtil.checkIsBase64(resultContent)) {
                    String jsonStr = null;
                    try {
                        jsonStr = new String(Base64.decode(resultContent.getBytes("UTF-8"), Base64.DEFAULT));
                        Object object = JSON.parseObject(jsonStr);
                        String action = ((JSONObject) object).getString("action");
                        String uuID = ((JSONObject) object).getString("uuID");
                        String tokenData = ((JSONObject) object).getString("data");
                        Bundle argz = new Bundle();
                        argz.putString("uuID", uuID);
                        argz.putString("tokenData", tokenData);
                        argz.putString("buBalance", tokenBalance);
                        if (action.equals(TokenActionTypeEnum.ISSUE.getCode())) {
                            BPIssueTokenFragment bpIssueTokenFragment = new BPIssueTokenFragment();
                            bpIssueTokenFragment.setArguments(argz);
                            startFragment(bpIssueTokenFragment);
                        } else if (action.equals(TokenActionTypeEnum.REGISTER.getCode())) {
                            BPRegisterTokenFragment bpRegisterTokenFragment = new BPRegisterTokenFragment();
                            bpRegisterTokenFragment.setArguments(argz);
                            startFragment(bpRegisterTokenFragment);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showToast(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT);
                    }

                } else {

                    try {
                        java.net.URL url = new java.net.URL(resultContent);
                        String path = url.getPath();
                        resultContent = path;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    if (resultContent.startsWith(Constants.QR_LOGIN_PREFIX)) {
                        final String uuid = resultContent.replace(Constants.QR_LOGIN_PREFIX, "");
                        NodePlanManagementSystemService nodePlanManagementSystemService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanManagementSystemService.class);
                        Call<ApiResult<UserScanQrLoginDto>> call;
                        Map<String, Object> paramsMap = new HashMap<>();
                        paramsMap.put("uuid", uuid);
                        paramsMap.put("address", currentWalletAddress);
                        call = nodePlanManagementSystemService.userScanQrLogin(paramsMap);
                        call.enqueue(new Callback<ApiResult<UserScanQrLoginDto>>() {
                            @Override
                            public void onResponse(Call<ApiResult<UserScanQrLoginDto>> call, Response<ApiResult<UserScanQrLoginDto>> response) {
                                ApiResult<UserScanQrLoginDto> respDto = response.body();
                                if (null != respDto) {
                                    if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                        UserScanQrLoginDto userScanQrLoginDto = respDto.getData();
                                        String appId = userScanQrLoginDto.getAppId();
                                        String appName = userScanQrLoginDto.getAppName();
                                        String appPic = userScanQrLoginDto.getAppPic();
                                        Bundle argz = new Bundle();
                                        argz.putString("appId", appId);
                                        argz.putString("uuid", uuid);
                                        argz.putString("address", currentWalletAddress);
                                        argz.putString("appName", appName);
                                        argz.putString("appPic", appPic);
                                        BPNodePlanManagementSystemLoginFragment bpNodePlanManagementSystemLoginFragment = new BPNodePlanManagementSystemLoginFragment();
                                        bpNodePlanManagementSystemLoginFragment.setArguments(argz);
                                        startFragment(bpNodePlanManagementSystemLoginFragment);
                                    } else if (ExceptionLoginEnum.ERROR_TIMEOUT.getCode().equals(respDto.getErrCode())) {
                                        Bundle argz = new Bundle();
                                        argz.putString("errorCode", respDto.getErrCode());
                                        argz.putString("errorMessage", mContext.getString(ExceptionLoginEnum.ERROR_TIMEOUT.getMsg()));
                                        BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                        bpScanErrorFragment.setArguments(argz);
                                        startFragment(bpScanErrorFragment);
                                    } else if (ExceptionLoginEnum.ERROR_VOTE_CLOSE.getCode().equals(respDto.getErrCode())) {
                                        Bundle argz = new Bundle();
                                        argz.putString("errorCode", respDto.getErrCode());
                                        argz.putString("errorMessage", mContext.getString(ExceptionLoginEnum.ERROR_VOTE_CLOSE.getMsg()));
                                        BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                        bpScanErrorFragment.setArguments(argz);
                                        startFragment(bpScanErrorFragment);
                                    } else {
                                        ToastUtil.showToast(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResult<UserScanQrLoginDto>> call, Throwable t) {
                                Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (resultContent.startsWith(Constants.QR_NODE_PLAN_PREFIX)) {
                        String qrCodeSessionId = resultContent.replace(Constants.QR_NODE_PLAN_PREFIX, "");
                        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                        Call<ApiResult<GetQRContentDto>> call;
                        Map<String, Object> paramsMap = new HashMap<>();
                        paramsMap.put("qrcodeSessionId", qrCodeSessionId);
                        paramsMap.put("initiatorAddress", currentWalletAddress);
                        call = nodePlanService.getQRContent(paramsMap);
                        call.enqueue(new Callback<ApiResult<GetQRContentDto>>() {
                            @Override
                            public void onResponse(Call<ApiResult<GetQRContentDto>> call, Response<ApiResult<GetQRContentDto>> response) {
                                ApiResult<GetQRContentDto> respDto = response.body();
                                if (null != respDto) {

                                    if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                        showTransactionConfirmView(respDto.getData());
                                    } else if (ExceptionEnum.ERROR_TIMEOUT.getCode().equals(respDto.getErrCode())) {
                                        Bundle argz = new Bundle();
                                        argz.putString("errorCode", respDto.getErrCode());
                                        argz.putString("errorMessage", respDto.getMsg());
                                        BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                        bpScanErrorFragment.setArguments(argz);
                                        startFragment(bpScanErrorFragment);
                                    } else {
                                        String msg = CommonUtil.byCodeToMsg(mContext, respDto.getErrCode());
                                        if (!msg.isEmpty()) {
                                            CommonUtil.showMessageDialog(getContext(), msg);
                                        } else {
                                            Bundle argz = new Bundle();
                                            argz.putString("errorCode", respDto.getErrCode());
                                            argz.putString("errorMessage", respDto.getMsg());
                                            BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                            bpScanErrorFragment.setArguments(argz);
                                            startFragment(bpScanErrorFragment);
                                        }
                                    }

                                } else {
                                    Toast.makeText(getContext(), "response is null", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResult<GetQRContentDto>> call, Throwable t) {
                                System.out.print(t.getMessage());
                                t.printStackTrace();
                                Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (resultContent.contains(Constants.INFO_UDCBU)) {
                        final String udcbuContent = resultContent.replace(Constants.INFO_UDCBU, "");
                        UDCBUModel udcbuModel = null;
                        try {
                            udcbuModel = new Gson().fromJson(udcbuContent.trim(), UDCBUModel.class);
                            if (udcbuModel == null) {
                                return;
                            }

                            if (TextUtils.isEmpty(udcbuModel.getDest_address())) {
                                ToastUtil.showToast(getActivity(), R.string.transfer_address_empty, Toast.LENGTH_SHORT);
                                return;
                            }

                            if (TextUtils.isEmpty(udcbuModel.getAmount())) {
                                ToastUtil.showToast(getActivity(), R.string.transfer_bu_empty, Toast.LENGTH_SHORT);
                                return;
                            }

                            if (TextUtils.isEmpty(udcbuModel.getInput())) {
                                ToastUtil.showToast(getActivity(), R.string.transfer_parameter_empty, Toast.LENGTH_SHORT);
                                return;
                            }

                            if (TextUtils.isEmpty(udcbuModel.getTx_fee())) {
                                ToastUtil.showToast(getActivity(), R.string.transfer_fee_empty, Toast.LENGTH_SHORT);
                                return;
                            }

                            final UDCBUModel finalUdcbuModel = udcbuModel;
                            TransferUtils.confirmTxSheet(mContext, getWalletAddress(), udcbuModel.getDest_address()
                                    , udcbuModel.getAmount(), Double.parseDouble(udcbuModel.getTx_fee()),
                                    getString(R.string.transaction_metadata), udcbuModel.getInput(), new TransferUtils.TransferListener() {
                                        @Override
                                        public void confirm() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        final TransactionBuildBlobResponse buildBlobResponse = Wallet.getInstance().buildBlob(finalUdcbuModel.getAmount(), finalUdcbuModel.getInput(), currentWalletAddress, finalUdcbuModel.getTx_fee(), finalUdcbuModel.getDest_address(), getString(R.string.transaction_metadata));

                                                        if (TextUtils.isEmpty(tokenBalance) || (Double.valueOf(tokenBalance) < Double.valueOf(finalUdcbuModel.getAmount()))) {
                                                            ToastUtil.showToast(getActivity(), getString(R.string.send_tx_bu_not_enough), Toast.LENGTH_SHORT);
                                                            return;
                                                        }
                                                        getSignatureInfo(new SingatureListener() {
                                                            @Override
                                                            public void success(String privateKey) {

                                                                submitTransactionBase(privateKey, buildBlobResponse);
                                                            }
                                                        });

                                                    }catch (WalletException e) {
                                                        if (e.getErrCode().equals(com.bupocket.wallet.enums.ExceptionEnum.ADDRESS_NOT_EXIST.getCode())) {
                                                            ToastUtil.showToast(getActivity(), getString(R.string.address_not_exist), Toast.LENGTH_SHORT);
                                                        }
                                                    }catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();
                                        }
                                    });

                        } catch (Exception e) {
                            ToastUtil.showToast(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT);
                        }


                    } else {
                        Toast.makeText(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Bundle argz = new Bundle();
                argz.putString("destAddress", resultContent);
                argz.putString("tokenCode", "BU");
                argz.putString("tokenDecimals", "8");
                argz.putString("tokenIssuer", "");
                argz.putString("tokenBalance", tokenBalance);
                argz.putString("tokenType", "0");
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        }
    }
}
