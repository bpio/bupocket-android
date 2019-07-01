package com.bupocket.fragment.voucher;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.BPCollectionFragment;
import com.bupocket.fragment.BPWalletManageFragment;
import com.bupocket.fragment.BPWalletsHomeFragment;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.TimeUtil;
import com.bupocket.utils.WalletLocalInfoUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPVoucherHomeFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.voucher_list_Lv)
    ListView voucherListLv;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_home_fragment;
    }

    @Override
    protected void initView() {
        mTopBar.addLeftImageButton(R.mipmap.icon_voucher_qrcode, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPCollectionFragment());
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.invite_share_txt));
        mTopBar.addRightImageButton(R.mipmap.icon_voucher_green, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                BPWalletsHomeFragment fragment = new BPWalletsHomeFragment();
                Bundle args = new Bundle();
                args.putString(ConstantsType.FRAGMENT_TAG,BPVoucherHomeFragment.class.getSimpleName());
                fragment.setArguments(args);
                startFragment(fragment);
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }


    @Override
    public void onResume() {
        super.onResume();
        mTopBar.setTitle(WalletLocalInfoUtil.getInstance(spHelper).getWalletName());
    }
}
