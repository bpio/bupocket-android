package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.enums.NodeStatusEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.enums.VoteNodeEnum;
import com.bupocket.model.MyVoteInfoModel;

public class VoteRecordAdapter extends AbsViewHolderAdapter<MyVoteInfoModel> {


    private int adapterType;

    public final static int SOME_RECORD=1;

    public void setAdapterType(int adapterType) {
        this.adapterType = adapterType;
    }

    public VoteRecordAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_node_vote_record_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, MyVoteInfoModel itemData) {
        TextView recordType = (TextView) holder.getView(R.id.recordTypeTv);

        if (itemData==null) {
            return;
        }

        String type = itemData.getType();
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(VoteNodeEnum.VOTE.getCode())) {
                recordType.setText(VoteNodeEnum.VOTE.getName1());
                recordType.setBackgroundResource(VoteNodeEnum.VOTE.getDrawableRes());
//                holder.setText(R.id.voterName,VoteNodeEnum.VOTE.getName2());
            }else if (type.equals(VoteNodeEnum.CANCLE.getCode())){
                recordType.setText(VoteNodeEnum.CANCLE.getName1());
                recordType.setBackgroundResource(VoteNodeEnum.CANCLE.getDrawableRes());
//                holder.setText(R.id.voterName,VoteNodeEnum.CANCLE.getName2());

            }
        }


        if (adapterType==SOME_RECORD) {

            holder.getView(R.id.nodeTypeTv).setVisibility(View.INVISIBLE);
            holder.getView(R.id.voterName).setVisibility(View.INVISIBLE);
        }else{
            holder.setText(R.id.voterName, itemData.getNodeName());

            String identityType = itemData.getIdentityType();
            if (!TextUtils.isEmpty(identityType)) {
                if (identityType.equals(SuperNodeTypeEnum.VALIDATOR.getCode())) {
                    holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.common_node));
                }else if (identityType.equals(SuperNodeTypeEnum.ECOLOGICAL.getCode())){
                    holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.ecological_node));
                }
            }
        }




        holder.setText(R.id.amountTv, itemData.getAmount());
        TextView tvStatus = holder.getView(R.id.statusTv);
        String status = itemData.getStatus();
        if (!TextUtils.isEmpty(status)) {

            if (status.equals(NodeStatusEnum.SUCCESS.getCode())) {
                tvStatus.setText(NodeStatusEnum.SUCCESS.getName());
                tvStatus.setTextColor(context.getResources().getColor(NodeStatusEnum.SUCCESS.getColor()));
            }else if (status.equals(NodeStatusEnum.FAILURE.getCode())){
                tvStatus.setText(NodeStatusEnum.FAILURE.getName());
                tvStatus.setTextColor(context.getResources().getColor(NodeStatusEnum.FAILURE.getColor()));
            }else if (status.equals(NodeStatusEnum.RUNING.getCode())){
                tvStatus.setText(NodeStatusEnum.RUNING.getName());
                tvStatus.setTextColor(context.getResources().getColor(NodeStatusEnum.RUNING.getColor()));
            }
        }

        holder.setText(R.id.timeTv, itemData.getDate());


    }
}
