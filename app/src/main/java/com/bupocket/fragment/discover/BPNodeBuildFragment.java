package com.bupocket.fragment.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.NodeBuildModel;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BPNodeBuildFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvRefresh)
    ListView lvNodeBuild;
    @BindView(R.id.copyCommandBtn)
    QMUIRoundButton copyCommandBtn;
    @BindView(R.id.llLoadFailed)
    LinearLayout llLoadFailed;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;


    private Unbinder bind;
    private NodeBuildAdapter nodeBuildAdapter;
    private ArrayList<NodeBuildModel> newData;
    public static String NODE_INFO = "nodeInfo";


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bpnode_build, null);
        bind = ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initTopBar();
        initData();
        setListener();
    }

    private void setListener() {
        lvNodeBuild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BPNodeBuildDetailFragment fragment = new BPNodeBuildDetailFragment();
                Bundle args = new Bundle();
                if (newData != null) {
                    NodeBuildModel nodebuildmodel = newData.get(position);
                    nodebuildmodel.setName("小明" + position);
                    args.putSerializable(NODE_INFO, nodebuildmodel);
                }
                fragment.setArguments(args);
                startFragment(fragment);
            }
        });
        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    private void initData() {
        if (nodeBuildAdapter == null) {
            nodeBuildAdapter = new NodeBuildAdapter(getContext());
        }

        if (newData == null) {
            newData = new ArrayList<>();
        }

        for (int i = 0; i < 5; i++) {
            newData.add(new NodeBuildModel());
        }
        nodeBuildAdapter.setNewData(newData);
        lvNodeBuild.setAdapter(nodeBuildAdapter);

        getBuildData();

    }

    private void getBuildData() {


    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
//        TextView title = mTopBar.setTitle(getResources().getString(R.string.building_information_details));


    }
}
