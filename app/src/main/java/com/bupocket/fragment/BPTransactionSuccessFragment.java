package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.bupocket.fragment.home.HomeFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPTransactionSuccessFragment extends QMUIFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_transaction_success, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        return root;
    }

    private void initData() {

    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAndDestroyCurrent(new HomeFragment());
            }
        });
        mTopBar.setTitle(R.string.transaction_success_title);
    }

}
