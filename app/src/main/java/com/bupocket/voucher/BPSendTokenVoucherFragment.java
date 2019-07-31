package com.bupocket.voucher;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.AddressClickEventEnum;

import com.bupocket.enums.VoucherStatusEnum;
import com.bupocket.fragment.BPAddressBookFragment;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.model.CallVoucherBalanceModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.NetworkUtils;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.utils.WalletUtils;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import io.bumo.model.response.TransactionBuildBlobResponse;
import io.bumo.model.response.result.ContractCallResult;

public class BPSendTokenVoucherFragment extends BaseTransferFragment {
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

    @BindView(R.id.sendTokenAmountLabel)
    TextView mSendTokenAmountLabel;
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
    @BindView(R.id.firstAddVoucherFl)
    FrameLayout firstAddVoucherFl;



    private static final int CHOOSE_ADDRESS_CODE = 1;

    private String toAddress;
    private String minFee = Constants.VOUCHER_MIN_FEE + "";
    private boolean isVoucherDetailFragment;
    private String fragmentTag;
    private String available = "";
    private VoucherDetailModel voucherDetailModel;
    private QMUITipDialog submitDialog;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_send_voucher;
    }

    @Override
    protected void initView() {

        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());

        confirmSendInfo();
        initTopBar();
        setDestAddress();

        buildWatcher();
        voucherNumHint.setVisibility(View.GONE);
        sendFormTxFeeEt.setText(minFee + "");
    }


    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mConfirmSendBtn.setEnabled(false);
                mConfirmSendBtn.setBackgroundColor(getResources().getColor(R.color.app_color_green_disabled));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mConfirmSendBtn.setEnabled(false);
                mConfirmSendBtn.setBackgroundColor(getResources().getColor(R.color.app_color_green_disabled));
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {
                boolean signAccountAddress = destAccountAddressEt.getText().toString().trim().length() > 0;
                String amount = sendAmountET.getText().toString().trim();
                boolean signAmount = amount.length() > 0;
                boolean signTxFee = sendFormTxFeeEt.getText().toString().trim().length() > 0;
                if (signAccountAddress && signAmount && signTxFee && Double.parseDouble(amount) > 0) {
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
        if (getArguments() != null) {

            fragmentTag = getArguments().getString(ConstantsType.FRAGMENT_TAG);
            isVoucherDetailFragment = fragmentTag.equals(VoucherStatusEnum.VOUCHER_HOME_FRAGMENT.getCode());
            if (fragmentTag.equals(VoucherStatusEnum.ASSETS_HOME_FRAGMENT.getCode())) {
                toAddress = getArguments().getString(ConstantsType.ADDRESS);
                destAccountAddressEt.setText(toAddress);
            }

            if (isVoucherDetailFragment) {
                initItemVoucherView(voucherDetailModel);
            }

        }


    }

    @Override
    protected void setListeners() {
        voucherItemLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goVoucherFragment();

            }
        });

        firstAddVoucherFl.setOnClickListener(new View.OnClickListener() {
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

        mOpenAddressBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goAddressFragment();

            }
        });

    }

    private void goAddressFragment() {
        BPAddressBookFragment fragment = new BPAddressBookFragment();
        Bundle args = new Bundle();
        args.putString("flag", AddressClickEventEnum.VOUCHER.getCode());
        fragment.setArguments(args);
        startFragmentForResult(fragment, CHOOSE_ADDRESS_CODE);
    }

    private void goVoucherFragment() {
        BPVoucherHomeFragment fragment = new BPVoucherHomeFragment();
        Bundle args = new Bundle();
        args.putString(ConstantsType.FRAGMENT_TAG, BPSendTokenVoucherFragment.class.getSimpleName());
        if (voucherDetailModel != null) {
            args.putSerializable("selectedVoucherDetail", voucherDetailModel);
        }
        fragment.setArguments(args);
        startFragment(fragment);

        fragment.setSelectedVoucherListener(new BPVoucherHomeFragment.SelectedVoucherListener() {
            @Override
            public void getSelectedDetail(VoucherDetailModel voucherDetailModel) {
                initItemVoucherView(voucherDetailModel);
            }
        });
    }

    private void initItemVoucherView(VoucherDetailModel detailModel) {
        if (detailModel == null) {
            return;
        }
        this.voucherDetailModel = detailModel;
        VoucherDetailModel.VoucherAcceptanceBean voucherAcceptance = detailModel.getVoucherAcceptance();
        if (voucherAcceptance != null) {

            firstAddVoucherFl.setVisibility(View.GONE);
            voucherItemLL.setVisibility(View.VISIBLE);

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
        String voucherName = detailModel.getVoucherName();
        if (!TextUtils.isEmpty(voucherName)) {
            goodsNameTv.setText(voucherName);
        }


        String startTime = detailModel.getStartTime();
        String endTime = detailModel.getEndTime();
        String date = mContext.getString(R.string.validity_date) + ": ";
        date = date + WalletCurrentUtils.voucherDate(startTime, endTime, mContext);
        goodsDateTv.setText(date);
        mTokenCodeTv.setText(detailModel.getBalance());
        available = detailModel.getBalance();
        callVoucherBalance();

    }

    private void callVoucherBalance() {

        if (voucherDetailModel == null) {
            return;
        }

        Runnable balanceRunnable = new Runnable() {
            @Override
            public void run() {
                JSONObject input = new JSONObject();
                input.put("method", "balanceOf");
                JSONObject params = new JSONObject();
                params.put("skuId", voucherDetailModel.getVoucherId());
                params.put("address", WalletCurrentUtils.getWalletAddress(spHelper));
                input.put("params", params);
                ContractCallResult contractCallResult = Wallet.getInstance().
                        callContract(voucherDetailModel.getContractAddress(), input.toJSONString(), Constants.MAX_FEE);
                if (contractCallResult != null) {

                    try {
                        JSONArray queryRets = contractCallResult.getQueryRets();
                        JSONObject resultOne = (JSONObject) queryRets.get(0);
                        LogUtils.e("Call" + resultOne.toJSONString());
                        String json = resultOne.toJSONString();
                        json = CommonUtil.string2Json(json);

                        final CallVoucherBalanceModel callVoucherBalanceMode = new Gson().fromJson(json, CallVoucherBalanceModel.class);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                available = callVoucherBalanceMode.getResult().getValue().getAvailable();
                                mTokenCodeTv.setText(available);

                            }
                        });

                    } catch (Exception e) {

                    }

                }
            }
        };
        ThreadManager.getInstance().execute(balanceRunnable);
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(R.string.send_voucher_title1);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    private void confirmSendInfo() {

        mConfirmSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSendVoucher();

            }
        });
    }

    private void startSendVoucher() {
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

        String sendNumStr = sendAmountET.getText().toString().trim();

        if (!CommonUtil.isPureDigital(sendNumStr)) {
            ToastUtil.showToast(getActivity(), R.string.no_valid_send_voucher, Toast.LENGTH_LONG);
            return;
        }

        int sendNum = Integer.parseInt(sendNumStr);
        if (available.isEmpty()) {
            ToastUtil.showToast(getActivity(), R.string.send_voucher_num_limit, Toast.LENGTH_LONG);
            return;
        }

        int availableNum = Integer.parseInt(BPSendTokenVoucherFragment.this.available);
        if (TextUtils.isEmpty(available) || availableNum == 0 || (sendNum > availableNum)) {
            ToastUtil.showToast(getActivity(), R.string.send_voucher_num_limit, Toast.LENGTH_LONG);
            return;
        }
        minFee = sendFormTxFeeEt.getText().toString().trim();
        String tokenBalance = spHelper.getSharedPreference(getWalletAddress() + "tokenBalance", "0").toString();
        if (Double.parseDouble(tokenBalance) < Double.parseDouble(minFee)) {
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.balance_not_enough))
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

        if (destAccountAddressEt.getText().toString().trim().equals(WalletCurrentUtils.getWalletAddress(spHelper))) {
            ToastUtil.showToast(getActivity(), R.string.not_my_self_send_voucher, Toast.LENGTH_LONG);
            return;
        }

        showConfirmDialog();
    }

    private void showConfirmDialog() {

        final String transferDetail = getString(R.string.send_voucher);

        final String toAddress = destAccountAddressEt.getText().toString().trim();


        final String contractAddress = voucherDetailModel.getContractAddress();
        final String amount = sendAmountET.getText().toString();

        final String input = "{\"method\":\"transfer\",\"params\":{\"skuId\":\"" + voucherDetailModel.getVoucherId() + "\",\"trancheId\":\"" + voucherDetailModel.getTrancheId() + "\",\"to\":\"" + toAddress + "\",\"value\":\"" + amount + "\"}}";
        final String remark = sendFormNoteEt.getText().toString();
        TransferUtils.confirmSendVoucherDialog(mContext, getWalletAddress(), toAddress,
                "", amount, minFee,
                transferDetail, input, remark, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {

                        if (!NetworkUtils.isNetWorkAvailable(getActivity())) {
                            ToastUtil.showToast(getActivity(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT);
                            return;
                        }

                        Runnable buildBlobRunnable = new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    final TransactionBuildBlobResponse transactionBuildBlobResponse = Wallet.getInstance().buildBlob("0",
                                            input, WalletCurrentUtils.getWalletAddress(spHelper),
                                            minFee, contractAddress, remark);

//
                                    DialogUtils.getSignatureInfo(getActivity(), mContext, getBPAccountData(), getWalletAddress(), new SignatureListener() {
                                        @Override
                                        public void success(final String privateKey) {

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
                                            WalletUtils.checkToAddressValidateAndOpenAccount
                                                    (privateKey, getBPAccountData(), getWalletAddress(),
                                                            toAddress, 0.02 + "",
                                                            Constants.MIN_FEE + "", new WalletUtils.ReqListener() {
                                                                @Override
                                                                public void success(final int status, final long nonce) {

                                                                    Runnable buildBlobRunnable = new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (status == 0) {
                                                                                try {

                                                                                    final TransactionBuildBlobResponse transactionBuildBlobResponse = Wallet.getInstance().buildBlob("0",
                                                                                            input, WalletCurrentUtils.getWalletAddress(spHelper),
                                                                                            minFee, contractAddress, remark, nonce + 1);
//
                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            submitDialog.dismiss();
                                                                                        }
                                                                                    });
                                                                                    LogUtils.e("bbbb0000" + Thread.currentThread().getName());
                                                                                    submitTransactionBase(privateKey, transactionBuildBlobResponse, fragmentTag, toAddress, amount);

                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            } else if (status == 1) {


                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        submitDialog.dismiss();
                                                                                    }
                                                                                });

                                                                                LogUtils.e("bbbb11111" + Thread.currentThread().getName());
                                                                                submitTransactionBase(privateKey, transactionBuildBlobResponse, fragmentTag, toAddress, amount);
                                                                            }

//
                                                                        }
                                                                    };
                                                                    ThreadManager.getInstance().execute(buildBlobRunnable);

                                                                }

                                                                @Override
                                                                public void failed() {

                                                                }
                                                            });
                                        }
                                    });
//
//
                                } catch (WalletException e) {
                                    if (e.getErrCode().equals(ExceptionEnum.ADDRESS_NOT_EXIST.getCode())) {
                                        ToastUtil.showToast(getActivity(), getString(R.string.address_not_exist), Toast.LENGTH_SHORT);
                                    }
                                } catch (Exception e) {


                                }
                            }
                        };

                        ThreadManager.getInstance().execute(buildBlobRunnable);

                    }
                });
    }


    private void setDestAddress() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            final String destAddress = getArguments().getString("destAddress");
            destAccountAddressEt.setText(destAddress);

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
                destAddress = destAddress.replace(Constants.VOUCHER_QRCODE, "");
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
                        destAddress = destAddress.replace(Constants.VOUCHER_QRCODE, "");
                        destAccountAddressEt.setText(destAddress);
                    }
                }
                break;
            }
        }
    }


    public void setDetailVoucher(VoucherDetailModel voucherDetailModel) {
        this.voucherDetailModel = voucherDetailModel;
    }


}
