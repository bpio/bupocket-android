package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseRecyclerAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.ImageUtil;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.zhy.http.okhttp.utils.ImageUtils;

import org.bouncycastle.util.encoders.UrlBase64Encoder;

import java.util.ArrayList;


public class SuperNodeAdapter extends AbsViewHolderAdapter<SuperNodeModel> {

    QMUIListPopup morePop;
    MoreBtnAdapter moreBtnAdapter;
    private int position;

    public SuperNodeAdapter(@NonNull Context context) {
        super(context);

        initData();
    }

    private void initData() {
        if (moreBtnAdapter == null) {
            moreBtnAdapter = new MoreBtnAdapter(context);
            ArrayList<String> newData = new ArrayList<>();
            newData.add(context.getResources().getString(R.string.cancel_vote));
            newData.add(context.getResources().getString(R.string.vote_record));
            moreBtnAdapter.setNewData(newData);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_node_plan_list_item;
    }

    @Override
    protected void convert(final BaseViewHolder holder, SuperNodeModel itemData) {

        if (itemData == null) {
            return;
        }
        holder.setText(R.id.nodeNameTv, itemData.getNodeName());
        String identityType = itemData.getIdentityType();

        if (SuperNodeTypeEnum.VALIDATOR.getCode().equals(identityType)) {
            holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.common_node));
        } else if (SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(identityType)) {
            holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.ecological_node));
        }

        holder.setText(R.id.haveVotesNumTv, itemData.getNodeVote());
        holder.setText(R.id.myVotesNumTv, itemData.getMyVoteCount());

        final View shareBtn = holder.getView(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemBtnListener.onClick(holder.getPosition(),shareBtn.getId());
            }
        });

        final View revokeVoteBtn = holder.getView(R.id.revokeVoteBtn);
        revokeVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemBtnListener.onClick(holder.getPosition(),revokeVoteBtn.getId());
            }
        });

        final View voteRecordBtn = holder.getView(R.id.voteRecordBtn);
        voteRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemBtnListener.onClick(holder.getPosition(),R.id.voteRecordBtn);
            }
        });

        ImageView nodeIconIv = holder.getView(R.id.nodeIconIv);

        String nodeLogo = itemData.getNodeLogo();
        if (!TextUtils.isEmpty(nodeLogo)) {
            nodeIconIv.setImageBitmap(CommonUtil.base64ToBitmap(nodeLogo));
            nodeIconIv.setBackgroundColor(context.getResources().getColor(R.color.app_color_white));
        }



    }



    public interface OnItemBtnListener {
        void onClick(int position,int btn);
    }
    private OnItemBtnListener onItemBtnListener;

    public void setOnItemBtnListener(OnItemBtnListener onItemBtnListener) {
        this.onItemBtnListener = onItemBtnListener;
    }



}
