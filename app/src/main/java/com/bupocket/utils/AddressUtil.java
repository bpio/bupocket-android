package com.bupocket.utils;

import android.text.TextUtils;

public class AddressUtil {
    public static String anonymous(String address){
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        String partOne = address.substring(0,5);
        String partTwo = address.substring(address.length()-5,address.length());
        return partOne + "***" + partTwo;
    }

    public static String anonymous(String address,int startLength){
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        String partOne = address.substring(0,startLength);
        String partTwo = address.substring(address.length()-startLength,address.length());
        return partOne + "***" + partTwo;
    }
}
