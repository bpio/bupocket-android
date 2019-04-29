package com.bupocket.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.BPSendStatusFragment;
import com.bupocket.fragment.BPTransactionTimeoutFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseFragment extends QMUIFragment {

    private final static String BP_FILE_NAME = "buPocket";
    public SharedPreferencesHelper spHelper;
    public Context mContext;
    private QMUITipDialog txSendingTipDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        if (spHelper == null) {
            spHelper = new SharedPreferencesHelper(getContext(), BP_FILE_NAME);
        }

    }


    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }


    @Override
    public void onResume() {
        super.onResume();

//        BPUpgradeManager.getInstance(getContext()).runUpgradeTipTaskIfExist(getActivity());
    }


    public String getWalletAddress() {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return spHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        return currentIdentityWalletAddress;
    }

    /**
     * @return false currentAccAddr  true currentWalletAddress
     */
    private boolean isCurrentAddress() {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return false;
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (currentAccAddr.equals(currentIdentityWalletAddress)) {
            return false;
        }
        return true;
    }


    public String getBPAccountData() {
        String accountBPData = spHelper.getSharedPreference("BPData", "").toString();
        if (isCurrentAddress()) {
            return spHelper.getSharedPreference(spHelper.getSharedPreference("currentWalletAddress", "").toString() + "-BPdata", "").toString();
        }
        return accountBPData;
    }



    protected void submitTransactionBase(final TransactionBuildBlobResponse transBlob) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.view_password_comfirm);

                qmuiDialog.show();
                LogUtils.e("Thread==="+Thread.currentThread().getName());

                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                final EditText tvPw = qmuiDialog.findViewById(R.id.passwordConfirmEt);

                mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();
                    }
                });

                tvPw.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSoftInputFromWindow(tvPw);
                    }
                }, 10);

                mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();

                        txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                                .create();
                        txSendingTipDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                txHash = submitTransaction(tvPw.getText().toString(), transBlob);
                                if (TextUtils.isEmpty(txHash)) {
                                    txSendingTipDialog.dismiss();
                                    return;
                                }

                                LogUtils.e("submit hash="+txHash);
                                ByHashQueryResult(txHash);

                            }
                        }).start();

                    }
                });

            }
        });

    }




    private String submitTransaction(final String password, final TransactionBuildBlobResponse buildBlobResponse) {

        String hash = "";
        try {
            hash = Wallet.getInstance().submitTransaction(password, getBPAccountData(), getWalletAddress(), buildBlobResponse);

        } catch (WalletException e) {
            e.printStackTrace();
            if (com.bupocket.wallet.enums.ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())) {
                ToastUtil.showToast(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT);
            } else if (com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())) {
                ToastUtil.showToast(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT);
            }else if (ExceptionEnum.PASSWORD_ERROR.getCode().equals(e.getErrCode())){
                ToastUtil.showToast(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT);
            } else {
                ToastUtil.showToast(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT);
        } finally {

        }

        return hash;
    }
    private TimerTask timerTask=null;
    private String txHash;
    private int timerTimes = 0;
    private final Timer timer = new Timer();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (timerTimes > Constants.TX_REQUEST_TIMEOUT_TIMES) {
                        timerTask.cancel();
                        if (txSendingTipDialog!=null) {
                            txSendingTipDialog.dismiss();
                        }
                        BPTransactionTimeoutFragment fragment = new BPTransactionTimeoutFragment();
                        Bundle args = new Bundle();
                        args.putString("txHash",txHash);
                        fragment.setArguments(args);
                        startFragment(fragment);
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash", txHash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(paramsMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>() {

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            if (!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())) {
                                return;
                            } else {
                                TxDetailRespDto.TxDeatilRespBoBean txDetailRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                if (txSendingTipDialog!=null) {
                                    txSendingTipDialog.dismiss();
                                }
                                if (com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH_FOR_PAYMENT.getCode().equals(txDetailRespBoBean.getErrorCode())) {
                                    Toast.makeText(getActivity(), R.string.balance_not_enough, Toast.LENGTH_SHORT).show();
                                }
                                Bundle argz = new Bundle();
                                argz.putString("destAccAddr", txDetailRespBoBean.getDestAddress());
                                argz.putString("sendAmount", txDetailRespBoBean.getAmount());
                                argz.putString("txFee", txDetailRespBoBean.getFee());
                                argz.putString("tokenCode", "BU");
                                argz.putString("note", txDetailRespBoBean.getOriginalMetadata());
                                argz.putString("state", txDetailRespBoBean.getStatus().toString());
                                argz.putString("sendTime", txDetailRespBoBean.getApplyTimeDate());
                                argz.putString("txHash",txHash);
                                BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();
                                bpSendStatusFragment.setArguments(argz);
                                startFragment(bpSendStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            if (txSendingTipDialog!=null) {
                                txSendingTipDialog.dismiss();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };




    public void ByHashQueryResult(@NonNull String hash){
        txHash=hash;
        if (timerTask!=null) {
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
            }
        };
        timer.schedule(timerTask,
                1 * 1000,//延迟1秒执行
                1000);
    }


    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }


}
