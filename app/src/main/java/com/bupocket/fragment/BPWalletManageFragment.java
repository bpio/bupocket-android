package com.bupocket.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DialogTitle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPWalletManageFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.walletNameTv)
    TextView mWalletNameTv;
    @BindView(R.id.walletAddressTv)
    TextView mWalletAddressTv;
    @BindView(R.id.deleteWalletBtn)
    QMUIRoundButton mDeleteWalletBtn;

    @BindView(R.id.walletInfoLl)
    LinearLayout mWalletInfoLl;
    @BindView(R.id.exportKeystoreRl)
    LinearLayout mExportKeystoreRl;
    @BindView(R.id.exportPrivateRl)
    LinearLayout mExportPrivateRl;
    @BindView(R.id.backupMnemonicRl)
    LinearLayout mBackupMnemonicRl;
    @BindView(R.id.changePwRl)
    LinearLayout mChangePwRl;
    @BindView(R.id.walletHeadRiv)
    ImageView walletHeadRiv;


    private String walletAddress;
    private String currentWalletAddress;
    private String walletName;
    private String keystoreStr;
    private String privateKeyStr;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> importedWallets = new ArrayList<>();
    private List<String> mnemonicCodeList;

    private Boolean whetherIdentityWallet = false;

    public QMUITipDialog exportingTipDialog;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet_manage, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void setListener() {
        mWalletInfoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WalletDetailsFragment fragment = new WalletDetailsFragment();
                Bundle args = new Bundle();
                args.putString("address", walletAddress);
                args.putString("walletName", walletName);
                args.putString("walletIon", "walletIon");
                fragment.setArguments(args);
                startFragment(fragment);
            }
        });

        mExportKeystoreRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.view_password_comfirm);
                qmuiDialog.show();
                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                TextView mPasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

                mPasswordConfirmNotice.setText(getString(R.string.export_keystore_password_confirm_txt));
                mPasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

                mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();

                        exportingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                                .create();
                        exportingTipDialog.show();
                        exportingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String bpData = getAccountBPData();
                                    keystoreStr = Wallet.getInstance().exportKeyStore(password, bpData, walletAddress);
                                    exportingTipDialog.dismiss();

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Bundle argz = new Bundle();
                                            argz.putString("keystoreStr", keystoreStr);
                                            BPWalletExportKeystoreFragment bpWalletExportKeystoreFragment = new BPWalletExportKeystoreFragment();
                                            bpWalletExportKeystoreFragment.setArguments(argz);
                                            startFragment(bpWalletExportKeystoreFragment);
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                    exportingTipDialog.dismiss();
                                    Looper.loop();
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
        });

        mExportPrivateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.view_password_comfirm);
                qmuiDialog.show();
                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                TextView mPasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

                mPasswordConfirmNotice.setText(getString(R.string.export_private_password_confirm_txt));
                mPasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

                mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();

                        exportingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                                .create();
                        exportingTipDialog.show();
                        exportingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String bpData = getAccountBPData();
                                LogUtils.e("bpData::::" + bpData);
                                try {
                                    privateKeyStr = Wallet.getInstance().exportPrivateKey(password, bpData, walletAddress);
                                    exportingTipDialog.dismiss();

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Bundle argz = new Bundle();
                                            argz.putString("address", walletAddress);
                                            argz.putString("privateKey", privateKeyStr);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    BPWalletExportPrivateFragment bpWalletExportPrivateFragment = new BPWalletExportPrivateFragment();
                                                    bpWalletExportPrivateFragment.setArguments(argz);
                                                    startFragment(bpWalletExportPrivateFragment);
                                                }
                                            });
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                    exportingTipDialog.dismiss();
                                    Looper.loop();
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
        });

        mDeleteWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final boolean isCurrentWallet = walletAddress.equals(WalletCurrentUtils.getWalletAddress(spHelper));
                if (isCurrentWallet) {
                    DialogUtils.showConfirmDialog(mContext,
                            getString(R.string.delete_current_wallet_title),
                            getString(R.string.delete_current_wallet_info),
                            new DialogUtils.KnowListener() {
                        @Override
                        public void Know() {
                            deleteWallet(isCurrentWallet);
                        }
                    });

                    return;
                }

                deleteWallet(isCurrentWallet);


            }
        });

        mBackupMnemonicRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2BPCreateWalletShowMnemonicCodeFragment();
            }
        });

        mChangePwRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BPChangePwdFragment fragment = new BPChangePwdFragment();
                Bundle args = new Bundle();
                args.putString("address", walletAddress);
                fragment.setArguments(args);
                startFragment(fragment);
            }
        });
    }

    private void deleteWallet(final boolean isCurrentWallet) {

        DialogUtils.showPassWordInputDialog(getActivity(), getString(R.string.delete_wallet_password_confirm_txt), new DialogUtils.ConfirmListener() {
            @Override
            public void confirm(final String msg) {
                exportingTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create();
                exportingTipDialog.show();
                exportingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                            return true;
                        }
                        return false;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String bpData = getAccountBPData();
                        try {
                            privateKeyStr = Wallet.getInstance().exportPrivateKey(msg, bpData, walletAddress);
                            importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets", "[]").toString(), String.class);
                            importedWallets.remove(walletAddress);
                            sharedPreferencesHelper.put(walletAddress + "-walletName", "");
                            sharedPreferencesHelper.put(walletAddress + "-BPdata", "");
                            sharedPreferencesHelper.put("importedWallets", JSONObject.toJSONString(importedWallets));
                            sharedPreferencesHelper.put(walletAddress + "-mnemonicCodes", "");
                            if (walletAddress.equals(currentWalletAddress)) {
                                sharedPreferencesHelper.put("currentWalletAddress", sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString());
                            }


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(getActivity(), R.string.delete_wallet_success_message_txt, Toast.LENGTH_SHORT);

                                    exportingTipDialog.dismiss();
                                    if (isCurrentWallet) {
                                        startFragment(new HomeFragment());
                                    } else {
                                        popBackStack();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT);
                            exportingTipDialog.dismiss();
                        }
                    }
                }).start();
            }
        });


    }

    private void refreshWalletName() {
        walletName = sharedPreferencesHelper.getSharedPreference(walletAddress + "-walletName", "").toString();
        mWalletNameTv.setText(walletName);
    }

    private String getAccountBPData() {
        String accountBPData = null;
        if (whetherIdentityWallet) {
            accountBPData = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        } else {
            accountBPData = sharedPreferencesHelper.getSharedPreference(walletAddress + "-BPdata", "").toString();
        }
        return accountBPData;
    }

    private void initData() {
        Bundle argz = getArguments();
        walletAddress = argz.getString("walletAddress");
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress)) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        if (sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString().equals(walletAddress)) {
            walletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", Constants.NORMAL_WALLET_NAME).toString();
            whetherIdentityWallet = true;
        } else {
            walletName = sharedPreferencesHelper.getSharedPreference(walletAddress + "-walletName", "").toString();
        }


        String isMnemonicCodes = (String) spHelper.getSharedPreference(walletAddress + "-mnemonicCodes", "");
        if (isMnemonicCodes.equals("yes")) {
            mBackupMnemonicRl.setVisibility(View.VISIBLE);
        }

    }

    private void initUI() {
        initTopBar();
        initView();
    }

    private void initView() {
        mWalletNameTv.setText(walletName);
        mWalletAddressTv.setText(AddressUtil.anonymous(walletAddress));
        if (whetherIdentityWallet) {
            mDeleteWalletBtn.setVisibility(View.GONE);
            mBackupMnemonicRl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String walletName;
        if (checkIdentity(walletAddress)) {
            walletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", Constants.NORMAL_WALLET_NAME).toString();
        } else {
            walletName = sharedPreferencesHelper.getSharedPreference(walletAddress + "-walletName", "").toString();
        }
        mWalletNameTv.setText(walletName);


        CommonUtil.setHeadIvRes(walletAddress, walletHeadRiv, spHelper);

    }

    private void refreshIdentityWalletName() {
        walletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", Constants.NORMAL_WALLET_NAME).toString();
        mWalletNameTv.setText(walletName);
    }

    private String getSkeyStr() {
        return sharedPreferencesHelper.getSharedPreference(walletAddress + ConstantsType.WALLET_SKEY, "").toString();
    }

    private void go2BPCreateWalletShowMnemonicCodeFragment() {


        getFragmentManager().findFragmentByTag(BPWalletManageFragment.class.getSimpleName());
        BPBackupWalletFragment backupWalletFragment = new BPBackupWalletFragment();
        Bundle argz = new Bundle();
//        argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mnemonicCodeList);
        argz.putString(ConstantsType.WALLET_ADDRESS, walletAddress);
        BPCreateWalletFormFragment.isCreateWallet = true;
        backupWalletFragment.setArguments(argz);
        startFragment(backupWalletFragment);

    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.manage_txt));
    }
}
