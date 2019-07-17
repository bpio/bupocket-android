package com.bupocket.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.BPSendStatusFragment;
import com.bupocket.fragment.BPTransactionTimeoutFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.bupocket.voucher.BPSendVoucherStatusFragment;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseFragment extends QMUIFragment {

    private final static String BP_FILE_NAME = "buPocket";
    public static final int TRANSFER_CODE = 200001;
    public SharedPreferencesHelper spHelper;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        if (spHelper == null) {
            spHelper = new SharedPreferencesHelper(getContext(), BP_FILE_NAME);
        }

    }


    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }


    public String getWalletAddress() {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return spHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        return currentIdentityWalletAddress;
    }

    public boolean checkIdentity(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (address.equals(currentAccAddr))
            return true;
        else
            return false;
    }

    /**
     * @return false currentAccAddr  true currentWalletAddress
     */
    private boolean isCurrentAddress() {
        String currentIdentityWalletAddress = spHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (TextUtils.isEmpty(currentIdentityWalletAddress)) {
            return false;
        }
        String currentAccAddr = spHelper.getSharedPreference("currentAccAddr", "").toString();
        if (currentAccAddr.equals(currentIdentityWalletAddress)) {
            return false;
        }
        return true;
    }


    public String getBPAccountData() {
        String accountBPData = spHelper.getSharedPreference("BPData", "").toString();
        if (isCurrentAddress()) {
            return spHelper.getSharedPreference(spHelper.getSharedPreference("currentWalletAddress", "").toString() + "-BPdata", "").toString();
        }
        return accountBPData;
    }





    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getTag().getClass().toString()); //统计页面("MainScreen"为页面名称，可自定义)
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getTag());
    }


    @Override
    public void popBackStack() {
        super.popBackStack();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BPApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }


}
