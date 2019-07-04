package com.bupocket.voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.interfaces.InitViewListener;
import com.bupocket.voucher.adapter.BPAssetIssuerAdapter;
import com.bupocket.voucher.model.VoucherIssuerBean;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPAssetIssuerFragment extends AbsBaseFragment implements InitViewListener {


    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.dPartyIconRiv)
    RoundedImageView dPartyIconRiv;
    @BindView(R.id.dPartyNameRiv)
    TextView dPartyNameRiv;
    @BindView(R.id.dPartyNickRiv)
    TextView dPartyNickRiv;
    @BindView(R.id.dPartyIntroduceTv)
    TextView dPartyIntroduceTv;
    @BindView(R.id.assetIssuerLv)
    ListView assetIssuerLv;


    Unbinder unbinder;
    private VoucherIssuerBean voucherIssuer;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_asset_issuer;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
    }

    @Override
    protected void initData() {
        if (getArguments()!=null) {
            voucherIssuer = ((VoucherIssuerBean) getArguments().getSerializable("voucherIssuer"));

            Glide.with(mContext)
                    .load(voucherIssuer.getIcon())
                    .into(dPartyIconRiv);

            dPartyNameRiv.setText(voucherIssuer.getName());
        }
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void initTopBar() {
        topbar.setTitle(R.string.asset_issuer_title);
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    @Override
    public void initListView() {
        assetIssuerLv.setAdapter(new BPAssetIssuerAdapter(mContext));
    }


}
