package com.bupocket.fragment.discover;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeCampaignAdapter;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.database.greendao.SuperNodeModelDao;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SuperNodeDto;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.wallet.Account;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BPNodeCampaignFragment extends BaseTransferFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.refreshComLv)
    ListView nodeCampaignLv;
    @BindView(R.id.myNodeCB)
    CheckBox myNodeCB;
    @BindView(R.id.myNodeTv)
    TextView myNodeTv;
    @BindView(R.id.nodeSearchET)
    EditText nodeSearchET;
    @BindView(R.id.recordEmptyLL)
    LinearLayout recordEmptyLL;
    @BindView(R.id.myNodeTipsIv)
    ImageView mMyNodeTipsIv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.reloadBtn)
    QMUIRoundButton reloadBtn;
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;
    @BindView(R.id.nodeSearchRL)
    RelativeLayout nodeSearchRL;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;


    private String currentWalletAddress;
    private NodeCampaignAdapter superNodeAdapter;
    private QMUIPopup myNodeExplainPopup;
    private List<SuperNodeModel> myVoteInfoList;
    private List<SuperNodeModel> nodeList;

    private Call<ApiResult<SuperNodeDto>> serviceSuperNode;
    private SuperNodeDto allData;
    private SuperNodeModelDao superNodeModelDao;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_campaign;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
    }

    @Override
    protected void initData() {
        if (myVoteInfoList == null) {
            myVoteInfoList = new ArrayList<>();
        }
        if (nodeList == null) {
            nodeList = new ArrayList<>();
        }

        currentWalletAddress = getWalletAddress();

        queryData();
        nodeCampaignLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                reqAllNodeData();
            }
        }, 500);

    }

    private void queryData() {
        superNodeModelDao = mApplication.getDaoSession().getSuperNodeModelDao();
        List<SuperNodeModel> superNodeModels = superNodeModelDao.loadAll();
        if (superNodeModels == null || superNodeModels.size() == 0) {
            qmuiEmptyView.show(true);
            return;
        }
        nodeList = superNodeModels;
        superNodeAdapter.setNewData(nodeList);
    }


    @Override
    protected void setListeners() {
        myNodeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifyData();
            }
        });

        nodeSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                notifyData();
            }
        });

        nodeSearchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(nodeSearchET.getApplicationWindowToken(), 0);
                    }

                }
                return false;
            }
        });

        nodeCampaignLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SuperNodeModel superNodeModel = superNodeAdapter.getItem(position);
                goNodeDetail(superNodeModel);

            }
        });

//        superNodeAdapter.setOnItemBtnListener(new NodeCampaignAdapter.OnItemBtnListener() {
//            @Override
//            public void onClick(int position, int btn) {
//                SuperNodeModel superNodeModel = superNodeAdapter.getItem(position);
//                switch (btn) {
//                    case R.id.revokeVoteBtn:
//                        GoRevokeVote(superNodeModel);
//                        break;
//                    case R.id.shareBtn:

//                        break;
//                    case R.id.voteRecordBtn:
//                        GoVoteRecord(superNodeModel);
//                        break;
//                }
//            }
//        });
//        superNodeAdapter.setOnItemBtnListener(new NodeCampaignAdapter.OnItemBtnListener() {
//            @Override
//            public void onClick(int position, int btn) {
//                SuperNodeModel superNodeModel = superNodeAdapter.getItem(position);
//                switch (btn) {
//                    case R.id.revokeVoteBtn:
//                        GoRevokeVote(superNodeModel);
//                        break;
//                    case R.id.shareBtn:

//                        break;
//                    case R.id.voteRecordBtn:
//                        GoVoteRecord(superNodeModel);
//                        break;
//                }
//            }
//        });


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqAllNodeData();
            }
        });

        mMyNodeTipsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopup();
                myNodeExplainPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                myNodeExplainPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                myNodeExplainPopup.show(v);
                ImageView arrowUp = myNodeExplainPopup.getDecorView().findViewById(R.id.arrow_up);
                arrowUp.setImageDrawable(getResources().getDrawable(R.mipmap.triangle));
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0, 200, 1, false);
            }
        });


    }

    private void notifyData() {
        List<SuperNodeModel> notifyNodeList = new ArrayList<>();
        if (myNodeCB.isChecked() && !nodeSearchET.getText().toString().isEmpty()) {
            notifyNodeList = searchData(myVoteInfoList);
        } else if (myNodeCB.isChecked()) {
            notifyNodeList = myVoteInfoList;
        } else if (!nodeSearchET.getText().toString().isEmpty()) {
            notifyNodeList = searchData(nodeList);
        } else {
            notifyNodeList = nodeList;
        }
        setEmpty(notifyNodeList.size() == 0);
        superNodeAdapter.setNewData(notifyNodeList);
    }

    private List<SuperNodeModel> searchData(List<SuperNodeModel> sourceData) {
        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();
        String inputSearch = nodeSearchET.getText().toString();
        Pattern pattern = Pattern.compile(inputSearch);
        for (int i = 0; i < sourceData.size(); i++) {
            Matcher matcher = pattern.matcher(sourceData.get(i).getNodeName());
            if (matcher.find()) {
                superNodeModels.add(sourceData.get(i));
            }
        }
        return superNodeModels;
    }

    private void GoVoteRecord(SuperNodeModel superNodeModel) {
        BPMyNodeVoteRecordFragment fragment = new BPMyNodeVoteRecordFragment();
        Bundle args1 = new Bundle();
        args1.putSerializable("itemNodeInfo", superNodeModel);
        fragment.setArguments(args1);
        startFragment(fragment);
    }

    private void goNodeDetail(SuperNodeModel superNodeModel) {
//        String status = superNodeModel.getStatus();
//        if (SuperNodeStatusEnum.RUNNING.getCode().equals(status)) {
//            DialogUtils.showMessageNoTitleDialog(mContext, String.format(getString(R.string.super_status_info), getString(SuperNodeStatusEnum.RUNNING.getNameRes())));
//        } else if (SuperNodeStatusEnum.FAILED.getCode().equals(status)) {
//            DialogUtils.showMessageNoTitleDialog(mContext, String.format(getString(R.string.super_status_info), getString(SuperNodeStatusEnum.FAILED.getNameRes())));
//        } else {
        Bundle args = new Bundle();
        args.putSerializable("itemInfo", superNodeModel);
        String accountTag;
        if (LocaleUtil.isChinese()) {
            accountTag=allData.getAccountTag();
        }else{
            accountTag=allData.getAccountTagEn();
        }
        args.putString(ConstantsType.ACCOUNT_TAG,accountTag);

        BPNodeDetailFragment bpNodeShareFragment = new BPNodeDetailFragment();
        bpNodeShareFragment.setArguments(args);
        startFragment(bpNodeShareFragment);
//        }
    }




    private void initPopup() {
        if (myNodeExplainPopup == null) {
            myNodeExplainPopup = new QMUIPopup(getContext(), QMUIPopup.DIRECTION_NONE);
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(myNodeExplainPopup.generateLayoutParam(
                    QMUIDisplayHelper.dp2px(getContext(), 280),
                    WRAP_CONTENT
            ));
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
            int padding = QMUIDisplayHelper.dp2px(getContext(), 10);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(getString(R.string.node_associated_with_me_help_txt2));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_white));
            textView.setBackgroundColor(getResources().getColor(R.color.popup_background_color));
            myNodeExplainPopup.setContentView(textView);
        }
    }






    private void setEmpty(boolean isVisible) {
        if (isVisible) {
            recordEmptyLL.setVisibility(View.VISIBLE);
        } else {
            recordEmptyLL.setVisibility(View.GONE);
        }
    }


    private void reqAllNodeData() {

        HashMap<String, Object> listReq = new HashMap<>();
        listReq.put(Constants.ADDRESS, currentWalletAddress);

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
        serviceSuperNode = nodePlanService.getSuperNodeList(listReq);
        serviceSuperNode.enqueue(new Callback<ApiResult<SuperNodeDto>>() {

            @Override
            public void onResponse(Call<ApiResult<SuperNodeDto>> call, Response<ApiResult<SuperNodeDto>> response) {

                ApiResult<SuperNodeDto> body = response.body();

                if (body == null) {
                    return;
                }
                allData = body.getData();
                nodeList = body.getData().getNodeList();

                insertNodeData(nodeList);

                myVoteInfoList = myVoteInfoList(nodeList);

                notifyData();
                loadFailedLL.setVisibility(View.GONE);
                refreshLayout.finishRefresh();
                qmuiEmptyView.show("", "");
            }

            @Override
            public void onFailure(Call<ApiResult<SuperNodeDto>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                qmuiEmptyView.show("", "");
                refreshLayout.finishRefresh();
                if (nodeList != null && nodeList.size() > 0) {
                    return;
                }

                myVoteInfoList.clear();
                nodeList.clear();
                loadFailedLL.setVisibility(View.VISIBLE);
                setEmpty(false);
                superNodeAdapter.setNewData(new ArrayList<SuperNodeModel>());

            }
        });

    }

    private void insertNodeData(List<SuperNodeModel> nodeList) {
        superNodeModelDao.deleteAll();
        superNodeModelDao.insertInTx(nodeList);
    }

    /**
     * @param nodeList
     * @return
     */
    private List<SuperNodeModel> myVoteInfoList(@NonNull List<SuperNodeModel> nodeList) {

        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            SuperNodeModel superNodeModel = nodeList.get(i);
            if (superNodeModel != null) {
                String myVoteCount = superNodeModel.getMyVoteCount();
                if ((!TextUtils.isEmpty(myVoteCount) && (Integer.parseInt(myVoteCount)) > 0)
                        || currentWalletAddress.equals(superNodeModel.getNodeCapitalAddress())) {
                    superNodeModels.add(superNodeModel);
                }
            }
        }

        return superNodeModels;
    }


    private void initListView() {
        superNodeAdapter = new NodeCampaignAdapter(this.getContext());
        nodeCampaignLv.setAdapter(superNodeAdapter);
        refreshLayout.setEnableLoadMore(false);

    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(R.string.run_for_node_txt);
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


    @Override
    public void onPause() {
        super.onPause();
        serviceSuperNode.cancel();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        myVoteInfoList = null;
        nodeList = null;
        currentWalletAddress = null;
    }
}