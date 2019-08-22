package com.bupocket.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.HiddenFunctionStatusEnum;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BPUserInfoFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.userInfoBackupWalletBtn)
    Button mUserInfoBackupWalletTv;
    @BindView(R.id.userInfoLogoutWalletBtn)
    Button mUserInfoLogoutWalletTv;
    @BindView(R.id.userInfoAccNameTv)
    TextView mUserInfoAccNameTv;
    @BindView(R.id.identityIdTv)
    TextView mIdentityIdTv;
    @BindView(R.id.tipsIv)
    ImageView mTipsIv;


    private SharedPreferencesHelper sharedPreferencesHelper;
    private QMUIPopup identityIdExplainPopup;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initData();
        eventListeners();
        return root;
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        String identityId = sharedPreferencesHelper.getSharedPreference("identityId", "").toString();
        String accName = getArguments().getString("accName");
        mUserInfoAccNameTv.setText(accName);
        mIdentityIdTv.setText(AddressUtil.anonymous(identityId));
    }

    private void eventListeners() {
        mUserInfoBackupWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                go2BPCreateWalletShowMneonicCodeFragment();
            }
        });

        mUserInfoLogoutWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessagePositiveDialog();
            }
        });

        mTipsIv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
//                initEasyPopup();
                initPopup();
                identityIdExplainPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                identityIdExplainPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                identityIdExplainPopup.show(v);
                ImageView arrowUp = identityIdExplainPopup.getDecorView().findViewById(R.id.arrow_up);
                arrowUp.setImageDrawable(getResources().getDrawable(R.mipmap.triangle));
            }
        });
    }

    private void initPopup() {
        if (identityIdExplainPopup == null) {
            identityIdExplainPopup = new QMUIPopup(getContext(), QMUIPopup.DIRECTION_NONE);
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(identityIdExplainPopup.generateLayoutParam(
                    QMUIDisplayHelper.dp2px(getContext(), 280),
                    WRAP_CONTENT
            ));
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
            int padding = QMUIDisplayHelper.dp2px(getContext(), 20);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(getString(R.string.identity_id_explain_txt));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_white));
            textView.setBackgroundColor(getResources().getColor(R.color.popup_background_color));
            identityIdExplainPopup.setContentView(textView);
        }
    }

    private void go2BPCreateWalletShowMneonicCodeFragment() {
        getFragmentManager().findFragmentByTag(BPUserInfoFragment.class.getSimpleName());
        BPCreateWalletFormFragment.isCreateWallet = true;
        BPBackupWalletFragment createWalletShowMneonicCodeFragment = new BPBackupWalletFragment();
        Bundle argz = new Bundle();
        argz.putString(ConstantsType.WALLET_ADDRESS, WalletCurrentUtils.getIdentityWalletAddress(spHelper));
        createWalletShowMneonicCodeFragment.setArguments(argz);
        startFragment(createWalletShowMneonicCodeFragment);

    }

    private String getSkeyStr() {
        return sharedPreferencesHelper.getSharedPreference("skey", "").toString();
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(R.string.user_info_title);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    private void showMessagePositiveDialog() {

        DialogUtils.showConfirmDialog(mContext,
                getString(R.string.user_info_logout),
                getString(R.string.user_info_logout_notice),
                new DialogUtils.KnowListener() {
                    @Override
                    public void Know() {
                        showPasswordConfirmDialog();
                    }
                });

    }

    private void showPasswordConfirmDialog() {

        DialogUtils.showPassWordInputDialog(getActivity(), "",
                getString(R.string.user_info_logout_warning),
                "", getResources().getColor(R.color.qmui_config_color_red), new DialogUtils.ConfirmListener() {
                    @Override
                    public void confirm(final String password) {

                        if (CommonUtil.isNull(password)) {
                            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                    .setTipWord(getResources().getString(R.string.common_dialog_input_pwd))
                                    .create();
                            tipDialog.show();
                            return;
                        }
                        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.user_info_logout_loading))
                                .create();
                        tipDialog.show();

                        Runnable checkPwd = new Runnable() {
                            @Override
                            public void run() {
                                String ciphertextSkeyData = getSkeyStr();
                                try {
                                    Wallet.getInstance().checkPwd(password, ciphertextSkeyData);
                                    String firstVoucher = (String) spHelper.getSharedPreference(ConstantsType.FIRST_OPEN_VOUCHER, "yes");
                                    sharedPreferencesHelper.clear();
                                    SharedPreferencesHelper.getInstance().save("hiddenFunctionStatus", HiddenFunctionStatusEnum.DISABLE.getCode());
                                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                                    BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
                                    spHelper.put(ConstantsType.FIRST_OPEN_VOUCHER, firstVoucher);

                                    tipDialog.dismiss();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startFragment(new BPCreateWalletFragment());
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.showToast(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT);
                                    tipDialog.dismiss();
                                    return;
                                }
                            }
                        };

                        ThreadManager.getInstance().execute(checkPwd);
                    }
                });


    }
}
