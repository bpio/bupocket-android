package com.bupocket.model;

import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.voucher.model.VoucherIssuerBean2;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

public class Voucher_Issuer implements PropertyConverter<VoucherIssuerBean2,String> {


    @Override
    public VoucherIssuerBean2 convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue, VoucherIssuerBean2.class);
    }

    @Override
    public String convertToDatabaseValue(VoucherIssuerBean2 entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
