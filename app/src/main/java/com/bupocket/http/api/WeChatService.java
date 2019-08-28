package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.UserInfoModel;
import com.bupocket.model.WeChatInfoModel;
import com.bupocket.model.WeChatModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WeChatService {

    @POST("wx/v1/bind")
    Call<ApiResult<WeChatModel>> bindWeChat(@Body Map<String, Object> map);


    @POST("wallet/v1/identity/info")
    Call<ApiResult<UserInfoModel>> getWalletIdentityInfo(@Body Map<String, Object> map);



}
