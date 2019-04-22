package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.AdvertisingModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AdvertisingService {

    @GET("nodeServer/ad/v1/6aa3838c760b4e2abf37910f75394834")
    Call<ApiResult<AdvertisingModel>>  getAdInfo();

}
