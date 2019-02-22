package com.bupocket.fragment;

import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.HiddenFunctionStatusEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.bupocket.wallet.model.WalletBPData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPRecoverWalletFormFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.recoverShowPwdIv)
    ImageView mPwdShow;

    @BindView(R.id.recoverShowConfirmPwdIv)
    ImageView mConfirmPwdShow;

    @BindView(R.id.recoverMneonicCodeEt)
    EditText mMneonicCodeEt;

    @BindView(R.id.recoverWalletNameEt)
    EditText mWalletNameEt;

    @BindView(R.id.recoverPwdEt)
    EditText mPwdEt;

    @BindView(R.id.recoverConfirmPwdEt)
    EditText mConfirmPwdEt;

    @BindView(R.id.recoverWalletSubmitBtn)
    QMUIRoundButton recoverSubmit;

    private boolean isPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {

        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recover_wallet_form, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        eventListeners();
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        buildWatcher();
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                recoverSubmit.setEnabled(false);
                recoverSubmit.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recoverSubmit.setEnabled(false);
                recoverSubmit.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signMneonicCode = mMneonicCodeEt.getText().toString().trim().length() > 0;
                boolean signWalleName = mWalletNameEt.getText().toString().trim().length() > 0;
                boolean signPwd = mPwdEt.getText().toString().trim().length() > 0;
                boolean signConfirm = mConfirmPwdEt.getText().toString().trim().length() > 0;
                if (signMneonicCode && signWalleName && signPwd && signConfirm) {
                    recoverSubmit.setEnabled(true);
                    recoverSubmit.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    recoverSubmit.setEnabled(false);
                    recoverSubmit.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mMneonicCodeEt.addTextChangedListener(watcher);
        mWalletNameEt.addTextChangedListener(watcher);
        mPwdEt.addTextChangedListener(watcher);
        mConfirmPwdEt.addTextChangedListener(watcher);
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
//                startFragmentAndDestroyCurrent(new BPCreateWalletFragment());
                popBackStack();
            }
        });
    }


    private boolean mneonicFlag() {
        String mneonic = mMneonicCodeEt.getText().toString().trim();
        String regex = "[a-zA-Z\\s]+";
        if ("".equals(mneonic)) {
            Toast.makeText(getActivity(), R.string.recover_edit_mneonic_code_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!mneonic.matches(regex)) {
            Toast.makeText(getActivity(), R.string.recover_mneonic_input_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean walletNameFlag() {
        String walletName = mWalletNameEt.getText().toString().trim();
        if ("".equals(walletName)) {
            Toast.makeText(getActivity(), R.string.recover_edit_new_wallet_name_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!CommonUtil.validateNickname(walletName)) {
            Toast.makeText(getActivity(), R.string.wallet_create_form_error4, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean pwdFlag() {
        String password = mPwdEt.getText().toString().trim();
        if ("".equals(password)) {
            Toast.makeText(getActivity(), R.string.wallet_create_form_input_password_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!CommonUtil.validatePassword(password)) {
            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean confirmPwdFlag() {
        String pwd = mPwdEt.getText().toString().trim();
        String confirmPwd = mConfirmPwdEt.getText().toString().trim();
        if ("".equals(confirmPwd)) {
            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!CommonUtil.validatePassword(confirmPwd)) {
            Toast.makeText(getActivity(), R.string.recover_set_pwd_error, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!confirmPwd.equals(pwd)) {
            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void eventListeners() {
        mPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mPwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPwdEt.setSelection(mPwdEt.getText().length());
                    isPwdHideFirst = true;
                } else {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPwdEt.setSelection(mPwdEt.getText().length());
                    isPwdHideFirst = false;
                }
            }
        });
        mConfirmPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mConfirmPwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mConfirmPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConfirmPwdEt.setSelection(mConfirmPwdEt.getText().length());
                    isConfirmPwdHideFirst = true;
                } else {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mConfirmPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mConfirmPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConfirmPwdEt.setSelection(mConfirmPwdEt.getText().length());
                    isConfirmPwdHideFirst = false;
                }
            }
        });

        recoverSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new QMUIDialog.MessageDialogBuilder(getActivity())
                        .setMessage(getString(R.string.recover_identity_confirm_message_txt))
                        .addAction(getString(R.string.common_dialog_cancel), new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(getString(R.string.common_dialog_confirm), new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                if (!mneonicFlag()) {
                                    return;
                                } else if (!walletNameFlag()) {
                                    return;
                                } else if (!pwdFlag()) {
                                    return;
                                } else if (!confirmPwdFlag()) {
                                    return;
                                }
                                final String password = mPwdEt.getText().toString().trim();
                                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                        .setTipWord(getResources().getString(R.string.recover_wallet_reloading))
                                        .create();
                                tipDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            List<String> mnemonicCodes = getMnemonicCode();
                                            WalletBPData walletBPData = Wallet.getInstance().importMnemonicCode(mnemonicCodes, password, getContext());
                                            sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                                            sharedPreferencesHelper.put("currentAccNick", mWalletNameEt.getText().toString());
                                            sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
                                            sharedPreferencesHelper.put("currentAccAddr", walletBPData.getAccounts().get(1).getAddress());
                                            sharedPreferencesHelper.put("identityId", walletBPData.getAccounts().get(0).getAddress());
                                            sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode());
                                            sharedPreferencesHelper.put("currentWalletAddress",walletBPData.getAccounts().get(1).getAddress());
                                            sharedPreferencesHelper.put("mnemonicWordBackupState", "0");
                                            tipDialog.dismiss();
                                            startFragmentAndDestroyCurrent(new HomeFragment(), false);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Looper.prepare();
                                            Toast.makeText(getActivity(), R.string.recover_wallet_error_tip, Toast.LENGTH_SHORT).show();
                                            tipDialog.dismiss();
                                            Looper.loop();
                                            return;
                                        }
                                    }
                                }).start();

                            }
                        })
                        .setCanceledOnTouchOutside(false)
                        .create().show();
            }
        });
    }

    private List<String> getMnemonicCode() {
        String inputMneonicCodeStr = mMneonicCodeEt.getText().toString().trim();
        String regex = "\\s+";
        String[] mneonicCodeArr = inputMneonicCodeStr.replaceAll(regex, " ").split(" ");
        return Arrays.asList(mneonicCodeArr);

    }

}
