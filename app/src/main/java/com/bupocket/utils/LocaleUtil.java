package com.bupocket.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.bupocket.BPMainActivity;
import com.bupocket.R;
import com.bupocket.common.ConstantsType;

import java.util.Locale;

public class LocaleUtil {
    /**
     * @return Locale
     */
    public static Locale getUserLocale() {
        int currentLanguage = SharedPreferencesHelper.getInstance().getInt("currentLanguage", 0);
        Locale myLocale = Locale.SIMPLIFIED_CHINESE;
        switch (currentLanguage) {
            case 0:
                myLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                myLocale = Locale.ENGLISH;
                break;
            case 2:
                myLocale = Locale.TRADITIONAL_CHINESE;
                break;
        }
        return myLocale;
    }

    /**
     *
     */
    public static void changeAppLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        int currentLanguage = SharedPreferencesHelper.getInstance().getInt("currentLanguage", -1);
        Locale myLocale;
        // 0 简体中文  1 English
        switch (currentLanguage) {
            case 0:
                myLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                myLocale = Locale.ENGLISH;
                break;
            default:
                myLocale = appContext.getResources().getConfiguration().locale;
        }
        // 本地语言设置
        if (needUpdateLocale(appContext, myLocale)) {
            updateLocale(appContext, myLocale);
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }


    public static int getLanguageStatus() {
        int currentLanguage = (int) SharedPreferencesHelper.getInstance().getInt("currentLanguage", 0);

        switch (currentLanguage) {
            case 0:// Locale.SIMPLIFIED_CHINESE;
                return 0;
            case 1://Locale.ENGLISH;
                return 1;
        }

        return 0;
    }

    /**
     *
     * @return
     */
    public static boolean isChinese() {
        int currentLanguage = (int) SharedPreferencesHelper.getInstance().getInt("currentLanguage", 0);

        switch (currentLanguage) {
            case 0:// Locale.SIMPLIFIED_CHINESE;
                return true;
            case 1://Locale.ENGLISH;
                return false;
        }

        return false;
    }


    /**
     * @param currentLanguage index
     */
    public static void changeAppLanguage(Context context, int currentLanguage) {
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        SharedPreferencesHelper.getInstance().save("currentLanguage", currentLanguage);
        Locale myLocale = Locale.SIMPLIFIED_CHINESE;
        // 0 简体中文 1 English
        switch (currentLanguage) {
            case 0:
                myLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                myLocale = Locale.ENGLISH;
                break;
        }
        // 本地语言设置
        if (LocaleUtil.needUpdateLocale(appContext, myLocale)) {
            LocaleUtil.updateLocale(appContext, myLocale);
        }

        restartApp(appContext);
    }

    /**
     * @param context
     */
    public static void restartApp(Context context) {
        Intent intent = new Intent(context, BPMainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra(ConstantsType.CHANGE_LANGUAGE, ConstantsType.STATUS_YES);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * @param context Context
     * @return Locale
     */
    public static Locale getCurrentLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    /**
     * @param context Context
     * @param locale  New User Locale
     */
    public static void updateLocale(Context context, Locale locale) {
        if (needUpdateLocale(context, locale)) {
            Configuration configuration = context.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= 19) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            context.getResources().updateConfiguration(configuration, displayMetrics);
        }
    }

    /**
     * @param context Context
     * @param locale  New User Locale
     * @return true / false
     */
    public static boolean needUpdateLocale(Context context, Locale locale) {
        return locale != null && !getCurrentLocale(context).equals(locale);
    }

    /**
     * @param context
     * @param newConfig
     */
    public static void setLanguage(Context context, Configuration newConfig) {
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        int currentLanguage = SharedPreferencesHelper.getInstance().getInt("currentLanguage", -1);
        Locale locale;
        // 0 简体中文 1 繁体中文 2 English
        switch (currentLanguage) {
            case 0:
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                locale = Locale.ENGLISH;
                break;
            default:
                locale = appContext.getResources().getConfiguration().locale;
        }
        // 系统语言改变了应用保持之前设置的语言
        if (locale != null) {
            Locale.setDefault(locale);
            Configuration configuration = new Configuration(newConfig);
            if (Build.VERSION.SDK_INT >= 19) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }
            appContext.getResources().updateConfiguration(configuration, appContext.getResources().getDisplayMetrics());
        }
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 8.0需要使用createConfigurationContext处理
            return updateResources(context);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = getUserLocale();// 获取新设置的语言

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }
}
