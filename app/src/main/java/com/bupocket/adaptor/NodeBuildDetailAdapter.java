package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.NodeBuildDetailModel;
import com.bupocket.model.NodeBuildSupportModel;
import com.bupocket.utils.AddressUtil;

public class NodeBuildDetailAdapter extends AbsViewHolderAdapter<NodeBuildSupportModel> {


    public NodeBuildDetailAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.node_build_detail_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, NodeBuildSupportModel itemData) {

        ImageView ivHead = holder.getView(R.id.ivBuildSupportHead);
        holder.setText(R.id.tvBuildSupportAmount,itemData.getAmount()+"BU");
        holder.setText(R.id.tvBuildSupportDate,itemData.getCreateTime());
        holder.setText(R.id.tvBuildDetailAddress, AddressUtil.anonymous(itemData.getInitiatorAddress()));

    }
}
