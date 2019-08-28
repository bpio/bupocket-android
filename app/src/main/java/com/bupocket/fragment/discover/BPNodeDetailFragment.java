package com.bupocket.fragment.discover;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.enums.TokenTypeEnum;
import com.bupocket.http.api.NodeBuildService;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetQRContentDto;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.model.NodeDetailModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.model.TransConfirmModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPNodeDetailFragment extends BaseTransferFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.nodeNameTv)
    TextView mNodeNameTv;
    @BindView(R.id.nodeTypeTv)
    TextView mNodeTypeTv;
    @BindView(R.id.headIconIv)
    QMUIRadiusImageView mNodeIconIv;
    @BindView(R.id.wbShare)
    WebView webView;
    @BindView(R.id.nodeDataDetailLL)
    LinearLayout nodeDataDetailLL;
    @BindView(R.id.nodeDetailStateLL)
    LinearLayout nodeDetailStateLL;
    @BindView(R.id.nodeRevokeBtn)
    Button nodeRevokeBtn;
    @BindView(R.id.nodeInfoTv)
    TextView nodeInfoTv;


    private QMUIPopup nodeMorePop;
    private SuperNodeModel itemData;
    private String metaData;
    private String accountTag;
    public final static String CSS_STYLE = "<style>* {font-size:13px;line-height:20px;}p {color:#666666;}</style>";

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_node_share;
    }

    @Override
    protected void initView() {
        initTopBar();
    }


    @Override
    protected void initData() {
        itemData = (SuperNodeModel) getArguments().getSerializable("itemInfo");
        accountTag = getArguments().getString(ConstantsType.ACCOUNT_TAG);
        mNodeNameTv.setText(itemData.getNodeName());


        initHeadView();
        getNodeDetailData();
    }


    private void goRevokeVote(SuperNodeModel superNodeModel) {
        if ("0".equals(superNodeModel.getMyVoteCount())) {
            DialogUtils.showMessageNoTitleDialog(getContext(), getString(R.string.revoke_no_vote_error_message_txt));
        } else {
            showRevokeVoteDialog(superNodeModel);
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
        String accountTag = this.accountTag;


        TransferUtils.confirmTxSheet(mContext, getWalletAddress(), destAddress, accountTag,
                transactionAmount, Constants.NODE_COMMON_FEE,
                transactionDetail, transactionParams, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {
                        confirmUnVote(input, nodeId);
                    }
                });
    }


    private void confirmUnVote(final JSONObject input, final String nodeId) {

        final String amount = "0";

        getSignatureInfo(new SignatureListener() {
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

                Runnable unVoteRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final TransactionBuildBlobResponse buildBlobResponse = Wallet.getInstance().buildBlob(amount, input.toJSONString(), WalletCurrentUtils.getWalletAddress(spHelper), String.valueOf(Constants.NODE_COMMON_FEE), Constants.CONTRACT_ADDRESS, metaData);
                            String txHash = buildBlobResponse.getResult().getHash();
                            NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                            Call<ApiResult> call;
                            Map<String, Object> paramsMap = new HashMap<>();
                            paramsMap.put("hash", txHash);
                            paramsMap.put("nodeId", nodeId);
                            paramsMap.put("initiatorAddress", WalletCurrentUtils.getWalletAddress(spHelper));
                            call = nodePlanService.revokeVote(paramsMap);
                            call.enqueue(new Callback<ApiResult>() {
                                @Override
                                public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {

                                    txSendingTipDialog.dismiss();
                                    ApiResult respDto = response.body();
                                    if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                                        submitTransactionBase(privateKey, buildBlobResponse);
                                    } else {
                                        String msg = DialogUtils.byCodeToMsg(mContext, respDto.getErrCode());
                                        if (!msg.isEmpty()) {
                                            DialogUtils.showMessageNoTitleDialog(getContext(), msg);
                                            return;
                                        }
                                        DialogUtils.showMessageNoTitleDialog(getContext(), respDto.getMsg());

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
                };

                ThreadManager.getInstance().execute(unVoteRunnable);
            }
        });


    }


    private void addNodeStateItem(String date, String time, String amount, String title, boolean isNode, boolean isTop, boolean isBottom) {
        RelativeLayout nodeDataLL = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.view_node_state_item, null, false);

        TextView stateTimeTv = (TextView) nodeDataLL.findViewById(R.id.stateTimeTv);
        stateTimeTv.setText(time);
        TextView stateDetailTv = (TextView) nodeDataLL.findViewById(R.id.stateDetailTv);
        stateDetailTv.setText(amount);

        TextView stateDateTv = (TextView) nodeDataLL.findViewById(R.id.stateDateTv);
        stateDateTv.setText(date);
        TextView stateDetailNodeTv = (TextView) nodeDataLL.findViewById(R.id.stateDetailNodeTv);

        ImageView stateTagIV = (ImageView) nodeDataLL.findViewById(R.id.stateTagIV);
        ImageView stateTagGreenIV = (ImageView) nodeDataLL.findViewById(R.id.stateTagGreenIV);

        if (isNode) {
            stateTagGreenIV.setVisibility(View.VISIBLE);
            stateDetailNodeTv.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(title)) {
                stateDetailNodeTv.setText(title);
            }
        } else {
            stateTagGreenIV.setVisibility(View.GONE);
            stateDetailNodeTv.setVisibility(View.GONE);
        }

        View lineBottom = nodeDataLL.findViewById(R.id.stateLineBottomView);
        if (isBottom) {
            lineBottom.setVisibility(View.GONE);
        }
        View lineTop = nodeDataLL.findViewById(R.id.stateLineTopView);
        if (isTop) {
            lineTop.setVisibility(View.GONE);
        }


        nodeDetailStateLL.addView(nodeDataLL);
    }


    private void addNodeItemData(int title, String amount) {
        String titleMsg = getResources().getString(title);
        addNodeItemData(titleMsg, amount, false);
    }

    private void addNodeItemData(String title, String amount, boolean isInfo) {
        LinearLayout nodeDataLL = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.view_node_data_item, null, false);
        TextView nodeTitle = (TextView) nodeDataLL.findViewById(R.id.nodeDataItemTitleTv);
        nodeTitle.setText(title);
        ImageView nodeDataIv = (ImageView) nodeDataLL.findViewById(R.id.nodeDataItemIv);
        if (isInfo) {
            nodeDataIv.setVisibility(View.VISIBLE);
        } else {
            nodeDataIv.setVisibility(View.GONE);
        }
        nodeDataIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showInformationDialog(getString(R.string.node_ratio_dialog), mContext, v);
            }
        });
        TextView nodeAmountTv = (TextView) nodeDataLL.findViewById(R.id.nodeDataItemAmountTv);
        nodeAmountTv.setText(amount);

        nodeDataDetailLL.addView(nodeDataLL);
    }

    private void initHeadView() {
        // set node type
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(itemData.getIdentityType())) {
            mNodeTypeTv.setText(getContext().getResources().getString(R.string.common_node));

        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(itemData.getIdentityType())) {
            mNodeTypeTv.setText(getContext().getResources().getString(R.string.ecological_node));
        }


        Glide.with(getContext())
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(itemData.getNodeLogo()))
                .into(mNodeIconIv);
    }

    @Override
    protected void setListeners() {

        nodeRevokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogVoteNode();
            }
        });
    }

    private void dialogVoteNode() {
        final QMUIBottomSheet supportDialog = new QMUIBottomSheet(getContext());
        supportDialog.setContentView(supportDialog.getLayoutInflater().inflate(R.layout.view_node_detail_vote, null));
        final EditText nodeVoteEt = (EditText) supportDialog.findViewById(R.id.nodeVoteEt);
        final TextView amountTotalTv = (TextView) supportDialog.findViewById(R.id.tvDialogTotalAmount);
        final View confirmBtn = supportDialog.findViewById(R.id.tvDialogSupport);
        amountTotalTv.setText("0");
        nodeVoteEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String amount = s.toString();
                if (TextUtils.isEmpty(amount)) {
                    amount="0";
                }

                if (amount.length()>9) {
                    confirmBtn.setEnabled(false);
                    return;
                }

                amountTotalTv.setText(CommonUtil.format(amount));
                long amountNum = Long.parseLong(amount);
                if (amountNum != 0 && amountNum % 10 == 0 && amountNum < Constants.MAX_SEND_AMOUNT) {
                    confirmBtn.setEnabled(true);
                } else {
                    confirmBtn.setEnabled(false);
                }


            }
        });


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = nodeVoteEt.getText().toString();
                if (TextUtils.isEmpty(amount)) {
                    return;
                }

                showConfirmSupport(amount, supportDialog);

            }
        });

        supportDialog.findViewById(R.id.ivDialogCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.dismiss();
            }
        });

        supportDialog.show();
    }

    private void showConfirmSupport(String amount, QMUIBottomSheet supportDialog) {


        String tokenBalance = spHelper.getSharedPreference(WalletCurrentUtils.getWalletAddress(spHelper) + "tokenBalance", "0").toString();
        if (Double.parseDouble(tokenBalance) < Double.parseDouble(amount)) {
            ToastUtil.showToast(getActivity(), getResources().getString(R.string.balance_not_enough), Toast.LENGTH_SHORT);
            return;
        }


        supportDialog.dismiss();
        final GetQRContentDto getQRContentDto = new GetQRContentDto();
        String destAddress = Constants.CONTRACT_ADDRESS;
        String transactionAmount = amount;
        double scanTxFee = Constants.NODE_COMMON_FEE;
        String transactionDetail = "";
        String nodeType = "validator";
        String accountTag = "";
        if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(itemData.getIdentityType())) {
            nodeType = "kol";
        }

        String transactionParams = "{\"method\":\"vote\",\"params\":{\"role\":\"" + nodeType + "\",\"address\":\"" + itemData.getNodeCapitalAddress() + "\"}}";


        getQRContentDto.setAmount(amount);
        getQRContentDto.setDestAddress(destAddress);
        getQRContentDto.setScript(transactionParams);
        getQRContentDto.setFee(scanTxFee);
        TransferUtils.confirmTxSheet(mContext, getWalletAddress(), destAddress,
                accountTag, transactionAmount, scanTxFee,
                transactionDetail, transactionParams, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {

                        confirmTransaction(getQRContentDto);
                    }
                });
    }

    private void confirmTransaction(final GetQRContentDto getQRContentDto) {

        Runnable confirmSupportRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                    final TransactionBuildBlobResponse transBlob = Wallet.getInstance().buildBlob(getQRContentDto.getAmount(), getQRContentDto.getScript(), getWalletAddress(), String.valueOf(getQRContentDto.getFee()), getQRContentDto.getDestAddress(), "");
                    final String hash = transBlob.getResult().getHash();
                    if (TextUtils.isEmpty(hash)) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.showMessageNoTitleDialog(mContext, transBlob.getErrorDesc());
                            }
                        });
                        return;
                    }


                    getSignatureInfo(new SignatureListener() {
                        @Override
                        public void success(String privateKey) {

                            submitTransactionBase(privateKey, transBlob);

                        }
                    });

                } catch (WalletException walletException) {

                    if (walletException.getErrCode().equals(com.bupocket.wallet.enums.ExceptionEnum.ADDRESS_NOT_EXIST.getCode())) {
                        ToastUtil.showToast(getActivity(), getString(R.string.address_not_exist), Toast.LENGTH_SHORT);
                    }
//                    ToastUtil.showToast(getActivity(), walletException.getErrMsg(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadManager.getInstance().execute(confirmSupportRunnable);

    }

    private void getNodeDetailData() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("nodeId", itemData.getNodeId());
        final NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
        Call<ApiResult<NodeDetailModel>> nodeValidateDetail = null;

        final String identityType = itemData.getIdentityType();
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(identityType)) {
            nodeValidateDetail = nodePlanService.getNodeValidateDetail(map);

        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(identityType)) {
            nodeValidateDetail = nodePlanService.getNodeEcologyDetail(map);
        }


        nodeValidateDetail.enqueue(new Callback<ApiResult<NodeDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<NodeDetailModel>> call, Response<ApiResult<NodeDetailModel>> response) {
                ApiResult<NodeDetailModel> body = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    NodeDetailModel data = body.getData();
                    if (data != null) {
                        NodeDetailModel.NodeDataBean nodeData = data.getNodeData();
                        NodeDetailModel.NodeInfoBean nodeInfo = data.getNodeInfo();
                        String slogan = nodeInfo.getSlogan();
                        String introduce = nodeInfo.getIntroduce();
                        if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(identityType)) {
                            introduce = nodeInfo.getApplyIntroduce();
                        }

                        nodeInfoTv.setText(slogan);

                        webView.loadDataWithBaseURL(null, CSS_STYLE + introduce, "text/html", "utf-8", null);

                        initNodeDataView(nodeData);
                        List<NodeDetailModel.NodeInfoBean.TimelineBean> timeline = data.getNodeInfo().getTimeline();
                        initNodeLineView(timeline);

                    }


                } else {
                    ToastUtil.showToast(getActivity(), body.getMsg(), Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onFailure(Call<ApiResult<NodeDetailModel>> call, Throwable t) {

            }
        });

    }

    private void initNodeLineView(List<NodeDetailModel.NodeInfoBean.TimelineBean> timeline) {


        for (int i = 0; i < timeline.size(); i++) {
            String title = "";
            boolean isNode = false;
            boolean isTop = false;
            boolean isBottom = false;

            NodeDetailModel.NodeInfoBean.TimelineBean timelineBean = timeline.get(i);
            long createTime = timelineBean.getCreateTime();
            String date = TimeUtil.timeStamp2Date(createTime + "", "MM-dd");
            String time = TimeUtil.timeStamp2Date(createTime + "", "HH:mm");

            String content = timelineBean.getContent();
            String type = timelineBean.getType();
            title = timelineBean.getTitle();
            if (!LocaleUtil.isChinese()) {
                content = timelineBean.getEnContent();
                title = timelineBean.getEnTitle();
            }


            if (i == 0) {
                isTop = true;
            }
            if (i == timeline.size() - 1) {
                isBottom = true;
            }
            if (!TextUtils.isEmpty(type) && type.equals("1")) {
                isNode = true;

            }
            addNodeStateItem(date, time, content, title, isNode, isTop, isBottom);

        }
    }

    private void initNodeDataView(NodeDetailModel.NodeDataBean nodeData) {

        addNodeItemData(R.string.node_equity_value, CommonUtil.format(nodeData.getEquityValue()));
        addNodeItemData(R.string.node_deposit, CommonUtil.format(nodeData.getDeposit()));


        addNodeItemData(R.string.node_total_reward_amount, CommonUtil.formatDecimalDouble(nodeData.getTotalRewardAmount()));


        addNodeItemData(getResources().getString(R.string.node_ratio), nodeData.getRatio() + "%", true);

        String totalVoteCount = nodeData.getTotalVoteCount();
        int haveVote = R.string.number_have_votes;
        if (!CommonUtil.isSingle(totalVoteCount)) {
            haveVote = R.string.number_have_votes_s;
        }
        addNodeItemData(haveVote, totalVoteCount);


        String totalVoteCount1 = itemData.getMyVoteCount();
        int myVote = R.string.my_votes_number;
        if (!CommonUtil.isSingle(totalVoteCount1)) {
            myVote = R.string.my_votes_number_s;
        }
        addNodeItemData(myVote, totalVoteCount1);

    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(true);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.node_detail));
        mTopBar.addRightImageButton(R.mipmap.ic_node_more, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initPopup(v);

            }
        });
    }


    private void initPopup(View v) {
        if (nodeMorePop == null) {
            nodeMorePop = new QMUIPopup(getContext(), QMUIPopup.DIRECTION_NONE);
            View moreView = LayoutInflater.from(mContext).inflate(R.layout.view_node_more, null);
            moreView.findViewById(R.id.revokeLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nodeMorePop.dismiss();
                    goRevokeVote(itemData);
                }
            });

            moreView.findViewById(R.id.voteRecordLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoVoteRecord(itemData);
                    nodeMorePop.dismiss();
                }
            });
            nodeMorePop.setContentView(moreView);
        }
        nodeMorePop.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        nodeMorePop.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
        nodeMorePop.show(v);
        ImageView arrowUp = nodeMorePop.getDecorView().findViewById(R.id.arrow_up);
        arrowUp.setImageDrawable(getResources().getDrawable(R.mipmap.triangle));
    }


    private void GoVoteRecord(SuperNodeModel superNodeModel) {
        BPMyNodeVoteRecordFragment fragment = new BPMyNodeVoteRecordFragment();
        Bundle args1 = new Bundle();
        args1.putSerializable("itemNodeInfo", superNodeModel);
        fragment.setArguments(args1);
        startFragment(fragment);
    }

}
