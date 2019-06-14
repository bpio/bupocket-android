package com.bupocket.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.HiddenFunctionStatusEnum;
import com.bupocket.enums.LanguageEnum;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BPProfileHomeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.tvProfileAddress)
    TextView tvProfileAddress;
    @BindView(R.id.llProfileIdentity)
    LinearLayout llProfileIdentity;
    @BindView(R.id.ivProfileAddressManage)
    ImageView ivProfileAddressManage;
    @BindView(R.id.ivProfileWalletManage)
    ImageView ivProfileWalletManage;
    @BindView(R.id.settingIcon)
    ImageView settingIcon;
    @BindView(R.id.addressBookIcon)
    ImageView addressBookIcon;
    @BindView(R.id.manageWalletIcon)
    ImageView manageWalletIcon;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentAccNick;

    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.userNick)
    TextView userNickTx;
    @BindView(R.id.changePwdRL)
    RelativeLayout mChangePwdRL;
    @BindView(R.id.helpFeedbackRL)
    RelativeLayout mHelpRL;
    @BindView(R.id.settingRL)
    LinearLayout mSettingRL;
    @BindView(R.id.versionNameTv)
    TextView mVersionNameTv;
    @BindView(R.id.profileAvatarIv)
    ImageView mProfileAvatarIv;
    @BindView(R.id.versionRL)
    RelativeLayout mVersionRl;
    @BindView(R.id.manageWalletRl)
    RelativeLayout mManageWalletRl;
    @BindView(R.id.addressBookRL)
    LinearLayout mAddressBookRl;
    @BindView(R.id.llProfileProtocol)
    LinearLayout llProfileProtocol;
    @BindView(R.id.tvProfileCurrency)
    TextView tvProfileCurrency;
    @BindView(R.id.tvProfileLanguage)
    TextView  tvProfileLanguage;

    private final static int CLICKCOUNTS = 5;
    private final static long DURATION = 2 * 1000;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }

    private void init() {
        initView();
        initData();
        setListener();
    }

    private void initView() {
        topbar.setTitle(R.string.tabbar_profile_txt);
        String currencyType = spHelper.getSharedPreference("currencyType","CNY").toString();
        tvProfileCurrency.setText(currencyType);

        int language = SharedPreferencesHelper.getInstance().getInt("currentLanguage", LanguageEnum.UNDEFINED.getId());
        if(LanguageEnum.UNDEFINED.getId() == language){
            String myLocaleStr = getContext().getResources().getConfiguration().locale.getLanguage();
            switch (myLocaleStr){
                case "zh": {
                    language = LanguageEnum.CHINESE.getId();
                    break;
                }
                case "en": {
                    language = LanguageEnum.ENGLISH.getId();
                    break;
                }
                default : {
                    language = LanguageEnum.ENGLISH.getId();
                }
            }
        }
        if(LanguageEnum.CHINESE.getId() == language){

            tvProfileLanguage.setText(getString(R.string.language_cn));
        }else{
            tvProfileLanguage.setText(getString(R.string.language_en));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
    }

    private void initUI() {
        userNickTx.setText(currentAccNick);
        mVersionNameTv.setText(CommonUtil.packageName(getContext()));
        if (userNickTx.getWidth() > 260) {
            userNickTx.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT));
        }
    }

    private void setListener() {
        mChangePwdRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangePwdFragment();
            }
        });
        mManageWalletRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoManageWalletFragment();
            }
        });
        mHelpRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHelpFeedbackFragment();
            }
        });
        mSettingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingFragment();
            }
        });
        mAddressBookRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddressBookFragment();
            }
        });
//        mProfileAvatarIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle argz = new Bundle();
//                argz.putString("accName", currentAccNick);
//                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
//                bpUserInfoFragment.setArguments(argz);
//                startFragment(bpUserInfoFragment);
//            }
//        });

        int hiddenFunctionStatus = sharedPreferencesHelper.getInt("hiddenFunctionStatus", HiddenFunctionStatusEnum.DISABLE.getCode());
        if (HiddenFunctionStatusEnum.DISABLE.getCode() == hiddenFunctionStatus) {
            mVersionRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    straightClick();
                }
            });
        }


        llProfileIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName", currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });
        ivProfileAddressManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAddressBookFragment());
            }
        });

        ivProfileWalletManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startFragment(new BPWalletsHomeFragment());
            }
        });

        llProfileProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPUserTermsFragment());
            }
        });

    }

    long[] mHits = new long[CLICKCOUNTS];

    public void straightClick() {
        // Listening to the straight click 5 times
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] > SystemClock.uptimeMillis() - DURATION) {

            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getString(R.string.switch_test_net_message_txt))
                    .addAction(getString(R.string.no_txt), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(getString(R.string.yes_txt), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            SharedPreferencesHelper.getInstance().save("hiddenFunctionStatus", HiddenFunctionStatusEnum.ENABLE.getCode());
                            SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
                            BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
                            dialog.dismiss();
                            startFragment(new BPSettingFragment());
                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .create().show();
        }
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
    }


    private void gotoChangePwdFragment() {
        startFragment(new BPChangePwdFragment());
    }

    private void gotoHelpFeedbackFragment() {
        startFragment(new BPHelpFeedbackFragment());
    }

    private void gotoSettingFragment() {
        startFragment(new BPCurrencyFragment());
    }

    private void gotoManageWalletFragment() {
        startFragment(new BPNodeSetting());
    }

    private void gotoAddressBookFragment() {
        startFragment(new BPLanguageFragment());
    }

}
