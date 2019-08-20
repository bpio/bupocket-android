package com.bupocket.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.BPSendStatusFragment;
import com.bupocket.fragment.BPTransactionTimeoutFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.voucher.BPSendVoucherStatusFragment;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseTransferFragment extends AbsBaseFragment {


    private TransferHandler transferHandler;

    private static String fragmentTag;
    private static String toAddress;
    private static String voucherAmount;
    private QMUITipDialog submitDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (transferHandler == null) {
            transferHandler = new TransferHandler(this);
        }

    }



    protected void getSignatureInfo(final SignatureListener listener) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                DialogUtils.showPassWordInputDialog(getActivity(), new DialogUtils.ConfirmListener() {
                    @Override
                    public void confirm(final  String password) {
                        final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_sign_txt))
                                .create(false);
                        txSendingTipDialog.show();

                        Runnable privateKeyRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String pkbyAccountPassword = null;
                                try {
                                    pkbyAccountPassword = Wallet.getInstance().getPKBYAccountPassword(password, getBPAccountData(), getWalletAddress());
                                    txSendingTipDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.showToast(getActivity(), R.string.checking_password_error, Toast.LENGTH_LONG);
                                    txSendingTipDialog.dismiss();
                                }

                                if (pkbyAccountPassword == null || pkbyAccountPassword.isEmpty()) {
                                    return;
                                }

                                final String finalPkbyAccountPassword = pkbyAccountPassword;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.success(finalPkbyAccountPassword);
                                    }
                                });


                            }
                        };

                        ThreadManager.getInstance().execute(privateKeyRunnable);

                    }
                });

            }
        });
    }

    protected void submitTransactionBase(final String privateKey, final TransactionBuildBlobResponse transBlob, String fragmentTag, String toAddress, String amount) {
        this.toAddress = toAddress;
        this.fragmentTag = fragmentTag;
        this.voucherAmount = amount;
        submitTransactionBase(privateKey,transBlob);
    }

    protected void submitTransactionBase(final String privateKey, final TransactionBuildBlobResponse transBlob) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create(false);
                submitDialog.show();
            }
        });
        Runnable submitRunnable = new Runnable() {
            @Override
            public void run() {
                txHash = submitTransaction(privateKey, transBlob);
                if (TextUtils.isEmpty(txHash)) {
                    submitDialog.dismiss();
                    return;
                }
                ByHashQueryResult(txHash);

            }
        };

        ThreadManager.getInstance().execute(submitRunnable);
    }




    private String submitTransaction(final String privateKey, final TransactionBuildBlobResponse buildBlobResponse) {

        String hash = "";
        try {
            hash = Wallet.getInstance().submitTransaction(privateKey, buildBlobResponse);

        } catch (WalletException e) {
            e.printStackTrace();
            if (com.bupocket.wallet.enums.ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())) {
                ToastUtil.showToast(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT);
            } else if (com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())) {
                ToastUtil.showToast(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT);
            } else if (ExceptionEnum.PASSWORD_ERROR.getCode().equals(e.getErrCode())) {
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


    private static TimerTask timerTask = null;
    private String txHash;
    private  int timerTimes = 0;
    private static final Timer timer = new Timer();

    private void ByHashQueryResult(@NonNull String hash) {
        txHash = hash;
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                transferHandler.sendEmptyMessage(1);
            }
        };
        timer.schedule(timerTask,
                1 * 1000,
                1000);
    }



    private static class TransferHandler extends Handler {



        private final WeakReference<BaseTransferFragment> mFragment;

        private TransferHandler(BaseTransferFragment mFragment) {
            this.mFragment = new WeakReference<BaseTransferFragment>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BaseTransferFragment mFragment = this.mFragment.get();
            if (mFragment == null) {
                return;
            }

            switch (msg.what) {
                case 1:
                    if (mFragment.timerTimes > Constants.TX_REQUEST_TIMEOUT_TIMES) {
                        mFragment.timerTask.cancel();
                        if (mFragment.submitDialog != null) {
                            mFragment.submitDialog.dismiss();
                        }
                        BPTransactionTimeoutFragment fragment = new BPTransactionTimeoutFragment();
                        Bundle args = new Bundle();
                        args.putString("txHash", mFragment.txHash);
                        fragment.setArguments(args);
                        mFragment.startFragment(fragment);
                        return;
                    }
                    mFragment.timerTimes++;
                    System.out.println("timerTimes:" + mFragment.timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash", mFragment.txHash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(paramsMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>() {

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            if (!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())) {
                                return;
                            } else {
                                TxDetailRespDto.TxDetailRespBoBean txDetailRespBoBean = resp.getData().getTxDeatilRespBo();
                                mFragment.timerTask.cancel();

                                if (com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH_FOR_PAYMENT.getCode().equals(txDetailRespBoBean.getErrorCode())) {
                                    Toast.makeText(mFragment.getActivity(), R.string.balance_not_enough, Toast.LENGTH_SHORT).show();
                                }
                                Bundle argz = new Bundle();
                                argz.putString("destAccAddr", txDetailRespBoBean.getDestAddress());
                                argz.putString("sendAmount", txDetailRespBoBean.getAmount());
                                argz.putString("txFee", txDetailRespBoBean.getFee());
                                argz.putString("tokenCode", "BU");
                                argz.putString("note", txDetailRespBoBean.getOriginalMetadata());
                                argz.putString("state", txDetailRespBoBean.getStatus().toString());
                                argz.putString("sendTime", txDetailRespBoBean.getApplyTimeDate());
                                argz.putString("txHash", mFragment.txHash);

                                BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();

                                if (mFragment.submitDialog != null) {
                                    mFragment.submitDialog.dismiss();
                                }
                                if (!TextUtils.isEmpty(fragmentTag)) {
                                    argz.putString(ConstantsType.FRAGMENT_TAG, fragmentTag);
                                    argz.putString("destAccAddr",toAddress);
                                    argz.putString("sendAmount", voucherAmount);
                                    BPSendVoucherStatusFragment bpSendTokenVoucherFragment = new BPSendVoucherStatusFragment();
                                    bpSendTokenVoucherFragment.setArguments(argz);
                                    fragmentTag="";
                                    mFragment.startFragment(bpSendTokenVoucherFragment);
                                    return;
                                }

                                if (mFragment.getActivity()==null) {
                                    return;
                                }

                                if (((BaseFragmentActivity) mFragment.getActivity()).getCurrentFragment().getTag().equals(HomeFragment.class.getSimpleName())) {
                                    argz.putString("fragmentTag", HomeFragment.class.getSimpleName());
                                    bpSendStatusFragment.setArguments(argz);
                                    mFragment.startFragmentForResult(bpSendStatusFragment, TRANSFER_CODE);
                                } else {
                                    bpSendStatusFragment.setArguments(argz);
                                    mFragment.startFragment(bpSendStatusFragment);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            if (mFragment.submitDialog != null) {
                                mFragment.submitDialog.dismiss();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        transferHandler.removeCallbacksAndMessages(null);
    }


}
