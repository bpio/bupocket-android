package com.bupocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.base.BaseFragmentActivity;
import com.bupocket.fragment.BPBackupWalletFragment;
import com.bupocket.fragment.BPCreateWalletFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.manager.BPUpgradeManager;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class BPMainActivity extends BaseFragmentActivity {
    private static final String KEY_FRAGMENT = "key_fragment";
    private static final int VALUE_FRAGMENT_HOME = 0;
    private static final int VALUE_FRAGMENT_NOTCH_HELPER = 1;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected int getContextViewId() {
        return R.id.assetLinearLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesHelper = new SharedPreferencesHelper(BPMainActivity.this, "buPocket");
        try{
            if(savedInstanceState == null){
                BaseFragment baseFragment = getFirstFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(getContextViewId(), baseFragment, baseFragment.getClass().getSimpleName())
                        .addToBackStack(baseFragment.getClass().getSimpleName())
                        .commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        BPUpgradeManager.getInstance(this).init();
        loadSRLData();
    }

    private void loadSRLData() {
        ClassicsHeader.REFRESH_HEADER_PULLING = getString(R.string.srl_header_pulling);//"下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.srl_header_refreshing);//"正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = getString(R.string.srl_header_loading);//"正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.srl_header_release);//"释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.srl_header_finish);//"刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = getString(R.string.srl_header_failed);//"刷新失败";
        ClassicsHeader.REFRESH_HEADER_UPDATE = getString(R.string.srl_header_update);//"上次更新 M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_UPDATE = getString(R.string.srl_header_update);//"'Last update' M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_SECONDARY = getString(R.string.srl_header_secondary);//"释放进入二楼"

        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.srl_footer_pulling);//"上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.srl_footer_release);//"释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.srl_footer_loading);//"正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.srl_footer_refreshing);//"正在加载...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.srl_footer_finish);//"加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.srl_footer_failed);//"加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.srl_footer_nothing);//"全部加载完成";
    }

    private BaseFragment getFirstFragment() {
        BaseFragment fragment;
        // 检查本地是否第一次创建钱包
        String isFirstCreateWallet = sharedPreferencesHelper.getSharedPreference("isFirstCreateWallet", "").toString();
        if(isFirstCreateWallet == ""){
            String createWalletStep = sharedPreferencesHelper.getSharedPreference("createWalletStep","").toString();
            if(CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode().equals(createWalletStep)){
                fragment = new BPBackupWalletFragment();
            } else if(CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode().equals(createWalletStep)){
                fragment = new HomeFragment();
            }else{
                fragment = new BPCreateWalletFragment();
            }
        }else {
            fragment = new HomeFragment();
        }
        return fragment;
    }

    public static Intent createNotchHelperIntent(Context context) {
        Intent intent = new Intent(context, BPMainActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_NOTCH_HELPER);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if(getCurrentFragment().getTag().equals("BPBackupWalletFragment")){
            ((BPBackupWalletFragment)getCurrentFragment()).onBackPressed();
        }else if(getCurrentFragment().getTag().equals("HomeFragment")){
            ((HomeFragment)getCurrentFragment()).onBackPressed();
        }else if(getCurrentFragment().getTag().equals("BPCreateWalletFragment")){
            ((BPCreateWalletFragment)getCurrentFragment()).onBackPressed();
        }else{

            ((BaseFragment) getCurrentFragment()).popBackStack();
//            super.onBackPressed();
        }
    }
}
