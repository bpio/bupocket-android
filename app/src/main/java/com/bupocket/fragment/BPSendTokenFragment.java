package com.bupocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class BPSendTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.accountAvailableBalanceTv)
    TextView mAccountAvailableBalanceTv;

    private String currentAccAddress;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    @BindView(R.id.destAccountAddressEt)
    EditText destAccountAddressEt;
    @BindView(R.id.sendAmountEt)
    EditText sendAmountET;
    @BindView(R.id.sendFormNoteEt)
    EditText sendFormNoteEt;
    @BindView(R.id.sendFormTxFeeEt)
    EditText sendFormTxFeeEt;
    @BindView(R.id.completeMnemonicCodeBtn)
    QMUIRoundButton mCompleteMnemonicCodeBtn;
    @BindView(R.id.sendFormScanIv)
    ImageView mSendFormScanIv;

    private String buBalance = "-";
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());

        initData();
        confirmSendInfo();
        initTopBar();
        setDestAddress();

        mSendFormScanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        return root;

    }
    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        getAccountBUBalance();

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
    private String getAccountBUBalance(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buBalance = Wallet.getInstance().getAccountBUBalance(currentAccAddress);
                if(buBalance == null){
                    buBalance = "0";
                }
                mAccountAvailableBalanceTv.setText(buBalance);
            }
        },100);

        return buBalance;
    }

    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
    }


    private String getDestAccAddr(){
        return destAccountAddressEt.getText().toString().trim();
    }

    private void confirmSendInfo(){

        mCompleteMnemonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUITipDialog tipDialog;

                String address = destAccountAddressEt.getText().toString().trim();
                Boolean flag = Wallet.getInstance().checkAccAddress(address);
                if(!flag || CommonUtil.isNull(address)){
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

                if(address.equals(currentAccAddress)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.send_err1))
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



                final String sendAmount = sendAmountET.getText().toString().trim();
                if(!CommonUtil.isBU(sendAmount) || CommonUtil.isNull(sendAmount)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.invalid_amount))
                            .create();
                    tipDialog.show();
                    sendAmountET.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if(Double.parseDouble(mAccountAvailableBalanceTv.getText().toString()) < Double.parseDouble(sendAmount)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.balance_not_enough))
                            .create();
                    tipDialog.show();
                    mAccountAvailableBalanceTv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }

                // TODO 最大转出金额




                final String note = sendFormNoteEt.getText().toString();

                if(!CommonUtil.isNull(note) && note.length() > com.bupocket.common.Constants.SEND_TOKEN_NOTE_MAX_LENGTH){
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

                final String txFee = sendFormTxFeeEt.getText().toString();

                if(!CommonUtil.isBU(txFee) || CommonUtil.isNull(txFee)){
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




                final QMUIBottomSheet sheet = new QMUIBottomSheet(getContext());

                sheet.setContentView(R.layout.send_confirm_layout);

                final TextView addressTxt = sheet.findViewById(R.id.sendTargetAddress);
                addressTxt.setText(address);

                TextView amountTxt = sheet.findViewById(R.id.sendAmount);
                amountTxt.setText(sendAmount);

                TextView estimateCostTxt = sheet.findViewById(R.id.sendEstimateCost);
                estimateCostTxt.setText(txFee);

                TextView remarkTxt = sheet.findViewById(R.id.sendRemark);
                remarkTxt.setText(note);
                
                sheet.findViewById(R.id.sendConfirmCloseBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                    }
                });

                sheet.show();

                sheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                        qmuiDialog.setCanceledOnTouchOutside(false);
                        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
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
                                // 检查合法性
                                EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                                final String password = mPasswordConfirmEt.getText().toString().trim();
                                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                        .setTipWord("处理中...")
                                        .create();
                                tipDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        String accountBPData = getAccountBPData();
                                        String destAddess = getDestAccAddr();
                                        try {
                                            Wallet.getInstance().sendBu(password,accountBPData, currentAccAddress, destAddess, sendAmount, note,txFee);
                                            tipDialog.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Looper.prepare();
                                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                            tipDialog.dismiss();
                                            Looper.loop();
                                        }finally {
                                            tipDialog.dismiss();
                                            Bundle argz = new Bundle();
                                            argz.putString("destAccAddr",destAddess);
                                            argz.putString("sendAmount",sendAmount);
                                            argz.putString("txFee",txFee);
                                            argz.putString("note",note);
                                            argz.putString("sendTime","2018-09-15 19:02");
                                            BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();
                                            bpSendStatusFragment.setArguments(argz);
                                            startFragment(bpSendStatusFragment);

                                        }
                                    }
                                }).start();
                                qmuiDialog.dismiss();

                            }
                        });

                    }
                });
            }
        });
    }

    private void setDestAddress(){
        Bundle bundle = getArguments();
        if(bundle != null){
            String destAddress = getArguments().getString("destAddress");
            destAccountAddressEt.setText(destAddress);
        }
    }

    private void startScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt("请将二维码置于取景框内扫描");
        // 开始扫描
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                String destAddress = result.getContents();
                destAccountAddressEt.setText(destAddress);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
}
