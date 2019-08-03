package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TimeUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.enums.RedPacketKingEnum;
import com.bupocket.model.LuckRedModel;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.TimeUtil;

public class RedPacketAdapter extends AbsViewHolderAdapter<LuckRedModel> {


    @Override
    public int getCount() {
        return super.getCount()*100;
    }

    @Nullable
    @Override
    public LuckRedModel getItem(int position) {
        position=position%getData().size();
        return super.getItem(position);
    }

    public RedPacketAdapter(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_red_packet_detail_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, LuckRedModel itemData) {
        String receiver = itemData.getReceiver();
        if (!TextUtils.isEmpty(receiver)) {
            holder.setText(R.id.walletAddressItemTv, AddressUtil.anonymous(receiver));
        }

        String amount = itemData.getAmount();
        if (!TextUtils.isEmpty(amount)) {
            TextView amountTv = (TextView) holder.getView(R.id.amountItemTv);
            amountTv.setText(amount);
        }

        String tokenSymbol = itemData.getTokenSymbol();
        if (!TextUtils.isEmpty(tokenSymbol)) {
            holder.setText(R.id.tokenTypeItemTv, tokenSymbol);
        }

        String date = itemData.getDate();
        if (!TextUtils.isEmpty(date)) {
            holder.setText(R.id.dateItemTv, TimeUtil.getDateDiff(Long.parseLong(itemData.getDate()), context));
        }
        String mvpFlag = itemData.getMvpFlag();
        View mvpFlagView = holder.getView(R.id.redKingIv);
        if (mvpFlag.equals(RedPacketKingEnum.YES.getCode())) {
            mvpFlagView.setVisibility(View.VISIBLE);
        } else {
            mvpFlagView.setVisibility(View.INVISIBLE);
        }
    }
}
