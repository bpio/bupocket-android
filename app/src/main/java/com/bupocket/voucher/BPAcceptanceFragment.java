package com.bupocket.voucher;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.interfaces.InitViewListener;
import com.bupocket.voucher.model.VoucherAcceptanceBean2;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.Unbinder;

public class BPAcceptanceFragment extends AbsBaseFragment implements InitViewListener {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.dPartyIconRiv)
    RoundedImageView dPartyIconRiv;
    @BindView(R.id.dPartyNameRiv)
    TextView dPartyNameRiv;
    @BindView(R.id.dPartyNickRiv)
    TextView acceptanceShortNameTv;
    @BindView(R.id.dPartyIntroduceTv)
    TextView acceptanceIntroduceTv;
    @BindView(R.id.deliveryInstructionsTv)
    TextView deliveryInstructionsTv;
    @BindView(R.id.recordEmptyLL)
    LinearLayout recordEmptyLL;
    @BindView(R.id.emptyInfoTv)
    TextView emptyInfoTv;
    @BindView(R.id.acceptanceInfoLL)
    LinearLayout acceptanceInfoLL;

    Unbinder unbinder;
    private VoucherAcceptanceBean2 voucherAcceptance;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_acceptance;
    }

    @Override
    protected void initView() {
        initTopBar();
        emptyInfoTv.setText(R.string.empty_introduce);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            voucherAcceptance = (VoucherAcceptanceBean2) getArguments().getSerializable("voucherAcceptance");
            if (voucherAcceptance != null) {

                String icon = voucherAcceptance.getIcon();
                if (!TextUtils.isEmpty(icon)) {
                    Glide.with(mContext)
                            .load(icon)
                            .into(dPartyIconRiv);
                }

                dPartyNameRiv.setText(voucherAcceptance.getName());
                String intro = voucherAcceptance.getIntro();
                String shortName = voucherAcceptance.getShortName();
                if (!TextUtils.isEmpty(shortName)) {
                    acceptanceShortNameTv.setText(getString(R.string.short_name_hint) + shortName);
                }

                if (!TextUtils.isEmpty(intro)) {
                    acceptanceIntroduceTv.setText(intro);
                    acceptanceInfoLL.setVisibility(View.VISIBLE);
                } else {
                    recordEmptyLL.setVisibility(View.VISIBLE);
                    acceptanceInfoLL.setVisibility(View.GONE);
                }

            }
        }
    }

    @Override
    protected void setListeners() {

    }


    @Override
    public void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.d_party);

    }

    @Override
    public void initListView() {

    }
}
