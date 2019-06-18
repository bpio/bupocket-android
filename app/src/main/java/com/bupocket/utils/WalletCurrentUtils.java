package com.bupocket.utils;

import android.text.TextUtils;

public class WalletCurrentUtils {


    public static String getWalletName(String address, SharedPreferencesHelper spHelper) {

        if (TextUtils.isEmpty(address)) {
            return "Wallet-1";
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (address.equals(currentAccAddr)) {
            return spHelper.getSharedPreference("currentIdentityWalletName", "Wallet-1").toString();
        } else {
            return spHelper.getSharedPreference(address + "-walletName", "").toString();
        }
    }

    public String getWalletAddress(SharedPreferencesHelper spHelper) {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return spHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        return currentIdentityWalletAddress;
    }

    public boolean checkIdentity(String address, SharedPreferencesHelper spHelper) {
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
