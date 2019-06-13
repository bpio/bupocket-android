package com.bupocket.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import butterknife.BindView;

public class SettingUserInfoFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.ivSettingUserIcon)
    ImageView ivSettingUserIcon;
    @BindView(R.id.llSettingUserHead)
    LinearLayout llSettingUserHead;
    @BindView(R.id.tvSettingUserName)
    TextView tvSettingUserName;
    @BindView(R.id.llSettingUserName)
    LinearLayout llSettingUserName;
    private String walletAddress;
    private String walletName;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_setting_userin_info;
    }

    @Override
    protected void initView() {
        initTopBar();
    }

    private void initTopBar() {
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        topbar.setTitle(getString(R.string.settting_user_title));
    }

    @Override
    protected void initData() {

        Bundle arguments = getArguments();
//        args.putString("address",walletAddress);
//        args.putString("walletName",walletName);
//        args.putString("walletIon","walletIon");
        walletAddress = arguments.getString("address", "");
        walletName = arguments.getString("walletName", "");

        tvSettingUserName.setText(walletName);
    }

    @Override
    protected void setListeners() {

        llSettingUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llSettingUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingName();
            }
        });
    }

    private void settingName() {


        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_change_wallet_name);
        qmuiDialog.show();

        TextView cancelTv = qmuiDialog.findViewById(R.id.changeNameCancel);
        final TextView confirmTv = qmuiDialog.findViewById(R.id.changeNameConfirm);
        final EditText walletNewNameEt = qmuiDialog.findViewById(R.id.walletNewNameEt);
        walletNewNameEt.setHint(walletName);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmTv.setClickable(false);
                confirmTv.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmTv.setClickable(false);
                confirmTv.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (walletNewNameEt.getText().toString().trim().length() > 0) {
                    confirmTv.setClickable(true);
                    confirmTv.setEnabled(true);
                }
            }
        };
        walletNewNameEt.addTextChangedListener(watcher);

        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText walletNewNameEt = qmuiDialog.findViewById(R.id.walletNewNameEt);
                String walletNewName = walletNewNameEt.getText().toString().trim();
                if (!CommonUtil.validateNickname(walletNewName)) {
                    Toast.makeText(getActivity(), R.string.error_import_wallet_name_message_txt, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkIdentity(walletAddress)) {
                    spHelper.put("currentIdentityWalletName", walletNewName);
                } else {
                    spHelper.put(walletAddress + "-walletName", walletNewName);
                }

                tvSettingUserName.setText(walletNewName);

                qmuiDialog.dismiss();
            }
        });
    }

}
