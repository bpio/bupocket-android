package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetAddressBookRespDto;
import com.bupocket.model.AdvertisingModel;
import com.bupocket.model.DeviceBindModel;
import com.bupocket.model.SKModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DeviceBindService {


    @GET("wallet/v1/config")
    Call<ApiResult<SKModel>> getConfig();


    @POST("wallet/v1/check")
    Call<ApiResult<DeviceBindModel>> deviceBind(@Body Map<String, Object> map);
}
