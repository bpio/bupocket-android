package com.bupocket.fragment.discover;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.SuperNodeAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SuperNodeDto;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPNodePlanFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.lvPlan)
    ListView lvPlan;
    @BindView(R.id.myNodeCB)
    CheckBox myNodeCB;
    @BindView(R.id.myNodeTv)
    TextView myNodeTv;


    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletAddress;
    private SuperNodeAdapter superNodeAdapter;


    private ArrayList<SuperNodeModel> myVoteInfolist;
    private ArrayList<SuperNodeModel> nodeList;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_plan, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initUI();
        initData();
        initListener();
    }

    private void initListener() {
        myNodeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    superNodeAdapter.setNewData(myVoteInfolist);
                }else {
                    superNodeAdapter.setNewData(nodeList);
                }

                superNodeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {

        getAllNode();
    }

    private void getAllNode() {

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        LogUtils.e(currentIdentityWalletAddress);
        HashMap<String, Object> listReq = new HashMap<>();

//        "address": "buafafsaffasfsafsfafds",
//         "identityType": "1",
//          "nodeName": "华润",
//          "capitalAddress": "{capitalAddress}"
        listReq.put(Constants.ADDRESS, currentIdentityWalletAddress);
//        listReq.put("identityType","");
//        listReq.put("nodeName","");
//        listReq.put("capitalAddress","");

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        Call<ApiResult<SuperNodeDto>> superNodeList = nodePlanService.getSuperNodeList(listReq);
        superNodeList.enqueue(new Callback<ApiResult<SuperNodeDto>>() {

            @Override
            public void onResponse(Call<ApiResult<SuperNodeDto>> call, Response<ApiResult<SuperNodeDto>> response) {
                ApiResult<SuperNodeDto> body = response.body();
                LogUtils.e("请求成功" + body.getData());
                nodeList = body.getData().getNodeList();
                if (nodeList !=null) {
                    superNodeAdapter.setNewData(nodeList);
                    superNodeAdapter.notifyDataSetChanged();
                    myVoteInfolist = myVoteInfoList(nodeList);
                }


            }

            @Override
            public void onFailure(Call<ApiResult<SuperNodeDto>> call, Throwable t) {
                LogUtils.e("请求失败" + t.getMessage());

            }
        });


    }

    private ArrayList<SuperNodeModel> myVoteInfoList(@NonNull ArrayList<SuperNodeModel> nodeList) {

        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            SuperNodeModel superNodeModel = nodeList.get(i);
            if (superNodeModel != null) {
                String myVoteCount = superNodeModel.getMyVoteCount();
                if ( (TextUtils.isEmpty(myVoteCount) &&(Integer.parseInt(myVoteCount)) > 0)
                        ||currentIdentityWalletAddress.equals(superNodeModel.getNodeCapitalAddress())) {
                    superNodeModels.add(superNodeModel);
                }
            }
        }

        return superNodeModels;
    }

    private void initUI() {
        initTopBar();
        initListView();
    }

    private void initListView() {
        superNodeAdapter = new SuperNodeAdapter(this.getContext());
        lvPlan.setAdapter(superNodeAdapter);


    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.addRightImageButton(R.mipmap.icon_vote_record, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPVoteRecordFragment());
            }
        });
    }

}
