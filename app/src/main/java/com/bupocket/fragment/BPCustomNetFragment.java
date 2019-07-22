package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.LogTime;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CustomNodeTypeEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.WalletCheckService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.NodeAddressModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPCustomNetFragment extends AbsBaseFragment implements TextWatcher {


    @BindView(R.id.wallet_service_et)
    EditText walletServiceEt;
    @BindView(R.id.node_service_et)
    EditText nodeServiceEt;
    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.setNodeBtn)
    Button setNodeBtn;
    @BindView(R.id.nodeHintTv)
    TextView nodeHintTv;
    @BindView(R.id.visibleStartBtnLL)
    LinearLayout visibleStartBtnLL;
    Unbinder unbinder;
    private Button saveBtn;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_custom_net;
    }

    @Override
    protected void initView() {
        initTopBar();
    }


    @Override
    protected void initData() {

//        walletServiceEt.setText("http://api-bp.bumotest.io/");
//        nodeServiceEt.setText("http://wallet-node.bumotest.io");

        initServiceData();

        changeBtnStatus();
    }

    private void initServiceData() {
        String walletService = (String) spHelper.getSharedPreference(ConstantsType.CUSTOM_WALLET_SERVICE, "");
        String nodeService = (String) spHelper.getSharedPreference(ConstantsType.CUSTOM_NODE_SERVICE, "");
        walletServiceEt.setText(walletService);
        nodeServiceEt.setText(nodeService);


    }

    @Override
    protected void setListeners() {
        nodeServiceEt.addTextChangedListener(this);
        walletServiceEt.addTextChangedListener(this);
        setNodeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //save
                changeService();


            }
        });

    }


    @SuppressLint("ResourceAsColor")
    private void initTopBar() {
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popBackStack();

            }
        });
        topbar.setTitle(R.string.custom_environment);
        saveBtn = topbar.addRightTextButton(getString(R.string.save), R.id.skipBackupBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!walletServiceEt.isEnabled()) {
                    walletServiceEt.setEnabled(true);
                    nodeServiceEt.setEnabled(true);
                    visibleStartBtnLL.setVisibility(View.GONE);
                    saveBtn.setText(R.string.save);
                } else {

                    if (checkWalletService()) {

                        try {
                            final OkHttpClient okHttpClient = new OkHttpClient();
                            final Request request = new Request.Builder()
                                    .url(walletServiceEt.getText().toString().trim()+"healthcheck/")
                                    .build();

                            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(okhttp3.Call call, IOException e) {

                                    LogUtils.e(e.getMessage());
                                    ToastUtil.showToast(getActivity(),R.string.not_use_wallet_service,Toast.LENGTH_LONG);
                                }

                                @Override
                                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                    if (response.code() == 200) {
                                        checkNodeService();
                                    } else {
                                        ToastUtil.showToast(getActivity(),R.string.not_use_wallet_service,Toast.LENGTH_LONG);
                                    }

                                }
                            });
                        } catch (Exception e) {
                            LogUtils.e("");
                            ToastUtil.showToast(getActivity(),R.string.not_use_wallet_service,Toast.LENGTH_LONG);
                        }


                    }
                }


            }
        });
        saveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_green));
    }

    private void checkNodeService() {

        CommonUtil.invalidService(nodeServiceEt.getText().toString().trim(), getActivity(), new NodeSettingAdapter.NodeAddressListener() {
            @Override
            public void success(String url) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveBtn.setText(R.string.edit_node_info);
                        walletServiceEt.setEnabled(false);
                        nodeServiceEt.setEnabled(false);
                        visibleStartBtnLL.setVisibility(View.VISIBLE);
                        saveServiceInfo();
                    }
                });


            }

            @Override
            public void failed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }
        });
    }

    private void saveServiceInfo() {
        spHelper.put(ConstantsType.CUSTOM_WALLET_SERVICE, walletServiceEt.getText().toString().trim());
        spHelper.put(ConstantsType.CUSTOM_NODE_SERVICE, nodeServiceEt.getText().toString().trim());
        spHelper.put(ConstantsType.IS_START_CUSTOM_SERVICE, CustomNodeTypeEnum.STOP.getServiceType());

    }

    private boolean checkWalletService() {

        String walletService = walletServiceEt.getText().toString().trim();
        if (TextUtils.isEmpty(walletService)) {
            ToastUtil.showToast(getActivity(), R.string.add_node_address_title, Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        String nodeService = nodeServiceEt.getText().toString().trim();
        String walletService = walletServiceEt.getText().toString().trim();

        if (!TextUtils.isEmpty(nodeService) && !TextUtils.isEmpty(walletService)) {
            saveBtn.setEnabled(true);
        } else {
            saveBtn.setEnabled(false);
        }
    }


    @SuppressLint("ResourceAsColor")
    private void changeService() {

        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, 0);

        if (isStart == CustomNodeTypeEnum.STOP.getServiceType()) {
            spHelper.put(ConstantsType.IS_START_CUSTOM_SERVICE, CustomNodeTypeEnum.START.getServiceType());
        } else if (isStart == CustomNodeTypeEnum.START.getServiceType()) {
            spHelper.put(ConstantsType.IS_START_CUSTOM_SERVICE, CustomNodeTypeEnum.STOP.getServiceType());
        }

        changeBtnStatus();

        boolean isSwitch = SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode();
        if (isSwitch) {
            BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
        } else {
            BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
        }

        startFragment(new HomeFragment());
    }

    private void changeBtnStatus() {
        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, -1);

        if (isStart == -1) {
            visibleStartBtnLL.setVisibility(View.GONE);
            saveBtn.setEnabled(false);
        } else {
            visibleStartBtnLL.setVisibility(View.VISIBLE);

            walletServiceEt.setEnabled(false);
            nodeServiceEt.setEnabled(false);

            if (isStart == CustomNodeTypeEnum.STOP.getServiceType()) {
                saveBtn.setText(R.string.edit_node_info);

                setNodeBtn.setBackgroundResource(R.drawable.shape_corner_green);
                setNodeBtn.setTextColor(getResources().getColor(R.color.app_color_white));
                setNodeBtn.setText(R.string.start_custom_service);
                nodeHintTv.setText(R.string.start_custom_service_hint);

            } else if (isStart == CustomNodeTypeEnum.START.getServiceType()) {
                saveBtn.setVisibility(View.INVISIBLE);
                setNodeBtn.setBackgroundResource(R.drawable.shape_corner_white_btn);
                setNodeBtn.setTextColor(getResources().getColor(R.color.app_txt_color_red));
                setNodeBtn.setText(R.string.stop_custom_service);
                nodeHintTv.setText(R.string.stop_custom_service_hint);
            }
        }


    }


    public static void main(String[] args) {

    }

}

