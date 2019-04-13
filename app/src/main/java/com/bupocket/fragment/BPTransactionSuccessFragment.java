package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.qmuiteam.qmui.arch.QMUIFragment;

import butterknife.ButterKnife;

public class BPTransactionSuccessFragment extends QMUIFragment {


    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_transaction_success, null);
        ButterKnife.bind(view);
        return view;
    }
}
