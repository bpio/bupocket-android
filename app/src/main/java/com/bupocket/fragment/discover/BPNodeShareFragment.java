package com.bupocket.fragment.discover;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.ShareUrlModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUitls;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.TimeUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPNodeShareFragment extends AbsBaseFragment {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.nodeNameTv)
    TextView mNodeNameTv;
    @BindView(R.id.nodeTypeTv)
    TextView mNodeTypeTv;
    @BindView(R.id.haveVotesNumTv)
    TextView mHaveVotesNumTv;
    @BindView(R.id.supportPeopleTv)
    TextView mSupportPeopleTv;
    @BindView(R.id.headIconIv)
    QMUIRadiusImageView mNodeIconIv;
    @BindView(R.id.wbShare)
    WebView webView;
    @BindView(R.id.haveVotesNumTvHint)
    TextView haveVotesNumTvHint;
    @BindView(R.id.supportPeopleTvHint)
    TextView supportPeopleTvHint;



    private View mShareImageRl;
    private Uri sharePhotoUri = null;
    private SuperNodeModel itemInfo;
    private SuperNodeModel itemData;
    private Bitmap nodeLogoBitmap = null;
    private Call<ApiResult<SuperNodeModel>> callShareService;
    private Call<ApiResult<ShareUrlModel>> callShareUrl;
    private String shareUrl;


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
        itemData = getArguments().getParcelable("itemInfo");
        mNodeNameTv.setText(itemData.getNodeName());

        shareUrl = Constants.SHARE_URL;

        initHeadView();
        getShareData();
        getUrlData();
    }

    private void initHeadView() {
        // set node type
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(itemData.getIdentityType())) {
            mNodeTypeTv.setText(getContext().getResources().getString(R.string.common_node));

        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(itemData.getIdentityType())) {
            mNodeTypeTv.setText(getContext().getResources().getString(R.string.ecological_node));
        }


        if (CommonUtil.isSingle(itemData.getNodeVote())) {
            haveVotesNumTvHint.setText(mContext.getString(R.string.number_have_votes));
        } else {
            haveVotesNumTvHint.setText(mContext.getString(R.string.number_have_votes_s));
        }
        mHaveVotesNumTv.setText(itemData.getNodeVote());


        if (CommonUtil.isSingle(itemData.getMyVoteCount())) {
            supportPeopleTvHint.setText(mContext.getString(R.string.my_votes_number));
        } else {
            supportPeopleTvHint.setText(mContext.getString(R.string.my_votes_number_s));
        }
        mSupportPeopleTv.setText(itemData.getMyVoteCount());


        Glide.with(getContext())
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(itemData.getNodeLogo()))
                .into(mNodeIconIv);
    }

    @Override
    protected void setListeners() {

    }

    private void getShareData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nodeId", itemData.getNodeId());
        map.put("address", getWalletAddress());
        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        callShareService = nodePlanService.getShareData(map);
        callShareService.enqueue(new Callback<ApiResult<SuperNodeModel>>() {
            @Override
            public void onResponse(Call<ApiResult<SuperNodeModel>> call, Response<ApiResult<SuperNodeModel>> response) {
                ApiResult<SuperNodeModel> body = response.body();
                if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    itemInfo = body.getData();
                    initNodeInfoUI();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<SuperNodeModel>> call, Throwable t) {

            }
        });


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



        new Thread(new Runnable() {
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
        }).start();
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
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.invite_share_txt));
        mTopBar.addRightImageButton(R.mipmap.icon_share_green, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemInfo==null) {
                    return;
                }
                String shareStartTime = itemInfo.getShareStartTime();

                if (TextUtils.isEmpty(shareStartTime)) {
                    return;
                }
                if (TimeUtil.judgeTime(Long.parseLong(shareStartTime))) {
                    DialogUitls.showMessageNoTitleDialog(getContext(), R.string.share_close);
                } else {
                    showShareDialog();
                }
            }
        });
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

        callShareService.cancel();
        callShareUrl.cancel();
        super.onDestroyView();
        webView.destroy();
    }
}
