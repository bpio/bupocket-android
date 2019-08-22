package com.bupocket.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;

import com.bupocket.BPApplication;
import com.bupocket.common.ConstantsType;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends QMUIFragment {

    public static final int TRANSFER_CODE = 200001;
    public SharedPreferencesHelper spHelper;
    protected Context mContext;
    protected BPApplication mApplication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        if (spHelper == null) {
            spHelper = new SharedPreferencesHelper(getContext(), ConstantsType.BU_POCKET);
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mApplication = ((BPApplication) getActivity().getApplication());
    }


    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }


    public String getWalletAddress() {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return spHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        return currentIdentityWalletAddress;
    }

    public boolean checkIdentity(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (address.equals(currentAccAddr))
            return true;
        else
            return false;
    }

    /**
     * @return false currentAccAddr  true currentWalletAddress
     */
    private boolean isCurrentAddress() {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return false;
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (currentAccAddr.equals(currentIdentityWalletAddress)) {
            return false;
        }
        return true;
    }


    public String getBPAccountData() {
        String accountBPData = spHelper.getSharedPreference("BPData", "").toString();
        if (isCurrentAddress()) {
            return spHelper.getSharedPreference(spHelper.getSharedPreference("currentWalletAddress", "").toString() + "-BPdata", "").toString();
        }
        return accountBPData;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getTag().getClass().toString()); //统计页面("MainScreen"为页面名称，可自定义)
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getTag());
    }


    @Override
    public void popBackStack() {
        super.popBackStack();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BPApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }


}
