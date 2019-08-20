package com.bupocket.model;

import com.bupocket.voucher.model.VoucherDetailModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

public class Voucher_Properties implements PropertyConverter<List<VoucherDetailModel.VoucherPropertiesBean>,String> {


    @Override
    public List<VoucherDetailModel.VoucherPropertiesBean> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        // 先得获得这个，然后再typeToken.getType()，否则会异常
        TypeToken<List<VoucherDetailModel.VoucherPropertiesBean>> typeToken = new TypeToken<List<VoucherDetailModel.VoucherPropertiesBean>>(){};
        return new Gson().fromJson(databaseValue, typeToken.getType());
    }

    @Override
    public String convertToDatabaseValue(List<VoucherDetailModel.VoucherPropertiesBean> arrays) {
        if (arrays == null||arrays.size()==0) {
            return null;
        } else {
            String sb = new Gson().toJson(arrays);
            return sb;

        }
    }
}
