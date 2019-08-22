package com.bupocket.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.http.api.HttpCallBack;
import com.bupocket.http.api.OkHttpUtils;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.WeChatService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.WeChatInfoModel;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.ToastUtil;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_LOGIN == resp.getType()) {
                    Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
                    Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token


                        String code = ((SendAuth.Resp) resp).code;
                        Toast.makeText(this, "wx:"+code, Toast.LENGTH_LONG).show();
                        //这里判断类型是微信登录还是绑定微信账号
                        String state = ((SendAuth.Resp) resp).state;
                        SendAuth.Resp resp1=((SendAuth.Resp) resp);

                        LogUtils.e("wx:"+resp1.code+"\n"+resp1.url+"\n"+resp1.country+"\n"+resp1.lang);

                        //判断是绑定微信账号
                        if (state.equals("wechat_sdk_bind")) {
                            /*
                             * 绑定微信
                             * */
                            doBindWxHttp(code);
                        } else {
                            /*
                             * 微信登录 获取用户信息
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
        }
        //支付成功
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("支付结果");
            builder.setMessage(resp.errCode + "");
            builder.show();
//            Log.d("info", "onPayFinish,errCode=" + resp.errCode);
//            if (resp.errCode == 0) {
//                SharedPreUtils.put(this,"success",0);
//                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent("ACTION_PAY");
//                sendBroadcast(intent);
//                this.finish();
//            } else if (resp.errCode == -1) {
//                Toast.makeText(this, "配置错误", Toast.LENGTH_SHORT).show();
//                this.finish();
//            } else if (resp.errCode == -2) {
//                Toast.makeText(this, "用户取消", Toast.LENGTH_SHORT).show();
//                this.finish();
//            }
//        }else {
//            Toast.makeText(this, resp.errStr, Toast.LENGTH_SHORT).show();
//        }
        }


    }

    private void getAccessToken(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);

        String wxUrl="https://api.weixin.qq.com/techApi/user/v1/weChatLogin";



        OkHttpUtils.post(wxUrl, map, new HttpCallBack() {
            @Override
            public void onSuccess(ApiResult result, Object data) {

                LogUtils.e("data:"+data.toString());
//                if (data != null){
//                    Toast.makeText(WXEntryActivity.this, ""+data, Toast.LENGTH_SHORT).show();
//                    Log.i("TAG","WxBind"+data);
//                    LoginEntity loginEntity = new Gson().fromJson(data, LoginEntity.class);
//                    LoginEntity.ResultBean result = loginEntity.getResult();
//                    String nickName = result.getNickName();
//                    String headPic = result.getHeadPic();
//                    int userId = result.getUserId();
//                    String sessionId = result.getSessionId();
//                    SharedPreUtils.put(WXEntryActivity.this, "nickName", nickName);
//                    SharedPreUtils.put(WXEntryActivity.this, "headPic", headPic);
//                    SharedPreUtils.put(WXEntryActivity.this, "userId", userId);
//                    SharedPreUtils.put(WXEntryActivity.this, "sessionId", sessionId);
//                    SharedPreUtils.put(WXEntryActivity.this,"SUCCESSUP",true);
//                    SharedPreUtils.put(WXEntryActivity.this, "isLogin", true);
//                    Toast.makeText(WXEntryActivity.this, ""+nickName, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(WXEntryActivity.this, ""+headPic, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(WXEntryActivity.this, ""+userId, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(WXEntryActivity.this, ""+sessionId, Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
//                    finish();
//                }
            }

            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onComplete() {

            }
        });

//        new HttpHelper(this).post("/techApi/user/v1/weChatLogin",map,null,false,false).result(new HttpListener() {
//            @Override
//            public void success(String data) {
//                if (data != null){
//                    Toast.makeText(WXEntryActivity.this, ""+data, Toast.LENGTH_SHORT).show();
//                    Log.i("TAG","WxBind"+data);
//                    LoginEntity loginEntity = new Gson().fromJson(data, LoginEntity.class);
//                    LoginEntity.ResultBean result = loginEntity.getResult();
//                    String nickName = result.getNickName();
//                    String headPic = result.getHeadPic();
//                    int userId = result.getUserId();
//                    String sessionId = result.getSessionId();
//                    SharedPreUtils.put(WXEntryActivity.this, "nickName", nickName);
//                    SharedPreUtils.put(WXEntryActivity.this, "headPic", headPic);
//                    SharedPreUtils.put(WXEntryActivity.this, "userId", userId);
//                    SharedPreUtils.put(WXEntryActivity.this, "sessionId", sessionId);
//                    SharedPreUtils.put(WXEntryActivity.this,"SUCCESSUP",true);
//                    SharedPreUtils.put(WXEntryActivity.this, "isLogin", true);
//                    Toast.makeText(WXEntryActivity.this, ""+nickName, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(WXEntryActivity.this, ""+headPic, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(WXEntryActivity.this, ""+userId, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(WXEntryActivity.this, ""+sessionId, Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
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

    //绑定微信账号
    private void doBindWxHttp(String code) {

        Map<String, Object> map = new HashMap<>();
        map.put("code", code);

        String wxUrl="https://api.weixin.qq.com/techApi/user/verify/v1/bindWeChat";
        String url="http://192.168.15.43:8080/bu_pocket_api_war/wx/v1/bind";


        WeChatService weChatService = RetrofitFactory.getInstance().getRetrofit().create(WeChatService.class);
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("wxCode",code);
        map1.put("identityAddress", "buQfy8S29vLJ9Xwqg3UqbZXT8WgJcfVZWXKD");
        weChatService.getWeChatInfo(map1).enqueue(new Callback<ApiResult<WeChatInfoModel>>() {
            @Override
            public void onResponse(Call<ApiResult<WeChatInfoModel>> call, Response<ApiResult<WeChatInfoModel>> response) {



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