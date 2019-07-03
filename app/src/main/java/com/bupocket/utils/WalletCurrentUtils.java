package com.bupocket.utils;

import android.text.TextUtils;

import com.bupocket.common.Constants;

public class WalletCurrentUtils {


    public static String getWalletName(String address, SharedPreferencesHelper spHelper) {

        if (TextUtils.isEmpty(address)) {
            return "Wallet-1";
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (address.equals(currentAccAddr)) {
            return spHelper.getSharedPreference("currentIdentityWalletName", Constants.NORMAL_WALLET_NAME).toString();
        } else {
            return spHelper.getSharedPreference(address + "-walletName", "").toString();
        }
    }

    public static String getWalletAddress(SharedPreferencesHelper spHelper) {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return spHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        return currentIdentityWalletAddress;
    }


    public static String getIdentityAddress(SharedPreferencesHelper spHelper) {
        return spHelper.getSharedPreference("currentAccAddr", "").toString();
    }

    public static boolean checkIdentity(String address, SharedPreferencesHelper spHelper) {
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
