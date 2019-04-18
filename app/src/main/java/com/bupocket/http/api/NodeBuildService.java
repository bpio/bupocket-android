package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.CoBuildListModel;
import com.bupocket.model.NodeBuildDetailModel;
import com.bupocket.model.NodeBuildModel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NodeBuildService {

    @POST("nodeServer/node/v1/cooperate/list")
    Call<ApiResult<CoBuildListModel>> getNodeBuildList(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/cooperate/detail")
    Call<ApiResult<NodeBuildDetailModel>> getNodeBuildDetail(@Body Map<String,Object> map);

    @POST("nodeServer/qr/v1/cobuild/list")
    Call<ApiResult<NodeBuildModel>> getNodeBuildSupport(@Body Map<String,Object> map);

    @POST("nodeServer/qr/v1/cobuild/list")
    Call<ApiResult<NodeBuildModel>> getNodeBuildExit(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/cooperate/support")
    Call<ApiResult> verifySupport(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/cooperate/exit")
    Call<ApiResult> verifyExit(@Body Map<String,Object> map);

}
