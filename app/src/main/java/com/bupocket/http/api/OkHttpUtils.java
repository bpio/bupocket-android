package com.bupocket.http.api;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {


    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static OkHttpClient okHttpClient;

    private static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)//10秒连接超时
                            .writeTimeout(10, TimeUnit.SECONDS)//10m秒写入超时
                            .readTimeout(10, TimeUnit.SECONDS)//10秒读取超时
                            //.addInterceptor(new HttpHeaderInterceptor())//头部信息统一处理
                            //.addInterceptor(new CommonParamsInterceptor())//公共参数统一处理
                            .build();
                }
            }
        }

        return okHttpClient;
    }



    public static   <T extends Class>  void post(String url,Map<String, Object> params, HttpCallBack callBack) {
        commonPost(getRequestForPost(url, params, null),null, callBack);
    }
    /**
     * @param url      url地址
     * @param params   HashMap<String, Object> 参数
     * @param callBack 请求回调接口
     */
    public static   <T extends Class>  void post(String url, final Class cls,Map<String, Object> params, HttpCallBack callBack) {
        commonPost(getRequestForPost(url, params, null),cls, callBack);
    }


    /**
     * POST请求 公共请求部分
     */
    private static <T extends Class> void commonPost(Request request,final Class<T> cls, final HttpCallBack callBack) {
        if (request == null) return;
        Call call = getInstance().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                try {
                    if (callBack != null && mainHandler != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure(e.getMessage());
                            }
                        });
                    }
                } catch (Exception e1) {
                    Log.e("caoliang", "HttpUtil----commonPost()---onFailure()--->" + e1.getMessage());
                }

                callBack.onComplete();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (callBack != null && mainHandler != null) {
                        final String data = response.body().string();
                        final ApiResult<String> apiResult = new Gson().fromJson(data, ApiResult.class);
                        final JSONObject jsonObject = new JSONObject(data);

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String data1 = null;
                                if (cls!=null) {
                                    try {
                                        data1 = jsonObject.getString("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    callBack.onSuccess(apiResult,new Gson().fromJson(data1, cls));
                                }else{
                                    callBack.onSuccess(apiResult,null);
                                }

                            }
                        });


                        callBack.onComplete();
                    }
                } catch (Exception e) {
                    Log.e("caoliang", "HttpUtil----commonPost()---onResponse()--->" + e.getMessage());
                }
            }

        });
    }

    private static Request getRequestForPost(String url, Map<String, Object> params, Object tag) {
        if (url == null || "".equals(url)) {
            Log.e("caoliang", "HttpUtil----getRequestForPost()---->" + "url地址为空 无法执行网络请求!!!");
            return null;
        }
        if (params == null) {
            params = new HashMap<>();
        }


        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, new Gson().toJson(params));
        Request request;
        if (tag != null) {
            request = new Request.Builder().url(url).post(body).tag(tag).build();
        } else {
            request = new Request.Builder().url(url).post(body).build();
        }
        return request;
    }


}
