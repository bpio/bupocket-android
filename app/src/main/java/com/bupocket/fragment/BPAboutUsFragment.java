package com.bupocket.fragment;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.Unbinder;

public class BPAboutUsFragment extends AbsBaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout topBar;
    @BindView(R.id.versionCodeTV)
    TextView versionCodeTV;
    @BindView(R.id.versionInfoListLL)
    LinearLayout versionInfoListLL;
    @BindView(R.id.tvProfileLanguage)
    TextView tvProfileLanguage;
    @BindView(R.id.addressBookRL)
    LinearLayout addressBookRL;
    @BindView(R.id.changeTestLL)
    LinearLayout changeTestLL;
    @BindView(R.id.changeTestNetTV)
    TextView changeTestNetTV;
    @BindView(R.id.customEnvironmentLL)
    LinearLayout customEnvironmentLL;
    Unbinder unbinder;
    private boolean isSwitch;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_about_us;
    }

    @Override
    protected void initView() {
        initTopBar();
        versionCodeTV.setText(getString(R.string.about_us_version_code).concat(CommonUtil.packageName(mContext)));
    }

    private void initTopBar() {
        topBar.setTitle(R.string.profile_about_us);
        topBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    @Override
    protected void initData() {
        isSwitch = SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode();
        if (isSwitch) {
            changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_checked));
        } else {
            changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_normal));
        }
    }

    @Override
    protected void setListeners() {
        changeTestNetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitch) {
                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                    BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
                    showSwitchMainNetDialog();
                }else {
                    ShowSwitchTestNetConfirmDialog();
                }
            }
        });
        versionInfoListLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new VersionLogFragment());
            }
        });
    }


    private void showSwitchMainNetDialog() {

        DialogUtils.showMessageDialog(mContext, getString(R.string.switch_main_net_message_txt), "", new DialogUtils.KnowListener() {
            @Override
            public void Know() {
                spHelper.put("tokensInfoCache","");
                spHelper.put("tokenBalance","");
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_normal));
                startFragment(new HomeFragment());
            }
        });

    }


    private void ShowSwitchTestNetConfirmDialog() {

        DialogUtils.showMessageDialog(mContext, getString(R.string.switch_to_test_net_message_txt), "", new DialogUtils.KnowListener() {
            @Override
            public void Know() {
                SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
                BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
                spHelper.put("tokensInfoCache","");
                spHelper.put("tokenBalance","");
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_checked));
                startFragment(new HomeFragment());
            }
        });
    }

}
