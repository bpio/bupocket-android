package com.bupocket.voucher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.AddressClickEventEnum;

import com.bupocket.enums.VoucherStatusEnum;
import com.bupocket.fragment.BPAddressBookFragment;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.model.CallVoucherBalanceModel;
import com.bupocket.model.EventBus.SendVoucherMessage;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.voucher.model.VoucherPackageDetailModel;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import butterknife.BindView;
import io.bumo.model.response.TransactionBuildBlobResponse;
import io.bumo.model.response.result.ContractCallResult;

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
    @BindView(R.id.firstAddVoucherFl)
    FrameLayout firstAddVoucherFl;


    private static final int CHOOSE_ADDRESS_CODE = 1;

    private String toAddress;
    final double minFee = Constants.MAX_FEE;
    private boolean isVoucherDetailFragment;
    private String fragmentTag;
    private String available = "";
    private VoucherDetailModel voucherDetailModel;

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
        mTokenCodeTv.setText(Html.fromHtml(String.format(mContext.getString(R.string.voucher_avail_balance), 0 + "")));
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
        this.voucherDetailModel=detailModel;
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
        String date = mContext.getString(R.string.validity_date);
        if (!TextUtils.isEmpty(startTime)) {
            date = date + ": " +
                    String.format(mContext.getString(R.string.goods_validity_date),
                            TimeUtil.timeStamp2Date(startTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD),
                            TimeUtil.timeStamp2Date(endTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD));

        }
        goodsDateTv.setText(date);
        mTokenCodeTv.setText(Html.fromHtml(String.format(mContext.getString(R.string.voucher_avail_balance), detailModel.getBalance() + "")));

        callVoucherBalance();

    }

    private void callVoucherBalance() {

        if (voucherDetailModel == null) {
            return;
        }

        new Thread(new Runnable() {
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
                               mTokenCodeTv.setText(Html.fromHtml(String.format(mContext.getString(R.string.voucher_avail_balance), available)));

                           }
                       });

                    } catch (Exception e) {

                    }

                }
            }
        }).start();
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

                if (!TextUtils.isEmpty(available)) {
                    int available = Integer.parseInt(BPSendTokenVoucherFragment.this.available);
                    if (available == 0) {
                        ToastUtil.showToast(getActivity(), R.string.send_voucher_num_limit, Toast.LENGTH_LONG);
                        return;
                    }
                }

                if (destAccountAddressEt.getText().toString().trim().equals(WalletCurrentUtils.getWalletAddress(spHelper))) {
                    ToastUtil.showToast(getActivity(), R.string.not_my_self_send_voucher, Toast.LENGTH_LONG);
                    return;
                }

                showConfirmDialog();


            }
        });
    }

    private void showConfirmDialog() {

        final String transferDetail = sendFormNoteEt.getText().toString().trim();

        String toAddress = destAccountAddressEt.getText().toString().trim();


        final String contractAddress = voucherDetailModel.getContractAddress();
        final String amount = sendAmountET.getText().toString();

        final String input = "{\"method\":\"transfer\",\"params\":{\"skuId\":\"" + voucherDetailModel.getVoucherId() + "\",\"trancheId\":\"" + voucherDetailModel.getTrancheId() + "\",\"to\":\"" + toAddress + "\",\"value\":\"" + amount + "\"}}";
        TransferUtils.confirmSendVoucherDialog(mContext, getWalletAddress(), toAddress,
                "", amount, minFee,
                transferDetail, input, sendFormNoteEt.getText().toString(), new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    final TransactionBuildBlobResponse buildBlobResponse;

                                    buildBlobResponse = Wallet.getInstance().buildBlob(amount,
                                            input, WalletCurrentUtils.getWalletAddress(spHelper),
                                            String.valueOf(minFee), contractAddress, transferDetail);

                                    DialogUtils.getSignatureInfo(getActivity(), mContext, getBPAccountData(), getWalletAddress(), new SignatureListener() {
                                        @Override
                                        public void success(String privateKey) {
                                            submitTransactionBase(privateKey, buildBlobResponse, fragmentTag);
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
