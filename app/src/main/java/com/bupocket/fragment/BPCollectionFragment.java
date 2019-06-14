package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPCollectionFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_collection;
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

    private void initTopBar() {
        topbar.setTitle(R.string.collection_title);
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }


}
