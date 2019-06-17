package com.bupocket.fragment.discover;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.VoteRecordAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.MyVoteRecordModel;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPVoteRecordFragment extends AbsBaseFragment {


    @BindView(R.id.lvRefresh)
    ListView lvVoteRecord;
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.llLoadFailed)
    LinearLayout llLoadFailed;
    @BindView(R.id.copyCommandBtn)
    Button copyCommandBtn;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;


    private VoteRecordAdapter voteRecordAdapter;
    private Call<ApiResult<MyVoteRecordModel>> serviceMyVoteList;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_vote_record;
    }

    @Override
    protected void initView() {
        initTopBar();
        voteRecordAdapter = new VoteRecordAdapter(getContext());
        lvVoteRecord.setAdapter(voteRecordAdapter);
        qmuiEmptyView.show();
        refreshLayout.setEnableLoadMore(false);
    }


    @Override
    protected void setListeners() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();

            }
        });

        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0,200,1,false);
            }
        });

    }

    private void refreshData() {
        initData();
    }

    @Override
    protected void initData() {


        HashMap<String, Object> listReq = new HashMap<>();

        listReq.put(Constants.ADDRESS, getWalletAddress());

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        serviceMyVoteList = nodePlanService.getMyVoteList(listReq);
        serviceMyVoteList.enqueue(new Callback<ApiResult<MyVoteRecordModel>>() {

            @Override
            public void onResponse(Call<ApiResult<MyVoteRecordModel>> call, Response<ApiResult<MyVoteRecordModel>> response) {
                ApiResult<MyVoteRecordModel> body = response.body();
                llLoadFailed.setVisibility(View.GONE);
                if (body==null) {
                    return;
                }
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())
                        && body.getData() != null
                        && body.getData().getList() != null&&body.getData().getList().size()>0 ) {
                    voteRecordAdapter.setNewData(body.getData().getList());
                    voteRecordAdapter.notifyDataSetChanged();
                    addressRecordEmptyLL.setVisibility(View.GONE);
                }else{
                    addressRecordEmptyLL.setVisibility(View.VISIBLE);
                }

                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
                qmuiEmptyView.show(null,null);
            }

            @Override
            public void onFailure(Call<ApiResult<MyVoteRecordModel>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                llLoadFailed.setVisibility(View.VISIBLE);
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
                qmuiEmptyView.show(null,null);
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
        TextView title = mTopBar.setTitle(getResources().getString(R.string.vote_record_txt));
    }

    @Override
    public void onDestroy() {
        serviceMyVoteList.cancel();
        super.onDestroy();
    }
}
