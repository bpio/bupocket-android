package com.bupocket.voucher.model;

public class VoucherPackageDetailModel {



    private String contractAddress;
    private String endTime;
    private String faceValue;
    private String spuId;
    private String startTime;
    private String trancheId;
    private VoucherAcceptanceBean voucherAcceptance;
    private String voucherIcon;
    private String voucherId;
    private VoucherIssuerBean voucherIssuer;
    private String voucherName;
    private String voucherSpec;
    private String description;

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

    public VoucherAcceptanceBean getVoucherAcceptance() {
        return voucherAcceptance;
    }

    public void setVoucherAcceptance(VoucherAcceptanceBean voucherAcceptance) {
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

    public VoucherIssuerBean getVoucherIssuer() {
        return voucherIssuer;
    }

    public void setVoucherIssuer(VoucherIssuerBean voucherIssuer) {
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
