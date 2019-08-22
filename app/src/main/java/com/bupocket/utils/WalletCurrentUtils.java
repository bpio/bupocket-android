package com.bupocket.utils;

import android.content.Context;
import android.text.TextUtils;

import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;

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


    public static String getIdentityWalletAddress(SharedPreferencesHelper spHelper) {
        return spHelper.getSharedPreference("currentAccAddr", "").toString();
    }

    public static String getIdentityAddress(SharedPreferencesHelper spHelper) {
        return spHelper.getSharedPreference(ConstantsType.IDENTITY_ID, "").toString();
    }

    public static boolean isIdentityWallet(SharedPreferencesHelper spHelper) {
        return getIdentityWalletAddress(spHelper).equals(getWalletAddress(spHelper));
    }

    public static void saveInitHeadIcon(SharedPreferencesHelper spHelper, String walletAddress) {
        int selectedPosition = (int) (Math.random() * 10);
        spHelper.put(walletAddress + ConstantsType.WALLET_HEAD_ICON, selectedPosition);
    }


    public static String voucherDate(String startTime, String endTime, Context context){
      String date="";
        if (!TextUtils.isEmpty(startTime)&&!startTime.equals("-1")) {
            date = date  +
                    String.format(context.getString(R.string.goods_validity_date),
                            TimeUtil.timeStamp2Date(startTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD),
                            TimeUtil.timeStamp2Date(endTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD));

        }else{
            date+="  "+context.getResources().getString(R.string.long_date);
        }

        return date;
    }

}
