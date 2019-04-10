package com.bupocket.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public abstract class BaseFragment extends QMUIFragment {

    private final static String BP_FILE_NAME = "buPocket";
    public  SharedPreferencesHelper spHelper;
    public  Context mContext;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        if (spHelper==null) {
            spHelper = new SharedPreferencesHelper(getContext(), BP_FILE_NAME);
        }

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
