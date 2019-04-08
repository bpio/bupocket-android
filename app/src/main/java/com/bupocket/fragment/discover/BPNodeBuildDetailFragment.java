package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildDetailAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.NodeBuildDetailModel;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import org.w3c.dom.Text;

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
        supportDialog.findViewById(R.id.ivDialogCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
            }
        });
        final TextView num = (TextView) supportDialog.findViewById(R.id.tvDialogNum);
        num.setText(""+1);
        supportDialog.findViewById(R.id.tvDialogAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num.setText((Integer.parseInt(num.getText().toString())+1)+"");
            }
        });
        
        supportDialog.findViewById(R.id.tvDialogSub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int num1 = Integer.parseInt(num.getText().toString());
                if (num1==1) {
                    Toast.makeText(getContext(), R.string.build_dialog_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                int numSub = num1 - 1;
                num.setText(numSub+"");
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
}
