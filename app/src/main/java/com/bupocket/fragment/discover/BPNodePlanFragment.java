package com.bupocket.fragment.discover;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.adaptor.SuperNodeAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SuperNodeDto;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class BPNodePlanFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.lvPlan)
    ListView lvPlan;
    @BindView(R.id.myNodeCB)
    CheckBox myNodeCB;
    @BindView(R.id.myNodeTv)
    TextView myNodeTv;
    @BindView(R.id.etNodeSearch)
    EditText etNodeSearch;


    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentWalletAddress;
    private Boolean whetherIdentityWallet = false;
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
        setListener();
    }

    private void setListener() {
        myNodeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    superNodeAdapter.setNewData(myVoteInfolist);
                } else {
                    superNodeAdapter.setNewData(nodeList);
                }

                superNodeAdapter.notifyDataSetChanged();
            }
        });

        etNodeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchNode( s.toString());
            }
        });

        etNodeSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    //先隐藏键盘
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(etNodeSearch.getApplicationWindowToken(), 0);
                    }

                }
                //记得返回false
                return false;
            }
        });
    }

    private ArrayList<SuperNodeModel> searchNode(String s) {

        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();

        Pattern pattern = Pattern.compile(s);
        for (int i = 0; i < nodeList.size(); i++) {
            Matcher matcher = pattern.matcher(nodeList.get(i).getNodeName());
            if (matcher.find()) {
                superNodeModels.add(nodeList.get(i));
            }
        }
        if (superNodeModels.size()>0) {
            superNodeAdapter.setNewData(superNodeModels);
            superNodeAdapter.notifyDataSetChanged();
        }

        return superNodeModels;
    }

    private void initData() {

        getAllNode();
    }

    private void getAllNode() {

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress","").toString();
        if(CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString())){
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
            whetherIdentityWallet = true;
        }
        LogUtils.e(currentWalletAddress);
        HashMap<String, Object> listReq = new HashMap<>();

//        "address": "buafafsaffasfsafsfafds",
//         "identityType": "1",
//          "nodeName": "华润",
//          "capitalAddress": "{capitalAddress}"
        listReq.put(Constants.ADDRESS, currentWalletAddress);
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
                if (nodeList != null) {
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
                if ((TextUtils.isEmpty(myVoteCount) && (Integer.parseInt(myVoteCount)) > 0)
                        || currentWalletAddress.equals(superNodeModel.getNodeCapitalAddress())) {
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

    public void confirmUnVote(SuperNodeModel nodeInfo){
        final String nodeAddress = nodeInfo.getNodeCapitalAddress();
        final String role = nodeInfo.getIdentityType();
        final String nodeId = nodeInfo.getNodeId();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final TransactionBuildBlobResponse buildBlobResponse = Wallet.getInstance().unVoteBuildBlob(currentWalletAddress,role,nodeAddress,String.valueOf(Constants.MIN_FEE));
                    String txHash = buildBlobResponse.getResult().getHash();
                    NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                    Call<ApiResult> call;
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash",txHash);
                    paramsMap.put("nodeId",nodeId);
                    paramsMap.put("initiatorAddress",currentWalletAddress);
                    call = nodePlanService.revokeVote(paramsMap);
                    call.enqueue(new Callback<ApiResult>() {
                        @Override
                        public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                            ApiResult respDto = response.body();
                            if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
//                                submitTransaction(buildBlobResponse);
                            }else {
                                Toast.makeText(getContext(),respDto.getErrCode(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult> call, Throwable t) {
                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
}