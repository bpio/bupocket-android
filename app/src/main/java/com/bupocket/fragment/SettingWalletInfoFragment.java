package com.bupocket.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.model.HeadIconModel;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;

import butterknife.BindView;

public class SettingWalletInfoFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.walletHeadRiv)
    ImageView walletHeadRiv;
    @BindView(R.id.settingWalletHeadLL)
    LinearLayout llSettingUserHead;
    @BindView(R.id.settingWalletNameTv)
    TextView tvSettingUserName;
    @BindView(R.id.settingWalletNameLL)
    LinearLayout llSettingUserName;
    private String walletAddress;
    private String walletName;

    private ArrayList<HeadIconModel> headData;

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
    public void onResume() {
        super.onResume();
        CommonUtil.setHeadIvRes(walletAddress,walletHeadRiv,spHelper);
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
        int oldPosition = (int) spHelper.getSharedPreference(walletAddress + ConstantsType.WALLET_HEAD_ICON, 0);
        headData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HeadIconModel headIconModel = new HeadIconModel();
            headIconModel.setIconRes(CommonUtil.getWalletHeadRes(i));
            headIconModel.setSelectedPosition(oldPosition);
            headData.add(headIconModel);
        }
        final HeadIconAdapter adapter = new HeadIconAdapter(mContext, headData);
        adapter.setOnItemClickListener(new HeadIconAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                for (int i = 0; i < headData.size(); i++) {
                    headData.get(i).setSelectedPosition(position);
                }
                adapter.setData(headData);
            }
        });
        headIconRv.setAdapter(adapter);
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
                saveHeadData();
            }
        });





    }

    private void saveHeadData() {

        int selectedPosition = headData.get(0).getSelectedPosition();
        spHelper.put(walletAddress+ ConstantsType.WALLET_HEAD_ICON,selectedPosition);
        walletHeadRiv.setImageResource(CommonUtil.getWalletHeadRes(selectedPosition));
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
