package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.enums.NodePlanMoreEnum;

public class MoreBtnAdapter  extends AbsViewHolderAdapter<String> {

    public MoreBtnAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void convert(BaseViewHolder holder, String itemData) {


        if (itemData.equals(NodePlanMoreEnum.CANCLE_VOTE.getName())) {
            holder.setImageResource(R.id.iv_plan_more,NodePlanMoreEnum.CANCLE_VOTE.getCode());
            holder.setText(R.id.tv_plan_more,NodePlanMoreEnum.CANCLE_VOTE.getName());
        }else if (itemData.equals(NodePlanMoreEnum.VOTING_RECORD.getName())){
            holder.setImageResource(R.id.iv_plan_more,NodePlanMoreEnum.VOTING_RECORD.getCode());
            holder.setText(R.id.tv_plan_more,NodePlanMoreEnum.VOTING_RECORD.getName());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.node_plan_more_item;
    }

}
