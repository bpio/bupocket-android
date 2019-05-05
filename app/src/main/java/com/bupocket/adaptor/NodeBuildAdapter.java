package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.NodeBuildModel;
import com.bupocket.utils.CommonUtil;

import java.text.DecimalFormat;

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

        if (itemData == null) {
            return;
        }
        holder.setText(R.id.tvBuildName, itemData.getTitle());


        holder.setText(R.id.tvBuildRewardRate, itemData.getRewardRate() + "%");

        ProgressBar pbBuild = holder.getView(R.id.pbBuild);
        pbBuild.setMax(itemData.getCobuildCopies());
        int supportCopies = itemData.getCobuildCopies() - itemData.getLeftCopies();
        pbBuild.setProgress(supportCopies);


        holder.setText(R.id.tvProgress, CommonUtil.setRatio(supportCopies, itemData.getCobuildCopies()));

        String amount = itemData.getPerAmount();
        if (!TextUtils.isEmpty(amount)) {
            holder.setText(R.id.tvBuildNum, CommonUtil.format(amount));
        }

        TextView tvRemainNum = holder.getView(R.id.tvRemainNum);
        TextView tvRemainCopy = holder.getView(R.id.tvRemainCopy);

        int build_support_copies;
        int build_left_copies;
        if (CommonUtil.isSingle(supportCopies)) {
            build_support_copies = R.string.build_support_copies;
        } else {
            build_support_copies = R.string.build_support_copies_s;

        }
        if (CommonUtil.isSingle(itemData.getLeftCopies())) {
            build_left_copies = R.string.build_left_copies;
        } else {
            build_left_copies = R.string.build_left_copies_s;
        }

        tvRemainNum.setText(Html.fromHtml(String.format(context.getString(build_support_copies), supportCopies + "")));
        tvRemainCopy.setText(Html.fromHtml(String.format(context.getString(build_left_copies), itemData.getLeftCopies() + "")));


    }


}
