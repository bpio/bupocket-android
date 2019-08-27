package com.bupocket.fragment.discover;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.base.BaseTransferFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.model.NodeDetailModel;
import com.bupocket.model.ShareUrlModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.ThreadManager;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.intellij.lang.annotations.Language;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
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
    private QMUIPopup nodeMorePop;

    private View mShareImageRl;
    private Uri sharePhotoUri = null;
    private SuperNodeModel itemInfo;
    private SuperNodeModel itemData;
    private Bitmap nodeLogoBitmap = null;
    private Call<ApiResult<SuperNodeModel>> callShareService;
    private Call<ApiResult<ShareUrlModel>> callShareUrl;
    private String shareUrl;
    private String metaData;
    private String accountTag;

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
        accountTag = ((String) getArguments().getString(ConstantsType.ACCOUNT_TAG));
        mNodeNameTv.setText(itemData.getNodeName());

        shareUrl = Constants.SHARE_URL;

        initHeadView();
        getNodeDetailData();
        getUrlData();

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


    private void addNodeStateItem(String date, String time, String amount) {
        addNodeStateItem(date, time, amount, "", false, false, false);
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
        }else{
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

    }

    private void getNodeDetailData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nodeId", itemData.getNodeId());
        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
        nodePlanService.getNodeDetail(map).enqueue(new Callback<ApiResult<NodeDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<NodeDetailModel>> call, Response<ApiResult<NodeDetailModel>> response) {
                ApiResult<NodeDetailModel> body = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    NodeDetailModel data = body.getData();
                    if (data != null) {
                        NodeDetailModel.NodeDataBean nodeData = data.getNodeData();
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
                content=timelineBean.getEnContent();
                title=timelineBean.getEnTitle();
            }


            if (i == 0) {
                isTop = true;
            }
            if (i == timeline.size() - 1) {
                isBottom = true;
            }
            if (!TextUtils.isEmpty(type)&&type.equals("1")) {
                isNode = true;

            }
            addNodeStateItem(date, time, content,title, isNode, isTop, isBottom);

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


//        String totalVoteCount1 = nodeData.getTotalVoteCount();
//        int myVote = R.string.my_votes_number;
//        if (!CommonUtil.isSingle(totalVoteCount1)) {
//            myVote = R.string.my_votes_number_s;
//        }
//        addNodeItemData(myVote, totalVoteCount1);

    }

    private void getUrlData() {
        String path = null;
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(itemData.getIdentityType())) {
            path = Constants.VALIDATE_PATH + itemData.getNodeId();
        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(itemData.getIdentityType())) {
            path = Constants.KOL_PATH + itemData.getNodeId();
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.TYPE, "1");
        map.put(Constants.PATH, path);
        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        callShareUrl = nodePlanService.getShareUrl(map);
        callShareUrl.enqueue(new Callback<ApiResult<ShareUrlModel>>() {
            @Override
            public void onResponse(Call<ApiResult<ShareUrlModel>> call, Response<ApiResult<ShareUrlModel>> response) {
                ApiResult<ShareUrlModel> body = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    ShareUrlModel shareUrlModel = body.getData();
                    shareUrl = shareUrlModel.getShortlink();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<ShareUrlModel>> call, Throwable t) {

            }
        });


    }


    private void initNodeInfoUI() {

        final String nodeLogo = itemData.getNodeLogo();
        String source = itemInfo.getIntroduce().trim().toString();
        webView.loadDataWithBaseURL(null, source, "text/html", "utf-8", null);


        Runnable shareRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    nodeLogoBitmap = Glide.with(getContext())
                            .asBitmap()
                            .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(nodeLogo))
                            .centerCrop()
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadManager.getInstance().execute(shareRunnable);
    }


    @SuppressLint("ResourceType")
    private void showShareDialog() {

        mShareImageRl = LayoutInflater.from(getActivity()).inflate(R.layout.view_share_image, null);
        TextView mNodeNameTv = mShareImageRl.findViewById(R.id.nodeNameTv);
        mNodeNameTv.setText(itemInfo.getNodeName());
        QMUIRadiusImageView nodeIconIv = mShareImageRl.findViewById(R.id.headIconIv);
        nodeIconIv.setImageBitmap(nodeLogoBitmap);
        ImageView mQrIv = mShareImageRl.findViewById(R.id.qrIv);
        Bitmap QRBitmap = QRCodeUtil.createQRCodeBitmap(shareUrl, 356, 356);
        mQrIv.setImageBitmap(QRBitmap);

        Bitmap createFromViewBitmap = getViewBitmap(mShareImageRl);


        String fileName = "TEMP_" + System.currentTimeMillis() + ".jpg";
        File PHOTO_DIR = new File(getContext().getCacheDir() + "/image");
        File shareFile = new File(PHOTO_DIR, fileName);

        if (shareFile.exists()) {
            boolean delete = shareFile.delete();
        }
        if (!shareFile.getParentFile().exists()) {
            shareFile.getParentFile().mkdirs();
        }
        try {
            boolean newFile = shareFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(shareFile);
            createFromViewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharePhotoUri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", shareFile);
        } else {
            sharePhotoUri = Uri.fromFile(shareFile);
        }

        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_node_share, null));

        ImageView mShareImageIv = qmuiBottomSheet.findViewById(R.id.shareImageIv);
        mShareImageIv.setImageBitmap(createFromViewBitmap);
        TextView mCancelBtn = qmuiBottomSheet.findViewById(R.id.cancelBtn);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });

        qmuiBottomSheet.findViewById(R.id.shareToQQBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Share2.Builder(getActivity())
                        .setContentType(ShareContentType.IMAGE)
                        .setShareFileUri(sharePhotoUri)
                        .setShareToComponent("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
                        .setTitle("Share Image")
                        .build()
                        .shareBySystem();
            }
        });

        qmuiBottomSheet.findViewById(R.id.shareToWeixinBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Share2.Builder(getActivity())
                        .setContentType(ShareContentType.IMAGE)
                        .setShareFileUri(sharePhotoUri)
                        .setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
                        .setTitle("Share Image")
                        .build()
                        .shareBySystem();
            }
        });
        qmuiBottomSheet.findViewById(R.id.reloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
                final QMUIDialog xiaobuCommandDialog = new QMUIDialog(getContext());
                xiaobuCommandDialog.setCanceledOnTouchOutside(true);
                xiaobuCommandDialog.setContentView(R.layout.view_share_xiaobu_command);
                final TextView mXiaobuCommandContentTv = xiaobuCommandDialog.findViewById(R.id.xiaobuCommandContentTv);
                mXiaobuCommandContentTv.setText(Html.fromHtml(String.format(getString(R.string.xiaobu_command_content_txt), itemInfo.getNodeName(), shareUrl)));

                QMUIRoundButton copyCommandBtn = xiaobuCommandDialog.findViewById(R.id.reloadBtn);
                copyCommandBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String xiaobuCommandStr = mXiaobuCommandContentTv.getText().toString();
                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", xiaobuCommandStr);
                        cm.setPrimaryClip(mClipData);
                        final QMUITipDialog copySuccessDiglog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                .setTipWord(getString(R.string.qr_copy_success_message))
                                .create();
                        copySuccessDiglog.show();
                        getView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                copySuccessDiglog.dismiss();
                            }
                        }, 1500);
                    }
                });
                xiaobuCommandDialog.show();
            }
        });
        qmuiBottomSheet.show();


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

//                if (itemInfo == null) {
//                    return;
//                }
//                String shareStartTime = itemInfo.getShareStartTime();
//
//                if (TextUtils.isEmpty(shareStartTime)) {
//                    return;
//                }
//                if (TimeUtil.judgeTime(Long.parseLong(shareStartTime))) {
//                    DialogUtils.showMessageNoTitleDialog(getContext(), R.string.share_close);
//                } else {
//                    showShareDialog();
//                }
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


    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }


    @Override
    public void onDestroyView() {

//        callShareService.cancel();
//        callShareUrl.cancel();
        super.onDestroyView();
//        webView.destroy();
    }


    private void GoVoteRecord(SuperNodeModel superNodeModel) {
        BPMyNodeVoteRecordFragment fragment = new BPMyNodeVoteRecordFragment();
        Bundle args1 = new Bundle();
        args1.putSerializable("itemNodeInfo", superNodeModel);
        fragment.setArguments(args1);
        startFragment(fragment);
    }

}
