package com.bupocket.fragment.discover;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

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
    @BindView(R.id.nodeIntroduceTv)
    TextView mNodeIntroduceTv;
    @BindView(R.id.nodeIconIv)
    QMUIRadiusImageView mNodeIconIv;

    private View mShareImageRl;
    private Uri sharePhotoUri = null;

    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 121;
    private SuperNodeModel itemInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
            } else {
                Toast.makeText(getContext(), getString(R.string.write_external_storage_no_permissions_txt), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), getString(R.string.write_external_storage_no_permissions_txt), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_share, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void initData() {
        itemInfo = getArguments().getParcelable("itemInfo");


    }

    private void initUI() {
        initNodeInfoUI();
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
        mSupportPeopleTv.setText(String.format(getString(R.string.support_people_num_txt),itemInfo.getSupport()));
        mNodeIntroduceTv.setText(itemInfo.getIntroduce());
        String nodeLogo = itemInfo.getNodeLogo();
        if (!TextUtils.isEmpty(nodeLogo)) {
            mNodeIconIv.setImageBitmap(CommonUtil.base64ToBitmap(nodeLogo));
            mNodeIconIv.setBackgroundColor(getContext().getResources().getColor(R.color.app_color_white));
        }
    }

    private void setListener() {
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void showShareDialog() {

        mShareImageRl = LayoutInflater.from(getActivity()).inflate(R.layout.view_share_image, null);
        TextView mNodeNameTv = mShareImageRl.findViewById(R.id.nodeNameTv);
        mNodeNameTv.setText(itemInfo.getNodeName());
        QMUIRadiusImageView nodeIconIv = mShareImageRl.findViewById(R.id.nodeIconIv);
        String nodeLogo = itemInfo.getNodeLogo();
        if (!TextUtils.isEmpty(nodeLogo)) {
            nodeIconIv.setImageBitmap(CommonUtil.base64ToBitmap(nodeLogo));
            nodeIconIv.setBackgroundColor(getContext().getResources().getColor(R.color.app_color_white));
        }
        Bitmap createFromViewBitmap = getViewBitmap(mShareImageRl);
//        final Bitmap createFromViewBitmap = QMUIDrawableHelper.createBitmapFromView(mShareImageRl,1);


        String fileName = "TEMP_" + System.currentTimeMillis() + ".jpg";
        File PHOTO_DIR = new File(getContext().getCacheDir()+"/image");
        File shareFile = new File(PHOTO_DIR, fileName);

        if(shareFile.exists()){
            boolean delete = shareFile.delete();
        }
        if(!shareFile.getParentFile().exists()) {
            shareFile.getParentFile().mkdirs();
        }
        try{
            boolean newFile = shareFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(shareFile);
            createFromViewBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharePhotoUri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", shareFile);
        } else {
            sharePhotoUri = Uri.fromFile(shareFile);
        }

        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_node_share,null));

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
                        .setShareToComponent("com.tencent.mobileqq","com.tencent.mobileqq.activity.JumpActivity")
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
                        .setShareToComponent("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI")
                        .setTitle("Share Image")
                        .build()
                        .shareBySystem();
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
