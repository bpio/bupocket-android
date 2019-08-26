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
        Log.d("WXEntryActivity", "错误码 : " + resp.errCode + "");
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        SendAuth.Resp resp1=((SendAuth.Resp) resp);
                        LogUtils.e("wx:"+resp1.code+"\n"+resp1.url+"\n"+resp1.country+"\n"+resp1.lang);

                        String code = ((SendAuth.Resp) resp).code;
                        String state = ((SendAuth.Resp) resp).state;
                        if (state.equals("wechat_sdk_bind")) {
                            /*
                             * wechat bind
                             * */
                            doBindWxHttp(resp);
                        } else {
                            /*
                             *wechat login
                             **/
                            getAccessToken(code);
                        }
                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        Toast.makeText(this, "微信分享成功", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                break;

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                default:
                if (RETURN_MSG_TYPE_LOGIN == resp.getType()) {
                  ToastUtil.showToast(WXEntryActivity.this, R.string.bind_wechat_error,Toast.LENGTH_SHORT);
                }
                break;
        }
        finish();
    }

    private void getAccessToken(String code) {
    }

    //绑定微信账号
    private void doBindWxHttp(BaseResp resp) {
        String code = ((SendAuth.Resp) resp).code;

        final SharedPreferencesHelper spHelper = new SharedPreferencesHelper(getBaseContext(), ConstantsType.BU_POCKET);
        String identityAddress = WalletCurrentUtils.getIdentityAddress(spHelper);

        WeChatService weChatService = RetrofitFactory.getInstance().getRetrofit().create(WeChatService.class);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("wxCode",code);
        map1.put("identityAddress", identityAddress);
        weChatService.bindWeChat(map1).enqueue(new Callback<ApiResult<WeChatInfoModel>>() {
            @Override
            public void onResponse(Call<ApiResult<WeChatInfoModel>> call, Response<ApiResult<WeChatInfoModel>> response) {
                ApiResult<WeChatInfoModel> body = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    String wxHeadImgUrl = body.getData().getWxHeadImgUrl();
                    spHelper.put(ConstantsType.BIND_WECHAT_STATE, WXBindEnum.UNBIND_WECHAT.getCode());
                    spHelper.put(ConstantsType.WX_HEAD_IMG_URL,wxHeadImgUrl);
                }else{

                    String msg = body.getMsg();
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtil.showToast(WXEntryActivity.this,msg,Toast.LENGTH_SHORT);
                    }

                }


            }

            @Override
            public void onFailure(Call<ApiResult<WeChatInfoModel>> call, Throwable t) {

            }
        });

//
//        FormBody  formBody=new FormBody.Builder()
//                .add("code",code)
//                .add("identityAddress", "buQfy8S29vLJ9Xwqg3UqbZXT8WgJcfVZWXKD")
//                .build();
//
//
//        final OkHttpClient okHttpClient = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url(url)
//                .post(formBody)
//                .build();
//
//        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//
//                LogUtils.e(e.getMessage());
////                ToastUtil.showToast(getActivity(), R.string.not_use_wallet_service,Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                String json = response.body().string();
//                LogUtils.e("response:"+ json);
//                if (response.code() == 200) {
//
//                } else {
////                    ToastUtil.showToast(getActivity(),R.string.not_use_wallet_service,Toast.LENGTH_LONG);
//                }
//
//            }
//        });


//        Map<String, String> map = new HashMap<>();
//        map.put("code", code);
//        new HttpHelper(this).post("/techApi/user/verify/v1/bindWeChat", map, null, false, false).result(new HttpListener() {
//            @Override
//            public void success(String data) {
//                SignEntity signEntity = new Gson().fromJson(data, SignEntity.class);
//                if (signEntity.getStatus().equals("0000")) {
//                    Toast.makeText(WXEntryActivity.this, "" + data, Toast.LENGTH_SHORT).show();
//                    //绑定完微信进行做任务
//                    doTaskHttp();
//                    finish();
//                } else {
//                    Toast.makeText(WXEntryActivity.this, "绑定失败", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//
//            }
//
//            @Override
//            public void fail(String error) {
//
//            }
//        });
    }
    //绑定完微信进行做任务
    private void doTaskHttp() {
        Map<String, String> map = new HashMap<>();
        map.put("taskId", 1007+"" );
//        new HttpHelper(this).post("techApi/user/verify/v1/doTheTask", map, null, false, false).result(new HttpListener() {
//            @Override
//            public void success(String data) {
//                SignEntity signEntity = new Gson().fromJson(data, SignEntity.class);
//                String status = signEntity.getStatus();
//                if (status.equals("0000")) {
//                    Toast.makeText(WXEntryActivity.this, "做任务成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(WXEntryActivity.this, "做任务失败", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//            @Override
//            public void fail(String error) {
//
//            }
//        });
    }

}