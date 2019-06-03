package com.bupocket.fragment.discover;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.SuperNodeAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.SingatureListener;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.SuperNodeStatusEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SuperNodeDto;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.wallet.Wallet;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.okhttp.internal.framed.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BPNodePlanFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.lvRefresh)
    ObservableListView lvPlan;
    @BindView(R.id.myNodeCB)
    CheckBox myNodeCB;
    @BindView(R.id.myNodeTv)
    TextView myNodeTv;
    @BindView(R.id.etNodeSearch)
    EditText etNodeSearch;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.myNodeTipsIv)
    ImageView mMyNodeTipsIv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.copyCommandBtn)
    QMUIRoundButton copyCommandBtn;
    @BindView(R.id.llLoadFailed)
    LinearLayout llLoadFailed;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.llNodeSearch)
    RelativeLayout llNodeSearch;
    @BindView(R.id.llHeadView)
    LinearLayout llHeadView;


    private String currentWalletAddress;
    private SuperNodeAdapter superNodeAdapter;
    private QMUIPopup myNodeExplainPopup;
    private ArrayList<SuperNodeModel> myVoteInfoList;
    private ArrayList<SuperNodeModel> nodeList;
    private int mBaseTranslationY;
    private String metaData;
    private Call<ApiResult<SuperNodeDto>> serviceSuperNode;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_plan;
    }

    @Override
    protected void initView() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initTopBar();
        initListView();
        setEmpty(true);
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
        lvPlan.postDelayed(new Runnable() {
            @Override
            public void run() {
                reqAllNodeData();
            }
        }, 200);
    }


    @Override
    protected void setListeners() {
        myNodeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifyData();
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
                notifyData();
            }
        });

        etNodeSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(etNodeSearch.getApplicationWindowToken(), 0);
                    }

                }
                return false;
            }
        });

        superNodeAdapter.setOnItemBtnListener(new SuperNodeAdapter.OnItemBtnListener() {
            @Override
            public void onClick(int position, int btn) {
                SuperNodeModel superNodeModel = superNodeAdapter.getItem(position);
                switch (btn) {
                    case R.id.revokeVoteBtn:
                        GoRevokeVote(superNodeModel);
                        break;
                    case R.id.shareBtn:
                        GoShareVote(superNodeModel);
                        break;
                    case R.id.voteRecordBtn:
                        GoVoteRecord(superNodeModel);
                        break;
                }
            }
        });


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

        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0, 200, 1, false);
            }
        });


        lvPlan.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//                LogUtils.e("scrollY:" + scrollY + "\tfirstScroll:" + firstScroll + "\tdragging:" + dragging);

                if (dragging) {
                    int toolbarHeight = tvTitle.getHeight();
                    if (firstScroll) {
                        float currentHeaderTranslationY = ViewHelper.getTranslationY(llHeadView);
                        if (-toolbarHeight < currentHeaderTranslationY) {
                            mBaseTranslationY = scrollY;
                        }
                    }
                    float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
                    ViewPropertyAnimator.animate(llHeadView).cancel();
                    ViewHelper.setTranslationY(llHeadView, headerTranslationY);
//                    setMargin((int) (llHeadView.getHeight()-headerTranslationY));
                }

            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                mBaseTranslationY = 0;

                if (scrollState == ScrollState.DOWN) {
                    showToolbar();
                } else if (scrollState == ScrollState.UP) {
                    int toolbarHeight = tvTitle.getHeight();
                    int scrollY = lvPlan.getCurrentScrollY();
                    if (toolbarHeight <= scrollY) {
                        hideToolbar();
                    } else {
                        showToolbar();
                    }
                } else {
                    // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
                    if (!toolbarIsShown() && !toolbarIsHidden()) {
                        // Toolbar is moving but doesn't know which to move:
                        // you can change this to hideToolbar()
                        showToolbar();
                    }
                }
            }
        });
    }

    private void notifyData() {
        ArrayList<SuperNodeModel> notifyNodeList = new ArrayList<>();
        if (myNodeCB.isChecked() && !etNodeSearch.getText().toString().isEmpty()) {
            notifyNodeList = searchData(myVoteInfoList);
        } else if (myNodeCB.isChecked()) {
            notifyNodeList = myVoteInfoList;
        } else if (!etNodeSearch.getText().toString().isEmpty()) {
            notifyNodeList = searchData(nodeList);
        } else {
            notifyNodeList = nodeList;
        }
        setEmpty(notifyNodeList.size()==0);
        superNodeAdapter.setNewData(notifyNodeList);

    }

    private ArrayList<SuperNodeModel> searchData(ArrayList<SuperNodeModel> sourceData) {
        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();
        String inputSearch = etNodeSearch.getText().toString();
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
        BPMeNodeVoteRecordFragment fragment = new BPMeNodeVoteRecordFragment();
        Bundle args1 = new Bundle();
        args1.putParcelable("itemNodeInfo", superNodeModel);
        fragment.setArguments(args1);
        startFragment(fragment);
    }

    private void GoShareVote(SuperNodeModel superNodeModel) {
        String status = superNodeModel.getStatus();
        if (SuperNodeStatusEnum.RUNNING.getCode().equals(status)) {
            CommonUtil.showMessageDialog(mContext, String.format(getString(R.string.super_status_info), getString(SuperNodeStatusEnum.RUNNING.getNameRes())));
        } else if (SuperNodeStatusEnum.FAILED.getCode().equals(status)) {
            CommonUtil.showMessageDialog(mContext, String.format(getString(R.string.super_status_info), getString(SuperNodeStatusEnum.FAILED.getNameRes())));
        } else {
            Bundle args = new Bundle();
            args.putParcelable("itemInfo", superNodeModel);
            BPNodeShareFragment bpNodeShareFragment = new BPNodeShareFragment();
            bpNodeShareFragment.setArguments(args);
            startFragment(bpNodeShareFragment);
        }
    }

    private void GoRevokeVote(SuperNodeModel superNodeModel) {
        if ("0".equals(superNodeModel.getMyVoteCount())) {
            CommonUtil.showMessageDialog(getContext(), getString(R.string.revoke_no_vote_error_message_txt));
        } else {
            showRevokeVoteDialog(superNodeModel);
        }
    }


    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(llHeadView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(llHeadView) == -tvTitle.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(llHeadView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(llHeadView).cancel();
            ViewPropertyAnimator.animate(llHeadView).translationY(0).setDuration(200).start();
        }
        setMargin(llHeadView.getHeight());
        mTopBar.setTitle("");

    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(llHeadView);
        int toolbarHeight = tvTitle.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(llHeadView).cancel();
            ViewPropertyAnimator.animate(llHeadView).translationY(-toolbarHeight).setDuration(200).start();
        }
        setMargin(llHeadView.getHeight() - tvTitle.getHeight());
        mTopBar.setTitle(R.string.run_for_node_txt);

    }


    private void setMargin(int margin) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) refreshLayout.getLayoutParams();
        lp.setMargins(0, margin, 0, 0);
        refreshLayout.getLayout().setLayoutParams(lp);
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


    private void showRevokeVoteDialog(SuperNodeModel itemInfo) {
        @SuppressLint("StringFormatMatches")
        String nodeType = itemInfo.getIdentityType();
        String role = null;
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(nodeType)) {
            role = SuperNodeTypeEnum.VALIDATOR.getName();
        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(nodeType)) {
            role = SuperNodeTypeEnum.ECOLOGICAL.getName();
        }
        String nodeAddress = itemInfo.getNodeCapitalAddress();
        final String nodeId = itemInfo.getNodeId();

        String destAddress = Constants.CONTRACT_ADDRESS;
        String transactionDetail = String.format(getString(R.string.revoke_vote_tx_details_txt), itemInfo.getNodeName());
        metaData = String.format(getString(R.string.revoke_vote_tx_details_txt), itemInfo.getNodeName());
        String transactionAmount = "0";

        final JSONObject input = new JSONObject();
        input.put("method", "unVote");
        JSONObject params = new JSONObject();
        params.put("role", role);
        params.put("address", nodeAddress);
        input.put("params", params);

        String transactionParams = input.toJSONString();

        TransferUtils.confirmTxSheet(mContext, getWalletAddress(), destAddress,
                transactionAmount, Constants.NODE_REVOKE_FEE,
                transactionDetail, transactionParams, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {
                        confirmUnVote(input, nodeId);
                    }
                });
    }

    private void confirmUnVote(final JSONObject input, final String nodeId) {

        final String amount = "0";

        getSignatureInfo(new SingatureListener() {
            @Override
            public void success(final String privateKey) {

                if (privateKey.isEmpty()) {
                    return;
                }

                final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_verify))
                        .create();
                txSendingTipDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final TransactionBuildBlobResponse buildBlobResponse = Wallet.getInstance().buildBlob(amount, input.toJSONString(), currentWalletAddress, String.valueOf(Constants.NODE_REVOKE_FEE), Constants.CONTRACT_ADDRESS, metaData);
                            String txHash = buildBlobResponse.getResult().getHash();
                            NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                            Call<ApiResult> call;
                            Map<String, Object> paramsMap = new HashMap<>();
                            paramsMap.put("hash", txHash);
                            paramsMap.put("nodeId", nodeId);
                            paramsMap.put("initiatorAddress", currentWalletAddress);
                            call = nodePlanService.revokeVote(paramsMap);
                            call.enqueue(new Callback<ApiResult>() {
                                @Override
                                public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {

                                    txSendingTipDialog.dismiss();
                                    ApiResult respDto = response.body();
                                    if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                        submitTransactionBase(privateKey, buildBlobResponse);
                                    } else {
                                        String msg = CommonUtil.byCodeToMsg(mContext, respDto.getErrCode());
                                        if (!msg.isEmpty()) {
                                            CommonUtil.showMessageDialog(getContext(), msg);
                                            return;
                                        }
                                        CommonUtil.showMessageDialog(getContext(), respDto.getMsg());

                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResult> call, Throwable t) {
                                    Toast.makeText(getContext(), getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show();
                                    txSendingTipDialog.dismiss();
                                }
                            });
                        } catch (Exception e) {

                            ToastUtil.showToast(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT);
                            txSendingTipDialog.dismiss();
                        }
                    }
                }).start();

            }
        });


    }

    private void setEmpty(boolean isVisible) {
        if (isVisible) {
            addressRecordEmptyLL.setVisibility(View.VISIBLE);
        } else {
            addressRecordEmptyLL.setVisibility(View.GONE);
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
                nodeList = body.getData().getNodeList();
                myVoteInfoList = myVoteInfoList(nodeList);

                notifyData();

                llLoadFailed.setVisibility(View.GONE);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ApiResult<SuperNodeDto>> call, Throwable t) {
                llLoadFailed.setVisibility(View.VISIBLE);
                refreshLayout.finishRefresh();
                setEmpty(false);
                superNodeAdapter.setNewData(new ArrayList<SuperNodeModel>());

            }
        });
        setMargin(llHeadView.getHeight());

    }

    /**
     * @param nodeList
     * @return
     */
    private ArrayList<SuperNodeModel> myVoteInfoList(@NonNull ArrayList<SuperNodeModel> nodeList) {

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
        superNodeAdapter = new SuperNodeAdapter(this.getContext());
        lvPlan.setAdapter(superNodeAdapter);
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
        mTopBar.addRightImageButton(R.mipmap.icon_vote_record, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPVoteRecordFragment());
            }
        });
    }


    @Override
    public void onDestroy() {
        serviceSuperNode.cancel();
        super.onDestroy();

        myVoteInfoList = null;
        nodeList = null;
        currentWalletAddress = null;
    }
}