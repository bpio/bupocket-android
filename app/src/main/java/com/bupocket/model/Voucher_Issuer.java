package com.bupocket.model;

import com.bupocket.voucher.model.VoucherDetailModel;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

public class Voucher_Issuer implements PropertyConverter<VoucherDetailModel.VoucherIssuerBean,String> {


    @Override
    public VoucherDetailModel.VoucherIssuerBean convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue, VoucherDetailModel.VoucherIssuerBean.class);
    }

    @Override
    public String convertToDatabaseValue(VoucherDetailModel.VoucherIssuerBean entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
