package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.CommonUtil;
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
        lvNodeSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedPosition(position);
            }
        });
    }

    @Override
    protected void initData() {

        String urlJson = (String) spHelper.getSharedPreference(Constants.BUMO_NODE_URL, "");
        if (urlJson.isEmpty()) {
            ArrayList<NodeSettingModel> nodeSettingModels = new ArrayList<>();
            NodeSettingModel mainNode = new NodeSettingModel();
            mainNode.setMore(false);
            mainNode.setSelected(true);
            mainNode.setUrl(Constants.BUMO_NODE_URL);
            nodeSettingModels.add(mainNode);
            nodeSettingAdapter.setNewData(nodeSettingModels);
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<NodeSettingModel>>() {
            }.getType();
            List<NodeSettingModel> data = gson.fromJson(urlJson, type);
            nodeSettingAdapter.setNewData(data);
        }


    }

    @Override
    protected void setListeners() {
        llAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showEditMessageDialog(mContext,
                        getString(R.string.add_node_address_title),
                        getString(R.string.add_node_address_title),
                        new DialogUtils.ConfirmListener() {
                            @Override
                            public void confirm(String url) {

                                addNodeAddress(url);
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

//        nodeSettingAdapter.saveNodeData();

    }

    @Override
    public void onPause() {
        super.onPause();
        nodeSettingAdapter.saveNodeData();
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

