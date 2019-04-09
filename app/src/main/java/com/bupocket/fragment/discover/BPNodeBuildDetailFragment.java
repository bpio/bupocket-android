package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildDetailAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.NodeBuildDetailModel;
import com.bupocket.model.NodeBuildModel;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BPNodeBuildDetailFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvBuildDetail)
    ListView lvBuildDetail;
    @BindView(R.id.btnBuildExit)
    Button btnBuildExit;
    @BindView(R.id.btnBuildSupport)
    Button btnBuildSupport;
    @BindView(R.id.llBtnBuild)
    LinearLayout llBtnBuild;


    private Unbinder bind;
    private NodeBuildDetailAdapter nodeBuildDetailAdapter;
    private TextView tvNodeBuilding;
    private View addBtn;
    private View subBtn;
    private TextView tvDialogAmount;
    private TextView tvDialogOneNum;
    private TextView numSupport;


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

        NodeBuildModel nodeBuildModel = (NodeBuildModel)getArguments().getSerializable(BPNodeBuildFragment.NODE_INFO);
        LogUtils.e("信息："+nodeBuildModel.getName());

        if (nodeBuildDetailAdapter == null) {
            nodeBuildDetailAdapter = new NodeBuildDetailAdapter(getContext());
        }
        ArrayList<NodeBuildDetailModel> newData = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            newData.add(new NodeBuildDetailModel());
        }
        nodeBuildDetailAdapter.setNewData(newData);
        lvBuildDetail.setAdapter(nodeBuildDetailAdapter);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.node_build_header, null);


        tvNodeBuilding = ((TextView) headerView.findViewById(R.id.tvNodeBuilding));
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



    @OnClick({R.id.btnBuildExit, R.id.btnBuildSupport})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBuildExit:
                startFragment(new  BPNodeBuildExitFragment());
                break;
            case R.id.btnBuildSupport:
                ShowSupport();
                break;
        }
    }

    private void ShowSupport() {
        final QMUIBottomSheet supportDialog = new QMUIBottomSheet(getContext());
        supportDialog.setContentView(supportDialog.getLayoutInflater().inflate(R.layout.dialog_node_detail_support,null));
        addBtn = supportDialog.findViewById(R.id.tvDialogAdd);
        subBtn = supportDialog.findViewById(R.id.tvDialogSub);
        tvDialogAmount =  supportDialog.findViewById(R.id.tvDialogAmount);
        tvDialogOneNum =  supportDialog.findViewById(R.id.tvDialogOneNum);
        supportDialog.findViewById(R.id.ivDialogCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
            }
        });
        numSupport = supportDialog.findViewById(R.id.tvDialogNum);
        numSupport.setText(""+1);
//        tvDialogAmount.setText();//初始化数据

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addNum = Integer.parseInt(numSupport.getText().toString()) + 1;
                setNumStatus(addNum);
            }
        });
        addBtn.setSelected(true);


        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numSub = Integer.parseInt(numSupport.getText().toString()) - 1;
                setNumStatus(numSub);
            }
        });
        supportDialog.findViewById(R.id.tvDialogSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
                tvNodeBuilding.setSelected(true);
                tvNodeBuilding.setText(R.string.build_finish);
                llBtnBuild.setVisibility(View.GONE);
            }
        });



        supportDialog.show();

    }

    private void setNumStatus(int num) {

        if (num<1||num>10){
            return;
        }

         if (num==1) {
            subBtn.setSelected(false);
        } else if (num==10){
            addBtn.setSelected(false);
        }else{
            if (!subBtn.isSelected()) {
                subBtn.setSelected(true);
            }
            if (!addBtn.isSelected()) {
                addBtn.setSelected(true);
            }

        }


        numSupport.setText(num +"");
        int amount = num * Integer.parseInt(tvDialogOneNum.getText().toString());
        tvDialogAmount.setText(amount +"");
    }
}
