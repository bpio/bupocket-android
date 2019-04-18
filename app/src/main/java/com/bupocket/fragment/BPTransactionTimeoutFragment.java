package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.AddressUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BPTransactionTimeoutFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    Unbinder unbinder;
    @BindView(R.id.ivTimeoutCopy)
    ImageView ivTimeoutCopy;
    @BindView(R.id.tvTimeoutHash)
    TextView tvTimeoutHash;
    @BindView(R.id.tvTransactionInfo)
    TextView tvTransactionInfo;


    private String txHash;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_transaction_timeout, null);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {

        txHash = getArguments().getString("txHash", "");
        tvTimeoutHash.setText(AddressUtil.anonymous(txHash,8));
        tvTransactionInfo.setText(Html.fromHtml(getString(R.string.transaction_timeout_info)));
        initTopBar();
    }

    private void initTopBar() {

        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.transaction_timeout_title);
    }


    @OnClick(R.id.ivTimeoutCopy)
    public void onViewClicked() {

        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", txHash);
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
        }, 1000);
    }

}
