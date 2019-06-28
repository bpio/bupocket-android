package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.TO;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPCollectionFragment extends AbsBaseFragment {


    @BindView(R.id.backIv)
    ImageView backIv;
    @BindView(R.id.currentWalletNameTv)
    TextView currentWalletNameTv;
    @BindView(R.id.shareQrCodeIv)
    ImageView shareQrCodeIv;
    @BindView(R.id.walletNameTv)
    TextView walletNameTv;
    @BindView(R.id.walletAddressTv)
    TextView walletAddressTv;
    @BindView(R.id.copyWalletAddressIv)
    ImageView copyWalletAddressIv;
    @BindView(R.id.walletIconRIV)
    RoundedImageView walletIconRIV;
    @BindView(R.id.addressQrCodeIv)
    ImageView addressQrCodeIv;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;


    Unbinder unbinder;
    private Bitmap mBitmap;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_collection;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initData() {
        String currentWalletAddress = getWalletAddress();
        walletAddressTv.setText(currentWalletAddress);
        mBitmap = QRCodeUtil.createQRCodeBitmap(currentWalletAddress, TO.dip2px(mContext, 170), TO.dip2px(mContext, 170));
        addressQrCodeIv.setImageBitmap(mBitmap);
        String walletName = (String) spHelper.getSharedPreference(currentWalletAddress + "-walletName", "");
        walletNameTv.setText(walletName);
        CommonUtil.setHeadIvRes(currentWalletAddress, walletIconRIV, spHelper);
    }

    @Override
    protected void setListeners() {

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        shareQrCodeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQrCode();
            }
        });

        copyWalletAddressIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyAddress();
            }
        });

    }

    private void copyAddress() {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", getWalletAddress());
        cm.setPrimaryClip(mClipData);
        final QMUITipDialog copySuccessDiglog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(copySuccessMessage)
                .create();
        copySuccessDiglog.show();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                copySuccessDiglog.dismiss();
            }
        }, 1500);
    }

    private void shareQrCode() {
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), mBitmap, null, null));
        shareImg("收款码", "收款码", "收款码", uri);
    }


    private void shareImg(String dlgTitle, String subject, String content,
                          Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (content != null && !"".equals(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }

        // 设置弹出框标题
        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }

}
