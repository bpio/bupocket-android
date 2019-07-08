package com.bupocket.fragment;

import android.location.Address;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPChangePwdFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.oldPasswordET)
    EditText mOldPasswordET;
    @BindView(R.id.newPasswordET)
    EditText mNewPasswordET;
    @BindView(R.id.newPasswordConfirmET)
    EditText mNewPasswordConfirmET;
    @BindView(R.id.nextChangePwdBtn)
    QMUIRoundButton mNextChangePwdBtn;
    @BindView(R.id.oldPasswordIv)
    ImageView mOldPasswordIv;
    @BindView(R.id.newPasswordIv)
    ImageView mNewPasswordIv;
    @BindView(R.id.newPasswordConfirmIv)
    ImageView mNewPasswordConfirmIv;
    @BindView(R.id.walletNameTv)
    TextView walletNameTv;
    @BindView(R.id.walletAddressTv)
    TextView walletAddressTv;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private boolean isNewPwdHideFirst = false;
    private boolean isOldPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    private String walletAddress;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_pwd, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initData();
        buildWatcher();
        eventListeners();


        mNextChangePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getResources().getString(R.string.handling))
                            .create();
                    tipDialog.show();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            String oldPwd = mOldPasswordET.getText().toString().trim();
                            String newPwd = mNewPasswordET.getText().toString().trim();

                            try {
                                WalletBPData walletBPData = null;
                                String identityWalletAddress = spHelper.getSharedPreference("currentAccAddr", "").toString();
                                String skey;

                                skey = sharedPreferencesHelper.getSharedPreference(BPChangePwdFragment.this.walletAddress + ConstantsType.WALLET_SKEY_PRIV, "").toString();
                                if (!TextUtils.isEmpty(skey)) {
                                    walletBPData = Wallet.getInstance().updateAccountWalletPassword(oldPwd, newPwd, skey, getContext());
                                    sharedPreferencesHelper.put(walletAddress +ConstantsType.WALLET_SKEY_PRIV, walletBPData.getSkey());
                                    sharedPreferencesHelper.put(walletAddress + "-BPdata", JSON.toJSONString(walletBPData.getAccounts()));
                                } else {
                                    if (walletAddress.equals(identityWalletAddress)) {
                                        skey = sharedPreferencesHelper.getSharedPreference("skey", "").toString();
                                    } else {
                                        skey = sharedPreferencesHelper.getSharedPreference(BPChangePwdFragment.this.walletAddress + ConstantsType.WALLET_SKEY, "").toString();
                                    }
                                    walletBPData = Wallet.getInstance().updateAccountPassword(oldPwd, newPwd, skey, getContext());
                                    if (walletAddress.equals(identityWalletAddress)) {
                                        sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                                        sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
                                    } else {
                                        sharedPreferencesHelper.put(walletAddress + "-skey", walletBPData.getSkey());
                                        sharedPreferencesHelper.put(walletAddress + "-BPdata", JSON.toJSONString(walletBPData.getAccounts()));
                                    }
                                }



                                tipDialog.dismiss();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        popBackStack();
                                        DialogUtils.showMessageNoTitleDialog(mContext, walletNameTv.getText() + getResources().getString(R.string.change_pwd_success_hint));
                                    }
                                });


                            } catch (WalletException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(getActivity(), R.string.old_pwd_error, Toast.LENGTH_SHORT).show();
                                tipDialog.dismiss();
                                Looper.loop();
                            } finally {
                                tipDialog.dismiss();
                            }
                        }
                    }).start();


                }
            }
        });

        return root;
    }

    private void eventListeners() {
        mOldPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOldPwdHideFirst) {
                    mOldPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mOldPasswordET.setInputType(InputType.TYPE_CLASS_TEXT);
                    mOldPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mOldPasswordET.setSelection(mOldPasswordET.getText().length());
                    isOldPwdHideFirst = true;
                } else {
                    mOldPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mOldPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mOldPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mOldPasswordET.setSelection(mOldPasswordET.getText().length());
                    isOldPwdHideFirst = false;
                }
            }
        });
        mNewPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewPwdHideFirst) {
                    mNewPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mNewPasswordET.setInputType(InputType.TYPE_CLASS_TEXT);
                    mNewPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mNewPasswordET.setSelection(mNewPasswordET.getText().length());
                    isNewPwdHideFirst = true;
                } else {
                    mNewPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mNewPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mNewPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mNewPasswordET.setSelection(mNewPasswordET.getText().length());
                    isNewPwdHideFirst = false;
                }
            }
        });
        mNewPasswordConfirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mNewPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mNewPasswordConfirmET.setInputType(InputType.TYPE_CLASS_TEXT);
                    mNewPasswordConfirmET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mNewPasswordConfirmET.setSelection(mNewPasswordConfirmET.getText().length());
                    isConfirmPwdHideFirst = true;
                } else {
                    mNewPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mNewPasswordConfirmET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mNewPasswordConfirmET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mNewPasswordConfirmET.setSelection(mNewPasswordConfirmET.getText().length());
                    isConfirmPwdHideFirst = false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNextChangePwdBtn.setEnabled(false);
                mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNextChangePwdBtn.setEnabled(false);
                mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signOldPassword = mOldPasswordET.getText().toString().trim().length() > 0;
                boolean signNewPassword = mNewPasswordET.getText().toString().trim().length() > 0;
                boolean signNewPasswordConfirm = mNewPasswordConfirmET.getText().toString().trim().length() > 0;
                if (signOldPassword && signNewPassword && signNewPasswordConfirm) {
                    mNextChangePwdBtn.setEnabled(true);
                    mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mNextChangePwdBtn.setEnabled(false);
                    mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mOldPasswordET.addTextChangedListener(watcher);
        mNewPasswordET.addTextChangedListener(watcher);
        mNewPasswordConfirmET.addTextChangedListener(watcher);
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        walletAddress = getArguments().getString("address", "");

        String walletName = WalletCurrentUtils.getWalletName(walletAddress, spHelper);
        walletNameTv.setText(walletName);
        walletAddressTv.setText(AddressUtil.anonymous(walletAddress));

    }

    private boolean validateData() {

        String oldPwd = mOldPasswordET.getText().toString().trim();
        String newPwd = mNewPasswordET.getText().toString().trim();
        String newPasswordConfirm = mNewPasswordConfirmET.getText().toString().trim();


        if (!CommonUtil.validatePassword(newPwd)) {
            DialogUtils.showTitleDialog(mContext, R.string.create_wallet_pw_err_hint, R.string.error_hint);
            return false;
        }


        if (oldPwd.equals(newPwd)) {
            DialogUtils.showTitleDialog(mContext, R.string.pwd_repeat, R.string.error_hint);
            return false;
        }


        if (!newPasswordConfirm.equals(newPwd)) {
            DialogUtils.showTitleDialog(mContext, R.string.new_safe_pwd_error, R.string.error_hint);
            return false;
        }
        return true;
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle(R.string.change_the_password_txt);
    }
}
