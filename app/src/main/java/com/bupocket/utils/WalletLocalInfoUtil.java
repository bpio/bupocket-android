package com.bupocket.utils;

import android.text.TextUtils;

import com.bupocket.wallet.Wallet;

import static com.bupocket.common.Constants.NORMAL_WALLET_NAME;

public class WalletLocalInfoUtil {


    private static WalletLocalInfoUtil walletLocalInfoUtil;
    private static SharedPreferencesHelper spHelper;

    private WalletLocalInfoUtil() {
    }

    public static WalletLocalInfoUtil getInstance(SharedPreferencesHelper myHelper) {

        if (walletLocalInfoUtil == null) {
            walletLocalInfoUtil = new WalletLocalInfoUtil();
        }
        spHelper = myHelper;
        return walletLocalInfoUtil;
    }

    public String getWalletName() {

        if (isCurrentAddress()) {
            return spHelper.getSharedPreference("currentIdentityWalletName", NORMAL_WALLET_NAME).toString();
        }
        return spHelper.getSharedPreference(getWalletAddress() + "-walletName", "").toString();
    }


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


}
