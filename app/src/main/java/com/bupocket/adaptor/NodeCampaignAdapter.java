package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.common.Constants;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;


public class NodeCampaignAdapter extends AbsViewHolderAdapter<SuperNodeModel> {


    public NodeCampaignAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_super_node_item;
    }

    @Override
    protected void convert(final BaseViewHolder holder, SuperNodeModel itemData) {

        if (itemData == null) {
            return;
        }

        String nodeLogo = itemData.getNodeLogo();
        ImageView nodeIconIv = holder.getView(R.id.headIconIv);
        Glide.with(context)
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(nodeLogo))
                .into(nodeIconIv);

        holder.setText(R.id.nodeNameTv, itemData.getNodeName());
        String identityType = itemData.getIdentityType();


        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(identityType)) {
            holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.common_node));
        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(identityType)) {
            holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.ecological_node));
        }

        TextView haveVotesNumTvHint = holder.getView(R.id.haveVotesNumTvHint);
        if (CommonUtil.isSingle(itemData.getNodeVote())) {
            haveVotesNumTvHint.setText(context.getString(R.string.number_have_votes));
        } else {
            haveVotesNumTvHint.setText(context.getString(R.string.number_have_votes_s));
        }
        holder.setText(R.id.haveVotesNumTv, itemData.getNodeVote());


        TextView myVotesNumTvHint = holder.getView(R.id.myVotesNumTvHint);
        if (CommonUtil.isSingle(itemData.getMyVoteCount())) {
            myVotesNumTvHint.setText(context.getString(R.string.my_votes_number));
        } else {
            myVotesNumTvHint.setText(context.getString(R.string.my_votes_number_s));
        }

        holder.setText(R.id.myVotesNumTv, itemData.getMyVoteCount());

        final View shareBtn = holder.getView(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemBtnListener.onClick(holder.getPosition(), shareBtn.getId());
            }
        });

        final View revokeVoteBtn = holder.getView(R.id.revokeVoteBtn);
        revokeVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemBtnListener.onClick(holder.getPosition(), revokeVoteBtn.getId());
            }
        });

        final View voteRecordBtn = holder.getView(R.id.voteRecordBtn);
        voteRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemBtnListener.onClick(holder.getPosition(), R.id.voteRecordBtn);
            }
        });

    }


    public interface OnItemBtnListener {
        void onClick(int position, int btn);
    }

    private OnItemBtnListener onItemBtnListener;

    public void setOnItemBtnListener(OnItemBtnListener onItemBtnListener) {
        this.onItemBtnListener = onItemBtnListener;
    }
}
