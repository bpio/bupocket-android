package com.bupocket.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.adaptor.HeadIconAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.model.HeadIconModel;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;

import butterknife.BindView;

public class SettingWalletInfoFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.ivSettingUserIcon)
    ImageView ivSettingUserIcon;
    @BindView(R.id.settingWalletHeadLL)
    LinearLayout llSettingUserHead;
    @BindView(R.id.settingWalletNameTv)
    TextView tvSettingUserName;
    @BindView(R.id.settingWalletNameLL)
    LinearLayout llSettingUserName;
    private String walletAddress;
    private String walletName;

    int[] walletHeadResList= new int[]{
            R.mipmap.ic_wallet_head_0,
            R.mipmap.ic_wallet_head_1,
            R.mipmap.ic_wallet_head_2,
            R.mipmap.ic_wallet_head_3,
            R.mipmap.ic_wallet_head_4,
            R.mipmap.ic_wallet_head_5,
            R.mipmap.ic_wallet_head_6,
            R.mipmap.ic_wallet_head_7,
            R.mipmap.ic_wallet_head_8,
            R.mipmap.ic_wallet_head_9
    };

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
                settingHeadIcon();
            }
        });

        llSettingUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingName();
            }
        });

    }

    private void settingHeadIcon() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_change_wallet_head_icon);
        RecyclerView headIconRv = (RecyclerView) qmuiDialog.findViewById(R.id.headIconRV);
        ArrayList<HeadIconModel> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HeadIconModel headIconModel = new HeadIconModel();
            headIconModel.setIconRes(walletHeadResList[i]);
            headIconModel.setSelectedPosition(0);
            data.add(headIconModel);
        }
        headIconRv.setAdapter(new HeadIconAdapter(mContext,data));
        GridLayoutManager layoutManager = new GridLayoutManager(mContext,2);
        headIconRv.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);

        headIconRv.setItemAnimator(new DefaultItemAnimator());
        qmuiDialog.show();
        qmuiDialog.findViewById(R.id.cancelTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        qmuiDialog.findViewById(R.id.confirmTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //save data


                qmuiDialog.dismiss();
            }
        });



    }

    private void settingName() {


        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_change_wallet_name);
        qmuiDialog.show();

        TextView cancelTv = qmuiDialog.findViewById(R.id.cancelTV);
        final TextView confirmTv = qmuiDialog.findViewById(R.id.cancelTV);
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
