package com.bupocket.http.api;

import com.bupocket.common.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static final long DEFAULT_TIMEOUT = 10;
    private static RetrofitFactory factory;
    private Retrofit retrofit = null;

    private RetrofitFactory() {
        init();
    }

    public synchronized static RetrofitFactory getInstance() {
        if (factory == null) {
            factory = new RetrofitFactory();
        }
        return factory;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setNull4Retrofit() {
        factory = null;
    }

    private void init() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//设置超时时间
                .retryOnConnectionFailure(true);

        client.addInterceptor(new LoggingInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEB_SERVER_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client.build())
                .build();
    }


}
