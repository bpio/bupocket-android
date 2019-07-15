package com.bupocket.voucher;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.interfaces.InitViewListener;
import com.bupocket.voucher.model.VoucherAcceptanceBean;
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
    TextView dPartyNickRiv;
    @BindView(R.id.dPartyIntroduceTv)
    TextView dPartyIntroduceTv;
    @BindView(R.id.deliveryInstructionsTv)
    TextView deliveryInstructionsTv;
    @BindView(R.id.recordEmptyLL)
    LinearLayout recordEmptyLL;
    @BindView(R.id.emptyInfoTv)
    TextView emptyInfoTv;

    Unbinder unbinder;
    private VoucherAcceptanceBean voucherAcceptance;

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
            voucherAcceptance = (VoucherAcceptanceBean) getArguments().getSerializable("voucherAcceptance");
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
                dPartyNickRiv.setText(shortName);
                if (!TextUtils.isEmpty(intro)) {
                    dPartyIntroduceTv.setText(intro);
                    dPartyIntroduceTv.setVisibility(View.VISIBLE);

                }else{
                    dPartyIntroduceTv.setVisibility(View.GONE);
                    recordEmptyLL.setVisibility(View.VISIBLE);
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
