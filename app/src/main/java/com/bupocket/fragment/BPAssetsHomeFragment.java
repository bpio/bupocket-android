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
import android.util.Base64;
import android.view.KeyEvent;
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
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.components.AssetsListView;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.NodePlanManagementSystemService;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.TxService;
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
import com.bupocket.utils.TimeUtil;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
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
import java.util.Timer;
import java.util.TimerTask;

import static com.bupocket.utils.TimeUtil.getCurrentTimeMillis;

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
                argz.putString("accName",currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });
        mNotBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSafetyTipsLl.setVisibility(View.GONE);
                sharedPreferencesHelper.put("backupTipsState",BackupTipsStateEnum.HIDE.getCode());
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
        String backupState = sharedPreferencesHelper.getSharedPreference("mnemonicWordBackupState","").toString();
        String backupTipsState = sharedPreferencesHelper.getSharedPreference("backupTipsState","").toString();
        if(MnemonicWordBackupStateEnum.ALREADYBACKUP.getCode().equals(backupState) || BackupTipsStateEnum.HIDE.getCode().equals(backupTipsState)){
            mSafetyTipsLl.setVisibility(View.GONE);
        }
    }

    private void showAccountAddressView() {
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_show_address,null));
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

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            mTotalAssetsValueTv.setText("≈" + msg.getData().get("assetValuation").toString());
        };
    };

    private void initWalletInfoView() {
        String shortCurrentAccAddress = AddressUtil.anonymous(currentWalletAddress);
    }

    private void initTokensView() {
        mMaterialHeader = (MaterialHeader)refreshLayout.getRefreshHeader();

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
        for(CurrencyTypeEnum currencyTypeEnum : CurrencyTypeEnum.values()){
            if(currencyTypeEnum.getName().equals(currencyType)){
                return currencyTypeEnum.getSymbol();
            }
        }
        return null;
    }

    private void loadAssetList() {
        tokenBalance = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "tokenBalance","0").toString();
        Runnable getBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                tokenBalance = Wallet.getInstance().getAccountBUBalance(currentWalletAddress);
                if(!CommonUtil.isNull(tokenBalance)){
                    sharedPreferencesHelper.put(currentWalletAddress + "tokenBalance",tokenBalance);
                }
            }
        };
        new Thread(getBalanceRunnable).start();


        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        if(BumoNodeEnum.TEST.getCode() == bumoNodeType){
            localTokenListSharedPreferenceKey = BumoNodeEnum.TEST.getLocalTokenListSharedPreferenceKey();
        }else if(BumoNodeEnum.MAIN.getCode() == bumoNodeType){
            localTokenListSharedPreferenceKey = BumoNodeEnum.MAIN.getLocalTokenListSharedPreferenceKey();
        }
        GetTokensRespDto getTokensRespDto = JSON.parseObject(sharedPreferencesHelper.getSharedPreference(localTokenListSharedPreferenceKey, "").toString(), GetTokensRespDto.class);
        if(getTokensRespDto != null){
            mLocalTokenList = getTokensRespDto.getTokenList();
        }
        String currencyType = sharedPreferencesHelper.getSharedPreference("currencyType","CNY").toString();
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("address",currentWalletAddress);
        parmasMap.put("currencyType",currencyType);
        parmasMap.put("tokenList", mLocalTokenList);
        Call<ApiResult<GetTokensRespDto>> call = tokenService.getTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokensRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokensRespDto>> call, Response<ApiResult<GetTokensRespDto>> response) {
                mAssetsHomeEmptyView.show(null,null);
                ApiResult<GetTokensRespDto> respDtoApiResult = response.body();

                if(respDtoApiResult != null){
                    sharedPreferencesHelper.put(currentWalletAddress + "tokensInfoCache", JSON.toJSONString(respDtoApiResult.getData()));
                    if(isAdded()){
                        handleTokens(respDtoApiResult.getData());
                    }
                }else {
                    mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetTokensRespDto>> call, Throwable t) {
                t.printStackTrace();
                if(isAdded()){
                    mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }
        });
    }

    private void handleTokens(GetTokensRespDto tokensRespDto) {
        List<GetTokensRespDto.TokenListBean> mTokenList;
        if(tokensRespDto != null){
            if(tokensRespDto.getTokenList() == null || tokensRespDto.getTokenList().size() == 0){
                mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                return;
            }else{
                mAssetsHomeEmptyView.show("","");
            }

            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putString("assetValuation", tokensRespDto.getTotalAmount());
            msg.setData(data);
            handler.sendMessage(msg);
            assetsSv.smoothScrollTo(0,0);
            mTokenList = tokensRespDto.getTokenList();

        }else{
            mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

        mTokensAdapter = new TokensAdapter(mTokenList,getContext());
        mTokenListLv.setAdapter(mTokensAdapter);
        mTokenListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle argz = new Bundle();
                GetTokensRespDto.TokenListBean tokenInfo = (GetTokensRespDto.TokenListBean) mTokensAdapter.getItem(position);
                argz.putString("assetCode",tokenInfo.getAssetCode());
                argz.putString("icon",tokenInfo.getIcon());
                argz.putString("amount",tokenInfo.getAmount());
                argz.putString("price",tokenInfo.getPrice());
                argz.putString("issuer",tokenInfo.getIssuer());
                argz.putString("decimals",tokenInfo.getDecimals()+"");
                argz.putString("tokenType",tokenInfo.getType()+"");
                argz.putString("assetAmount",tokenInfo.getAssetAmount());
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
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress","").toString();
        if(CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString())){
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
            currentWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName","Wallet-1").toString();
            whetherIdentityWallet = true;
        }else {
            currentWalletName = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "-walletName","").toString();
        }
        currencyType = sharedPreferencesHelper.getSharedPreference("currencyType","CNY").toString();
        bumoNodeType = sharedPreferencesHelper.getInt("bumoNode",Constants.DEFAULT_BUMO_NODE);
        GetTokensRespDto tokensCache = JSON.parseObject(sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "tokensInfoCache", "").toString(), GetTokensRespDto.class);
        if(tokensCache != null){
            handleTokens(tokensCache);
        }
//        initBackground();
        initTokensView();
        refreshLayout.autoRefresh();
    }

    private void initBackground() {
        if(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE)== BumoNodeEnum.TEST.getCode()){
            mCurrentTestNetTipsTv.setText(getString(R.string.current_test_message_txt));
            mAssetLinearLayout.setBackgroundResource(R.drawable.bg_asset_home_test_net);
        }
        mCurrentWalletNameTv.setText(currentWalletName);
    }

    private void refreshData(){
        loadAssetList();
    }

    private void startScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getResources().getString(R.string.wallet_scan_notice));
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(null != data){
            if(Constants.REQUEST_IMAGE == resultCode){
                if(null != data){
                    String resultFromBitmap = data.getStringExtra("resultFromBitmap");
                    handleResult(resultFromBitmap);
                }
            }else{
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
                } else if(resultContent.startsWith(Constants.QR_LOGIN_PREFIX)) {
                    final String uuid = resultContent.replace(Constants.QR_LOGIN_PREFIX,"");
                    NodePlanManagementSystemService nodePlanManagementSystemService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanManagementSystemService.class);
                    Call<ApiResult<UserScanQrLoginDto>> call;
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("uuid",uuid);
                    paramsMap.put("address",currentWalletAddress);
                    call = nodePlanManagementSystemService.userScanQrLogin(paramsMap);
                    call.enqueue(new Callback<ApiResult<UserScanQrLoginDto>>() {
                        @Override
                        public void onResponse(Call<ApiResult<UserScanQrLoginDto>> call, Response<ApiResult<UserScanQrLoginDto>> response) {
                            ApiResult<UserScanQrLoginDto> respDto = response.body();
                            if(null != respDto){
                                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                                    UserScanQrLoginDto userScanQrLoginDto = respDto.getData();
                                    String appId = userScanQrLoginDto.getAppId();
                                    String appName = userScanQrLoginDto.getAppName();
                                    String appPic = userScanQrLoginDto.getAppPic();
                                    Bundle argz = new Bundle();
                                    argz.putString("appId",appId);
                                    argz.putString("uuid",uuid);
                                    argz.putString("address",currentWalletAddress);
                                    argz.putString("appName",appName);
                                    argz.putString("appPic",appPic);
                                    BPNodePlanManagementSystemLoginFragment bpNodePlanManagementSystemLoginFragment = new BPNodePlanManagementSystemLoginFragment();
                                    bpNodePlanManagementSystemLoginFragment.setArguments(argz);
                                    startFragment(bpNodePlanManagementSystemLoginFragment);
                                }else {
                                    Bundle argz = new Bundle();
                                    argz.putString("errorCode",respDto.getErrCode());
                                    BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                    bpScanErrorFragment.setArguments(argz);
                                    startFragment(bpScanErrorFragment);
                                }
                            }else {
                                Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<UserScanQrLoginDto>> call, Throwable t) {
                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if(resultContent.startsWith(Constants.QR_NODE_PLAN_PREFIX)) {
                    String qrCodeSessionId = resultContent.replace(Constants.QR_NODE_PLAN_PREFIX,"");
                    NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                    Call<ApiResult<GetQRContentDto>> call;
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("qrcodeSessionId",qrCodeSessionId);
                    paramsMap.put("initiatorAddress",currentWalletAddress);
                    call = nodePlanService.getQRContent(paramsMap);
                    call.enqueue(new Callback<ApiResult<GetQRContentDto>>() {
                        @Override
                        public void onResponse(Call<ApiResult<GetQRContentDto>> call, Response<ApiResult<GetQRContentDto>> response) {
                            ApiResult<GetQRContentDto> respDto = response.body();
                            if(null != respDto){
                                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                                    showTransactionConfirmView(respDto.getData());
                                }else {
                                    Bundle argz = new Bundle();
                                    argz.putString("errorCode",respDto.getErrCode());
                                    argz.putString("errorMessage",respDto.getMsg());
                                    BPScanErrorFragment bpScanErrorFragment = new BPScanErrorFragment();
                                    bpScanErrorFragment.setArguments(argz);
                                    startFragment(bpScanErrorFragment);
                                }
                            }else {
                                Toast.makeText(getContext(),"response is null",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<GetQRContentDto>> call, Throwable t) {
                            System.out.print(t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
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
        if(ScanTransactionTypeEnum.NODE_VOTE.getCode().equals(transactionType)){
            scanTxFee = Constants.NODE_VOTE_FEE;
        } else if(ScanTransactionTypeEnum.NODE_AUDIT.getCode().equals(transactionType)){
            scanTxFee = Constants.NODE_AUDIT_FEE;
        } else if(ScanTransactionTypeEnum.APPLY_CO_BUILD.getCode().equals(transactionType)){
            scanTxFee = Constants.NODE_CO_BUILD_FEE;
        } else {
            scanTxFee = Constants.MIN_FEE;
        }

        // confirm page
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm,null));
        TextView mTransactionAmountTv = qmuiBottomSheet.findViewById(R.id.transactionAmountTv);
        LinearLayout mTransactionAmountLl = qmuiBottomSheet.findViewById(R.id.transactionAmountLl);
        if("0".equals(transactionAmount)){
            mTransactionAmountLl.setVisibility(View.GONE);
        }else {
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
        mDetailsAmountTv.setText(CommonUtil.addSuffix(CommonUtil.thousandSeparator(transactionAmount),"BU"));
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

        if(Double.valueOf(tokenBalance) < Double.valueOf(amount)){
            Toast.makeText(getContext(),getString(R.string.send_tx_bu_not_enough),Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TransactionBuildBlobResponse buildBlobResponse;
                    if(ScanTransactionTypeEnum.APPLY_CO_BUILD.getCode().equals(transactionType)) {
                        // handle script


                        org.json.JSONObject jsonObj = new org.json.JSONObject(script);
                        org.json.JSONObject input = jsonObj.getJSONObject("input");
                        String payload = jsonObj.getString("payload");
//                        ScriptModel scriptModel = new Gson().fromJson(script, ScriptModel.class);


//                        buildBlobResponse = Wallet.getInstance().buildBlob(amount, script, currentWalletAddress, String.valueOf(scanTxFee), contractAddress);
                        buildBlobResponse = Wallet.getInstance().applyCoBuildBlob(currentWalletAddress, amount,input.toString(),payload,Constants.NODE_CO_BUILD_FEE );
                    }else {
                        buildBlobResponse = Wallet.getInstance().buildBlob(amount, script, currentWalletAddress, String.valueOf(scanTxFee), contractAddress);
                    }
                    String txHash = buildBlobResponse.getResult().getHash();
                    NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                    Call<ApiResult<TransConfirmModel>> call;
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("qrcodeSessionId",qrCodeSessionID);
                    paramsMap.put("hash",txHash);
                    paramsMap.put("initiatorAddress",currentWalletAddress);
                    call = nodePlanService.confirmTransaction(paramsMap);
                    call.enqueue(new Callback<ApiResult<TransConfirmModel>>() {
                        @Override
                        public void onResponse(Call<ApiResult<TransConfirmModel>> call, Response<ApiResult<TransConfirmModel>> response) {
                            ApiResult<TransConfirmModel> respDto = response.body();
                            if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                                expiryTime = respDto.getData().getExpiryTime();
                                LogUtils.e("超时时间"+expiryTime);
                                submitTransaction(buildBlobResponse);
                            }else {
                                LogUtils.e("超时时间"+respDto.getMsg()+"\t"+respDto.getErrCode());
                                CommonUtil.showMessageDialog(getContext(),respDto.getMsg());
//                                Toast.makeText(getContext(),respDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TransConfirmModel>> call, Throwable t) {
                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void submitTransaction(final TransactionBuildBlobResponse buildBlobResponse) {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_password_comfirm);
        qmuiDialog.show();

        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });

        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentTimeMillis()>=Long.parseLong(expiryTime)) {
                    Toast.makeText(mContext, R.string.transaction_timeout, Toast.LENGTH_SHORT).show();
                    qmuiDialog.dismiss();
                    startFragment(new HomeFragment());
                    return;
                }

                txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create();
                txSendingTipDialog.show();
                txSendingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                            return true;
                        }
                        return false;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String accountBPData = getAccountBPData();
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();
                        try {
                            txHash = Wallet.getInstance().submitTransaction(password,accountBPData,currentWalletAddress,buildBlobResponse);
                        }catch (WalletException e){
                            e.printStackTrace();
                            Looper.prepare();
                            if(com.bupocket.wallet.enums.ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT).show();
                            }else if(com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                            }
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        }finally {
                            timer.schedule(timerTask,
                                    1 * 1000,//延迟1秒执行
                                    1000);
                        }
                    }
                }).start();
                qmuiDialog.dismiss();

            }
        });
    }

    private String getAccountBPData(){
        String accountBPData = null;
        if(whetherIdentityWallet) {
            accountBPData = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        }else {
            accountBPData = sharedPreferencesHelper.getSharedPreference(currentWalletAddress+ "-BPdata", "").toString();
        }
        return accountBPData;
    }

    private int timerTimes = 0;
    private final Timer timer = new Timer();
    @SuppressLint("HandlerLeak")
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(timerTimes > Constants.TX_REQUEST_TIMEOUT_TIMES){
                        timerTask.cancel();
                        txSendingTipDialog.dismiss();
                        startFragmentAndDestroyCurrent(new BPTxRequestTimeoutFragment());
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash",txHash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(paramsMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>(){

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            if(!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())){
                                return;
                            }else{
                                txDetailRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                if (com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH_FOR_PAYMENT.getCode().equals(txDetailRespBoBean.getErrorCode())) {
                                    Toast.makeText(getActivity(), R.string.balance_not_enough, Toast.LENGTH_SHORT).show();
                                }
                                Bundle argz = new Bundle();
                                argz.putString("destAccAddr",txDetailRespBoBean.getDestAddress());
                                argz.putString("sendAmount",txDetailRespBoBean.getAmount());
                                argz.putString("txFee",txDetailRespBoBean.getFee());
                                argz.putString("tokenCode","BU");
                                argz.putString("note",txDetailRespBoBean.getOriginalMetadata());
                                argz.putString("state",txDetailRespBoBean.getStatus().toString());
                                argz.putString("sendTime",txDetailRespBoBean.getApplyTimeDate());
                                BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();
                                bpSendStatusFragment.setArguments(argz);
                                startFragment(bpSendStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            Toast.makeText(getActivity(), R.string.tx_timeout_err, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(txHash != null && !txHash.equals("")){
                mHanlder.sendEmptyMessage(1);
            }
        }
    };
}
