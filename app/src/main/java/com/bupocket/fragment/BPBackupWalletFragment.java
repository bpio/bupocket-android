package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BPBackupWalletFragment extends AbsBaseFragment {
    @BindView(R.id.backupWalletBtn)
    QMUIRoundButton mBackupWalletBtn;
    @BindView(R.id.skipBackupBtn)
    QMUIRoundButton mSkipBackupBtn;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    private List<String> mnemonicCodeList;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private long exitTime = 0;
    private String wallet_address="";


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_backup_wallet;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
    }

    private void initTopBar() {

        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(R.string.backup_wallet_btn_txt);
        mTopBar.addRightTextButton(R.string.skip_backup_mneonic_btn_code, R.id.skipBackupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BPCreateWalletFormFragment.isCreateWallet) {
                    popBackStack();
                } else {
                    sharedPreferencesHelper.put("isFirstCreateWallet", "0");
                    startFragment(new HomeFragment());
                }


            }
        });
        Button skipBackuoBtn = mTopBar.findViewById(R.id.skipBackupBtn);
        skipBackuoBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_main));

    }


    public void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        if (getArguments() != null) {
            mnemonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
            wallet_address=getArguments().getString(ConstantsType.WALLET_ADDRESS);
        }
    }

    @Override
    protected void setListeners() {
        mBackupWalletBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onClick(View v) {
                if (mnemonicCodeList == null) {
                    String input=getString(R.string.password_backup_hint);
                    DialogUtils.showPassWordInputDialog(getActivity(), input, new DialogUtils.ConfirmListener() {
                        @Override
                        public void confirm(final String password) {
                            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                    .setTipWord(getResources().getString(R.string.tx_status_runing))
                                    .create();
                            tipDialog.show();
                            Runnable getSkeyRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    String ciphertextSkeyData = getSkeyStr();
                                    try {
                                        byte[] skeyByte = Wallet.getInstance().getSkey(password, ciphertextSkeyData);
                                        mnemonicCodeList = new MnemonicCode().toMnemonic(skeyByte);
                                        tipDialog.dismiss();

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                go2BPCreateWalletShowMnemonicCodeFragment();
                                            }
                                        });
                                    } catch (Exception e) {
                                        ToastUtil.showToast(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT);
                                        tipDialog.dismiss();
                                        return;
                                    }
                                }
                            };
                            ThreadManager.getInstance().execute(getSkeyRunnable);
                        }
                    });

                } else {
                    go2BPCreateWalletShowMnemonicCodeFragment();
                }
            }
        });
    }

    private String getSkeyStr() {
        String walletAddress = "";
        if (getArguments() != null) {
            walletAddress = getArguments().getString(ConstantsType.WALLET_ADDRESS, "");
        }
        return sharedPreferencesHelper.getSharedPreference(walletAddress + ConstantsType.WALLET_SKEY, "").toString();
    }

    private void go2BPCreateWalletShowMnemonicCodeFragment() {
        BPCreateWalletShowMneonicCodeFragment createWalletShowMneonicCodeFragment = new BPCreateWalletShowMneonicCodeFragment();
        Bundle argz = new Bundle();
        argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mnemonicCodeList);
        createWalletShowMneonicCodeFragment.setArguments(argz);
        startFragment(createWalletShowMneonicCodeFragment);
    }


    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getContext(), getResources().getText(R.string.next_key_down_err), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            getActivity().finish();
        }

    }

}
