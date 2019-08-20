package com.bupocket.model;

import com.bupocket.voucher.model.VoucherDetailModel;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

public class Voucher_Acceptance implements PropertyConverter<VoucherDetailModel.VoucherAcceptanceBean,String> {


    @Override
    public VoucherDetailModel.VoucherAcceptanceBean convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue, VoucherDetailModel.VoucherAcceptanceBean.class);
    }

    @Override
    public String convertToDatabaseValue(VoucherDetailModel.VoucherAcceptanceBean entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
