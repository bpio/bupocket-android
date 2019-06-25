package com.bupocket.fragment.discover;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.adaptor.VoteRecordAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.MyVoteRecordModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPMyNodeVoteRecordFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.recordEmptyLL)
    LinearLayout recordEmptyLL;
    @BindView(R.id.refreshComLv)
    ListView lvVoteRecord;
    @BindView(R.id.headIconIv)
    QMUIRadiusImageView nodeIconIv;
    @BindView(R.id.nodeNameTv)
    TextView nodeNameTv;
    @BindView(R.id.nodeTypeTv)
    TextView nodeTypeTv;
    @BindView(R.id.haveVotesNumTv)
    TextView haveVotesNumTv;
    @BindView(R.id.supportPeopleTvHint)
    TextView myVotesNunTvHint;
    @BindView(R.id.supportPeopleTv)
    TextView myVotesNumTv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.reloadBtn)
    QMUIRoundButton reloadBtn;
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;
    @BindView(R.id.haveVotesNumTvHint)
    TextView haveVotesNumTvHint;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;


    private VoteRecordAdapter voteRecordAdapter;
    private Call<ApiResult<MyVoteRecordModel>> myVoteRecordService;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_my_vote_record;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
    }



    @Override
    protected void initData() {

        SuperNodeModel itemNodeInfo = getArguments().getParcelable("itemNodeInfo");

        nodeNameTv.setText(itemNodeInfo.getNodeName());
        if (CommonUtil.isSingle(itemNodeInfo.getNodeVote())) {
            haveVotesNumTvHint.setText(getString(R.string.number_have_votes));
        } else {
            haveVotesNumTvHint.setText(getString(R.string.number_have_votes_s));
        }
        haveVotesNumTv.setText(itemNodeInfo.getNodeVote());


        if (CommonUtil.isSingle(itemNodeInfo.getMyVoteCount())) {
            myVotesNunTvHint.setText(mContext.getString(R.string.my_votes_number));
        } else {
            myVotesNunTvHint.setText(mContext.getString(R.string.my_votes_number_s));
        }
        myVotesNumTv.setText(itemNodeInfo.getMyVoteCount());

        String identityType = itemNodeInfo.getIdentityType();
        if (!TextUtils.isEmpty(identityType)) {
            if (identityType.equals(SuperNodeTypeEnum.VALIDATOR.getCode())) {
                nodeTypeTv.setText(getContext().getResources().getString(R.string.common_node));
            } else if (identityType.equals(SuperNodeTypeEnum.ECOLOGICAL.getCode())) {
                nodeTypeTv.setText(getContext().getResources().getString(R.string.ecological_node));
            }
        }
        String nodeLogo = itemNodeInfo.getNodeLogo();
        Glide.with(getContext())
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(nodeLogo))
                .into(nodeIconIv);


        HashMap<String, Object> listReq = new HashMap<>();
        listReq.put(Constants.ADDRESS, getWalletAddress());
        listReq.put(Constants.NODE_ID, itemNodeInfo.getNodeId());

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        myVoteRecordService = nodePlanService.getMyVoteList(listReq);
        myVoteRecordService.enqueue(new Callback<ApiResult<MyVoteRecordModel>>() {

            @Override
            public void onResponse(Call<ApiResult<MyVoteRecordModel>> call, Response<ApiResult<MyVoteRecordModel>> response) {
                ApiResult<MyVoteRecordModel> body = response.body();
                loadFailedLL.setVisibility(View.GONE);

                if (body == null | body.getData() == null |
                        body.getData().getList() == null | body.getData().getList().size() == 0) {
                    recordEmptyLL.setVisibility(View.VISIBLE);
                } else {
                    MyVoteRecordModel data = body.getData();
                    voteRecordAdapter.setNewData(data.getList());
                    voteRecordAdapter.notifyDataSetChanged();
                }
                refreshLayout.finishRefresh();
                qmuiEmptyView.show(null, null);
            }

            @Override
            public void onFailure(Call<ApiResult<MyVoteRecordModel>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                if (loadFailedLL != null) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }

                if (refreshLayout!=null) {
                    refreshLayout.finishRefresh();
                }

                qmuiEmptyView.show(null, null);

            }
        });


    }


    @Override
    protected void setListeners() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
                refreshLayout.setNoMoreData(false);
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0, 200, 1, false);
            }
        });

    }

    private void refreshData() {
        initData();
    }


    private void initListView() {
        voteRecordAdapter = new VoteRecordAdapter(getContext());
        voteRecordAdapter.setAdapterType(VoteRecordAdapter.SOME_RECORD);
        lvVoteRecord.setAdapter(voteRecordAdapter);
        qmuiEmptyView.show();
        refreshLayout.setEnableLoadMore(false);
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.vote_record_txt));
    }


    @Override
    public void onDestroy() {
       myVoteRecordService.cancel();
        super.onDestroy();
    }
}