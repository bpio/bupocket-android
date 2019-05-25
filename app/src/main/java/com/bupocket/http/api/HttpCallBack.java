package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;

public interface HttpCallBack<T> {


    void onSuccess(ApiResult<String> result,T data);

    void onFailure(String error);

    void onComplete();
}
