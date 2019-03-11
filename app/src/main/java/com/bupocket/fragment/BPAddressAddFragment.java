package com.bupocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.AddressBookService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.view.DrawableEditText;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bumo.encryption.key.PrivateKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPAddressAddFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.saveAddressBtn)
    QMUIRoundButton mSaveAddressBtn;
    @BindView(R.id.addressNameEt)
    EditText mAddressNameEt;
    @BindView(R.id.addressDescribeEt)
    EditText mAddressDescribeEt;
    @BindView(R.id.newAddressEt)
    DrawableEditText mNewAddressEt;

    private String identityAddress;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_address_add, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        buildWatcher();
        setListener();
    }

    private void buildWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mSaveAddressBtn.setEnabled(false);
                mSaveAddressBtn.setBackgroundResource(R.drawable.radius_button_disable_bg);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveAddressBtn.setEnabled(false);
                mSaveAddressBtn.setBackgroundResource(R.drawable.radius_button_disable_bg);
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signAddressName = mAddressNameEt.getText().toString().trim().length() > 0;
                boolean signAddress = mNewAddressEt.getText().toString().trim().length() > 0;
                if(signAddressName && signAddress){
                    mSaveAddressBtn.setEnabled(true);
                    mSaveAddressBtn.setBackgroundResource(R.drawable.radius_button_able_bg);
                }else {
                    mSaveAddressBtn.setEnabled(false);
                    mSaveAddressBtn.setBackgroundResource(R.drawable.radius_button_disable_bg);
                }
            }
        };
        mAddressNameEt.addTextChangedListener(textWatcher);
        mNewAddressEt.addTextChangedListener(textWatcher);
    }

    private void setListener() {
        mNewAddressEt.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                startScan();
            }
        });
        mSaveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAddress();
            }
        });
    }

    private void submitAddress() {
        if(!addressNameFlag()){
            return;
        }else if(!describeFlag()){
            return;
        }else if(!addressFlag()){
            return;
        }
        final String addressName = mAddressNameEt.getText().toString().trim();
        final String describe = mAddressDescribeEt.getText().toString().trim();
        final String linkmanAddress = mNewAddressEt.getText().toString().trim();

        AddressBookService addressBookService = RetrofitFactory.getInstance().getRetrofit().create(AddressBookService.class);
        Call<ApiResult> call;
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("identityAddress",identityAddress);
        paramsMap.put("linkmanAddress",linkmanAddress);
        paramsMap.put("nickName",addressName);
        paramsMap.put("remark",describe);
        call = addressBookService.addAddress(paramsMap);
        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                ApiResult respDto = response.body();
                if(null != respDto){
                    if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                        Toast.makeText(getContext(),getString(R.string.save_address_success_message_txt),Toast.LENGTH_SHORT).show();
                        popBackStack();
                    }else if(ExceptionEnum.ADDRESS_ALREADY_EXISTED.getCode().equals(respDto.getErrCode())){
                        Toast.makeText(getContext(),getString(R.string.address_already_exist_message_txt),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(),getString(R.string.save_address_failure_message_txt),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),getString(R.string.save_address_failure_message_txt),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                Toast.makeText(getContext(),getString(R.string.save_address_failure_message_txt),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean addressNameFlag() {
        final String addressName = mAddressNameEt.getText().toString().trim();
        if (!CommonUtil.validateNickname(addressName)) {
            Toast.makeText(getActivity(), R.string.address_name_format_error_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean describeFlag() {
        final String describe = mAddressDescribeEt.getText().toString().trim();
        if(!CommonUtil.validateAddressDescribe(describe)){
            Toast.makeText(getActivity(), R.string.describe_format_error_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean addressFlag() {
        final String address = mNewAddressEt.getText().toString().trim();
        if(!PrivateKey.isAddressValid(address)){
            Toast.makeText(getActivity(), R.string.address_format_error_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        identityAddress = sharedPreferencesHelper.getSharedPreference("identityId","").toString();
    }

    private void startScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getResources().getString(R.string.wallet_scan_notice));
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
            } else {
                String destAddress = result.getContents();
                mNewAddressEt.setText(destAddress);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.add_address_title);
    }
}
