package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.WeChatInfoModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WeChatService {

    @POST("wx/v1/bind")
    Call<ApiResult<WeChatInfoModel>> getWeChatInfo(@Body Map<String, Object> map);



}
