package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        if (itemData==null) {
            return;
        }
        holder.setText(R.id.tvBuildName,itemData.getTitle());


        holder.setText(R.id.tvBuildRewardRate,itemData.getRewardRate()+"%");

        ProgressBar pbBuild = holder.getView(R.id.pbBuild);
        pbBuild.setMax(itemData.getCopies());
        pbBuild.setProgress(itemData.getCopies()-itemData.getLeftCopies());
        String amount = itemData.getPerAmount();
        if (!TextUtils.isEmpty(amount)) {
            holder.setText(R.id.tvBuildNum,amount+" BU/份");
        }

        holder.setText(R.id.tvRemainNum,itemData.getTotalAmount()+"BU");
        holder.setText(R.id.tvRemainCopy,itemData.getLeftCopies()+"");



    }
}
