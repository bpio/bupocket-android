package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCurrentVersionRespDto;
import com.bupocket.model.VersionLogModel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VersionService {


    @GET("wallet/version")
    Call<ApiResult<GetCurrentVersionRespDto>> getCurrentVersion(@Query("appType") int appTypeCode);


    @POST("wallet/version/log")
    Call<ApiResult<VersionLogModel>> getVersionLog(@Body HashMap<Object, Object> map);
}
