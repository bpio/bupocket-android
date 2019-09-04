package com.bupocket.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.WXBindEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.WeChatService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.WeChatInfoModel;
import com.bupocket.model.WeChatModel;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //如果没回调onResp，八成是这句没有写
        ((BPApplication) getApplication()).getWxApi().handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        SendAuth.Resp resp1 = ((SendAuth.Resp) resp);
                        LogUtils.e("wx:" + resp1.code + "\n" + resp1.url + "\n" + resp1.country + "\n" + resp1.lang);

                        String code = ((SendAuth.Resp) resp).code;
                        String state = ((SendAuth.Resp) resp).state;
                        if (state.equals("wechat_sdk_bind")) {
                            /*
                             * wechat bind
                             * */
                            doBindWxHttp(resp);
                        }
                        break;
                }
                break;

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
            default:
                finish();
                break;
        }

    }


    //绑定微信账号
    private void doBindWxHttp(BaseResp resp) {
        String code = ((SendAuth.Resp) resp).code;

        final SharedPreferencesHelper spHelper = new SharedPreferencesHelper(getBaseContext(), ConstantsType.BU_POCKET);
        String identityAddress = WalletCurrentUtils.getIdentityAddress(spHelper);

        WeChatService weChatService = RetrofitFactory.getInstance().getRetrofit().create(WeChatService.class);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("wxCode", code);
        map1.put("identityAddress", identityAddress);
        weChatService.bindWeChat(map1).enqueue(new Callback<ApiResult<WeChatModel>>() {
            @Override
            public void onResponse(Call<ApiResult<WeChatModel>> call, Response<ApiResult<WeChatModel>> response) {
                ApiResult<WeChatModel> body = response.body();

                if (body == null) {
                    finish();
                    return;
                }

                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    String wxHeadImgUrl = body.getData().getWxHeadImgUrl();
                    spHelper.put(ConstantsType.BIND_WECHAT_STATE, WXBindEnum.BIND_WECHAT.getCode());
                    spHelper.put(ConstantsType.WX_HEAD_IMG_URL, wxHeadImgUrl);
                    ToastUtil.showToast(WXEntryActivity.this, R.string.wechat_bind_success, Toast.LENGTH_SHORT);
                } else if (ExceptionEnum.ERROR_IDENTITY_BIND.getCode().equals(body.getErrCode())) {
                    ToastUtil.showToast(WXEntryActivity.this, getString(R.string.error_identity_bind), Toast.LENGTH_SHORT);
                } else if (ExceptionEnum.ERROR_WE_CHAT_BIND.getCode().equals(body.getErrCode())) {
                    ToastUtil.showToast(WXEntryActivity.this, getString(R.string.error_we_chat_bind), Toast.LENGTH_SHORT);
                } else {
                    String msg = body.getMsg();
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtil.showToast(WXEntryActivity.this, msg, Toast.LENGTH_SHORT);
                    }
                }


                finish();
            }

            @Override
            public void onFailure(Call<ApiResult<WeChatModel>> call, Throwable t) {
                finish();
            }
        });

    }


}