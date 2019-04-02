package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bupocket.R;
import com.bupocket.base.AbsBaseAdapter;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.SuperNodeModel;

import java.util.List;

public class SuperNodeAdapter extends AbsViewHolderAdapter<SuperNodeModel> {


    public SuperNodeAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_node_plan_list_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, SuperNodeModel itemData) {

        if (itemData==null) {
            return;
        }
        holder.setText(R.id.nodeNameTv,itemData.getNodeName());
        String identityType = itemData.getIdentityType();
        //节点身份（1 共识节点 ，2 生态节点）
        switch (identityType) {
            case "1":
                holder.setText(R.id.nodeTypeTv,context.getResources().getString(R.string.common_node));
                break;
            case "2":
                holder.setText(R.id.nodeTypeTv,context.getResources().getString(R.string.ecological_node));
                break;
        }

        holder.setText(R.id.haveVotesNumTv,itemData.getNodeVote());
        holder.setText(R.id.myVotesNumTv,itemData.getMyVoteCount());



//        holder.setText(R.id.nodeIconBgIv,itemData.getNodeName());


    }
}
