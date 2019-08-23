package com.bupocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CustomNodeTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.LanguageEnum;
import com.bupocket.enums.WXBindEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.WeChatService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.WeChatInfoModel;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.WalletCurrentUtils;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPProfileHomeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.profileAddressTv)
    TextView profileAddressTv;
    @BindView(R.id.llProfileIdentity)
    LinearLayout llProfileIdentity;
    @BindView(R.id.ivProfileAddressManage)
    RelativeLayout ivProfileAddressManage;
    @BindView(R.id.ivProfileWalletManage)
    RelativeLayout ivProfileWalletManage;
    @BindView(R.id.settingIcon)
    ImageView settingIcon;
    @BindView(R.id.addressBookIcon)
    ImageView addressBookIcon;
    @BindView(R.id.manageWalletIcon)
    ImageView manageWalletIcon;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentAccNick;

    @BindView(R.id.userNick)
    TextView userNickTx;

    @BindView(R.id.helpFeedbackRL)
    RelativeLayout mHelpRL;
    @BindView(R.id.settingRL)
    LinearLayout mSettingRL;
    @BindView(R.id.versionNameTv)
    TextView mVersionNameTv;
    @BindView(R.id.identityHeadRiv)
    ImageView identityHeadRiv;
    @BindView(R.id.versionRL)
    RelativeLayout mVersionRl;
    @BindView(R.id.nodeSettingRl)
    RelativeLayout nodeSettingRl;
    @BindView(R.id.languageLL)
    LinearLayout languageLL;
    @BindView(R.id.llProfileProtocol)
    LinearLayout llProfileProtocol;
    @BindView(R.id.tvProfileCurrency)
    TextView tvProfileCurrency;
    @BindView(R.id.newVersionCodeTV)
    TextView tvProfileLanguage;
    @BindView(R.id.wxBindTV)
    TextView wxBindTv;

    private String identityId;
    private String identityAddress;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }


    private void init() {
        initView();
        initTopBar();
        initData();
        setListener();
    }


    private void initTopBar() {
        topbar.setBackgroundDividerEnabled(false);
        topbar.setTitle(R.string.tabbar_profile_txt);
        Button button = topbar.addLeftTextButton("", R.id.topbar_left_arrow);
        button.setTextColor(getResources().getColor(R.color.app_color_green));

        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, 0);
        if (isStart == CustomNodeTypeEnum.START.getServiceType()) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            button.setText(getString(R.string.custom_environment));
        } else if (SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            button.setText(getString(R.string.current_test_message_txt));
        }
    }

    private void initView() {

        String currencyType = spHelper.getSharedPreference("currencyType", "CNY").toString();
        tvProfileCurrency.setText(currencyType);

        int language = SharedPreferencesHelper.getInstance().getInt("currentLanguage", LanguageEnum.UNDEFINED.getId());
        if (LanguageEnum.UNDEFINED.getId() == language) {
            String myLocaleStr = getContext().getResources().getConfiguration().locale.getLanguage();
            switch (myLocaleStr) {
                case "zh": {
                    language = LanguageEnum.CHINESE.getId();
                    break;
                }
                case "en": {
                    language = LanguageEnum.ENGLISH.getId();
                    break;
                }
                default: {
                    language = LanguageEnum.ENGLISH.getId();
                }
            }
        }
        if (LanguageEnum.CHINESE.getId() == language) {

            tvProfileLanguage.setText(getString(R.string.language_cn));
        } else {
            tvProfileLanguage.setText(getString(R.string.language_en));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
    }


    @Override
    public void onResume() {
        super.onResume();

        isWeChatBind();
    }

    private void initUI() {
        mVersionNameTv.setText(CommonUtil.packageName(getContext()));

    }

    private void setListener() {
        nodeSettingRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoManageWalletFragment();
            }
        });
        mHelpRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHelpFeedbackFragment();
            }
        });
        mSettingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingFragment();
            }
        });
        languageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLanguageFragment();
            }
        });


//        }
        mVersionRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAboutUsFragment());
            }
        });

        llProfileIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName", currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });
        ivProfileAddressManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAddressBookFragment());
            }
        });

        ivProfileWalletManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPWalletsHomeFragment());
            }
        });

        llProfileProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPUserTermsFragment());
            }
        });

        wxBindTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindWeChat();
            }
        });
    }


    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        userNickTx.setText(currentAccNick);
        identityId = sharedPreferencesHelper.getSharedPreference("identityId", "").toString();
        identityAddress = spHelper.getSharedPreference("currentAccAddr", "").toString();
        profileAddressTv.setText(AddressUtil.anonymous(identityId));


        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, 0);
        if (isStart == CustomNodeTypeEnum.STOP.getServiceType()) {
            nodeSettingRl.setVisibility(View.VISIBLE);
        } else if (isStart == CustomNodeTypeEnum.START.getServiceType()) {
            nodeSettingRl.setVisibility(View.GONE);
        }


    }


    private void isWeChatBind() {

        String bindState = (String) spHelper.getSharedPreference(ConstantsType.BIND_WECHAT_STATE, "");

        if (TextUtils.isEmpty(bindState)) {
            queryBindState();
            CommonUtil.setHeadIvRes(identityAddress, identityHeadRiv, spHelper);
        } else if (bindState.equals(WXBindEnum.BIND_WECHAT.getCode())){
            setWechatInfo();
        }





    }

    private void setWechatInfo() {
        String wxHeadImgUrl = (String) spHelper.getSharedPreference(ConstantsType.WX_HEAD_IMG_URL, "");
        if (!wxHeadImgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(wxHeadImgUrl)
                    .into(identityHeadRiv);

            wxBindTv.setText(R.string.wx_is_binding);
            wxBindTv.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
            wxBindTv.setClickable(false);
        }
    }

    private void queryBindState() {
        WeChatService weChatService = RetrofitFactory.getInstance().getRetrofit().create(WeChatService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.IDENTITY_ADDRESS, WalletCurrentUtils.getIdentityAddress(spHelper));
        weChatService.getWalletIdentityInfo(map).enqueue(new Callback<ApiResult<WeChatInfoModel>>() {
            @Override
            public void onResponse(Call<ApiResult<WeChatInfoModel>> call, Response<ApiResult<WeChatInfoModel>> response) {
                ApiResult<WeChatInfoModel> body = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    WeChatInfoModel data = body.getData();
                    String isBindWx = data.getIsBindWx();
                    if (WXBindEnum.BIND_WECHAT.getCode().equals(isBindWx)) {

                        String wxHeadImgUrl = data.getWxHeadImgUrl();
                        if (!TextUtils.isEmpty(wxHeadImgUrl)) {
                            spHelper.put(ConstantsType.WX_HEAD_IMG_URL, wxHeadImgUrl);
                        }


                        spHelper.put(ConstantsType.BIND_WECHAT_STATE, isBindWx);


                        setWechatInfo();

                    } else if (WXBindEnum.UNBIND_WECHAT.getCode().equals(isBindWx)) {
                        spHelper.put(ConstantsType.BIND_WECHAT_STATE, isBindWx);
                    }


                }

            }

            @Override
            public void onFailure(Call<ApiResult<WeChatInfoModel>> call, Throwable t) {

            }
        });
    }


    private void bindWeChat() {


        DialogUtils.showConfirmNoTitleDialog(mContext,
                getString(R.string.dialog_bind_wx_info),
                getString(R.string.wachat_bind),
                new DialogUtils.KnowListener() {
                    @Override
                    public void Know() {
                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = ConstantsType.WX_SCOPE;
                        req.state = ConstantsType.WX_STATE;
                        mApplication.getWxApi().sendReq(req);
                    }
                });


    }

    private void gotoChangePwdFragment() {
        startFragment(new BPChangePwdFragment());
    }

    private void gotoHelpFeedbackFragment() {
        startFragment(new BPHelpFeedbackFragment());
    }

    private void gotoSettingFragment() {
        startFragment(new BPCurrencyFragment2());
    }

    private void gotoManageWalletFragment() {
        startFragment(new BPNodeSettingFragment());
    }

    private void goLanguageFragment() {
        startFragment(new BPLanguageFragment());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.e("bpProfileHomeFragment");

    }
}
