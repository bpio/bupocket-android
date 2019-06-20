package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BPNodeSetting extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.lvNodeSet)
    ListView lvNodeSet;
    @BindView(R.id.llAddMore)
    LinearLayout llAddMore;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_setting;
    }

    @Override
    protected void initView() {
        initTopBar();
    }

    @Override
    protected void initData() {


//        if (SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()) {
//            ivNodeSetTestMain.setVisibility(View.VISIBLE);
//            ivNodeSetMain.setVisibility(View.INVISIBLE);
//        } else {
//            ivNodeSetTestMain.setVisibility(View.INVISIBLE);
//            ivNodeSetMain.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void setListeners() {

    }

    @SuppressLint("ResourceAsColor")
    private void initTopBar() {
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();

            }
        });

        Button rightButton = topbar.addRightTextButton(getString(R.string.save), R.id.skipBackupBtn);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isChecked = ivNodeSetMain.getVisibility() == View.VISIBLE;
//                if (isChecked) {
//                    showSwitchMainNetDialog();
//                } else {
//                    ShowSwitchTestNetConfirmDialog();
//                }
            }
        });
        Button skipBackuoBtn = topbar.findViewById(R.id.skipBackupBtn);
        skipBackuoBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.app_color_main));

    }

    private void ShowSwitchTestNetConfirmDialog() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setMessage(getString(R.string.switch_to_test_net_message_txt))
                .addAction(getString(R.string.i_knew_btn_txt), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
                        BPApplication.switchNetConfig(BumoNodeEnum.TEST.getName());
                        dialog.dismiss();
                        spHelper.put("tokensInfoCache", "");
                        spHelper.put("tokenBalance", "");
                        startFragment(new HomeFragment());
                    }
                }).setCanceledOnTouchOutside(false).create().show();
    }


    private void showSwitchMainNetDialog() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setMessage(getString(R.string.switch_main_net_message_txt))
                .addAction(getString(R.string.i_knew_btn_txt), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                        BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
                        spHelper.put("tokensInfoCache", "");
                        spHelper.put("tokenBalance", "");
                        startFragment(new HomeFragment());
                    }
                }).setCanceledOnTouchOutside(false).create().show();
    }
}
