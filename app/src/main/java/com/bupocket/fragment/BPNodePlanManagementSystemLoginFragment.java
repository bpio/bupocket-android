package com.bupocket.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.NodePlanManagementSystemService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.LogUtils;
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
    Button mLoginCancelBtn;
    @BindView(R.id.loginConfirmBtn)
    Button mLoginConfirmBtn;
    @BindView(R.id.appNameTv)
    TextView mAppNameTv;
    @BindView(R.id.appPicIv)
    ImageView mAppPicIv;
    @BindView(R.id.tvSystemLoginInfo)
    TextView tvSystemLoginInfo;


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
        if (null != bundle) {
            appId = bundle.getString("appId");
            uuid = bundle.getString("uuid");
            address = bundle.getString("address");
            appName = bundle.getString("appName");
            appPic = bundle.getString("appPic");
        }
    }

    private void initUI() {

        Glide.with(getContext())
                .load(appPic)
                .error(R.mipmap.icon_token_default_icon)
                .into(mAppPicIv);


        if (!TextUtils.isEmpty(appName)) {
            mAppNameTv.setText(appName);
            String format = String.format(getString(R.string.scan_qr_login_management_system_note_txt).toString(), appName);
            tvSystemLoginInfo.setText(format);
        }

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
                paramsMap.put("uuid", uuid);
                paramsMap.put("appId", appId);
                paramsMap.put("address", address);
                call = nodePlanManagementSystemService.userScanQrConfirmLogin(paramsMap);
                call.enqueue(new Callback<ApiResult>() {
                    @Override
                    public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                        ApiResult respDto = response.body();
                        if (null != respDto) {
                            if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                popBackStack();
                            } else {


                                Bundle argz = new Bundle();
                                argz.putString("errorCode", respDto.getErrCode());
                                BPScanErrorFragment bpNodePlanManagementSystemLoginErrorFragment = new BPScanErrorFragment();
                                bpNodePlanManagementSystemLoginErrorFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpNodePlanManagementSystemLoginErrorFragment);
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResult> call, Throwable t) {
                        popBackStack();
                        Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
