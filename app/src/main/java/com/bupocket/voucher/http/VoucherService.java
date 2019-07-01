package com.bupocket.voucher.http;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.voucher.model.VoucherListModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VoucherService {


    @POST("voucher/v1/my/list")
    Call<ApiResult<VoucherListModel>> getVoucherPackage(@Body Map<String, Object> map);
}
