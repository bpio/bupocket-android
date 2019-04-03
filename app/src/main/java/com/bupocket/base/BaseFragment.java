package com.bupocket.base;

import android.content.Context;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public abstract class BaseFragment extends QMUIFragment {


    public  Context mContext;

    public BaseFragment() {
        mContext = this.getContext();
    }

    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }


    @Override
    public void onResume() {
        super.onResume();

//        BPUpgradeManager.getInstance(getContext()).runUpgradeTipTaskIfExist(getActivity());
    }

}
