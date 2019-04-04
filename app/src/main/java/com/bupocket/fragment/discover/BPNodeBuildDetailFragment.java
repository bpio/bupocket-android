package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildDetailAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.NodeBuildDetailModel;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPNodeBuildDetailFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvBuildDetail)
    ListView lvBuildDetail;
    private Unbinder bind;
    private NodeBuildDetailAdapter nodeBuildDetailAdapter;


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bpnode_build_detail, null);
        bind = ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initTopBar();
        initData();
    }

    private void initData() {

        if (nodeBuildDetailAdapter==null) {
            nodeBuildDetailAdapter = new NodeBuildDetailAdapter(getContext());
        }
        ArrayList<NodeBuildDetailModel> newData = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            newData.add(new NodeBuildDetailModel());
        }
        nodeBuildDetailAdapter.setNewData(newData);
        lvBuildDetail.setAdapter(nodeBuildDetailAdapter);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.node_build_header,null);
        lvBuildDetail.addHeaderView(headerView);

    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        TextView title = mTopBar.setTitle(getResources().getString(R.string.building_information_details));


    }


}
