package com.bupocket.fragment;

import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.utils.WalletUtils;
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

public class BPRecoverWalletFormFragment extends BaseFragment implements View.OnFocusChangeListener {

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
    @BindView(R.id.importInfoHintLl)
    LinearLayout mnemonicWordLl;


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
        initCreateWalletPromptView();
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
                String mneonicCode = mMneonicCodeEt.getText().toString().trim();
                String name = mWalletNameEt.getText().toString().trim();
                String password = mPwdEt.getText().toString().trim();
                String passwordConfirm = mConfirmPwdEt.getText().toString().trim();


                if (!TextUtils.isEmpty(mneonicCode) &&
                        !TextUtils.isEmpty(name) &&
                        !TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(passwordConfirm)) {
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
        mTopBar.setTitle(R.string.recover_wallet_title);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
//                startFragmentAndDestroyCurrent(new BPCreateWalletFragment());
                popBackStack();
            }
        });
    }


    private void initCreateWalletPromptView() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_create_wallet_prompt);
        qmuiDialog.show();
        qmuiDialog.findViewById(R.id.createWalletPromptConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
    }


    private boolean mneonicFlag() {
        String mneonic = mMneonicCodeEt.getText().toString().trim();
        String regex = "[a-zA-Z\\s]+";
        if (!mneonic.matches(regex)) {

            DialogUtils.showTitleDialog(mContext, R.string.recover_mneonic_input_error, R.string.error_hint);
            return false;
        }
        return true;
    }

    private boolean walletNameFlag() {
        String walletName = mWalletNameEt.getText().toString().trim();
        if (!CommonUtil.validateNickname(walletName)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error4, R.string.error_hint);
            return false;
        }
        return true;
    }

    private boolean pwdFlag() {
        String password = mPwdEt.getText().toString().trim();
        if (!CommonUtil.validatePassword(password)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error2, R.string.error_hint);
            return false;
        }
        return true;
    }

    private boolean confirmPwdFlag() {
        String pwd = mPwdEt.getText().toString().trim();
        String confirmPwd = mConfirmPwdEt.getText().toString().trim();
        if (!confirmPwd.equals(pwd)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error1, R.string.error_hint);
            return false;
        }
        return true;
    }

    private void eventListeners() {
        mPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WalletUtils.setEditTextEyeHide(mPwdShow,mPwdEt,isPwdHideFirst);
                isPwdHideFirst=!isPwdHideFirst;

            }
        });
        mConfirmPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WalletUtils.setEditTextEyeHide(mConfirmPwdShow,mConfirmPwdEt,isConfirmPwdHideFirst);
                isConfirmPwdHideFirst=!isConfirmPwdHideFirst;
            }
        });

        recoverSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!walletNameFlag()) {
                    return;
                } else if (!pwdFlag()) {
                    return;
                } else if (!confirmPwdFlag()) {
                    return;
                } else if (!mneonicFlag()) {
                    return;
                }
                final String password = mPwdEt.getText().toString().trim();
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.recover_wallet_reloading))
                        .create();
                tipDialog.show();
                Runnable recoverRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<String> mnemonicCodes = getMnemonicCode();
                            WalletBPData walletBPData = Wallet.getInstance().importMnemonicCode(mnemonicCodes, password, getContext());
                            String address = walletBPData.getAccounts().get(1).getAddress();
                            sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                            sharedPreferencesHelper.put("currentAccNick", mWalletNameEt.getText().toString().trim());
                            sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
                            sharedPreferencesHelper.put("currentAccAddr", address);
                            sharedPreferencesHelper.put("identityId", walletBPData.getAccounts().get(0).getAddress());
                            sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode());
                            sharedPreferencesHelper.put("currentWalletAddress", address);
                            sharedPreferencesHelper.put("mnemonicWordBackupState", "0");
                            sharedPreferencesHelper.put(address + ConstantsType.WALLET_SKEY, walletBPData.getSkey());
                            WalletCurrentUtils.saveInitHeadIcon(spHelper, address);
                            tipDialog.dismiss();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startFragmentAndDestroyCurrent(new HomeFragment(), false);
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            DialogUtils.showTitleDialog(mContext, R.string.recover_mneonic_input_error, R.string.error_hint);
                            tipDialog.dismiss();
                            Looper.loop();
                            return;
                        }
                    }
                };
                ThreadManager.getInstance().execute(recoverRunnable);
            }
        });

        mWalletNameEt.setOnFocusChangeListener(this);
        mPwdEt.setOnFocusChangeListener(this);
        mConfirmPwdEt.setOnFocusChangeListener(this);

        mnemonicWordLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPWalletUnderstandMnemonicWordFragment());
            }
        });
    }

    private List<String> getMnemonicCode() {
        String inputMneonicCodeStr = mMneonicCodeEt.getText().toString().trim();
        String regex = "\\s+";
        String[] mneonicCodeArr = inputMneonicCodeStr.replaceAll(regex, " ").split(" ");
        return Arrays.asList(mneonicCodeArr);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
