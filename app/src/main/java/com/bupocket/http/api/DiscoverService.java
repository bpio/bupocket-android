package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.SlideModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DiscoverService {

    @GET("nodeServer/slideshow/v1")
    Call<ApiResult<SlideModel>> slideShow();

    @POST("newsApi/slideshow/list")
    Call<ApiResult<SlideModel>> slideShowNew(@Body Map<String, Object> map);

}
