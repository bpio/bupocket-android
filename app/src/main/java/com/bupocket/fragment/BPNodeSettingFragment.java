package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BPNodeSettingFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.lvNodeSet)
    ListView lvNodeSet;
    @BindView(R.id.llAddMore)
    LinearLayout llAddMore;
    private NodeSettingAdapter nodeSettingAdapter;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_setting;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
    }

    private void initListView() {
        nodeSettingAdapter = new NodeSettingAdapter(mContext);
        lvNodeSet.setAdapter(nodeSettingAdapter);
    }

    @Override
    protected void initData() {
        ArrayList<NodeSettingModel> nodeSettingModels = new ArrayList<>();
        NodeSettingModel mainNode = new NodeSettingModel();
        mainNode.setMore(false);
        mainNode.setSelected(true);
        mainNode.setUrl(Constants.MainNetConfig.BUMO_NODE_URL.getValue());

        NodeSettingModel testNode = new NodeSettingModel();
        testNode.setMore(true);
        testNode.setSelected(false);
        testNode.setUrl(Constants.TestNetConfig.BUMO_NODE_URL.getValue());
        nodeSettingModels.add(mainNode);
        nodeSettingModels.add(testNode);
        nodeSettingAdapter.setNewData(nodeSettingModels);

    }

    @Override
    protected void setListeners() {
        llAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.showEditMessageDialog(mContext,
                        getString(R.string.add_node_address_title),
                        getString(R.string.add_node_address_title),
                        new CommonUtil.ConfirmListener() {
                            @Override
                            public void confirm(String url) {

                                List<NodeSettingModel> data = nodeSettingAdapter.getData();
                                NodeSettingModel addNode = new NodeSettingModel();
                                addNode.setUrl(url);
                                addNode.setSelected(true);
                                addNode.setMore(true);
                                data.add(addNode);
                                nodeSettingAdapter.setNewData(data);

                            }
                        });
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
        skipBackuoBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_main));

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

