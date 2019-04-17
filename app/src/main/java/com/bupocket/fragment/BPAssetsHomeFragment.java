package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.adaptor.TokensAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BackupTipsStateEnum;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CurrencyTypeEnum;
import com.bupocket.enums.ExceptionEnum;
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
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import io.bumo.encryption.key.PublicKey;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private QMUITipDialog txSendingTipDialog;
    private TxDetailRespDto.TxDeatilRespBoBean txDetailRespBoBean;

    private Double scanTxFee;
    private String expiryTime;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_home, null);
        ButterKnife.bind(this, root);
        initData();
        initWalletInfoView();
        setListeners();
        backupState();
        return root;
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
                if (isAdded()) {
                    mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }
        });
    }

    private void handleTokens(GetTokensRespDto tokensRespDto) {
        List<GetTokensRespDto.TokenListBean> mTokenList;
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
            mAssetLinearLayout.setBackgroundResource(R.drawable.bg_asset_home_test_net);
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

    private void handleResult(String resultContent) {
        if (null == resultContent) {
            Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
        } else {
            if (!PublicKey.isAddressValid(resultContent)) {
                if (CommonUtil.checkIsBase64(resultContent)) {
                    String jsonStr = null;
                    try {
                        jsonStr = new String(Base64.decode(resultContent.getBytes("UTF-8"), Base64.DEFAULT));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
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
                } else if (resultContent.startsWith(Constants.QR_LOGIN_PREFIX)) {
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
                                } else {
                                    Bundle argz = new Bundle();
                                    argz.putString("errorCode", respDto.getErrCode());
                                    BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                    bpScanErrorFragment.setArguments(argz);
                                    startFragment(bpScanErrorFragment);
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
                                switch (ExceptionEnum.getByValue(respDto.getErrCode())) {
                                    case SUCCESS:
                                        showTransactionConfirmView(respDto.getData());
                                        break;
                                    case ERROR_VOTE_CLOSE:
                                    case ERROR_1003:
                                    case ERROR_ADDRESS_ALREADY_EXISTED:
                                        CommonUtil.showMessageDialog(getContext(), CommonUtil.byCodeToMsg(mContext, respDto.getErrCode()));
                                        break;
                                    default:
                                        Bundle argz = new Bundle();
                                        argz.putString("errorCode", respDto.getErrCode());
                                        argz.putString("errorMessage", respDto.getMsg());
                                        BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                        bpScanErrorFragment.setArguments(argz);
                                        startFragment(bpScanErrorFragment);
                                        break;
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

                } else {
                    Toast.makeText(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT).show();
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

    private void showTransactionConfirmView(final GetQRContentDto contentDto) {
        String destAddress = contentDto.getDestAddress();
        String transactionAmount = contentDto.getAmount();
        String transactionDetail = contentDto.getQrRemark();
        String transactionParams = contentDto.getScript();
        String transactionType = contentDto.getType();
        String accountTag = contentDto.getAccountTag();
        if (ScanTransactionTypeEnum.NODE_VOTE.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_VOTE_FEE;
        } else if (ScanTransactionTypeEnum.NODE_AUDIT.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_AUDIT_FEE;
        } else if (ScanTransactionTypeEnum.APPLY_CO_BUILD.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_CO_BUILD_FEE;
        } else {
            scanTxFee = Constants.MIN_FEE;
        }

        // confirm page
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm, null));
        TextView mTransactionAmountTv = qmuiBottomSheet.findViewById(R.id.transactionAmountTv);
        LinearLayout mTransactionAmountLl = qmuiBottomSheet.findViewById(R.id.transactionAmountLl);
        if ("0".equals(transactionAmount)) {
            mTransactionAmountLl.setVisibility(View.GONE);
        } else {
            mTransactionAmountTv.setText(CommonUtil.thousandSeparator(transactionAmount));
        }
        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
        mTransactionDetailTv.setText(transactionDetail);
        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
        mDestAddressTv.setText(destAddress.concat(accountTag));
        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
        mTxFeeTv.setText(String.valueOf(scanTxFee));

        qmuiBottomSheet.findViewById(R.id.detailBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.GONE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.VISIBLE);
            }
        });

        // confirm details page
        TextView mSourceAddressTv = qmuiBottomSheet.findViewById(R.id.sourceAddressTv);
        mSourceAddressTv.setText(currentWalletAddress);
        TextView mDetailsDestAddressTv = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTv);
        mDetailsDestAddressTv.setText(destAddress);
        TextView mDetailsAmountTv = qmuiBottomSheet.findViewById(R.id.detailsAmountTv);
        mDetailsAmountTv.setText(CommonUtil.addSuffix(CommonUtil.thousandSeparator(transactionAmount), "BU"));
        TextView mDetailsTxFeeTv = qmuiBottomSheet.findViewById(R.id.detailsTxFeeTv);
        mDetailsTxFeeTv.setText(String.valueOf(scanTxFee));
        TextView mTransactionParamsTv = qmuiBottomSheet.findViewById(R.id.transactionParamsTv);
        mTransactionParamsTv.setText(transactionParams);

        // title view listener
        qmuiBottomSheet.findViewById(R.id.goBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.VISIBLE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.GONE);
            }
        });
        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.findViewById(R.id.detailsCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
                confirmTransaction(contentDto);
            }
        });
        qmuiBottomSheet.show();
    }

    private void confirmTransaction(GetQRContentDto contentDto) {
        final String qrCodeSessionID = contentDto.getQrcodeSessionId();
        final String script = contentDto.getScript();
        final String amount = contentDto.getAmount();
        final String contractAddress = contentDto.getDestAddress();
        final String transactionType = contentDto.getType();

        if (TextUtils.isEmpty(tokenBalance) || (Double.valueOf(tokenBalance) < Double.valueOf(amount))) {
            Toast.makeText(getContext(), getString(R.string.send_tx_bu_not_enough), Toast.LENGTH_SHORT).show();
            return;
        }

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
//                        String input = "{\"params\": {\"ratio\": 100, \"unit\": 100000000000, \"shares\": 10010}}";
//                        String payload = "'use strict'; const oneBU= 100000000; const baseReserve    = 10000000; const minInitAmount  = 10000 * oneBU; const statesKey     = 'states'; const configKey     = 'config'; const withdrawKey   = 'withdraw'; const cobuildersKey = 'cobuilders'; const dposContract  = 'buQqzdS9YSnokDjvzg4YaNatcFQfkgXqk6ss'; const valid_period  = 1296000000000; const share     = 'share'; const award     = 'award'; const pledged   = 'pledged'; const validator = 'validator'; const kol       = 'kol'; let cfg  = {}; let states = {}; let cobuilders = {}; function loadObj(key){let data = Chain.load(key); if(data !== false){return JSON.parse(data); } return false; } function saveObj(key, value){let str = JSON.stringify(value); Chain.store(key, str); Utils.log('Set key(' + key + '), value(' + str + ') in metadata succeed.'); } function transferCoin(dest, amount){if(amount === '0'){return true; } Chain.payCoin(dest, amount); Utils.log('Pay coin( ' + amount + ') to dest account(' + dest + ') succeed.'); } function callDPOS(amount, input){Chain.payCoin(dposContract, amount, input); Utils.log('Call DPOS contract(address: ' + dposContract + ', input: ' + input +') succeed.'); } function prepare(){states = loadObj(statesKey); Utils.assert(states !== false, 'Failed to get ' + statesKey + ' from metadata.'); cfg = loadObj(configKey); Utils.assert(cfg !== false, 'Failed to get ' + configKey + ' from metadata.'); cobuilders = loadObj(cobuildersKey); Utils.assert(cobuilders !== false, 'Failed to get ' + cobuildersKey + ' from metadata.'); } function extractInput(){return JSON.stringify({ 'method' : 'extract' }); } function getReward(){let before = Chain.getBalance(Chain.thisAddress); callDPOS('0', extractInput()); let after = Chain.getBalance(Chain.thisAddress); return Utils.int64Sub(after, before); } function distribute(allReward){let dividend = Utils.int64Mul(Utils.int64Div(allReward, 100), cfg.rewardRatio); let unitReward = Utils.int64Div(dividend, states.pledgedShares); Object.keys(cobuilders).forEach(function(key){if(cobuilders[key][pledged]){let each = Utils.int64Mul(unitReward, cobuilders[key][share]); cobuilders[key][award] = Utils.int64Add(cobuilders[key][award], each); } }); let left = Utils.int64Mod(dividend, states.pledgedShares); let reserve = Utils.int64Sub(allReward, dividend); reserve = Utils.int64Add(reserve, left); cobuilders[cfg.initiator][award] = Utils.int64Add(cobuilders[cfg.initiator][award], reserve); } function cobuilder(shares, isPledged){return {share   :shares, pledged :isPledged || false, award    :'0'}; } function subscribe(shares){Utils.assert(shares > 0 && shares % 1 === 0, 'Invalid shares:' + shares + '.'); Utils.assert(Utils.int64Compare(Utils.int64Mul(cfg.unit, shares), Chain.msg.coinAmount) === 0, 'unit * shares !== Chain.msg.coinAmount.'); if(cobuilders[Chain.tx.sender] === undefined){cobuilders[Chain.tx.sender] = cobuilder(shares); } else{assert(cobuilders[Chain.tx.sender][pledged] === false, Chain.tx.sender + ' has already participated in the application.'); cobuilders[Chain.tx.sender][share] = Utils.int64Add(cobuilders[Chain.tx.sender][share], shares); } states.allShares = Utils.int64Add(states.allShares, shares); saveObj(statesKey, states); saveObj(cobuildersKey, cobuilders); Chain.tlog('subscribe', Chain.tx.sender, shares, Chain.msg.coinAmount); } function revoke(){Utils.assert(cobuilders[Chain.tx.sender][pledged] === false, 'The share of '+ Chain.tx.sender + ' has been pledged.'); let stake = cobuilders[Chain.tx.sender]; delete cobuilders[Chain.tx.sender]; saveObj(cobuildersKey, cobuilders); states.allShares = Utils.int64Sub(states.allShares, stake[share]); saveObj(statesKey, states); let amount = Utils.int64Mul(cfg.unit, stake[share]); if(stake[award] !== '0'){amount = Utils.int64Add(amount, stake[award]); } transferCoin(Chain.tx.sender, amount); Chain.tlog('revoke', Chain.tx.sender, stake[share], amount); } function applyInput(pool, ratio, node){let application = {'method' : 'apply', 'params':{'role': states.role, 'pool': pool, 'ratio':ratio } }; if(application.params.role === kol){return JSON.stringify(application); } Utils.assert(Utils.addressCheck(node) && node !== Chain.thisAddress, 'Invalid address:' + node + '.'); application.params.node = node; return JSON.stringify(application); } function setStatus(){states.applied = true; states.pledgedShares = states.allShares; saveObj(statesKey, states); Object.keys(cobuilders).forEach(function(key){ cobuilders[key][pledged] = true; }); saveObj(cobuildersKey, cobuilders); } function apply(role, pool, ratio, node){Utils.assert(role === validator || role === kol,  'Unknown role:' + role + '.'); Utils.assert(Utils.addressCheck(pool), 'Invalid address:' + pool + '.'); Utils.assert(0 <= ratio && ratio <= 100 && ratio % 1 === 0, 'Invalid vote reward ratio:' + ratio + '.'); Utils.assert(states.applied === false, 'Already applied.'); Utils.assert(Chain.tx.sender === cfg.initiator, 'Only the initiator has the right to apply.'); Utils.assert(Utils.int64Compare(states.allShares, cfg.raiseShares) >= 0, 'Co-building fund is not enough.'); states.role = role; setStatus(); let pledgeAmount = Utils.int64Mul(cfg.unit, states.allShares); callDPOS(pledgeAmount, applyInput(pool, ratio, node)); Chain.tlog('apply', pledgeAmount, pool, ratio); } function appendInput(){let addition = {'method' : 'append', 'params':{ 'role': states.role } }; return JSON.stringify(addition); } function append(){Utils.assert(states.applied === true, 'Has not applied.'); Utils.assert(Chain.tx.sender === cfg.initiator, 'Only the initiator has the right to append.'); let appendShares = Utils.int64Sub(states.allShares, states.pledgedShares); let appendAmount = Utils.int64Mul(cfg.unit, appendShares); setStatus(); callDPOS(appendAmount, appendInput()); Chain.tlog('append', appendAmount); } function setNodeAddress(address){Utils.assert(Utils.addressCheck(address),  'Invalid address:' + address + '.'); let input = {'method' : 'setNodeAddress', 'params':{'address': address } }; callDPOS('0', input); Chain.tlog('setNodeAddress', address); } function setVoteDividend(pool, ratio){let input = {'method' : 'setVoteDividend', 'params':{} }; if(pool !== undefined){Utils.assert(Utils.addressCheck(pool), 'Invalid address:' + pool + '.'); input.params.pool = pool; } if(ratio !== undefined){Utils.assert(0 <= ratio && ratio <= 100 && ratio % 1 === 0, 'Invalid vote reward ratio:' + ratio + '.'); input.params.ratio = ratio; } callDPOS('0', input); Chain.tlog('setVoteDividend', pool, ratio); } function transferKey(from, to){return 'transfer_' + from + '_to_' + to; } function transfer(to, shares){Utils.assert(Utils.addressCheck(to), 'Invalid address:' + to + '.'); Utils.assert(shares > 0 && shares % 1 === 0, 'Invalid shares:' + shares + '.'); Utils.assert(cobuilders[Chain.tx.sender][pledged] === true, 'Unpled shares can be withdrawn directly.'); Utils.assert(Utils.int64Compare(shares, cobuilders[Chain.tx.sender][share]) <= 0, 'Transfer shares > holding shares.'); shares = String(shares); let key = transferKey(Chain.tx.sender, to); let transfered = Chain.load(key); if(transfered !== false){shares = Utils.int64Add(transfered ,shares); } Chain.store(key, shares); } function accept(transferor){Utils.assert(Utils.addressCheck(transferor), 'Invalid address:' + transferor + '.'); Utils.assert(cobuilders[transferor][pledged] === true, 'Unpled shares can be revoked directly.'); let key = transferKey(transferor, Chain.tx.sender); let shares = Chain.load(key); Utils.assert(shares !== false, 'Failed to get ' + key + ' from metadata.'); Utils.assert(Utils.int64Compare(Utils.int64Mul(cfg.unit, shares), Chain.msg.coinAmount) === 0, 'unit * shares !== Chain.msg.coinAmount.'); let allReward = getReward(); if(allReward !== '0'){distribute(allReward); } if(cobuilders[Chain.tx.sender] === undefined){cobuilders[Chain.tx.sender] = cobuilder(shares, true); } else{cobuilders[Chain.tx.sender][share] = Utils.int64Add(cobuilders[Chain.tx.sender][share], shares); } let gain = '0'; if(Utils.int64Sub(cobuilders[transferor][share], shares) === 0){gain = cobuilders[transferor][award]; delete cobuilders[transferor]; } else{cobuilders[transferor][share] = Utils.int64Sub(cobuilders[transferor][share], shares); } Chain.del(key); saveObj(cobuildersKey, cobuilders); transferCoin(transferor, Utils.int64Add(Chain.msg.coinAmount, gain)); Chain.tlog('deal', transferor, Chain.tx.sender, shares, Chain.msg.coinAmount); } function withdrawProposal(){let proposal = {'withdrawed' : false, 'expiration' : Chain.block.timestamp + valid_period, 'sum':'0', 'ballot': {} }; return proposal; } function withdrawInput(){let application = {'method' : 'withdraw', 'params':{'role':states.role || kol } }; return JSON.stringify(application); } function withdrawing(proposal){proposal.withdrawed = true; saveObj(withdrawKey, proposal); callDPOS('0', withdrawInput()); } function withdraw(){Utils.assert(states.applied === true, 'Has not applied yet.'); Utils.assert(Chain.tx.sender === cfg.initiator, 'Only the initiator has the right to withdraw.'); let proposal = withdrawProposal(); withdrawing(proposal); Chain.tlog('withdraw', cfg.initiator); } function poll(){Utils.assert(states.applied === true, 'Has not applied yet.'); Utils.assert(cobuilders[Chain.tx.sender][pledged], Chain.tx.sender + ' is not involved in application.'); let proposal = loadObj(withdrawKey); if(proposal === false){proposal = withdrawProposal(); } else{if(proposal.ballot[Chain.tx.sender] !== undefined){return Chain.msg.sender + ' has polled.'; } } proposal.ballot[Chain.tx.sender] = cobuilders[Chain.tx.sender][share]; proposal.sum = Utils.int64Add(proposal.sum, cobuilders[Chain.tx.sender][share]); if(Utils.int64Div(states.pledgedShares, proposal.sum) >= 2){return saveObj(withdrawKey, proposal); } withdrawing(proposal); Chain.tlog('votePassed', proposal.sum); } function resetStatus(){delete states.role; states.applied = false; states.pledgedShares = '0'; saveObj(statesKey, states); Object.keys(cobuilders).forEach(function(key){ cobuilders[key][pledged] = false; }); saveObj(cobuildersKey, cobuilders); } function takeback(){let proposal = loadObj(withdrawKey); assert(proposal !== false, 'Failed to get ' + withdrawKey + ' from metadata.'); assert(proposal.withdrawed && Chain.block.timestamp >= proposal.expiration, 'Insufficient conditions for recovering the deposit.'); resetStatus(); Chain.del(withdrawKey); callDPOS('0', withdrawInput()); } function received(){Utils.assert(Chain.msg.sender === dposContract, 'Chain.msg.sender != dpos contract(' + dposContract + ').'); resetStatus(); if(false !== loadObj(withdrawKey)){Chain.del(withdrawKey); } Chain.tlog('receivedPledge', Chain.msg.coinAmount); } function extract(list){let allReward = getReward(); if(allReward !== '0'){distribute(allReward); } if(list === undefined){let profit = cobuilders[Chain.tx.sender][award]; cobuilders[Chain.tx.sender][award] = '0'; saveObj(cobuildersKey, cobuilders); transferCoin(Chain.tx.sender, profit); return Chain.tlog('extract', Chain.tx.sender, profit); } assert(typeof list === 'object', 'Wrong parameter type.'); assert(list.length <= 100, 'The award-receiving addresses:' + list.length + ' exceed upper limit:100.'); let i = 0; for(i = 0; i < list.length; i += 1){let gain = cobuilders[list[i]][award]; cobuilders[list[i]][award] = '0'; transferCoin(list[i], gain); Chain.tlog('extract', list[i], gain); } saveObj(cobuildersKey, cobuilders); } function getCobuilders(){return loadObj(cobuildersKey); } function getStatus(){return loadObj(statesKey); } function getConfiguration(){return loadObj(configKey); } function getWithdrawInfo(){return loadObj(withdrawKey); } function query(input_str){let input  = JSON.parse(input_str); let params = input.params; let result = {}; if(input.method === 'getCobuilders') {result.cobuilders = getCobuilders(); } else if(input.method === 'getStatus'){result.states = getStatus(); } else if(input.method === 'getConfiguration'){result.cfg = getConfiguration(); } else if(input.method === 'getWithdrawInfo'){result.withdrawInfo = getWithdrawInfo(); } else if(input.method === 'getTransferInfo'){let key = transferKey(params.from, params.to); result.transferShares = Chain.load(key); } return JSON.stringify(result); } function main(input_str){let input  = JSON.parse(input_str); let params = input.params; prepare(); if(input.method === 'subscribe'){subscribe(params.shares); } else if(input.method === 'revoke'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); revoke(); } else if(input.method === 'apply'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); apply(params.role, params.pool, params.ratio, params.node); } else if(input.method === 'append'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); append(); } else if(input.method === 'setNodeAddress'){setNodeAddress(params.address); } else if(input.method === 'setVoteDividend'){setVoteDividend(params.role, params.pool, params.ratio); } else if(input.method === 'transfer'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); transfer(params.to, params.shares); } else if(input.method === 'accept'){accept(params.transferor); } else if(input.method === 'extract'){extract(params !== undefined ? params.list : params); } else if(input.method === 'withdraw'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); withdraw(); } else if(input.method === 'poll'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); poll(); } else if(input.method === 'takeback'){Utils.assert(Chain.msg.coinAmount === '0', 'Chain.msg.coinAmount != 0.'); takeback(); } else if(input.method === 'reward'){distribute(Chain.msg.coinAmount); Chain.tlog('reward', Chain.msg.coinAmount); } else if(input.method === 'refund'){received(); } } function init(input_str){let params = JSON.parse(input_str).params; Utils.assert(0 <= params.ratio && params.ratio <= 100 && params.ratio % 1 === 0, 'Illegal reward ratio:' + params.ratio + '.'); Utils.assert(typeof params.unit === 'number' && params.unit % oneBU === 0, 'Illegal unit:' + params.unit + '.'); Utils.assert(typeof params.shares === 'number'&& params.shares % 1 === 0, 'Illegal raise shares:' + params.shares + '.'); let mul = Utils.int64Mul(params.unit, params.shares); Utils.assert(Utils.int64Compare(Chain.msg.coinAmount, minInitAmount) > 0, 'Initiating funds <= ' + minInitAmount + '.'); let reserve = Utils.int64Mod(Chain.msg.coinAmount, params.unit); Utils.assert(Utils.int64Compare(reserve, baseReserve) >= 0, 'Reserve balance < ' + baseReserve + '.'); cfg = {'initiator'   : Chain.tx.sender, 'rewardRatio' : params.ratio, 'unit'        : params.unit, 'raiseShares' : params.shares }; saveObj(configKey, cfg); let initShare = Utils.int64Div(Chain.msg.coinAmount, cfg.unit); cobuilders[Chain.tx.sender] = cobuilder(initShare); saveObj(cobuildersKey, cobuilders); states = {'applied': false, 'allShares': initShare, 'pledgedShares': '0'}; saveObj(statesKey, states); }";

                        buildBlobResponse = Wallet.getInstance().applyCoBuildBlob(currentWalletAddress, String.valueOf(Double.parseDouble(amount) + Constants.CO_BUILD_FEE), input.toString(), payload, Constants.NODE_CO_BUILD_FEE);
                    } else {
                        buildBlobResponse = Wallet.getInstance().buildBlob(amount, script, currentWalletAddress, String.valueOf(scanTxFee), contractAddress);
                    }
                    String txHash = buildBlobResponse.getResult().getHash();
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
                            ApiResult<TransConfirmModel> respDto = response.body();
                            if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                expiryTime = respDto.getData().getExpiryTime();
                                LogUtils.e("超时时间" + expiryTime);
                                submitTransactionBase(buildBlobResponse);
                            } else {
                                LogUtils.e("超时时间" + respDto.getMsg() + "\t" + respDto.getErrCode());
                                CommonUtil.showMessageDialog(getContext(), respDto.getMsg(),respDto.getErrCode());
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

                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }




    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("address:\t" + getWalletAddress());

    }
}
