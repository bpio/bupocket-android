package com.bupocket.fragment.discover;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.ShareUrlModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.ToastUtil;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPNodeShareFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.nodeNameTv)
    TextView mNodeNameTv;
    @BindView(R.id.nodeTypeTv)
    TextView mNodeTypeTv;
    @BindView(R.id.haveVotesNumTv)
    TextView mHaveVotesNumTv;
    @BindView(R.id.shareBtn)
    LinearLayout mShareBtn;
    @BindView(R.id.supportPeopleTv)
    TextView mSupportPeopleTv;
    @BindView(R.id.nodeIconIv)
    QMUIRadiusImageView mNodeIconIv;
    @BindView(R.id.wbShare)
    WebView webView;
    private View mShareImageRl;
    private Uri sharePhotoUri = null;

    private String shareUrl = "https://bumo.io/technology";

    private static final String VALIDATE_PATH = "dpos/detail/validate/";
    private static final String KOL_PATH = "dpos/detail/kol/";

    private SuperNodeModel itemInfo;
    private SuperNodeModel itemData;
    private Bitmap nodeLogoBitmap = null;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_share, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initUI();
        initData();
        setListener();
    }

    private void initData() {
        itemData = getArguments().getParcelable("itemInfo");
        getShareData();
        getUrlData();
    }

    private void getShareData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nodeId", itemData.getNodeId());
        map.put("address", getWalletAddress());
        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        nodePlanService.getShareData(map).enqueue(new Callback<ApiResult<SuperNodeModel>>() {
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
                ToastUtil.showToast(getActivity(), t.getMessage().toString(), Toast.LENGTH_SHORT);
            }
        });


    }

    private void getUrlData() {
        String path = null;
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(itemData.getIdentityType())) {
            path = VALIDATE_PATH + itemData.getNodeId();
        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(itemData.getIdentityType())) {
            path = KOL_PATH + itemData.getNodeId();
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.TYPE, "1");
        map.put(Constants.PATH, path);
        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        nodePlanService.getShareUrl(map).enqueue(new Callback<ApiResult<ShareUrlModel>>() {
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
                ToastUtil.showToast(getActivity(), t.getMessage().toString(), Toast.LENGTH_SHORT);
            }
        });


    }

    private void initUI() {
        initTopBar();
    }

    private void initNodeInfoUI() {
        mNodeNameTv.setText(itemInfo.getNodeName());
        // set node type
        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(itemInfo.getIdentityType())) {
            mNodeTypeTv.setText(getContext().getResources().getString(R.string.common_node));

        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(itemInfo.getIdentityType())) {
            mNodeTypeTv.setText(getContext().getResources().getString(R.string.ecological_node));
        }
        mHaveVotesNumTv.setText(itemInfo.getNodeVote());
        mSupportPeopleTv.setText(String.format(getString(R.string.support_people_num_txt), itemInfo.getSupport()));
        String source = itemInfo.getIntroduce().trim().toString();
        webView.loadDataWithBaseURL(null, source, "text/html", "utf-8", null);

        final String nodeLogo = itemInfo.getNodeLogo();
        Glide.with(getContext())
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(nodeLogo))
                .into(mNodeIconIv);
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
        mNodeIconIv.setBackgroundColor(getContext().getResources().getColor(R.color.app_color_white));
    }

    private void setListener() {
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (true){
                    CommonUtil.showMessageDialog(getContext(), R.string.share_close);
                }else{
                    showShareDialog();
                }

            }
        });
    }

    @SuppressLint("ResourceType")
    private void showShareDialog() {

        mShareImageRl = LayoutInflater.from(getActivity()).inflate(R.layout.view_share_image, null);
        TextView mNodeNameTv = mShareImageRl.findViewById(R.id.nodeNameTv);
        mNodeNameTv.setText(itemInfo.getNodeName());
        QMUIRadiusImageView nodeIconIv = mShareImageRl.findViewById(R.id.nodeIconIv);
        nodeIconIv.setImageBitmap(nodeLogoBitmap);
        nodeIconIv.setBackgroundColor(getContext().getResources().getColor(R.color.app_color_white));
        ImageView mQrIv = mShareImageRl.findViewById(R.id.qrIv);
        Bitmap QRBitmap = QRCodeUtil.createQRCodeBitmap(shareUrl, 356, 356);
        mQrIv.setImageBitmap(QRBitmap);
//        String nodeLogo = itemInfo.getNodeLogo();
//        Glide.with(getContext())
//                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(nodeLogo))
//                .into(nodeIconIv);

        Bitmap createFromViewBitmap = getViewBitmap(mShareImageRl);
//        final Bitmap createFromViewBitmap = QMUIDrawableHelper.createBitmapFromView(mShareImageRl,1);


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
//                                .setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
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
//                                .setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
                        .setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
                        .setTitle("Share Image")
                        .build()
                        .shareBySystem();
            }
        });
        qmuiBottomSheet.findViewById(R.id.copyCommandBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
                final QMUIDialog xiaobuCommandDialog = new QMUIDialog(getContext());
                xiaobuCommandDialog.setCanceledOnTouchOutside(true);
                xiaobuCommandDialog.setContentView(R.layout.view_share_xiaobu_command);
                final TextView mXiaobuCommandContentTv = xiaobuCommandDialog.findViewById(R.id.xiaobuCommandContentTv);
                mXiaobuCommandContentTv.setText(Html.fromHtml(String.format(getString(R.string.xiaobu_command_content_txt), itemInfo.getNodeName(), shareUrl)));

                QMUIRoundButton copyCommandBtn = xiaobuCommandDialog.findViewById(R.id.copyCommandBtn);
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

}
