package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsBaseAdapter;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.NodeSettingModel;

import java.util.zip.Inflater;

public class NodeSettingAdapter extends AbsViewHolderAdapter<NodeSettingModel> {


    public NodeSettingAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_node_setting_item;
    }


    @Override
    protected void convert(BaseViewHolder holder, NodeSettingModel itemData) {
        View ivNodeSetSelected = holder.getView(R.id.ivNodeSetSelected);
        View ivAddMore = holder.getView(R.id.ivAddMore);
        TextView tvNodeInfo = holder.getView(R.id.tvNodeInfo);

        if (itemData.isSelected()) ivNodeSetSelected.setVisibility(View.VISIBLE);
        else ivNodeSetSelected.setVisibility(View.INVISIBLE);

        if (itemData.isMore()) ivAddMore.setVisibility(View.VISIBLE);
        else ivAddMore.setVisibility(View.INVISIBLE);
        tvNodeInfo.setText(itemData.getUrl());
    }
}
