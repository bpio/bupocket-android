package com.bupocket.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import butterknife.BindString;
import butterknife.BindView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.bumptech.glide.Glide;
import com.bupocket.BPMainActivity;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.activity.RedPacketActivity;
import com.bupocket.adaptor.TokensAdapter;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.CustomNodeTypeEnum;
import com.bupocket.enums.RedPacketTypeEnum;
import com.bupocket.enums.VoucherStatusEnum;
import com.bupocket.http.api.DeviceBindService;
import com.bupocket.http.api.RedPacketService;
import com.bupocket.interfaces.SignatureListener;
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
import com.bupocket.http.api.dto.resp.UserScanQrLoginDto;
import com.bupocket.model.BonusInfoBean;
import com.bupocket.model.DeviceBindModel;
import com.bupocket.model.OpenStatusModel;
import com.bupocket.model.RedPacketDetailModel;
import com.bupocket.model.SKModel;
import com.bupocket.model.TransConfirmModel;
import com.bupocket.model.UDCBUModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.NetworkUtils;
import com.bupocket.utils.RedPacketAnimationUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.voucher.BPSendTokenVoucherFragment;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.commons.collections.functors.ConstantTransformer;

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

import static com.bupocket.common.Constants.NORMAL_WALLET_NAME;

public class BPAssetsHomeFragment extends BaseTransferFragment {

    private static final int REQUEST_CODE_RED_PACKET = 0x1022;
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
    Button mImmediatelyBackupBtn;

    @BindView(R.id.safetyTipsLl)
    LinearLayout mSafetyTipsLl;
    @BindView(R.id.manageWalletBtn)
    ImageView mManageWalletBtn;
    @BindView(R.id.currentWalletNameTv)
    TextView mCurrentWalletNameTv;
    @BindView(R.id.ivAssetsInfo)
    ImageView ivAssetsInfo;
    @BindView(R.id.redPacketTv)
    ImageView redPacketTv;

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
    private Boolean whetherIdentityWallet = false;

    private Double scanTxFee;
    private String expiryTime;
    private View faildlayout;
    List<GetTokensRespDto.TokenListBean> mTokenList;
    private String bonusCode;

    private BonusInfoBean redPacketNoOpenData;
    private RedPacketDetailModel redPacketDetailModel;
    public static String RED_PACKET_ERR_CODE = "-1";
    private String sk;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_assets_home;
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

    public void initView() {
        faildlayout = LayoutInflater.from(mContext).inflate(R.layout.view_load_failed, null);
        faildlayout.findViewById(R.id.reloadBtn).setOnClickListener(new View.OnClickListener() {
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onStart() {
        super.onStart();
        initBackground();
    }

    public void setListeners() {
        mHomeScanLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        mReceiveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startFragment(new BPCollectionFragment());
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
                go2BPCreateWalletShowMnemonicCodeFragment();

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
                DialogUtils.showMessageNoTitleDialog(mContext, getString(R.string.wallet_bu_info));
            }
        });

        redPacketTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RED_PACKET_ERR_CODE.equals(RedPacketTypeEnum.OPEN_RED_PACKET.getCode())) {
                    openRedPacketDetailFragment();
                } else if (RED_PACKET_ERR_CODE.equals(RedPacketTypeEnum.ALL_ALREADY_RECEIVED.getCode())) {
                    openRedPacketActivity(redPacketNoOpenData, "");
                } else if (RED_PACKET_ERR_CODE.equals(RedPacketTypeEnum.CLOSE_RED_PACKET.getCode())) {
                    openRedPacketActivity(redPacketNoOpenData, bonusCode);
                }
            }
        });
    }

    private void openRedPacketDetailFragment() {

        if (redPacketDetailModel == null) {
            return;
        }
        BPRedPacketHomeFragment bpRedPacketHomeFragment = new BPRedPacketHomeFragment();
        Bundle args = new Bundle();
        args.putString(ConstantsType.BONUSCODE, bonusCode);
        args.putSerializable(ConstantsType.REDPACKETDETAILMODEL, redPacketDetailModel);
        bpRedPacketHomeFragment.setArguments(args);
        startFragment(bpRedPacketHomeFragment);
    }

    private void backupState() {
        String backupState = sharedPreferencesHelper.getSharedPreference("mnemonicWordBackupState", "").toString();
        String backupTipsState = sharedPreferencesHelper.getSharedPreference("backupTipsState", "").toString();
        if (MnemonicWordBackupStateEnum.ALREADYBACKUP.getCode().equals(backupState) || BackupTipsStateEnum.HIDE.getCode().equals(backupTipsState)) {
            mSafetyTipsLl.setVisibility(View.GONE);
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mTotalAssetsValueTv.setText("≈" + msg.getData().get("assetValuation").toString());
        }

        ;
    };


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

        if (!NetworkUtils.isNetWorkAvailable(getContext())) {
//            ToastUtil.showToast(getActivity(), getString(R.string.network_error_msg), Toast.LENGTH_LONG);
            return;
        }

        Runnable getBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                tokenBalance = Wallet.getInstance().getAccountBUBalance(currentWalletAddress);
                if (!CommonUtil.isNull(tokenBalance)) {
                    sharedPreferencesHelper.put(currentWalletAddress + "tokenBalance", tokenBalance);
                }
            }
        };
        ThreadManager.getInstance().execute(getBalanceRunnable);
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
                ApiResult<GetTokensRespDto> respDtoApiResult = response.body();

                if (respDtoApiResult != null) {
                    sharedPreferencesHelper.put(currentWalletAddress + "tokensInfoCache", JSON.toJSONString(respDtoApiResult.getData()));
                    if (isAdded()) {
                        handleTokens(respDtoApiResult.getData());
                    }
                } else {
                    if (mAssetsHomeEmptyView != null) {
                        mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), "");
                    }
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
        String amount = mTokenList.get(0).getAmount();
        sharedPreferencesHelper.put(currentWalletAddress + "tokenBalance", amount);
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

    public void initData() {
        getSkData();
        mTokenList = new ArrayList<>();
        QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString())) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
            currentWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", NORMAL_WALLET_NAME).toString();
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
        initTokensView();
        refreshLayout.autoRefresh();
        initPermission();
        reqOpenRedPacketStatus();

    }

    private void getSkData() {

        DeviceBindService deviceBindService = RetrofitFactory.getInstance().getRetrofit().create(DeviceBindService.class);
        deviceBindService.getConfig().enqueue(new Callback<ApiResult<SKModel>>() {
            @Override
            public void onResponse(Call<ApiResult<SKModel>> call, Response<ApiResult<SKModel>> response) {
                ApiResult<SKModel> body = response.body();
                if (body != null) {
                    if (body.getErrCode().equals(ExceptionEnum.SUCCESS.getCode())) {
                        sk = body.getData().getSk();
                        spHelper.put(ConstantsType.SK_PACKET,sk);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResult<SKModel>> call, Throwable t) {

            }
        });
    }

    private void reqOpenRedPacketStatus() {
        final RedPacketService redPacketService = RetrofitFactory.getInstance().getRetrofit().create(RedPacketService.class);
        redPacketService.queryOpen().enqueue(new Callback<ApiResult<OpenStatusModel>>() {
            @Override
            public void onResponse(Call<ApiResult<OpenStatusModel>> call, Response<ApiResult<OpenStatusModel>> response) {
                ApiResult<OpenStatusModel> body = response.body();
                if (body == null) {
                    return;
                }
                String errCode = body.getErrCode();
                if (TextUtils.isEmpty(errCode)) {
                    return;
                }
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {//open
                    OpenStatusModel data = body.getData();
                    if (data.getType().equals("1")) {
                        bonusCode = data.getBonusId();
                        redPacketTv.setVisibility(View.VISIBLE);
                        Glide.with(getActivity()).load(data.getBonusEntranceImage()).into(redPacketTv);
                        queryRedPacket(bonusCode);

                    }
                    return;
                }

                if (ExceptionEnum.ERROR_RED_PACKET_NOT_OPEN.getCode().equals(body.getErrCode())) {//close
                    redPacketTv.setVisibility(View.GONE);

                    return;
                }


            }

            @Override
            public void onFailure(Call<ApiResult<OpenStatusModel>> call, Throwable t) {

            }
        });
    }


    private void queryRedPacket(final String bonusCode) {
        RedPacketService redPacketService = RetrofitFactory.getInstance().getRetrofit().create(RedPacketService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.BONUSCODE, bonusCode);
        map.put(Constants.ADDRESS, WalletCurrentUtils.getWalletAddress(spHelper));

        redPacketService.queryRedPacket(map).enqueue(new Callback<ApiResult<BonusInfoBean>>() {
            @Override
            public void onResponse(Call<ApiResult<BonusInfoBean>> call, Response<ApiResult<BonusInfoBean>> response) {
                ApiResult<BonusInfoBean> body = response.body();
                RED_PACKET_ERR_CODE = body.getErrCode();
                if (ExceptionEnum.SUCCESS.getCode().equals(RED_PACKET_ERR_CODE)) {
                    RedPacketAnimationUtils.loopRotateAnimation(redPacketTv);
                    redPacketNoOpenData = body.getData();
                    if (redPacketNoOpenData != null) {
                        openRedPacketActivity(redPacketNoOpenData, bonusCode);
                    }
                } else if (ExceptionEnum.ERROR_RED_PACKET_ALREADY_RECEIVED.getCode().equals(RED_PACKET_ERR_CODE)) {//
                    reqRedPacketData();
                } else if (ExceptionEnum.ERROR_RED_PACKET_ALL_ALREADY_RECEIVED.getCode().equals(RED_PACKET_ERR_CODE)) {
                    redPacketNoOpenData = body.getData();
                } else if (ExceptionEnum.ERROR_RED_PACKET_UNBIND_DEVICE.getCode().equals(RED_PACKET_ERR_CODE)) {

                    bindDevice();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<BonusInfoBean>> call, Throwable t) {
                LogUtils.e("");
            }
        });
    }

    private void bindDevice() {

        String skData = (String) spHelper.getSharedPreference(ConstantsType.SK_PACKET, "");
        if (TextUtils.isEmpty(skData)) {
            return;
        }
        DeviceBindService deviceBindService = RetrofitFactory.getInstance().getRetrofit().create(DeviceBindService.class);
        HashMap<String, Object> map = new HashMap<>();
        String walletAddress = WalletCurrentUtils.getWalletAddress(spHelper);
        map.put(ConstantsType.WALLETADDRESS, walletAddress);
        map.put(ConstantsType.IDENTITYADDRESS, WalletCurrentUtils.getIdentityAddress(spHelper));
        map.put(ConstantsType.DEVICEID, CommonUtil.getUniqueId(mContext));
        String walletAccountSignData = Wallet.getInstance().signData(skData, walletAddress);
        map.put(ConstantsType.SIGNDATA, walletAccountSignData);
        deviceBindService.deviceBind(map).enqueue(new Callback<ApiResult<DeviceBindModel>>() {
            @Override
            public void onResponse(Call<ApiResult<DeviceBindModel>> call, Response<ApiResult<DeviceBindModel>> response) {
                ApiResult<DeviceBindModel> body = response.body();
                if (body != null) {
                    if (body.getErrCode().equals(ExceptionEnum.SUCCESS.getCode())) {
                        queryRedPacket(bonusCode);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResult<DeviceBindModel>> call, Throwable t) {

            }
        });
    }

    private void reqRedPacketData() {
        RedPacketService redPacketService = RetrofitFactory.getInstance().getRetrofit().create(RedPacketService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.BONUSCODE, bonusCode);
        map.put(ConstantsType.ADDRESS, WalletCurrentUtils.getWalletAddress(spHelper));
        redPacketService.queryRedPacketDetail(map).enqueue(new Callback<ApiResult<RedPacketDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<RedPacketDetailModel>> call, Response<ApiResult<RedPacketDetailModel>> response) {
                ApiResult<RedPacketDetailModel> body = response.body();
                if (body.getErrCode().equals(ExceptionEnum.SUCCESS.getCode())) {
                    redPacketDetailModel = body.getData();
                    redPacketTv.clearAnimation();
//                    RedPacketAnimationUtils.isLoop=false;
                }
            }

            @Override
            public void onFailure(Call<ApiResult<RedPacketDetailModel>> call, Throwable t) {

            }
        });
    }

    private void openRedPacketActivity(BonusInfoBean data, String bonusCode) {

        if (data == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), RedPacketActivity.class);
        intent.putExtra(ConstantsType.BONUSCODE, bonusCode);
        intent.putExtra(ConstantsType.REDOPENSTATUS, "0");
        Bundle extras = new Bundle();
        extras.putSerializable(ConstantsType.BONUSINFOBEAN, data);
        intent.putExtras(extras);
        startActivityForResult(intent, REQUEST_CODE_RED_PACKET);

    }

    private void initBackground() {


        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, 0);

        if (isStart == CustomNodeTypeEnum.START.getServiceType()) {
            mCurrentTestNetTipsTv.setText(getString(R.string.custom_environment));
            mAssetLinearLayout.setBackgroundResource(R.mipmap.ic_custom_service_bg);

        } else if (SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()) {
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
        LogUtils.e(requestCode + "+requestCode+" + resultCode);
        if (requestCode == REQUEST_CODE_RED_PACKET) {
            if (resultCode == 1) {
                reqRedPacketData();
            } else if (resultCode == Integer.parseInt(RedPacketTypeEnum.ALL_ALREADY_RECEIVED.getCode())) {
                redPacketTv.clearAnimation();
//                RedPacketAnimationUtils.isLoop=false;
            }
            return;
        }


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


    @Override
    public void onResume() {
        super.onResume();
        initWalletName();
        backupState();

    }

    private void initWalletName() {
        if (CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString())) {
            currentWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", NORMAL_WALLET_NAME).toString();
        } else {
            currentWalletName = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "-walletName", "").toString();
        }
        if (!currentWalletName.equals(mCurrentWalletNameTv.getText())) {
            mCurrentWalletNameTv.setText(currentWalletName);
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
        if (ScanTransactionTypeEnum.NODE_ADD_MARGIN.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.NODE_VOTE.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.NODE_EXIT.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.CO_BUILD_SUPPORT.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.CO_BUILD_APPLY_KOL.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.CO_BUILD_APPLY_KOL_EXIT.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.COMMITTEE_EXIT.getCode().equals(transactionType)
                || ScanTransactionTypeEnum.COMMITTEE_APPLY.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_COMMON_FEE;
        } else if (ScanTransactionTypeEnum.CO_BUILD_PURCHASE.getCode().equals(transactionType)) {
            scanTxFee = Constants.NODE_CO_BUILD_PURCHASE_FEE;
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


        Runnable buildBlobRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                    final TransactionBuildBlobResponse buildBlobResponse;
                    if (ScanTransactionTypeEnum.CO_BUILD_PURCHASE.getCode().equals(transactionType)) {
                        // handle script

                        org.json.JSONObject jsonObj = new org.json.JSONObject(script);
                        String input = jsonObj.getString("input");
                        String payload = jsonObj.getString("payload");
                        buildBlobResponse = Wallet.getInstance().applyCoBuildBlob(currentWalletAddress, String.valueOf(Double.parseDouble(amount) + Constants.NODE_CO_BUILD_AMOUNT_FEE), input.toString(), payload, Constants.NODE_CO_BUILD_PURCHASE_FEE, transMetaData);
                    } else {
                        buildBlobResponse = Wallet.getInstance().buildBlob(amount, script, currentWalletAddress, String.valueOf(scanTxFee), contractAddress, transMetaData);
                    }
                    final String txHash = buildBlobResponse.getResult().getHash();


                    if (TextUtils.isEmpty(tokenBalance) || (Double.valueOf(tokenBalance) <= Double.valueOf(amount))) {
                        ToastUtil.showToast(getActivity(), getString(R.string.send_tx_bu_not_enough), Toast.LENGTH_SHORT);
                        return;
                    }

                    getSignatureInfo(new SignatureListener() {
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
                                            DialogUtils.showMessageNoTitleDialog(getContext(), respDto.getMsg(), respDto.getErrCode());
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
        };
        ThreadManager.getInstance().execute(buildBlobRunnable);
    }


    private void handleResult(String resultContent) {
        if (null == resultContent) {
            Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
        } else {
            if (!PublicKey.isAddressValid(resultContent)) {
                if (CommonUtil.checkIsBase64(resultContent)) {

                    goIssueTokenFragment(resultContent);
                } else {

                    try {
                        java.net.URL url = new java.net.URL(resultContent);
                        String path = url.getPath();
                        resultContent = path;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    if (resultContent.startsWith(Constants.QR_LOGIN_PREFIX)) {
                        goLogin(resultContent);
                    } else if (resultContent.startsWith(Constants.QR_NODE_PLAN_PREFIX)) {
                        goDpos(resultContent);
                    } else if (resultContent.contains(Constants.INFO_UDCBU)) {
                        goBuildTransfer(resultContent);
                    } else if (resultContent.startsWith(Constants.VOUCHER_QRCODE)) {
                        goSendVoucher(resultContent);
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

    private void goSendVoucher(String resultContent) {
        getFragmentManager().findFragmentByTag(BPAssetsHomeFragment.class.getSimpleName());
        String address = resultContent.replace(Constants.VOUCHER_QRCODE, "");
        BPSendTokenVoucherFragment fragment = new BPSendTokenVoucherFragment();
        Bundle args = new Bundle();
        args.putString(ConstantsType.ADDRESS, address);
        args.putString(ConstantsType.FRAGMENT_TAG, VoucherStatusEnum.ASSETS_HOME_FRAGMENT.getCode());
        fragment.setArguments(args);
        startFragment(fragment);

    }

    private void goBuildTransfer(String resultContent) {
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
                            Runnable buildBlob = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final TransactionBuildBlobResponse buildBlobResponse = Wallet.getInstance().buildBlob(finalUdcbuModel.getAmount(), finalUdcbuModel.getInput(), currentWalletAddress, finalUdcbuModel.getTx_fee(), finalUdcbuModel.getDest_address(), getString(R.string.transaction_metadata));

                                        if (TextUtils.isEmpty(tokenBalance) || (Double.valueOf(tokenBalance) <= Double.valueOf(finalUdcbuModel.getAmount()))) {
                                            ToastUtil.showToast(getActivity(), getString(R.string.send_tx_bu_not_enough), Toast.LENGTH_SHORT);
                                            return;
                                        }
                                        getSignatureInfo(new SignatureListener() {
                                            @Override
                                            public void success(String privateKey) {

                                                submitTransactionBase(privateKey, buildBlobResponse);
                                            }
                                        });

                                    } catch (WalletException e) {
                                        if (e.getErrCode().equals(com.bupocket.wallet.enums.ExceptionEnum.ADDRESS_NOT_EXIST.getCode())) {
                                            ToastUtil.showToast(getActivity(), getString(R.string.address_not_exist), Toast.LENGTH_SHORT);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            ThreadManager.getInstance().execute(buildBlob);
                        }
                    });

        } catch (Exception e) {
            ToastUtil.showToast(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT);
        }

    }

    private void goDpos(String resultContent) {
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
                        String msg = DialogUtils.byCodeToMsg(mContext, respDto.getErrCode());
                        if (!msg.isEmpty()) {
                            DialogUtils.showMessageNoTitleDialog(getContext(), msg);
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

    }

    private void goLogin(String resultContent) {
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
                        argz.putString("errorMessage", respDto.getData().getErrorMsg());
                        argz.putString("errorDescription", respDto.getData().getErrorDescription());
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
    }

    private void goIssueTokenFragment(String resultContent) {
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
    }

    private void go2BPCreateWalletShowMnemonicCodeFragment() {
        BPCreateWalletFormFragment.isCreateWallet = false;
        BPBackupWalletFragment createWalletShowMneonicCodeFragment = new BPBackupWalletFragment();
        Bundle argz = new Bundle();
        argz.putString(ConstantsType.WALLET_ADDRESS, WalletCurrentUtils.getIdentityAddress(spHelper));
        createWalletShowMneonicCodeFragment.setArguments(argz);
        startFragment(createWalletShowMneonicCodeFragment);

    }

}
