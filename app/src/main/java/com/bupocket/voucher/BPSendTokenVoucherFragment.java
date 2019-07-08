package com.bupocket.voucher;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.AddressClickEventEnum;
import com.bupocket.enums.TokenTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.BPAddressBookFragment;
import com.bupocket.fragment.BPSendStatusFragment;
import com.bupocket.fragment.BPTransactionTimeoutFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
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
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPSendTokenVoucherFragment extends AbsBaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.destAccountAddressEt)
    EditText destAccountAddressEt;
    @BindView(R.id.sendAmountEt)
    EditText sendAmountET;
    @BindView(R.id.sendFormNoteEt)
    EditText sendFormNoteEt;
    @BindView(R.id.sendFormTxFeeEt)
    EditText sendFormTxFeeEt;
    @BindView(R.id.completeMnemonicCodeBtn)
    QMUIRoundButton mConfirmSendBtn;
    @BindView(R.id.openAddressBookBtn)
    ImageView mOpenAddressBookBtn;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;

    @BindView(R.id.sendTokenAmountLable)
    TextView mSendTokenAmountLable;
    @BindView(R.id.voucherItemLL)
    LinearLayout voucherItemLL;
    @BindView(R.id.acceptanceIconRiv)
    RoundedImageView acceptanceIconRiv;
    @BindView(R.id.acceptanceNameTv)
    TextView acceptanceNameTv;
    @BindView(R.id.voucherGoodsIv)
    ImageView voucherGoodsIv;
    @BindView(R.id.goodsNameTv)
    TextView goodsNameTv;
    @BindView(R.id.goodsPriceTv)
    TextView goodsPriceTv;
    @BindView(R.id.goodsNumTv)
    TextView goodsNumTv;
    @BindView(R.id.selectedStatusIv)
    ImageView selectedStatusIv;
    @BindView(R.id.goodsDateTv)
    TextView goodsDateTv;
    @BindView(R.id.sendFormScanIv)
    ImageView sendFormScanIv;
    @BindView(R.id.voucherNumHint)
    TextView voucherNumHint;


    public final static String SEND_TOKEN_STATUS = "sendTokenStatus";;
    private static final int CHOOSE_ADDRESS_CODE = 1;

    private String hash;
    private String availableTokenBalance;
    private QMUITipDialog txSendingTipDialog;
    private Boolean whetherIdentityWallet = false;
    protected SharedPreferencesHelper sharedPreferencesHelper;

    private TxDetailRespDto.TxDeatilRespBoBean txDeatilRespBoBean;
    private long nonce;
    private VoucherDetailModel selectedVoucherDetail;
    private String toAddress;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_send_voucher;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());

        initData();
        confirmSendInfo();
        initTopBar();
        setDestAddress();

        buildWatcher();
        voucherNumHint.setVisibility(View.GONE);
    }

    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mConfirmSendBtn.setEnabled(false);
                mConfirmSendBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mConfirmSendBtn.setEnabled(false);
                mConfirmSendBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {
                boolean signAccountAddress = destAccountAddressEt.getText().toString().trim().length() > 0;
                boolean signAmount = sendAmountET.getText().toString().trim().length() > 0;
                boolean signTxFee = sendFormTxFeeEt.getText().toString().trim().length() > 0;
                if (signAccountAddress && signAmount && signTxFee) {
                    mConfirmSendBtn.setEnabled(true);
                    mConfirmSendBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mConfirmSendBtn.setEnabled(false);
                    mConfirmSendBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };

        destAccountAddressEt.addTextChangedListener(watcher);
        sendAmountET.addTextChangedListener(watcher);
        sendFormTxFeeEt.addTextChangedListener(watcher);

        TextWatcher addressEtWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        };
        destAccountAddressEt.addTextChangedListener(addressEtWatcher);
    }

    public void initData() {

        if (getArguments()!=null) {
            toAddress = getArguments().getString(ConstantsType.ADDRESS);
            destAccountAddressEt.setText(toAddress);
        }

        mTokenCodeTv.setText(Html.fromHtml(String.format(mContext.getString(R.string.voucher_avail_balance),0+"")));
    }

    @Override
    protected void setListeners() {
        voucherItemLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goVoucherFragment();

            }
        });

        sendFormScanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });


    }

    private void goVoucherFragment() {
        BPVoucherHomeFragment fragment = new BPVoucherHomeFragment();
        Bundle args = new Bundle();
        args.putString(ConstantsType.FRAGMENT_TAG, BPSendTokenVoucherFragment.class.getSimpleName());
        if (selectedVoucherDetail != null) {
            args.putSerializable("selectedVoucherDetail", selectedVoucherDetail);
        }
        fragment.setArguments(args);
        startFragment(fragment);

        fragment.setSelectedVoucherListener(new BPVoucherHomeFragment.SelectedVoucherListener() {
            @Override
            public void getSelectedDetail(VoucherDetailModel voucherDetailModel) {
                selectedVoucherDetail = voucherDetailModel;

                initItemVoucherView(selectedVoucherDetail);
            }
        });
    }

    private void initItemVoucherView(VoucherDetailModel detailModel) {
        VoucherDetailModel.VoucherAcceptanceBean voucherAcceptance = detailModel.getVoucherAcceptance();
        if (voucherAcceptance != null) {
            String icon = voucherAcceptance.getIcon();
            if (!TextUtils.isEmpty(icon)) {
                Glide.with(mContext)
                        .load(icon)
                        .into(acceptanceIconRiv);
            }
            String name = voucherAcceptance.getName();
            if (!TextUtils.isEmpty(name)) {
                acceptanceNameTv.setText(name);
            }

        }
        String voucherIcon = detailModel.getVoucherIcon();
        if (!TextUtils.isEmpty(voucherIcon)) {
            Glide.with(mContext)
                    .load(voucherIcon)
                    .into(voucherGoodsIv);
        }


        String faceValue = detailModel.getFaceValue();
        if (!TextUtils.isEmpty(faceValue)) {
            goodsPriceTv.setText(getString(R.string.goods_price) + faceValue);
        }


        String startTime = detailModel.getStartTime();
        String endTime = detailModel.getEndTime();
        String date = mContext.getString(R.string.validity_date);
        if (!TextUtils.isEmpty(startTime)) {
            date = date + ": " +
                    String.format(mContext.getString(R.string.goods_validity_date),
                            TimeUtil.timeStamp2Date(startTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD),
                            TimeUtil.timeStamp2Date(endTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD));

        }

        goodsDateTv.setText(date);

        mTokenCodeTv.setText(Html.fromHtml(String.format(mContext.getString(R.string.voucher_avail_balance),detailModel.getBalance()+"")));
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }




    private String getDestAccAddr() {
        return destAccountAddressEt.getText().toString().trim();
    }

    private void confirmSendInfo() {

        mConfirmSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUITipDialog tipDialog;

                String address = destAccountAddressEt.getText().toString().trim();
                Boolean flag = Wallet.getInstance().checkAccAddress(address);
                if (!flag || CommonUtil.isNull(address)) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.invalid_address))
                            .create();
                    tipDialog.show();
                    destAccountAddressEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);

                    return;
                }


                String sendAmountInput = sendAmountET.getText().toString().trim();
                final String sendAmount = CommonUtil.rvZeroAndDot(sendAmountET.getText().toString().trim());

                final String note = sendFormNoteEt.getText().toString();

                if (!CommonUtil.isNull(note) && note.length() > Constants.SEND_TOKEN_NOTE_MAX_LENGTH) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.send_token_note_too_long))
                            .create();
                    tipDialog.show();
                    sendFormNoteEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }

                final String txFee = CommonUtil.rvZeroAndDot(sendFormTxFeeEt.getText().toString());

                if (!CommonUtil.isBU(txFee) || CommonUtil.isNull(txFee) || CommonUtil.checkSendAmountDecimals(txFee, Constants.BU_DECIMAL.toString())) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.invalid_tx_fee))
                            .create();
                    tipDialog.show();
                    sendFormTxFeeEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if (Double.parseDouble(txFee) > Constants.MAX_FEE) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.tx_fee_too_big))
                            .create();
                    tipDialog.show();
                    sendFormTxFeeEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }

                showConfirmDialog();


            }
        });
    }

    private void showConfirmDialog() {
        TransferUtils.confirmSendVoucherDailog(mContext, getWalletAddress(), selectedVoucherDetail.getContractAddress(),
                "", 10+"", Constants.MIN_FEE,
                getString(R.string.send_voucher), "", new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {

                    }
                });
    }

    private void setDestAddress() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            final String destAddress = getArguments().getString("destAddress");
            destAccountAddressEt.setText(destAddress);

//            if (!TokenTypeEnum.BU.getCode().equals(tokenType)) {
//                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        Bundle data = msg.getData();
//                        String fee = data.getString("fee");
//                        sendFormTxFeeEt.setText(fee);
//                    }
//                };
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        String fee;
//                        if (Wallet.getInstance().checkAccAddress(destAddress)) {
//                            if (!Wallet.getInstance().checkAccountActivated(destAddress)) {
//                                fee = Constants.ACCOUNT_NOT_ACTIVATED_SEND_FEE;
//                            } else {
//                                fee = Constants.ACCOUNT_ACTIVATED_SEND_FEE;
//                            }
//                        } else {
//                            fee = Constants.ACCOUNT_ACTIVATED_SEND_FEE;
//                        }
//                        Message message = new Message();
//                        Bundle data = new Bundle();
//                        data.putString("fee", fee);
//                        message.setData(data);
//                        handler.sendMessage(message);
//                    }
//                };
//                new Thread(runnable).start();
//            }

        }
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
            } else {
                String destAddress = result.getContents();
                destAccountAddressEt.setText(destAddress);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_ADDRESS_CODE: {
                if (resultCode == RESULT_OK) {
                    if (null != data) {
                        String destAddress = data.getStringExtra("destAddress");
                        destAccountAddressEt.setText(destAddress);
                    }
                }
                break;
            }
        }
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

                        BPTransactionTimeoutFragment fragment = new BPTransactionTimeoutFragment();
                        Bundle args = new Bundle();
                        args.putString("txHash", hash);
                        fragment.setArguments(args);
                        startFragment(fragment);
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash", hash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(paramsMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>() {

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();

                            if (resp == null || resp.getErrCode() == null ||
                                    resp.getData() == null || resp.getData().getTxDeatilRespBo() == null ||
                                    !TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())

                            ) {
                                return;
                            } else {
                                txDeatilRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                if (ExceptionEnum.BU_NOT_ENOUGH_FOR_PAYMENT.getCode().equals(txDeatilRespBoBean.getErrorCode())) {
                                    Toast.makeText(getActivity(), R.string.balance_not_enough, Toast.LENGTH_SHORT).show();
                                }
                                Bundle argz = new Bundle();
                                argz.putString("destAccAddr", txDeatilRespBoBean.getDestAddress());
                                argz.putString("sendAmount", txDeatilRespBoBean.getAmount());
                                argz.putString("txFee", txDeatilRespBoBean.getFee());
                                argz.putString("note", txDeatilRespBoBean.getOriginalMetadata());
                                argz.putString("state", txDeatilRespBoBean.getStatus().toString());
                                argz.putString("sendTime", txDeatilRespBoBean.getApplyTimeDate());
                                argz.putString("sendTokenStatusKey", SEND_TOKEN_STATUS);
                                argz.putString("txHash", hash);
                                BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();
                                bpSendStatusFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpSendStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {

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


}
