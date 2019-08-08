package com.bupocket.fragment;

import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.utils.WalletUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCreateWalletFormFragment extends BaseFragment implements View.OnFocusChangeListener {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.create_wallet_identity_name_et)
    EditText mSetIdentityNameEt;
    @BindView(R.id.create_wallet_set_pwd_et)
    EditText mSetPwdEt;
    @BindView(R.id.create_wallet_repeat_pwd_et)
    EditText mRepeatPwdEt;

    @BindView(R.id.recoverShowPwdIv)
    ImageView mPwdShow;

    @BindView(R.id.recoverShowConfirmPwdIv)
    ImageView mConfirmPwdShow;

    @BindView(R.id.createWalletSubmitBtn)
    QMUIRoundButton mCreateWalletSubmitBtn;
    @BindView(R.id.create_wallet_name_title)
    TextView createWalletNameTitle;


    public boolean isPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    private SharedPreferencesHelper sharedPreferencesHelper;
    public static boolean isCreateWallet;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_form, null);
        ButterKnife.bind(this, root);

        initData();
        onSubmitBtnListener();
        buildWatcher();
        eventListeners();
        return root;
    }

    private void eventListeners() {
        mPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WalletUtils.setEditTextEyeHide(mPwdShow, mSetPwdEt, isPwdHideFirst);
                isPwdHideFirst = !isPwdHideFirst;
            }
        });
        mConfirmPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WalletUtils.setEditTextEyeHide(mConfirmPwdShow, mRepeatPwdEt, isConfirmPwdHideFirst);
                isConfirmPwdHideFirst = !isConfirmPwdHideFirst;

            }
        });

        mSetIdentityNameEt.setOnFocusChangeListener(this);
        mSetPwdEt.setOnFocusChangeListener(this);
        mRepeatPwdEt.setOnFocusChangeListener(this);

    }

    private void initData() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        initCreateWalletPromptView();
        initTopBar();
        isCreateWallet = false;
        Bundle arguments = getArguments();
        if (arguments != null) {
            String jumpPage = arguments.getString("jumpPage");
            isCreateWallet = !TextUtils.isEmpty(jumpPage) && jumpPage.equals(BPWalletsHomeFragment.class.getSimpleName());
            if (isCreateWallet) {
                createWalletNameTitle.setText(R.string.create_wallet_name_title);
                mSetIdentityNameEt.setHint(R.string.change_wallet_name_hint);
                mTopBar.setTitle(R.string.view_title_create_wallet);
            }
        }

    }


    private boolean validateData() {
        String indntityName = mSetIdentityNameEt.getText().toString().trim();

        if (!CommonUtil.validateNickname(indntityName)) {

            if (isCreateWallet) {
                DialogUtils.showTitleDialog(mContext, getString(R.string.wallet_create_form_error5), getString(R.string.error_hint));
            } else {
                DialogUtils.showTitleDialog(mContext, getString(R.string.wallet_create_form_error4), getString(R.string.error_hint));
            }


            return false;
        }

        String password = mSetPwdEt.getText().toString().trim();

        if (!CommonUtil.validatePassword(password)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error2, R.string.error_hint);
            return false;
        }

        String repeatPassword = mRepeatPwdEt.getText().toString().trim();

        if (!repeatPassword.equals(password)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error1, R.string.error_hint);
            return false;
        }

        return true;
    }


    private void onSubmitBtnListener() {
        mCreateWalletSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean flag = validateData();
                if (!flag) {
                    return;
                }


                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.wallet_create_creating_txt))
                        .create();
                tipDialog.show();
                Runnable createWalletRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String accountPwd = mSetPwdEt.getText().toString().trim();

                        WalletBPData walletBPData = null;
                        try {


                            if (isCreateWallet) {
                                walletBPData = Wallet.getInstance().createWallet(accountPwd, getContext());
                                createWallet(walletBPData);
                            } else {
                                walletBPData = Wallet.getInstance().createIdentity(accountPwd, getContext());
                                createIdentityWallet(walletBPData);
                            }

                            tipDialog.dismiss();
                        } catch (WalletException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getActivity(), R.string.create_wallet_fail, Toast.LENGTH_SHORT);
                            tipDialog.dismiss();
                            return;
                        }
                    }
                };
                ThreadManager.getInstance().execute(createWalletRunnable);

            }
        });
    }

    private void createWallet(WalletBPData walletBPData) {
        String walletName = mSetIdentityNameEt.getText().toString().trim();
        final String address = walletBPData.getAccounts().get(0).getAddress();
        String bpData = JSON.toJSONString(walletBPData.getAccounts());
        List<String> importedWallets = JSONObject.parseArray(spHelper.getSharedPreference("importedWallets", "[]").toString(), String.class);
        if (address.equals(spHelper.getSharedPreference("currentAccAddr", "")) || importedWallets.contains(address)) {
            ToastUtil.showToast(getActivity(), R.string.error_already_import_meaaage_txt, Toast.LENGTH_SHORT);
        } else {
            spHelper.put(address + "-walletName", walletName);
            spHelper.put(address + "-BPdata", bpData);
            importedWallets.add(address);
            spHelper.put(address + "-mnemonicCodes", "yes");
            spHelper.put("importedWallets", JSONObject.toJSONString(importedWallets));
            sharedPreferencesHelper.put(address + ConstantsType.WALLET_SKEY, walletBPData.getSkey());
            WalletCurrentUtils.saveInitHeadIcon(spHelper, address);
            final WalletBPData finalWalletBPData = walletBPData;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    getFragmentManager().findFragmentByTag(BPCreateWalletFormFragment.class.getSimpleName());

                    BPBackupWalletFragment backupWalletFragment = new BPBackupWalletFragment();
                    Bundle argz = new Bundle();
                    argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) finalWalletBPData.getMnemonicCodes());
                    argz.putString(ConstantsType.WALLET_ADDRESS, address);
                    backupWalletFragment.setArguments(argz);
                    startFragmentAndDestroyCurrent(backupWalletFragment);
                }
            });

        }

    }

    private void createIdentityWallet(WalletBPData walletBPData) {
        final String address = walletBPData.getAccounts().get(1).getAddress();
        sharedPreferencesHelper.put("skey", walletBPData.getSkey());
        String identityName = mSetIdentityNameEt.getText().toString().trim();
        sharedPreferencesHelper.put("currentAccNick", identityName);
        sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
        sharedPreferencesHelper.put("identityId", walletBPData.getAccounts().get(0).getAddress());
        sharedPreferencesHelper.put("currentAccAddr", walletBPData.getAccounts().get(1).getAddress());
        sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode());
        sharedPreferencesHelper.put("currentWalletAddress", address);
        sharedPreferencesHelper.put(address + ConstantsType.WALLET_SKEY, walletBPData.getSkey());
        WalletCurrentUtils.saveInitHeadIcon(spHelper, address);
        final WalletBPData finalWalletBPData = walletBPData;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                BPBackupWalletFragment backupWalletFragment = new BPBackupWalletFragment();
                Bundle argz = new Bundle();
                argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) finalWalletBPData.getMnemonicCodes());
                argz.putString(ConstantsType.WALLET_ADDRESS, address);
                backupWalletFragment.setArguments(argz);
                startFragment(backupWalletFragment);
            }
        });

    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.view_title_create_wallet_identity);
    }

    private void initCreateWalletPromptView() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_create_wallet_prompt);
        qmuiDialog.show();
        qmuiDialog.findViewById(R.id.createWalletPromptConfirmBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String name = mSetIdentityNameEt.getText().toString().trim();
                String password = mSetPwdEt.getText().toString().trim();
                String passwordConfirm = mRepeatPwdEt.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
                    mCreateWalletSubmitBtn.setEnabled(true);
                    mCreateWalletSubmitBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mCreateWalletSubmitBtn.setEnabled(false);
                    mCreateWalletSubmitBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }


            }
        };
        mSetIdentityNameEt.addTextChangedListener(watcher);
        mSetPwdEt.addTextChangedListener(watcher);
        mRepeatPwdEt.addTextChangedListener(watcher);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

}
