package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.BonusInfoBean;
import com.bupocket.model.OpenStatusModel;
import com.bupocket.model.RedPacketDetailModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RedPacketService {


    @POST("activity/v1/open/status")
    Call<ApiResult<OpenStatusModel>> queryOpen();

    @POST("activity/bonus/v1/status")
    Call<ApiResult<BonusInfoBean>> queryRedPacket(@Body Map<String, Object> map);


    @POST("activity/bonus/v1/open")
    Call<ApiResult<BonusInfoBean>> openRedPacket(@Body Map<String, Object> map);


    @POST("activity/bonus/v1/detail")
    Call<ApiResult<RedPacketDetailModel>> queryRedPacketDetail(@Body Map<String, Object> map);

}
