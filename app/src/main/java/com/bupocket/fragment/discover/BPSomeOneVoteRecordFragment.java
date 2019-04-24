package com.bupocket.fragment.discover;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.adaptor.VoteRecordAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.MyVoteRecordModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPSomeOneVoteRecordFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.lvRefresh)
    ListView lvVoteRecord;
    @BindView(R.id.nodeIconBgIv)
    ImageView nodeIconBgIv;
    @BindView(R.id.nodeIconIv)
    QMUIRadiusImageView nodeIconIv;
    @BindView(R.id.nodeIconRl)
    RelativeLayout nodeIconRl;
    @BindView(R.id.nodeNameTv)
    TextView nodeNameTv;
    @BindView(R.id.nodeTypeTv)
    TextView nodeTypeTv;
    @BindView(R.id.haveVotesNumTv)
    TextView haveVotesNumTv;
    @BindView(R.id.myVotesNumTv)
    TextView myVotesNumTv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.copyCommandBtn)
    QMUIRoundButton copyCommandBtn;
    @BindView(R.id.llLoadFailed)
    LinearLayout llLoadFailed;
    @BindView(R.id.haveVotesNumTvHint)
    TextView haveVotesNumTvHint;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletAddress;
    private VoteRecordAdapter voteRecordAdapter;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_someone_vote_record, null);
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

        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });

    }

    private void refreshData() {
        initData();
    }


    private void initData() {

        SuperNodeModel itemNodeInfo = getArguments().getParcelable("itemNodeInfo");

        nodeNameTv.setText(itemNodeInfo.getNodeName());
        if (CommonUtil.isSingle(itemNodeInfo.getNodeVote())) {
            haveVotesNumTvHint.setText(getString(R.string.number_have_votes));
        }else{
            haveVotesNumTvHint.setText(getString(R.string.number_have_votes_s));
        }
        haveVotesNumTv.setText(itemNodeInfo.getNodeVote());
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
        nodeIconIv.setBackgroundColor(getContext().getResources().getColor(R.color.app_color_white));

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentIdentityWalletAddress) || currentIdentityWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString())) {
            currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        }

        HashMap<String, Object> listReq = new HashMap<>();
        listReq.put(Constants.ADDRESS, currentIdentityWalletAddress);
        listReq.put(Constants.NODE_ID, itemNodeInfo.getNodeId());

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        Call<ApiResult<MyVoteRecordModel>> superNodeList = nodePlanService.getMyVoteList(listReq);
        superNodeList.enqueue(new Callback<ApiResult<MyVoteRecordModel>>() {

            @Override
            public void onResponse(Call<ApiResult<MyVoteRecordModel>> call, Response<ApiResult<MyVoteRecordModel>> response) {
                ApiResult<MyVoteRecordModel> body = response.body();
                llLoadFailed.setVisibility(View.GONE);

                if (body == null | body.getData() == null |
                        body.getData().getList() == null | body.getData().getList().size() == 0) {
                    addressRecordEmptyLL.setVisibility(View.VISIBLE);
                }

                MyVoteRecordModel data = body.getData();
                voteRecordAdapter.setNewData(data.getList());
                voteRecordAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<ApiResult<MyVoteRecordModel>> call, Throwable t) {

                llLoadFailed.setVisibility(View.VISIBLE);

            }
        });


    }

    private void initUI() {
        initTopBar();
        voteRecordAdapter = new VoteRecordAdapter(getContext());
        voteRecordAdapter.setAdapterType(VoteRecordAdapter.SOME_RECORD);
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