package com.bupocket.manager;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.enums.LanguageEnum;
import com.bupocket.http.api.dto.resp.GetCurrentVersionRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.UpdateAppHttpUtil;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

import java.util.Locale;

public class BPUpgradeManager {
    public static final int INVALIDATE_VERSION_CODE = -1;
    private static BPUpgradeManager bpUpgradeManager;
    private Activity mActivity;

    public BPUpgradeManager(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public static final BPUpgradeManager getInstance(Activity mActivity){
        if(bpUpgradeManager == null){
            bpUpgradeManager = new BPUpgradeManager(mActivity);
        }
        bpUpgradeManager.mActivity=mActivity;
        return bpUpgradeManager;
    }

    public void init(){
        new UpdateAppManager
                .Builder()
                .setActivity(mActivity)
                .setThemeColor(0xFF02CA71)
                .setUpdateUrl(Constants.WEB_SERVER_DOMAIN)
                .setHttpManager(new UpdateAppHttpUtil())
                .setTopPic(R.mipmap.upgrade_dialog_bg)
                .build()
                .checkNewApp(new UpdateCallback(){
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        LogUtils.e("downData"+json);
                        int language = SharedPreferencesHelper.getInstance().getInt("currentLanguage", LanguageEnum.UNDEFINED.getId());
                        if(language == LanguageEnum.UNDEFINED.getId()){
                            String myLocaleStr = Locale.getDefault().getLanguage();
                            switch (myLocaleStr){
                                case "zh": {
                                    language = LanguageEnum.CHINESE.getId();
                                    break;
                                }
                                case "en": {
                                    language = LanguageEnum.ENGLISH.getId();
                                    break;
                                }
                                default : {
                                    language = LanguageEnum.ENGLISH.getId();
                                }
                            }
                        }
                        GetCurrentVersionRespDto resp = JSON.parseObject(json, GetCurrentVersionRespDto.class);

                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        updateAppBean
                                .setUpdate(check(resp))
                                .setNewVersion(resp.getVerNumber())
                                .setApkFileUrl(resp.getDownloadLink())
                                .setTargetSize(CommonUtil.isNull(resp.getAppSize()) ? "" : resp.getAppSize())
                                .setUpdateLog(language == LanguageEnum.CHINESE.getId() ? resp.getVerContents() : resp.getEnglishVerContents())
                                .setConstraint(resp.getVerType() == 0 ? false : true);

                        return updateAppBean;
                    }

                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        if (updateApp.isConstraint()) {

                        } else {

                        }

                        updateAppManager.showDialogFragment();

                    }
                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
//                        CProgressDialogUtils.showProgressDialog(mActivity);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
//                        CProgressDialogUtils.cancelProgressDialog(mActivity);
                    }

                });
    }
    private String check(GetCurrentVersionRespDto resp){
        int oldVersion = CommonUtil.packageCode(mActivity.getBaseContext());
        if(resp.getVerNumberCode() != null){
            int currentVersion = Integer.parseInt(resp.getVerNumberCode());
            if(currentVersion > oldVersion){
                return "Yes";
            }
        }
        return "No";
    }
}
