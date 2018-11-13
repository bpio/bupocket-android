package com.bupocket;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import com.bupocket.common.Constants;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LocaleUtil;
import com.squareup.leakcanary.LeakCanary;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class BPApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    private Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        LocaleUtil.changeAppLanguage(context);

        initSocketConfig();
    }

    private void initSocketConfig() {
        try {
            String socketUrl = Constants.PUSH_MESSAGE_SOCKET_URL;
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};
            X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();

            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            mSocket = IO.socket(socketUrl, opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged");
        LocaleUtil.setLanguage(context, newConfig);
    }



    public static void switchNetConfig(String netType){
        Boolean isMainNetConfig = false;
        if(CommonUtil.isNull(netType)){
            isMainNetConfig = true;
        }else if("mainnet".equals(netType)){
            isMainNetConfig = true;
        }

        if(isMainNetConfig){
            Constants.BUMO_NODE_URL = Constants.MainNetConfig.BUMO_NODE_URL.getValue();
            Constants.PUSH_MESSAGE_SOCKET_URL = Constants.MainNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();
            Constants.WEB_SERVER_DOMAIN = Constants.MainNetConfig.WEB_SERVER_DOMAIN.getValue();
        }else {
            Constants.BUMO_NODE_URL = Constants.TestNetConfig.BUMO_NODE_URL.getValue();
            Constants.PUSH_MESSAGE_SOCKET_URL = Constants.TestNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();
            Constants.WEB_SERVER_DOMAIN = Constants.TestNetConfig.WEB_SERVER_DOMAIN.getValue();
        }
    }

//    public String getPushMessageSocketUrl(Context context){
//        int nodeCode = new SharedPreferencesHelper(context,"buPocket").getInt("bumoNode",Constants.DEFAULT_BUMO_NODE);
//        if(BumoNodeEnum.TEST.getCode() == nodeCode){
//            return Constants.PUSH_MESSAGE_SOCKET_TEST_URL;
//        }else {
//            return Constants.PUSH_MESSAGE_SOCKET_URL;
//        }
//    }
}
