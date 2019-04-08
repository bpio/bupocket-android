package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.NodeBuildDetailModel;

public class NodeBuildDetailAdapter extends AbsViewHolderAdapter<NodeBuildDetailModel> {


    public NodeBuildDetailAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.node_build_detail_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, NodeBuildDetailModel itemData) {

    }
}
