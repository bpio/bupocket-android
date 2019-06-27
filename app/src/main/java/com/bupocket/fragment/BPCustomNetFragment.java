package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.CustomNodeTypeEnum;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.ToastUtil;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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


    }

    @Override
    protected void setListeners() {
        nodeServiceEt.addTextChangedListener(this);
        walletServiceEt.addTextChangedListener(this);
        setNodeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                saveBtn.setEnabled(true);
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
        topbar.setTitle(R.string.custom_net_title);
        Button rightButton = topbar.addRightTextButton(getString(R.string.save), R.id.skipBackupBtn);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkWalletService()) {
                    CommonUtil.invalidService(nodeServiceEt.getText().toString().trim(), getActivity(), new NodeSettingAdapter.NodeAddressListener() {
                        @Override
                        public void success(String url) {
                            saveServiceInfo();
                            popBackStack();
                        }

                        @Override
                        public void failed() {
                            popBackStack();
                        }
                    });
                }


            }
        });
        saveBtn = topbar.findViewById(R.id.skipBackupBtn);
        saveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_txt_color_gray_2));
        saveBtn.setEnabled(false);
    }

    private void saveServiceInfo() {
        spHelper.put(ConstantsType.CUSTOM_WALLET_SERVICE, walletServiceEt.getText().toString().trim());
        spHelper.put(ConstantsType.CUSTOM_NODE_SERVICE, nodeServiceEt.getText().toString().trim());


    }

    private boolean checkWalletService() {

        String walletService = walletServiceEt.getText().toString().trim();
        if (TextUtils.isEmpty(walletService)) {
            ToastUtil.showToast(getActivity(), R.string.invalid_node_address_hint, Toast.LENGTH_SHORT);
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
            saveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_green));
            saveBtn.setEnabled(true);
        } else {
            saveBtn.setEnabled(false);
            saveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_txt_color_gray_2));
        }
    }


    @SuppressLint("ResourceAsColor")
    private void changeService() {

        int isStart = (int) spHelper.getSharedPreference(ConstantsType.IS_START_CUSTOM_SERVICE, 0);

        if (isStart==CustomNodeTypeEnum.STOP.getServiceType()) {
            spHelper.put(ConstantsType.IS_START_CUSTOM_SERVICE, CustomNodeTypeEnum.START.getServiceType());

            setNodeBtn.setBackgroundResource(R.drawable.shape_corner_green);
            setNodeBtn.setTextColor(getResources().getColor(R.color.app_color_white));
            setNodeBtn.setText(R.string.start_custom_service);
            nodeHintTv.setText(R.string.start_custom_service_hint);

        }else if (isStart==CustomNodeTypeEnum.START.getServiceType()){
            spHelper.put(ConstantsType.IS_START_CUSTOM_SERVICE, CustomNodeTypeEnum.STOP.getServiceType());

            setNodeBtn.setBackgroundResource(R.drawable.shape_corner_white);
            setNodeBtn.setTextColor(getResources().getColor(R.color.app_txt_color_red));
            setNodeBtn.setText(R.string.stop_custom_service);
            nodeHintTv.setText(R.string.stop_custom_service_hint);
        }


    }



}

