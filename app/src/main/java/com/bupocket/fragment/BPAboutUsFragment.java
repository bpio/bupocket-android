package com.bupocket.fragment;


import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CustomNodeTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.HiddenFunctionStatusEnum;
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
    @BindView(R.id.languageLL)
    LinearLayout versionUpdate;
    @BindView(R.id.changeTestLL)
    LinearLayout changeTestLL;
    @BindView(R.id.changeTestNetTV)
    TextView changeTestNetTV;
    @BindView(R.id.customEnvironmentLL)
    LinearLayout customEnvironmentLL;
    @BindView(R.id.newVersionCodeIconIV)
    ImageView newVersionCodeIconIV;
    @BindView(R.id.openTestNetIv)
    ImageView openTestNetIv;
    @BindView(R.id.customTv)
    TextView customTv;

    private final static int CLICKCOUNTS = 5;
    private final static long DURATION = 2 * 1000;

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
        topBar.setBackgroundDividerEnabled(false);
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
        reqUpdateData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSwitchStatus();
    }

    private void setSwitchStatus() {
        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, 0);
        if (isStart == CustomNodeTypeEnum.STOP.getServiceType()) {
            changeTestNetTV.setEnabled(true);
        } else if (isStart == CustomNodeTypeEnum.START.getServiceType()) {
            changeTestNetTV.setEnabled(false);
        }

        isSwitch = SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode();
        if (isSwitch) {
            if (changeTestNetTV.isEnabled()) {
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_checked));
            } else {
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_enable));
            }
        } else {
            changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_normal));
        }
    }

    private void reqUpdateData() {
        newVersionCodeTV.setText(CommonUtil.packageName(mContext));
        VersionService versionService = RetrofitFactory.getInstance().getRetrofit().create(VersionService.class);
        Call<ApiResult<GetCurrentVersionRespDto>> call = versionService.getCurrentVersion(Constants.APP_TYPE_CODE);
        call.enqueue(new retrofit2.Callback<ApiResult<GetCurrentVersionRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCurrentVersionRespDto>> call, Response<ApiResult<GetCurrentVersionRespDto>> response) {
                respDto = response.body();

                if (respDto!=null&&ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                    int verNumberCode = Integer.parseInt(respDto.getData().getVerNumberCode());
                    String verNumber = respDto.getData().getVerNumber();
                    isUpdate = ((int) CommonUtil.packageCode(mContext)) < verNumberCode;
                    if (isUpdate) {
                        newVersionCodeIconIV.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(verNumber)) {
                            newVersionCodeTV.setText("V" + verNumber);
                        }
                    } else {
                        if (newVersionCodeTV != null) {
                            newVersionCodeTV.setText(CommonUtil.packageName(mContext));
                        }

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
                    BPUpgradeManager.getInstance(BPAboutUsFragment.this.getActivity()).init();
                }
            }
        });
        customEnvironmentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPCustomNetFragment());
            }
        });

        initCustomService();


    }

    private void initCustomService() {
        int hiddenFunctionStatus = spHelper.getInt("hiddenFunctionStatus", HiddenFunctionStatusEnum.DISABLE.getCode());
        int hiddenFunctionCustomStatus = spHelper.getInt(ConstantsType.HIDDEN_CUSTOM_SERVICE, HiddenFunctionStatusEnum.DISABLE.getCode());
        if (HiddenFunctionStatusEnum.DISABLE.getCode() == hiddenFunctionStatus) {
            changeTestLL.setVisibility(View.GONE);

            openTestNetIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    straightClick();
                }
            });
        } else {
            changeTestLL.setVisibility(View.VISIBLE);
            if (HiddenFunctionStatusEnum.DISABLE.getCode() == hiddenFunctionCustomStatus) {
                customEnvironmentLL.setVisibility(View.GONE);
                customTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        straightCustomClick();
                    }
                });
            } else {
                customEnvironmentLL.setVisibility(View.VISIBLE);
            }
        }


    }


    private void showSwitchMainNetDialog() {

        DialogUtils.showMessageNoTitleDialog(mContext, getString(R.string.switch_main_net_message_txt), new DialogUtils.KnowListener() {
            @Override
            public void Know() {
                spHelper.put("tokensInfoCache", "");
                spHelper.put("tokenBalance", "");
                changeTestNetTV.setBackground(getResources().getDrawable(R.mipmap.icon_switch_normal));
                startFragment(new HomeFragment());
                mApplication.clearDaoData();
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
                mApplication.clearDaoData();
            }
        });
    }


    long[] mHits = new long[CLICKCOUNTS];

    public void straightClick() {
        // Listening to the straight click 5 times
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] > SystemClock.uptimeMillis() - DURATION) {


            DialogUtils.showConfirmNoTitleDialog(mContext,   getString(R.string.switch_test_net_message_txt)
                  ,
                    new DialogUtils.KnowListener() {
                        @Override
                        public void Know() {
                            SharedPreferencesHelper.getInstance().save("hiddenFunctionStatus", HiddenFunctionStatusEnum.ENABLE.getCode());
                            changeTestLL.setVisibility(View.VISIBLE);
                            ShowSwitchTestNetConfirmDialog();
                        }
                    }).setCancelable(false);

        }
    }

    long[] mHitsCustom = new long[CLICKCOUNTS];

    public void straightCustomClick() {
        // Listening to the straight click 5 times
        System.arraycopy(mHitsCustom, 1, mHitsCustom, 0, mHitsCustom.length - 1);
        mHitsCustom[mHitsCustom.length - 1] = SystemClock.uptimeMillis();
        if (mHitsCustom[0] > SystemClock.uptimeMillis() - DURATION) {
            SharedPreferencesHelper.getInstance().save(ConstantsType.HIDDEN_CUSTOM_SERVICE, HiddenFunctionStatusEnum.ENABLE.getCode());
            customEnvironmentLL.setVisibility(View.VISIBLE);

        }
    }
}
