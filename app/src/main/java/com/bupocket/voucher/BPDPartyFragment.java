package com.bupocket.voucher;

import android.view.View;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.interfaces.InitViewListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.Unbinder;

public class BPDPartyFragment extends AbsBaseFragment implements InitViewListener {


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
    Unbinder unbinder;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_d_party;
    }

    @Override
    protected void initView() {
        initTopBar();
    }

    @Override
    protected void initData() {

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
