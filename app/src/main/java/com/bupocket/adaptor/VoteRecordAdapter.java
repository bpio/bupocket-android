package com.bupocket.adaptor;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.MyVoteInfoModel;

public class VoteRecordAdapter extends AbsViewHolderAdapter<MyVoteInfoModel> {


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


        if (!TextUtils.isEmpty(itemData.getType())) {
            switch (itemData.getType()) {
                case "1":
                    recordType.setText("投");
                    recordType.setBackgroundResource(R.drawable.shape_corner_green);
                    break;
                case "2":
                    recordType.setText("撤");
                    recordType.setBackgroundResource(R.drawable.shape_corner_red);
                    break;
            }
        }



        holder.setText(R.id.voterName, itemData.getNodeName());

        holder.setText(R.id.nodeTypeTv, "");

        holder.setText(R.id.amountTv, itemData.getAmount());
        holder.setText(R.id.statusTv, itemData.getStatus());
        holder.setText(R.id.timeTv, itemData.getDate());


    }
}
