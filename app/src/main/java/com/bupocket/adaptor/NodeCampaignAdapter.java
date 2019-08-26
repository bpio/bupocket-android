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
import com.bupocket.utils.LogUtils;


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
        String iconUrl = Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(nodeLogo);
        Glide.with(context)
                .load(iconUrl)
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

//        holder.setBackgroundResource(R.id.nodeStateTv,);

    }


}
