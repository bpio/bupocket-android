package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.NodeBuildModel;

public class NodeBuildAdapter extends AbsViewHolderAdapter<NodeBuildModel> {


    public NodeBuildAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.node_build_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, NodeBuildModel itemData) {

    }
}
