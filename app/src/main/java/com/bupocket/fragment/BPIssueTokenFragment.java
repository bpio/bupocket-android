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

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetTokenDetailRespDto;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.model.IssueTokenInfo;
import com.bupocket.utils.AmountUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.SocketUtil;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
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
    private String assetCode;
    private String issueAddress;
    private Boolean whetherIdentityWallet = false;
    private String issueAmount;
    private String actualSupply;
    private String assetName;
    private String tokenDecimals;
    private String tokenDescription;
    private String totalSupply;
    private String tokenVersion;
    private String errorMsg;
    private String buBalance;
    private QMUITipDialog txSendingTipDialog;
    private String hash;
    private TxDetailRespDto.TxDeatilRespBoBean txDetailRespBoBean;

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
                if (Double.valueOf(buBalance) < Double.valueOf(Constants.ISSUE_TOKEN_FEE)) {
                    Toast.makeText(getActivity(), R.string.error_issue_balance_insufficient_message_txt, Toast.LENGTH_SHORT).show();
                } else if (!CommonUtil.isNull(errorMsg)) {
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                } else {
                    showPasswordConfirmDialog();
                }
            }
        });
        mIssueCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("token.issue.cancel", "");
                mSocket.disconnect();
                startFragment(new HomeFragment());
            }
        });
    }

    private void showPasswordConfirmDialog() {


        DialogUtils.showPassWordInputDialog(getActivity(),
                getString(R.string.password_comfirm_dialog_title),
                getString(R.string.issue_token_password_confirm_txt), "", new DialogUtils.ConfirmListener() {
                    @Override
                    public void confirm(final String password) {

                        txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                                .create();
                        txSendingTipDialog.show();
                        txSendingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        Runnable confirmRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String accountBPData = getAccountBPData();
                                try {
                                    hash = Wallet.getInstance().issueAtp10Token(password, accountBPData, issueAddress, assetCode, tokenDecimals, issueAmount, Constants.ISSUE_TOKEN_FEE);
                                } catch (WalletException e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    if (ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())) {
                                        ToastUtil.showToast(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT);
                                    } else if (ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())) {
                                        ToastUtil.showToast(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT);
                                    } else {
                                        ToastUtil.showToast(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT);
                                    }
                                    txSendingTipDialog.dismiss();
                                    Looper.loop();
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.error_issue_amount_message_txt, Toast.LENGTH_SHORT).show();
                                    txSendingTipDialog.dismiss();
                                    Looper.loop();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                    txSendingTipDialog.dismiss();
                                    Looper.loop();
                                } finally {
                                    mSocket.emit("token.issue.processing", "");
                                    timer.schedule(timerTask,
                                            1 * 1000,//延迟1秒执行
                                            1000);
                                }
                            }
                        };
                        ThreadManager.getInstance().execute(confirmRunnable);
                    }
                });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        final String uuID = bundle.getString("uuID");
        mSocket = SocketUtil.getInstance().getSocket();
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                mSocket.emit("token.issue.join", uuID);
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mSocket.emit("token.issue.join", uuID);
            }
        });
        mSocket.emit("token.issue.scanSuccess", "");
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        Bundle bundle = getArguments();
        String tokenData = bundle.getString("tokenData");
        IssueTokenInfo issueTokenInfo = IssueTokenInfo.objectFromData(tokenData);
        assetCode = issueTokenInfo.getCode();
        issueAmount = issueTokenInfo.getAmount();
        issueAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(issueAddress) || sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString().equals(issueAddress)) {
            issueAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
            whetherIdentityWallet = true;
        }
        buBalance = bundle.getString("buBalance");
        if (null == buBalance) {
            buBalance = "0";
        }
        getTokenDetail();
    }

    private void getTokenDetail() {
        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("assetCode", assetCode);
        parmasMap.put("issueAddress", issueAddress);
        Call<ApiResult<GetTokenDetailRespDto>> call = tokenService.getTokenDetail(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokenDetailRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokenDetailRespDto>> call, Response<ApiResult<GetTokenDetailRespDto>> response) {
                ApiResult<GetTokenDetailRespDto> respDto = response.body();
                String errorCode = respDto.getErrCode();
                if (errorCode.equals("500004")) {
                    @SuppressLint("StringFormatMatches") String msg = String.format(getString(R.string.error_issue_unregistered_message_txt, assetCode));
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setMessage(msg)
                            .addAction(getString(R.string.i_knew_btn_txt), new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    mSocket.disconnect();
                                    startFragment(new HomeFragment());
                                }
                            }).setCanceledOnTouchOutside(false).create().show();
                    String inexistenceStr = "--";
                    mTokenNameTv.setText(inexistenceStr);
                    mTotalIssueAmountTv.setText(inexistenceStr);
                    mTokenCodeTv.setText(assetCode);
                    mThisTimeIssueAmountTv.setText(CommonUtil.thousandSeparator(issueAmount));
                    mAccumulativeIssueAmountTv.setText(inexistenceStr);
                    mIssueFeeTv.setText(inexistenceStr);
                    errorMsg = msg;
                } else if (!errorCode.equals("0")) {
                    Toast.makeText(getActivity(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                } else {
                    GetTokenDetailRespDto tokenDetail = respDto.getData();

                    actualSupply = tokenDetail.getActualSupply();
                    assetName = tokenDetail.getAssetName();
                    tokenDecimals = tokenDetail.getDecimals();
                    tokenDescription = tokenDetail.getTokenDescription();
                    totalSupply = tokenDetail.getTotalSupply();
                    tokenVersion = tokenDetail.getVersion();

                    mTokenNameTv.setText(assetName);
                    mTokenCodeTv.setText(assetCode);
                    mThisTimeIssueAmountTv.setText(CommonUtil.thousandSeparator(issueAmount));
                    mIssueFeeTv.setText(CommonUtil.addSuffix(Constants.ISSUE_TOKEN_FEE, "BU"));

                    if (totalSupply.equals("0")) {
                        mAccumulativeIssueAmountTv.setText(CommonUtil.thousandSeparator(actualSupply));
                        mIssueTokenInfoLl.removeView(mIssueTokenInfoLl.findViewById(R.id.totalIssueAmountRl));
                        if (!CommonUtil.checkAmount(AmountUtil.amountAddition(actualSupply, issueAmount), tokenDecimals)) {
                            errorMsg = getString(R.string.error_issue_overflow_message_txt);
                        }
                    } else {
                        mTotalIssueAmountTv.setText(CommonUtil.thousandSeparator(totalSupply));
                        mAccumulativeIssueAmountTv.setText(CommonUtil.thousandSeparator(actualSupply));
                        if (new BigDecimal(AmountUtil.amountAddition(actualSupply, issueAmount)).compareTo(new BigDecimal(totalSupply)) == 1) {
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

    private String getAccountBPData() {
        String accountBPData = null;
        if (whetherIdentityWallet) {
            accountBPData = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        } else {
            accountBPData = sharedPreferencesHelper.getSharedPreference(issueAddress + "-BPdata", "").toString();
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
                    if (timerTimes > Constants.TX_REQUEST_TIMEOUT_TIMES) {
                        timerTask.cancel();
                        txSendingTipDialog.dismiss();
                        Bundle argz = new Bundle();
                        argz.putString("txStatus", "timeout");
                        argz.putString("assetName", assetName);
                        argz.putString("assetCode", assetCode);
                        argz.putString("issueAmount", issueAmount);
                        argz.putString("totalSupply", totalSupply);
                        argz.putString("tokenDecimals", tokenDecimals);
                        argz.putString("tokenDescription", tokenDescription);
                        argz.putString("issueAddress", issueAddress);
                        BPIssueTokenStatusFragment bpIssueTokenStatusFragment = new BPIssueTokenStatusFragment();
                        bpIssueTokenStatusFragment.setArguments(argz);
                        startFragmentAndDestroyCurrent(bpIssueTokenStatusFragment);
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> parmasMap = new HashMap<>();
                    parmasMap.put("hash", hash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(parmasMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>() {

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            if (!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())) {
                                return;
                            } else {
                                txDetailRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                Bundle argz = new Bundle();
                                argz.putString("assetName", assetName);
                                argz.putString("assetCode", assetCode);
                                argz.putString("issueAmount", issueAmount);
                                argz.putString("totalSupply", totalSupply);
                                argz.putString("tokenDecimals", tokenDecimals);
                                argz.putString("tokenDescription", tokenDescription);
                                argz.putString("txStatus", txDetailRespBoBean.getStatus().toString());
                                argz.putString("issueAddress", issueAddress);
                                argz.putString("txHash", hash);
                                argz.putString("txFee", txDetailRespBoBean.getFee());
                                argz.putString("errorMsg", txDetailRespBoBean.getErrorMsg());
                                argz.putString("actualSupply", actualSupply);
                                BPIssueTokenStatusFragment bpIssueTokenStatusFragment = new BPIssueTokenStatusFragment();
                                bpIssueTokenStatusFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpIssueTokenStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            timerTask.cancel();
                            txSendingTipDialog.dismiss();
                            Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
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
            if (hash != null && !hash.equals("")) {
                mHanlder.sendEmptyMessage(1);
            }
        }
    };

    private void initTopbar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.issue_token_page_title));
    }
}
