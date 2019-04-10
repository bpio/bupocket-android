package com.bupocket.fragment.discover;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.VoteRecordAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.MyVoteRecordModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPVoteRecordFragment extends BaseFragment {


    @BindView(R.id.lvVoteRecord)
    ListView lvVoteRecord;
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    private SharedPreferencesHelper sharedPreferencesHelper;
    private VoteRecordAdapter voteRecordAdapter;
    private String currentWalletAddress;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_vote_record, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initUI();
        initData();
        setListener();
    }

    private void setListener() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        });

    }

    private void refreshData() {
        initData();
    }

    private void initData() {


        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString())) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        }

        HashMap<String, Object> listReq = new HashMap<>();

        listReq.put(Constants.ADDRESS, currentWalletAddress);

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        Call<ApiResult<MyVoteRecordModel>> superNodeList = nodePlanService.getMyVoteList(listReq);
        superNodeList.enqueue(new Callback<ApiResult<MyVoteRecordModel>>() {

            @Override
            public void onResponse(Call<ApiResult<MyVoteRecordModel>> call, Response<ApiResult<MyVoteRecordModel>> response) {
                ApiResult<MyVoteRecordModel> body = response.body();
                LogUtils.e("请求成功" + body.getData());
                if (body == null | body.getData() == null |
                        body.getData().getList() == null | body.getData().getList().size() == 0) {
                    addressRecordEmptyLL.setVisibility(View.VISIBLE);
                }


                voteRecordAdapter.setNewData(body.getData().getList());
                voteRecordAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<ApiResult<MyVoteRecordModel>> call, Throwable t) {
                LogUtils.e("请求失败" + t.getMessage());

            }
        });


    }

    private void initUI() {
        initTopBar();
        voteRecordAdapter = new VoteRecordAdapter(getContext());
//        ArrayList<MyVoteInfoModel> myVoteRecordModels = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            myVoteRecordModels.add(new MyVoteInfoModel());
//        }
//        voteRecordAdapter.setNewData(myVoteRecordModels);
        lvVoteRecord.setAdapter(voteRecordAdapter);

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

}
