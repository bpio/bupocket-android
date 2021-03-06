package com.bupocket.http.api;

import android.os.SharedMemory;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetQRContentDto;
import com.bupocket.http.api.dto.resp.SuperNodeDto;
import com.bupocket.model.MyVoteRecordModel;
import com.bupocket.model.NodeDetailModel;
import com.bupocket.model.ShareUrlModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.model.TransConfirmModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NodePlanService {
    @POST("nodeServer/qr/v1/content")
    Call<ApiResult<GetQRContentDto>> getQRContent(@Body Map<String,Object> map);
    @POST("nodeServer/tx/v1/confirm")
    Call<ApiResult<TransConfirmModel>> confirmTransaction(@Body Map<String,Object> map);
    @POST("nodeServer/node/v1/vote/revoke/user")
    Call<ApiResult> revokeVote(@Body Map<String,Object> map);
    @POST("nodeServer/node/v1/get/reward/user")
    Call<ApiResult> getReward(@Body Map<String,Object> map);


    @POST("nodeServer/node/v1/list/app")
    Call<ApiResult<SuperNodeDto>> getSuperNodeList(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/vote/list")
    Call<ApiResult<MyVoteRecordModel>> getMyVoteList(@Body Map<String,Object> map);


    @POST("nodeServer/shortlink")
    Call<ApiResult<ShareUrlModel>> getShareUrl(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/detail")
    Call<ApiResult<SuperNodeModel>> getShareData(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/validate/detail")
    Call<ApiResult<NodeDetailModel>> getNodeValidateDetail(@Body Map<String,Object> map);

    @POST("nodeServer/node/v1/ecology/detail")
    Call<ApiResult<NodeDetailModel>> getNodeEcologyDetail(@Body Map<String,Object> map);

}
