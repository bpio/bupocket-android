package com.bupocket.fragment.discover;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildDetailAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.CoBuildDetailStatusEnum;
import com.bupocket.http.api.NodeBuildService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.NodeBuildDetailModel;
import com.bupocket.model.NodeBuildSupportModel;
import com.bupocket.utils.LogUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPNodeBuildDetailFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvRefresh)
    ListView lvBuildDetail;
    @BindView(R.id.btnBuildExit)
    Button btnBuildExit;
    @BindView(R.id.btnBuildSupport)
    Button btnBuildSupport;
    @BindView(R.id.llBtnBuild)
    LinearLayout llBtnBuild;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    private Unbinder bind;
    private NodeBuildDetailAdapter nodeBuildDetailAdapter;
    private TextView tvStutas;
    private View addBtn;
    private View subBtn;
    private TextView tvDialogTotalAmount;
    private TextView tvDialogAmount;
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
    private String nodeId;
    private View emptyLayout;

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
        refreshLayout.setEnableLoadMore(false);

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

        emptyLayout = LayoutInflater.from(getContext()).inflate(R.layout.view_empty_record, null);
        getBuildData();
    }


    private void getBuildData() {

        nodeId = getArguments().getString("nodeId", "");
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
                    tvBuildDetailAmount.setText(data.getPerAmount());
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
                    if (nodelist == null || nodelist.size() == 0) {
                        lvBuildDetail.addHeaderView(emptyLayout);
                    } else {
                        lvBuildDetail.removeHeaderView(emptyLayout);
                        nodeBuildDetailAdapter.setNewData(nodelist);
                        nodeBuildDetailAdapter.notifyDataSetChanged();
                    }


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
                showExit();
                break;
            case R.id.btnBuildSupport:
                if (data != null) {
                    ShowSupport();
                }

                break;
        }
    }

    private void showExit() {

        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm, null));
        TextView mTransactionAmountTv = qmuiBottomSheet.findViewById(R.id.transactionAmountTv);
        mTransactionAmountTv.setText("0");
        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
        mTransactionDetailTv.setText("退出\""+data.getTitle()+"\"项目");
        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
        mDestAddressTv.setText(data.getContractAddress());
        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
        mTxFeeTv.setText(String.valueOf(Constants.NODE_REVOKE_FEE));

        qmuiBottomSheet.findViewById(R.id.detailBtn).setVisibility(View.GONE);
        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmExit();
            }
        });
        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.show();
    }

    private void confirmExit() {
        final String amount="0";
        final String inputStr="{\"method\":\"revoke\"}";


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TransactionBuildBlobResponse transBlob = Wallet.getInstance().buildBlob(amount,inputStr, getWalletAddress(), String.valueOf(Constants.NODE_CO_BUILD_FEE), data.getContractAddress());
                    String hash = transBlob.getResult().getHash();
                    LogUtils.e(hash);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void ShowSupport() {
        final QMUIBottomSheet supportDialog = new QMUIBottomSheet(getContext());
        supportDialog.setContentView(supportDialog.getLayoutInflater().inflate(R.layout.view_dialog_node_detail_support, null));
        addBtn = supportDialog.findViewById(R.id.tvDialogAdd);
        subBtn = supportDialog.findViewById(R.id.tvDialogSub);
        tvDialogTotalAmount = supportDialog.findViewById(R.id.tvDialogTotalAmount);
        tvDialogAmount = supportDialog.findViewById(R.id.tvDialogAmount);

        supportDialog.findViewById(R.id.ivDialogCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
            }
        });
        numSupport = supportDialog.findViewById(R.id.tvDialogNum);

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

        //confirm
        supportDialog.findViewById(R.id.tvDialogSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();

                confirmSheet();


            }
        });

        //init
        tvDialogAmount.setText(data.getPerAmount());
        numSupport.setText("" + 1);
        tvDialogTotalAmount.setText(data.getPerAmount());

        supportDialog.show();

    }

    private void confirmSheet() {
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm, null));
        TextView mTransactionAmountTv = qmuiBottomSheet.findViewById(R.id.transactionAmountTv);
        mTransactionAmountTv.setText(tvDialogTotalAmount.getText());
        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
        mTransactionDetailTv.setText("支持\""+data.getTitle()+"\"项目");
        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
        mDestAddressTv.setText(data.getContractAddress());
        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
        mTxFeeTv.setText(String.valueOf(Constants.NODE_REVOKE_FEE));

        qmuiBottomSheet.findViewById(R.id.detailBtn).setVisibility(View.GONE);
        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmSupport();
            }
        });
        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.show();

    }

    private void confirmSupport() {
        final String amount = tvDialogTotalAmount.getText().toString();
        String num = numSupport.getText().toString();
        JSONObject params = new JSONObject();
        params.put("shares", num);
         JSONObject input = new JSONObject();
        input.put("method", "subscribe");
        input.put("params", params.toJSONString());

        final String inputStr="{\"method\":\"subscribe\",\"params\":"+ params.toJSONString() +" }";
//        final String inputStr="{\"method\":\"subscribe\",\"params\":{\"shares\":1}";


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final TransactionBuildBlobResponse transBlob = Wallet.getInstance().buildBlob(amount,inputStr, getWalletAddress(), String.valueOf(Constants.NODE_MIN_FEE), data.getContractAddress());
                    String hash = transBlob.getResult().getHash();
                    LogUtils.e(hash);
                    submitTransactionBase(transBlob);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



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
        int amount = num * Integer.parseInt(tvDialogAmount.getText().toString());
        tvDialogTotalAmount.setText(amount + "");
    }

}
