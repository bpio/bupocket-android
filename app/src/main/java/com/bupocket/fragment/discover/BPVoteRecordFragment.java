package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.VoteRecordAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.MyVoteInfoModel;
import com.bupocket.model.MyVoteRecordModel;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
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

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletAddress;
    private VoteRecordAdapter voteRecordAdapter;

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
    }

    private void initData() {


        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();

        HashMap<String, Object> listReq = new HashMap<>();

        listReq.put(Constants.ADDRESS, currentIdentityWalletAddress);
//        listReq.put("nodeId","");

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        Call<ApiResult<MyVoteRecordModel>> superNodeList = nodePlanService.getMyVoteList(listReq);
        superNodeList.enqueue(new Callback<ApiResult<MyVoteRecordModel>>() {

            @Override
            public void onResponse(Call<ApiResult<MyVoteRecordModel>> call, Response<ApiResult<MyVoteRecordModel>> response) {
                ApiResult<MyVoteRecordModel> body = response.body();
                LogUtils.e("请求成功" + body.getData());


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
        ArrayList<MyVoteInfoModel> myVoteRecordModels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            myVoteRecordModels.add(new MyVoteInfoModel());
        }
        voteRecordAdapter.setNewData(myVoteRecordModels);
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
        mTopBar.setTitle(getResources().getString(R.string.vote_record_txt));
    }


}
