package com.bupocket.model;

import com.bupocket.voucher.model.VoucherAcceptanceBean2;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

public class Voucher_Acceptance implements PropertyConverter<VoucherAcceptanceBean2,String> {


    @Override
    public VoucherAcceptanceBean2 convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue, VoucherAcceptanceBean2.class);
    }

    @Override
    public String convertToDatabaseValue(VoucherAcceptanceBean2 entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
