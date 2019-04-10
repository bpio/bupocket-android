package com.bupocket.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public abstract class BaseFragment extends QMUIFragment {

    private final static String BP_FILE_NAME = "buPocket";
    public  SharedPreferencesHelper spHelper;
    public  Context mContext;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        if (spHelper==null) {
            spHelper = new SharedPreferencesHelper(getContext(), BP_FILE_NAME);
        }

    }


    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }


    @Override
    public void onResume() {
        super.onResume();

//        BPUpgradeManager.getInstance(getContext()).runUpgradeTipTaskIfExist(getActivity());
    }


    public String getWalletAddress(){
       String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress","").toString();
        if(TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return spHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        return currentIdentityWalletAddress;
    }

    /**
     *
     * @return   false currentAccAddr  true currentWalletAddress
     */
    private boolean isCurrentAddress(){
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress","").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return false;
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (currentAccAddr.equals(currentIdentityWalletAddress)){
            return false;
        }
        return true;
    }


    public String getBPAccountData(){
        String  accountBPData = spHelper.getSharedPreference("BPData", "").toString();
        if (isCurrentAddress()){
            return  spHelper.getSharedPreference( spHelper.getSharedPreference("currentWalletAddress","").toString()+ "-BPdata", "").toString();
        }
        return accountBPData;
    }

}
