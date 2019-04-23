package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.ExceptionEnum;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPScanErrorFragment extends BaseFragment {
    @BindView(R.id.confirmBtn)
    QMUIRoundButton mConfirmBtn;
    @BindView(R.id.closeIv)
    ImageView mCloseTv;
    @BindView(R.id.errorTypeTv)
    TextView mErrorTypeTv;
    @BindView(R.id.errorMessageTv)
    TextView mErrorMessageTv;

    private String errorCode;
    private String errorMessage;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan_error, null);
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
        if(null != bundle){
            errorCode = bundle.getString("errorCode");
            errorMessage = bundle.getString("errorMessage");
            if (errorCode.equals(ExceptionEnum.ERROR_TIMEOUT.getCode())) {
                errorMessage=mContext.getResources().getString(R.string.error_timeout);
            }
        }
    }

    private void initUI() {
        if(!ExceptionEnum.SUCCESS.equals(errorCode)){
            mErrorTypeTv.setText(errorMessage);
            mErrorMessageTv.setText(getResources().getString(R.string.refresh_qr_tips_txt));
        }
    }

    private void setListener() {
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mCloseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
