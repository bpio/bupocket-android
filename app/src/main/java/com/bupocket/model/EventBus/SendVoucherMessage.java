package com.bupocket.model.EventBus;

import com.bupocket.voucher.model.VoucherDetailModel;

public class SendVoucherMessage {

//    private String name;
//
//    public SendVoucherMessage(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
        private VoucherDetailModel voucherDetailModel;

    public SendVoucherMessage(VoucherDetailModel voucherDetailModel) {
        this.voucherDetailModel = voucherDetailModel;
    }


    public VoucherDetailModel getVoucherDetailModel() {
        return voucherDetailModel;
    }

    public void setVoucherDetailModel(VoucherDetailModel voucherDetailModel) {
        this.voucherDetailModel = voucherDetailModel;
    }
}
