package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetTokenDetailRespDto;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.model.IssueTokenInfo;
import com.bupocket.utils.AmountUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPIssueTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;
//    @BindView(R.id.issueTypeTv)
//    TextView mIssueTypeTv;
    @BindView(R.id.thisTimeIssueAmountTv)
    TextView mThisTimeIssueAmountTv;
    @BindView(R.id.totalIssueAmountTv)
    TextView mTotalIssueAmountTv;
    @BindView(R.id.issueFeeTv)
    TextView mIssueFeeTv;
    @BindView(R.id.accumulativeIssueAmountTv)
    TextView mAccumulativeIssueAmountTv;
    @BindView(R.id.issueConfirmBtn)
    QMUIRoundButton mIssueConfirmBtn;
    @BindView(R.id.issueCancelBtn)
    QMUIRoundButton mIssueCancelBtn;
    @BindView(R.id.issueTokenInfoLl)
    LinearLayout mIssueTokenInfoLl;
    @BindView(R.id.tokenNameTv)
    TextView mTokenNameTv;


    private Socket mSocket;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    String assetCode;
    String issueAddress;
    String issueAmount;
    String actualSupply;
    String assetName;
    String decimals;
    String tokenDescription;
//    String tokenType;
    String totalSupply;
    String version;
    String errorMsg;
    String balance;
    QMUITipDialog txSendingTipDialog;
    private String hash;
    private TxDetailRespDto.TxDeatilRespBoBean txDeatilRespBoBean;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_issue_token_confirm, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopbar();
        initData();
        setListener();
        return root;
    }

    private void setListener() {
        mIssueConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.valueOf(balance) < Double.valueOf(Constants.ISSUE_TOKEN_FEE)){
                    Toast.makeText(getActivity(), R.string.error_issue_balance_insufficient_message_txt, Toast.LENGTH_SHORT).show();
                }else if(!CommonUtil.isNull(errorMsg)){
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }else{
                    showPasswordComfirmDialog();
                }
            }
        });
        mIssueCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("token.issue.cancel","");
                startFragment(new BPAssetsHomeFragment());
            }
        });
    }

    private void showPasswordComfirmDialog() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
        qmuiDialog.show();
        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
        TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
        TextView mPpasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

        mPasswordConfirmNotice.setText(getString(R.string.register_token_password_confirm_txt));
        mPpasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
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
                        try {
                            hash = Wallet.getInstance().issueAtp10Token(password,accountBPData,issueAddress,assetCode,decimals,issueAmount,Constants.ISSUE_TOKEN_FEE);
                        } catch (WalletException e){
                            e.printStackTrace();
                            Looper.prepare();
                            if(ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT).show();
                            }else if(ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                            }
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.error_issue_amount_message_txt, Toast.LENGTH_SHORT).show();
                            txSendingTipDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } finally {
                            mSocket.emit("token.issue.processing","");
                            timer.schedule(timerTask,
                                    1 * 1000,//延迟1秒执行
                                    1000);
                        }
                    }
                }).start();

                qmuiDialog.dismiss();
            }
        });

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        final String uuID = bundle.getString("uuID");

        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                System.out.println("socket id :" + mSocket.id());
                mSocket.emit("token.issue.join",uuID);
//                mSocket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mSocket.emit("token.issue.join",uuID);
            }
        });
        mSocket.emit("token.issue.scanSuccess","");
        mSocket.connect();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        Bundle bundle = getArguments();
        String tokenData = bundle.getString("tokenData");
        IssueTokenInfo issueTokenInfo = IssueTokenInfo.objectFromData(tokenData);
        assetCode = issueTokenInfo.getCode();
        issueAmount = issueTokenInfo.getAmount();
        issueAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        balance = bundle.getString("buBalance");
        getTokenDetail();
    }

    private void getTokenDetail() {
        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("assetCode",assetCode);
        parmasMap.put("issueAddress",issueAddress);
        Call<ApiResult<GetTokenDetailRespDto>> call = tokenService.getTokenDetail(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokenDetailRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokenDetailRespDto>> call, Response<ApiResult<GetTokenDetailRespDto>> response) {
                ApiResult<GetTokenDetailRespDto> respDto = response.body();
                String errorCode = respDto.getErrCode();
                if(errorCode.equals("500004")){
                    @SuppressLint("StringFormatMatches") String msg = String.format(getString(R.string.error_issue_unregistered_message_txt,assetCode));
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    errorMsg = msg;
                }else if(!errorCode.equals("0")){
                    Toast.makeText(getActivity(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                } else{
                    GetTokenDetailRespDto tokenDetail = respDto.getData();
//                    tokenType = tokenDetail.getTokenType();

                    actualSupply = tokenDetail.getActualSupply();
                    assetName = tokenDetail.getAssetName();
                    decimals = tokenDetail.getDecimals();
                    tokenDescription = tokenDetail.getTokenDescription();
                    totalSupply = tokenDetail.getTotalSupply();
                    version = tokenDetail.getVersion();

                    mTokenNameTv.setText(assetName);
                    mTokenCodeTv.setText(assetCode);
                    mThisTimeIssueAmountTv.setText(issueAmount);
                    mIssueFeeTv.setText(CommonUtil.addSuffix(Constants.ISSUE_TOKEN_FEE,"BU"));

                    if(totalSupply.equals("0")){
                        mAccumulativeIssueAmountTv.setText(actualSupply);
                        mIssueTokenInfoLl.removeView(mIssueTokenInfoLl.findViewById(R.id.totalIssueAmountRl));
                    }else {
                        mTotalIssueAmountTv.setText(totalSupply);
                        mAccumulativeIssueAmountTv.setText(actualSupply);
                        if(new BigDecimal(AmountUtil.amountAddition(actualSupply,issueAmount)).compareTo(new BigDecimal(totalSupply))==1){
                            errorMsg = getString(R.string.error_issue_issue_amount_overflow_message_txt);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetTokenDetailRespDto>> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
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
                        Bundle argz = new Bundle();
                        argz.putString("txStatus","timeout");
                        argz.putString("assetName",assetName);
                        argz.putString("assetCode",assetCode);
                        argz.putString("issueAmount",issueAmount);
                        argz.putString("totalSupply",totalSupply);
                        argz.putString("tokenDecimals",decimals);
                        argz.putString("tokenDescription",tokenDescription);
                        argz.putString("issueAddress",issueAddress);
                        BPIssueTokenStatusFragment bpIssueTokenStatusFragment = new BPIssueTokenStatusFragment();
                        bpIssueTokenStatusFragment.setArguments(argz);
                        startFragmentAndDestroyCurrent(bpIssueTokenStatusFragment);
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> parmasMap = new HashMap<>();
                    parmasMap.put("hash",hash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetail(parmasMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>(){

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            System.out.println(JSON.toJSONString(resp));
                            if(!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())){
                                return;
                            }else{
                                txDeatilRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                Bundle argz = new Bundle();
                                argz.putString("assetName",assetName);
                                argz.putString("assetCode",assetCode);
                                argz.putString("issueAmount",issueAmount);
                                argz.putString("totalSupply",totalSupply);
                                argz.putString("tokenDecimals",decimals);
                                argz.putString("tokenDescription",tokenDescription);
                                argz.putString("txStatus",txDeatilRespBoBean.getStatus().toString());
                                argz.putString("issueAddress",issueAddress);
                                argz.putString("txHash",hash);
                                argz.putString("txFee",txDeatilRespBoBean.getFee());
                                argz.putString("errorMsg",txDeatilRespBoBean.getErrorMsg());
                                argz.putString("actualSupply",actualSupply);
                                BPIssueTokenStatusFragment bpIssueTokenStatusFragment = new BPIssueTokenStatusFragment();
                                bpIssueTokenStatusFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpIssueTokenStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            Bundle argz = new Bundle();
                            argz.putString("txStatus","timeout");
                            argz.putString("assetName",assetName);
                            argz.putString("assetCode",assetCode);
                            argz.putString("issueAmount",issueAmount);
                            argz.putString("totalSupply",totalSupply);
                            argz.putString("tokenDecimals",decimals);
                            argz.putString("tokenDescription",tokenDescription);
                            argz.putString("issueAddress",issueAddress);
                            BPIssueTokenStatusFragment bpIssueTokenStatusFragment = new BPIssueTokenStatusFragment();
                            bpIssueTokenStatusFragment.setArguments(argz);
                            startFragmentAndDestroyCurrent(bpIssueTokenStatusFragment);
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
            if(hash != null && !hash.equals("")){
                mHanlder.sendEmptyMessage(1);
            }
        }
    };

    private void initTopbar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.issue_token_page_title));
    }
}