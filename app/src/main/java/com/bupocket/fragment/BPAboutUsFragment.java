package com.bupocket.fragment;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.VersionService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCurrentVersionRespDto;
import com.bupocket.manager.BPUpgradeManager;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class BPAboutUsFragment extends AbsBaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout topBar;
    @BindView(R.id.versionCodeTV)
    TextView versionCodeTV;
    @BindView(R.id.versionInfoListLL)
    LinearLayout versionInfoListLL;
    @BindView(R.id.newVersionCodeTV)
    TextView newVersionCodeTV;
    @BindView(R.id.versionUpdate)
    LinearLayout versionUpdate;
    @BindView(R.id.changeTestLL)
    LinearLayout changeTestLL;
    @BindView(R.id.changeTestNetTV)
    TextView changeTestNetTV;
    @BindView(R.id.customEnvironmentLL)
    LinearLayout customEnvironmentLL;
    @BindView(R.id.newVersionCodeIconIV)
    ImageView newVersionCodeIconIV;


    private boolean isSwitch;
    private ApiResult<GetCurrentVersionRespDto> respDto;
    private boolean isUpdate;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_about_us;
    }

    @Override
    protected void initView() {
        initTopBar();
        versionCodeTV.setText(getString(R.string.about_us_version_code).concat(CommonUtil.packageName(mContext)));
    }

    private void initTopBar() {
        topBar.setTitle(R.string.profile_about_us);
        topBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    @Override
    protected void initData() {
        isSwitch = SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode();
        if (isSwitch) {
            changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_checked));
        } else {
            changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_normal));
        }

        reqUpdateData();
    }

    private void reqUpdateData() {
        VersionService versionService = RetrofitFactory.getInstance().getRetrofit().create(VersionService.class);
        Call<ApiResult<GetCurrentVersionRespDto>> call = versionService.getCurrentVersion(Constants.APP_TYPE_CODE);
        call.enqueue(new retrofit2.Callback<ApiResult<GetCurrentVersionRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCurrentVersionRespDto>> call, Response<ApiResult<GetCurrentVersionRespDto>> response) {
                respDto = response.body();

                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    int verNumber = Integer.parseInt(respDto.getData().getVerNumberCode());
                    newVersionCodeTV.setText("V" + respDto.getData().getVerNumber());
                    isUpdate = ((int) CommonUtil.packageCode(mContext)) < verNumber;
                    if (isUpdate) {
                        newVersionCodeIconIV.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResult<GetCurrentVersionRespDto>> call, Throwable t) {

            }
        });
    }


    @Override
    protected void setListeners() {
        changeTestNetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitch) {
                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                    BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
                    showSwitchMainNetDialog();
                } else {
                    ShowSwitchTestNetConfirmDialog();
                }
            }
        });
        versionInfoListLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new VersionLogFragment());
            }
        });

        versionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUpdate) {
                    BPUpgradeManager.getInstance(getActivity()).init();
                }
            }
        });
        customEnvironmentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPCustomNetFragment());
            }
        });

    }


    private void showSwitchMainNetDialog() {

        DialogUtils.showMessageNoTitleDialog(mContext, getString(R.string.switch_main_net_message_txt), new DialogUtils.KnowListener() {
            @Override
            public void Know() {
                spHelper.put("tokensInfoCache", "");
                spHelper.put("tokenBalance", "");
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_normal));
                startFragment(new HomeFragment());
            }
        });

    }


    private void ShowSwitchTestNetConfirmDialog() {

        DialogUtils.showMessageNoTitleDialog(mContext, getString(R.string.switch_to_test_net_message_txt), new DialogUtils.KnowListener() {
            @Override
            public void Know() {
                SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
                BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
                spHelper.put("tokensInfoCache", "");
                spHelper.put("tokenBalance", "");
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_checked));
                startFragment(new HomeFragment());
            }
        });
    }

}
