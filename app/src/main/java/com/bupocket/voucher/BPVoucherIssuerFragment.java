package com.bupocket.voucher;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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

public class BPVoucherIssuerFragment extends AbsBaseFragment implements InitViewListener {


    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.dPartyIconRiv)
    RoundedImageView dPartyIconRiv;
    @BindView(R.id.dPartyNameRiv)
    TextView dPartyNameRiv;
    @BindView(R.id.dPartyNickRiv)
    TextView issuerNickRiv;
    @BindView(R.id.dPartyIntroduceTv)
    TextView issuerIntroduceTv;
    @BindView(R.id.assetIssuerLv)
    ListView assetIssuerLv;
    @BindView(R.id.recordEmptyLL)
    LinearLayout recordEmptyLL;
    @BindView(R.id.emptyInfoTv)
    TextView emptyInfoTv;


    private VoucherIssuerBean voucherIssuer;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_asset_issuer;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
        emptyInfoTv.setText(R.string.empty_introduce);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            voucherIssuer = ((VoucherIssuerBean) getArguments().getSerializable("voucherIssuer"));

            String icon = voucherIssuer.getIcon();
            if (!TextUtils.isEmpty(icon)) {
                Glide.with(mContext)
                        .load(icon)
                        .into(dPartyIconRiv);
            }

            String name = voucherIssuer.getName();
            if (!TextUtils.isEmpty(name)) {
                dPartyNameRiv.setText(name);
            }
            String shortName = voucherIssuer.getShortName();

            if (!TextUtils.isEmpty(shortName)) {
                issuerNickRiv.setText(getString(R.string.short_name_hint) + shortName);
            }

            String intro = voucherIssuer.getIntro();
            if (!TextUtils.isEmpty(intro)) {
                issuerIntroduceTv.setText(intro);
                issuerIntroduceTv.setVisibility(View.VISIBLE);
            } else {
                issuerIntroduceTv.setVisibility(View.GONE);
                recordEmptyLL.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void initTopBar() {
        topbar.setTitle(R.string.asset_issuer);
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
