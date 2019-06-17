package com.bupocket.base;

import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class AbsBaseFragment extends BaseFragment {


    private Unbinder bind;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(getLayoutView(), null);
        bind = ButterKnife.bind(this, root);
        initView();
        initData();
        setListeners();
        return root;
    }

    protected abstract int getLayoutView();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void setListeners();


    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
