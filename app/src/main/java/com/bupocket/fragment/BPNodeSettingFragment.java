package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CustomNodeTypeEnum;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BPNodeSettingFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.nodeSetLv)
    ListView nodeSetLv;
    @BindView(R.id.addMoreLL)
    LinearLayout addMoreLL;
    @BindView(R.id.nodeTitleTv)
    TextView nodeTitleTv;


    private NodeSettingAdapter nodeSettingAdapter;

    private boolean isSaveBtn;
    private int oldPosition;
    private ArrayList<NodeSettingModel> oldNodeSettingModels;
    private String testTitle="";

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
        nodeSettingAdapter = new NodeSettingAdapter(mContext, getActivity());
        nodeSetLv.setAdapter(nodeSettingAdapter);
        nodeSetLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedPosition(position);
            }
        });
    }

    @Override
    protected void initData() {


        setNodeData();

        setTestStatus();
    }

    private void setTestStatus() {

        if (SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()) {
            testTitle = "(" + getString(R.string.current_test_message_txt) + ")";
            nodeTitleTv.setText(getString(R.string.node_setting_title)+testTitle);
        }

    }

    private void setNodeData() {
        String urlJson = (String) spHelper.getSharedPreference(Constants.BUMO_NODE_URL, "");
        oldNodeSettingModels = new ArrayList<>();
        if (urlJson.isEmpty()) {
            NodeSettingModel mainNode = new NodeSettingModel();
            mainNode.setMore(false);
            mainNode.setSelected(true);
            mainNode.setUrl(Constants.BUMO_NODE_URL);
            oldNodeSettingModels.add(mainNode);
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<NodeSettingModel>>() {
            }.getType();
            oldNodeSettingModels = gson.fromJson(urlJson, type);
        }
        for (int i = 0; i < oldNodeSettingModels.size(); i++) {
            if (oldNodeSettingModels.get(i).isSelected()) {
                oldPosition = i;
            }
        }
        nodeSettingAdapter.setNewData(oldNodeSettingModels);
    }

    @Override
    protected void setListeners() {
        addMoreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nodeSettingAdapter.getData().size()==11) {
                    return;
                }

                DialogUtils.showEditMessageDialog(mContext,
                        getString(R.string.add_node_address_title)+testTitle,
                        getString(R.string.add_node_address_title),
                        new DialogUtils.ConfirmListener() {
                            @Override
                            public void confirm(String url) {

                                nodeSettingAdapter.invalidNodeAddress(url, 0, new NodeSettingAdapter.NodeAddressListener() {
                                    @Override
                                    public void success(String url) {
                                        addNodeAddress(url);
                                    }

                                    @Override
                                    public void failed() {

                                    }
                                });


                            }
                        });
            }
        });
    }

    private void addNodeAddress(String url) {
        List<NodeSettingModel> data = nodeSettingAdapter.getData();
        NodeSettingModel addNode = new NodeSettingModel();
        addNode.setUrl(url);
        addNode.setSelected(false);
        addNode.setMore(true);
        data.add(addNode);
        nodeSettingAdapter.setNewData(data);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isSaveBtn) {
            saveNodeSetAddress();
        }
    }

    private void saveNodeSetAddress() {
        nodeSettingAdapter.saveNodeData(oldPosition);
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


                for (int i = 0; i < oldNodeSettingModels.size(); i++) {
                    if (oldNodeSettingModels.get(i).isSelected()) {
                        String oldUrl = oldNodeSettingModels.get(i).getUrl();
                        for (int j = 0; j < nodeSettingAdapter.getData().size(); j++) {
                            if (nodeSettingAdapter.getData().get(i).isSelected()) {
                                if (oldUrl.equals(nodeSettingAdapter.getData().get(i).getUrl())) {

                                    return;
                                }
                            }
                        }
                    }
                }

                nodeSettingAdapter.saveNodeData(nodeSettingAdapter.getData());
                BPApplication.switchNetConfig(null);
                isSaveBtn = true;
                popBackStack();

            }
        });
        Button skipBackuoBtn = topbar.findViewById(R.id.skipBackupBtn);
        skipBackuoBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_main));


    }


    public void setSelectedPosition(int position) {
        List<NodeSettingModel> data = nodeSettingAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelected(false);
        }
        data.get(position).setSelected(true);
        nodeSettingAdapter.setNewData(data);

    }

}

