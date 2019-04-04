package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.NodeBuildModel;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BPNodeBuildFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvNodeBuild)
    ListView lvNodeBuild;
    private Unbinder bind;
    private NodeBuildAdapter nodeBuildAdapter;
    private ArrayList<NodeBuildModel> newData;


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
                startFragment(new BPNodeBuildDetailFragment());
            }
        });
    }

    private void initData() {
        if (nodeBuildAdapter == null) {
            nodeBuildAdapter = new NodeBuildAdapter(getContext());
        }

        if (newData==null) {
            newData = new ArrayList<>();
        }

        for (int i = 0; i < 5; i++) {
            newData.add(new NodeBuildModel());
        }
        nodeBuildAdapter.setNewData(newData);
        lvNodeBuild.setAdapter(nodeBuildAdapter);
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
