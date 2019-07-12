package com.bupocket.voucher.adapter;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TimeUtils;
import android.view.TextureView;
import android.view.View;
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


    public VoucherDetailModel selectedVoucherDetailModel;

    public VoucherDetailModel getSelectedVoucherDetailModel() {
        return selectedVoucherDetailModel;
    }

    public void setSelectedVoucherDetailModel(VoucherDetailModel selectedVoucherDetailModel) {
        this.selectedVoucherDetailModel = selectedVoucherDetailModel;
    }

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

        String icon = itemData.getVoucherAcceptance().getIcon();
        if (!TextUtils.isEmpty(icon)) {
            Glide.with(context)
                    .load(icon)
                    .into(acceptanceIconRiv);
        }

        String voucherIcon = itemData.getVoucherIcon();
        if (!TextUtils.isEmpty(voucherIcon)) {
            Glide.with(context)
                    .load(voucherIcon)
                    .into(voucherGoodsIv);
        }
        String name = itemData.getVoucherAcceptance().getName();
        if (!TextUtils.isEmpty(name)) {
            holder.setText(R.id.acceptanceNameTv, name);
        }
        String voucherName = itemData.getVoucherName();
        if (!TextUtils.isEmpty(voucherName)) {
            holder.setText(R.id.goodsNameTv, voucherName);
        }
        String balance = itemData.getBalance();
        if (!TextUtils.isEmpty(balance)) {
            holder.setText(R.id.goodsNumTv, balance);
        }
        String faceValue = itemData.getFaceValue();
        if (!TextUtils.isEmpty(faceValue)) {
            holder.setText(R.id.goodsPriceTv, context.getString(R.string.goods_price) + faceValue);
        }

        String startTime = itemData.getStartTime();
        String endTime = itemData.getEndTime();
        String date = context.getString(R.string.validity_date);
        if (!TextUtils.isEmpty(startTime)&&startTime!="-1") {
            date = date + ": " +
                    String.format(context.getString(R.string.goods_validity_date),
                            TimeUtil.timeStamp2Date(startTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD),
                            TimeUtil.timeStamp2Date(endTime, TimeUtil.TIME_TYPE_YYYYY_MM_DD));

        }else{
            date+="  "+context.getResources().getString(R.string.long_date);
        }
        holder.setText(R.id.goodsDateTv, date);

        if (selectedVoucherDetailModel!=null) {
            if (selectedVoucherDetailModel.getSpuId().equals(itemData.getSpuId())
                    &&selectedVoucherDetailModel.getTrancheId().equals(itemData.getTrancheId())) {
                holder.getView(R.id.selectedStatusIv).setVisibility(View.VISIBLE);
            }else{
                holder.getView(R.id.selectedStatusIv).setVisibility(View.INVISIBLE);
            }
        }

    }

}
