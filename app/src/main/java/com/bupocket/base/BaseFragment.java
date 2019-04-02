package com.bupocket.base;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public abstract class BaseFragment extends QMUIFragment {
    public BaseFragment() {


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
