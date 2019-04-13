package com.bupocket.fragment.discover;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildDetailAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.CoBuildDetailStatusEnum;
import com.bupocket.http.api.NodeBuildService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.NodeBuildDetailModel;
import com.bupocket.model.NodeBuildSupportModel;
import com.bupocket.utils.LogUtils;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private TextView tvStutas;
    private View addBtn;
    private View subBtn;
    private TextView tvDialogAmount;
    private TextView tvDialogOneNum;
    private TextView numSupport;
    private TextView tvName;
    private TextView tvBuildDetailAmount;
    private TextView tvTotalAmount;
    private ProgressBar pbDetail;
    private TextView tvBuildDetailLeftCopies;
    private TextView tvBuildDetailSharingRatio;
    private TextView tvBuildDetailOriginAmount;
    private TextView tvBuildDetailSupportNum;
    private TextView tvBuildDetailSupportAmount;
    private NodeBuildDetailModel data;

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
        lvBuildDetail.setAdapter(nodeBuildDetailAdapter);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.node_build_header, null);


        tvStutas = ((TextView) headerView.findViewById(R.id.tvNodeBuilding));
        lvBuildDetail.addHeaderView(headerView);

        tvName = headerView.findViewById(R.id.tvBuildDetailName);
        tvBuildDetailAmount = headerView.findViewById(R.id.tvBuildDetailAmount);
        tvTotalAmount = ((TextView) headerView.findViewById(R.id.tvDetailTotalAmount));
        pbDetail = ((ProgressBar) headerView.findViewById(R.id.pbBuildDetailProgress));

        tvBuildDetailLeftCopies = ((TextView) headerView.findViewById(R.id.tvBuildDetailLeftCopies));
        tvBuildDetailSharingRatio = ((TextView) headerView.findViewById(R.id.tvBuildDetailSharingRatio));
        tvBuildDetailOriginAmount = ((TextView) headerView.findViewById(R.id.tvBuildDetailOriginAmount));
        tvBuildDetailSupportNum = ((TextView) headerView.findViewById(R.id.tvBuildDetailSupportNum));
        tvBuildDetailSupportAmount = ((TextView) headerView.findViewById(R.id.tvBuildDetailSupportAmount));
        tvTotalAmount = ((TextView) headerView.findViewById(R.id.tvDetailTotalAmount));
        tvTotalAmount = ((TextView) headerView.findViewById(R.id.tvDetailTotalAmount));

        getBuildData();
    }


    private void getBuildData() {

        String nodeId = getArguments().getString("nodeId", "");
        if (TextUtils.isEmpty(nodeId)) {
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("nodeId", nodeId);

        final NodeBuildService nodeBuildService = RetrofitFactory.getInstance().getRetrofit().create(NodeBuildService.class);

        nodeBuildService.getNodeBuildDetail(map).enqueue(new Callback<ApiResult<NodeBuildDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<NodeBuildDetailModel>> call, Response<ApiResult<NodeBuildDetailModel>> response) {
                ApiResult<NodeBuildDetailModel> body = response.body();

                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {

                    data = body.getData();
                    if (data == null) {
                        return;
                    }
                    tvName.setText(data.getTitle());
                    tvBuildDetailAmount.setText(data.getAmount());
                    tvTotalAmount.setText(data.getTotalAmount());
                    pbDetail.setMax(data.getCopies());
                    pbDetail.setProgress(data.getCopies() - data.getLeftCopies());
                    tvBuildDetailLeftCopies.setText("剩余" + data.getLeftCopies() + "份");
                    tvBuildDetailSharingRatio.setText(data.getRewardRate() + "%");
                    tvBuildDetailOriginAmount.setText(data.getInitiatorAmount());
                    tvBuildDetailSupportNum.setText(data.getSupportPerson() + "人支持");
                    tvBuildDetailSupportAmount.setText(data.getSupportAmount());

                    if (CoBuildDetailStatusEnum.CO_BUILD_RUNING.getCode().equals(data.getStatus())) {
                        tvStutas.setSelected(false);
                        tvStutas.setText(CoBuildDetailStatusEnum.CO_BUILD_RUNING.getMsg());
                    } else if (CoBuildDetailStatusEnum.CO_BUILD_SUCCESS.getCode().equals(data.getStatus())) {
                        tvStutas.setSelected(true);
                        tvStutas.setText(CoBuildDetailStatusEnum.CO_BUILD_SUCCESS.getMsg());
                    } else if (CoBuildDetailStatusEnum.CO_BUILD_EXIT.getCode().equals(data.getStatus())) {
                        tvStutas.setSelected(true);
                        tvStutas.setText(CoBuildDetailStatusEnum.CO_BUILD_EXIT.getMsg());
                    }

                    ArrayList<NodeBuildSupportModel> nodelist = body.getData().getSupportList();
                    nodeBuildDetailAdapter.setNewData(nodelist);
                    nodeBuildDetailAdapter.notifyDataSetChanged();


                }


            }

            @Override
            public void onFailure(Call<ApiResult<NodeBuildDetailModel>> call, Throwable t) {
                LogUtils.e("" + t.toString());
            }


        });


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
                startFragment(new BPNodeBuildExitFragment());
                break;
            case R.id.btnBuildSupport:
                if (data!=null) {
                    ShowSupport();
                }

                break;
        }
    }

    private void ShowSupport() {
        final QMUIBottomSheet supportDialog = new QMUIBottomSheet(getContext());
        supportDialog.setContentView(supportDialog.getLayoutInflater().inflate(R.layout.view_dialog_node_detail_support, null));
        addBtn = supportDialog.findViewById(R.id.tvDialogAdd);
        subBtn = supportDialog.findViewById(R.id.tvDialogSub);
        tvDialogAmount = supportDialog.findViewById(R.id.tvDialogAmount);
        tvDialogOneNum = supportDialog.findViewById(R.id.tvDialogOneNum);
        tvDialogOneNum.setText(data.getAmount());
        supportDialog.findViewById(R.id.ivDialogCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
            }
        });
        numSupport = supportDialog.findViewById(R.id.tvDialogNum);
        numSupport.setText("" + 1);

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

                llBtnBuild.setVisibility(View.GONE);
            }
        });


        supportDialog.show();

    }

    private void setNumStatus(int num) {

        if (num < 1 || num > 10) {
            return;
        }

        if (num == 1) {
            subBtn.setSelected(false);
        } else if (num == 10) {
            addBtn.setSelected(false);
        } else {
            if (!subBtn.isSelected()) {
                subBtn.setSelected(true);
            }
            if (!addBtn.isSelected()) {
                addBtn.setSelected(true);
            }

        }


        numSupport.setText(num + "");
        int amount = num * Integer.parseInt(tvDialogOneNum.getText().toString());
        tvDialogAmount.setText(amount + "");
    }
}
