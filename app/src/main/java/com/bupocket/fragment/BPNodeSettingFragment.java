package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.DialogUtils;
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
    private NodeSettingAdapter nodeSettingAdapter;

    private boolean isSaveBtn;
    private int oldPosition;

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

        String urlJson = (String) spHelper.getSharedPreference(Constants.BUMO_NODE_URL, "");

        ArrayList<NodeSettingModel> nodeSettingModels = new ArrayList<>();
        if (urlJson.isEmpty()) {
            NodeSettingModel mainNode = new NodeSettingModel();
            mainNode.setMore(false);
            mainNode.setSelected(true);
            mainNode.setUrl(Constants.BUMO_NODE_URL);
            nodeSettingModels.add(mainNode);
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<NodeSettingModel>>() {
            }.getType();
            nodeSettingModels = gson.fromJson(urlJson, type);
        }
        for (int i = 0; i < nodeSettingModels.size(); i++) {
            if (nodeSettingModels.get(i).isSelected()) {
                oldPosition = i;
            }
        }
        nodeSettingAdapter.setNewData(nodeSettingModels);

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
                        getString(R.string.add_node_address_title),
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

