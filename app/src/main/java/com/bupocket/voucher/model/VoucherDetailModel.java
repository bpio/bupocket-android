package com.bupocket.voucher.model;

import com.bupocket.model.Voucher_Acceptance;
import com.bupocket.model.Voucher_Issuer;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class VoucherDetailModel implements Serializable {


    private static final long serialVersionUID = -4621715087305100001L;

    private String address;
    private String balance;
    private String contractAddress;
    private String endTime;
    private String faceValue;
    private String startTime;
    private String trancheId;
    private String spuId;

    @Convert(columnType = String.class, converter = Voucher_Acceptance.class)
    private VoucherAcceptanceBean2 voucherAcceptance;

    @Convert(columnType = String.class, converter = Voucher_Issuer.class)
    private VoucherIssuerBean2 voucherIssuer;

//    @Convert(columnType = String.class, converter = Voucher_Properties.class)
//    private List<VoucherPropertiesBean> voucherProperties;


    private String voucherIcon;
    private String voucherId;
    private String voucherName;




    @Generated(hash = 632054181)
    public VoucherDetailModel(String address, String balance,
            String contractAddress, String endTime, String faceValue,
            String startTime, String trancheId, String spuId,
            VoucherAcceptanceBean2 voucherAcceptance,
            VoucherIssuerBean2 voucherIssuer, String voucherIcon, String voucherId,
            String voucherName) {
        this.address = address;
        this.balance = balance;
        this.contractAddress = contractAddress;
        this.endTime = endTime;
        this.faceValue = faceValue;
        this.startTime = startTime;
        this.trancheId = trancheId;
        this.spuId = spuId;
        this.voucherAcceptance = voucherAcceptance;
        this.voucherIssuer = voucherIssuer;
        this.voucherIcon = voucherIcon;
        this.voucherId = voucherId;
        this.voucherName = voucherName;
    }

    @Generated(hash = 382339780)
    public VoucherDetailModel() {
    }

    


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(String faceValue) {
        this.faceValue = faceValue;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTrancheId() {
        return trancheId;
    }

    public void setTrancheId(String trancheId) {
        this.trancheId = trancheId;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public VoucherAcceptanceBean2 getVoucherAcceptance() {
        return voucherAcceptance;
    }

    public void setVoucherAcceptance(VoucherAcceptanceBean2 voucherAcceptance) {
        this.voucherAcceptance = voucherAcceptance;
    }

    public String getVoucherIcon() {
        return voucherIcon;
    }

    public void setVoucherIcon(String voucherIcon) {
        this.voucherIcon = voucherIcon;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public VoucherIssuerBean2 getVoucherIssuer() {
        return voucherIssuer;
    }

    public void setVoucherIssuer(VoucherIssuerBean2 voucherIssuer) {
        this.voucherIssuer = voucherIssuer;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }







}
