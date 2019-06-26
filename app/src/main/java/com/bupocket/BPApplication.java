package com.bupocket;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.bupocket.common.Constants;
import com.bupocket.enums.BackupTipsStateEnum;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.CrashHandler;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.SocketUtil;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.commonsdk.UMConfigure;

public class BPApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private RefWatcher refWatcher;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initLeakCanary();

        LocaleUtil.changeAppLanguage(context);
        QMUISwipeBackActivityManager.init(this);
        switchNetConfig(null);
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "buPocket");
        sharedPreferencesHelper.put("backupTipsState", BackupTipsStateEnum.SHOW.getCode());

        initCrash();

        initUMeng();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);
    }


    public static RefWatcher getRefWatcher(Context context) {
        BPApplication application = (BPApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private void initUMeng() {

//        UMConfigure.init(Context context, String appkey, String channel, int deviceType, String pushSecret);

        UMConfigure.init(this, Constants.UM_APPID, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.e("TAG", "onConfigurationChanged");
        LocaleUtil.setLanguage(context, newConfig);
    }

    public static void switchNetConfig(String netType) {
        RetrofitFactory.getInstance().setNull4Retrofit();
        Wallet.getInstance().setNull4Wallet();
        SocketUtil.getInstance().SetNull4SocketUtil();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "buPocket");
        int netTypeCode = sharedPreferencesHelper.getInt("bumoNode", Constants.DEFAULT_BUMO_NODE);
        Boolean isMainNetConfig = true;
        if (BumoNodeEnum.TEST.getCode() == netTypeCode) {
            isMainNetConfig = false;
        } else if (BumoNodeEnum.TEST.getName().equals(netType)) {
            isMainNetConfig = false;
        }

        if (isMainNetConfig) {
            Constants.BUMO_NODE_URL = Constants.MainNetConfig.BUMO_NODE_URL.getValue();
            Constants.PUSH_MESSAGE_SOCKET_URL = Constants.MainNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();

            Constants.NODE_PLAN_IMAGE_URL_PREFIX = Constants.MainNetConfig.WEB_SERVER_DOMAIN.getValue() + Constants.IMAGE_PATH;
            LogUtils.mDebuggable = LogUtils.LEVEL_NONE;
            CrashHandler.isWrite = false;

            Constants.WEB_SERVER_DOMAIN = Constants.MainNetConfig.WEB_SERVER_DOMAIN.getValue();

        } else {
            Constants.BUMO_NODE_URL = Constants.TestNetConfig.BUMO_NODE_URL.getValue();
            Constants.PUSH_MESSAGE_SOCKET_URL = Constants.TestNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();

            Constants.NODE_PLAN_IMAGE_URL_PREFIX = Constants.TestNetConfig.WEB_SERVER_DOMAIN.getValue() + Constants.IMAGE_PATH;
            LogUtils.mDebuggable = LogUtils.LEVEL_ERROR;
            CrashHandler.isWrite = true;
            Constants.WEB_SERVER_DOMAIN = Constants.TestNetConfig.WEB_SERVER_DOMAIN.getValue();
        }

        String nodeUrl = (String) sharedPreferencesHelper.getSharedPreference(Constants.BUMO_NODE_URL + "nodeUrl", "");
        if (!TextUtils.isEmpty(nodeUrl)) {
          Constants.BUMO_NODE_URL_BASE=nodeUrl;
        }
    }

    private void initCrash() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
