package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.NodePlanManagementSystemService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.UserScanQrLoginDto;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPNodePlanManagementSystemLoginFragment extends BaseFragment {
    @BindView(R.id.closeIv)
    ImageView mCloseIv;
    @BindView(R.id.loginCancelBtn)
    QMUIRoundButton mLoginCancelBtn;
    @BindView(R.id.loginConfirmBtn)
    QMUIRoundButton mLoginConfirmBtn;
    @BindView(R.id.appNameTv)
    TextView mAppNameTv;
    @BindView(R.id.appPicIv)
    ImageView mAppPicIv;

    private String appId;
    private String uuid;
    private String address;
    private String appName;
    private String appPic;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_plan_management_system_login, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle){
            appId = bundle.getString("appId");
            uuid = bundle.getString("uuid");
            address = bundle.getString("address");
            appName = bundle.getString("appName");
            appPic = bundle.getString("appPic");
        }
    }

    private void initUI() {
        mAppNameTv.setText(appName);
        Picasso.get().load(appPic).into(mAppPicIv);
    }

    private void setListener() {
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mLoginCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mLoginConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NodePlanManagementSystemService nodePlanManagementSystemService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanManagementSystemService.class);
                Call<ApiResult> call;
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("uuid",uuid);
                paramsMap.put("appId",appId);
                paramsMap.put("address",address);
                call = nodePlanManagementSystemService.userScanQrConfirmLogin(paramsMap);
                call.enqueue(new Callback<ApiResult>() {
                    @Override
                    public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                        ApiResult respDto = response.body();
                        if(null != respDto){
                            if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                                popBackStack();
                            }else{
                                Bundle argz = new Bundle();
                                argz.putString("errorCode",respDto.getErrCode());
                                BPNodePlanManagementSystemLoginErrorFragment bpNodePlanManagementSystemLoginErrorFragment = new BPNodePlanManagementSystemLoginErrorFragment();
                                bpNodePlanManagementSystemLoginErrorFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpNodePlanManagementSystemLoginErrorFragment);
                            }
                        }else {
                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResult> call, Throwable t) {
                        popBackStack();
                        Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
