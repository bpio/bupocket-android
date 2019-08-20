package com.bupocket.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public  class TxDetailRespBoBean {
    /**
     * amount : 1
     * applyTimeDate : 1526544917898299
     * destAddress : buQeZjdQLCoBCwbVYFnMvXByjMDYm9Hwhkgv
     * fee : 0.00246
     * sourceAddress : buQfxCc35fLqX95dAPztyH4aneb8GDx8Sy4i
     */
    private String address;
    private String optNo;
    private String amount;
    private String applyTimeDate;
    private String destAddress;
    private String fee;
    private String sourceAddress;
    private Integer status;
    private String originalMetadata;
    private String txMetadata;
    private String operaMetadata;
    private String errorMsg;
    private String errorCode;


    @Generated(hash = 1221306387)
    public TxDetailRespBoBean(String address, String optNo, String amount,
            String applyTimeDate, String destAddress, String fee,
            String sourceAddress, Integer status, String originalMetadata,
            String txMetadata, String operaMetadata, String errorMsg,
            String errorCode) {
        this.address = address;
        this.optNo = optNo;
        this.amount = amount;
        this.applyTimeDate = applyTimeDate;
        this.destAddress = destAddress;
        this.fee = fee;
        this.sourceAddress = sourceAddress;
        this.status = status;
        this.originalMetadata = originalMetadata;
        this.txMetadata = txMetadata;
        this.operaMetadata = operaMetadata;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    @Generated(hash = 520510681)
    public TxDetailRespBoBean() {
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOptNo() {
        return optNo;
    }

    public void setOptNo(String optNo) {
        this.optNo = optNo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getApplyTimeDate() {
        return applyTimeDate;
    }

    public void setApplyTimeDate(String applyTimeDate) {
        this.applyTimeDate = applyTimeDate;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOriginalMetadata() {
        return originalMetadata;
    }

    public void setOriginalMetadata(String originalMetadata) {
        this.originalMetadata = originalMetadata;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTxMetadata() {
        return txMetadata;
    }

    public void setTxMetadata(String txMetadata) {
        this.txMetadata = txMetadata;
    }

    public String getOperaMetadata() {
        return operaMetadata;
    }

    public void setOperaMetadata(String operaMetadata) {
        this.operaMetadata = operaMetadata;
    }
}