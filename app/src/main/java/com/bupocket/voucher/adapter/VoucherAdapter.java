package com.bupocket.voucher.adapter;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.TimeUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.common.Constants;
import com.bupocket.utils.TimeUtil;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.voucher.model.VoucherListModel;

public class VoucherAdapter extends AbsViewHolderAdapter<VoucherDetailModel> {


    public VoucherAdapter(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_voucher_package_item;
    }


    @Override
    protected void convert(BaseViewHolder holder, VoucherDetailModel itemData) {

        ImageView acceptanceIconRiv = (ImageView) holder.getView(R.id.acceptanceIconRiv);
        ImageView voucherGoodsIv = (ImageView) holder.getView(R.id.voucherGoodsIv);
        Glide.with(context)
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(itemData.getVoucherAcceptance().getIcon()))
                .into(acceptanceIconRiv);

        Glide.with(context)
                .load(Constants.NODE_PLAN_IMAGE_URL_PREFIX.concat(itemData.getVoucherIcon()))
                .into(voucherGoodsIv);


        holder.setText(R.id.acceptanceNameTv, itemData.getVoucherAcceptance().getName());
        holder.setText(R.id.goodsNameTv, itemData.getVoucherName());
        holder.setText(R.id.goodsPriceTv, context.getString(R.string.goods_price) + itemData.getFaceValue());

        holder.setText(R.id.goodsDateTv,
                String.format(context.getString(R.string.goods_validity_date),
                        TimeUtil.timeStamp2Date(itemData.getStartTime()),
                        TimeUtil.timeStamp2Date(itemData.getEndTime())));


    }

}
