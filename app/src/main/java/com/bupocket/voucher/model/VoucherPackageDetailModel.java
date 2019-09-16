package com.bupocket.voucher.model;

import com.bupocket.model.Voucher_Acceptance;
import com.bupocket.model.Voucher_Issuer;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class VoucherPackageDetailModel  implements Serializable {

    private static final long serialVersionUID = -4621715087305100002L;

    private String address;
    private String contractAddress;
    private String endTime;
    private String faceValue;
    private String spuId;
    private String startTime;
    private String trancheId;


    @Convert(columnType = String.class, converter = Voucher_Acceptance.class)
    private VoucherAcceptanceBean2 voucherAcceptance;
    @Convert(columnType = String.class, converter = Voucher_Issuer.class)
    private VoucherIssuerBean2 voucherIssuer;


    private String voucherIcon;
    private String voucherId;
    private String voucherName;
    private String voucherSpec;
    private String description;


    @Generated(hash = 1276462550)
    public VoucherPackageDetailModel(String address, String contractAddress,
            String endTime, String faceValue, String spuId, String startTime,
            String trancheId, VoucherAcceptanceBean2 voucherAcceptance,
            VoucherIssuerBean2 voucherIssuer, String voucherIcon, String voucherId,
            String voucherName, String voucherSpec, String description) {
        this.address = address;
        this.contractAddress = contractAddress;
        this.endTime = endTime;
        this.faceValue = faceValue;
        this.spuId = spuId;
        this.startTime = startTime;
        this.trancheId = trancheId;
        this.voucherAcceptance = voucherAcceptance;
        this.voucherIssuer = voucherIssuer;
        this.voucherIcon = voucherIcon;
        this.voucherId = voucherId;
        this.voucherName = voucherName;
        this.voucherSpec = voucherSpec;
        this.description = description;
    }

    @Generated(hash = 1561449982)
    public VoucherPackageDetailModel() {
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
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

    public String getVoucherSpec() {
        return voucherSpec;
    }

    public void setVoucherSpec(String voucherSpec) {
        this.voucherSpec = voucherSpec;
    }


}
