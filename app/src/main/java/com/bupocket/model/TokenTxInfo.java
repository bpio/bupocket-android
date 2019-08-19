package com.bupocket.model;

import com.bupocket.database.greendao.TokenTxInfoDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.OrderBy;

@Entity
public class TokenTxInfo {


    @Id
    private String txHash;

    private String optNo;
    private String address;
    private String assetCode;

    private String txAccountAddress;
    private String txDate;


    private long txTime;
    private String txAmount;
    private String txStatus;
    private String outinType;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTxTime() {
        return txTime;
    }

    public void setTxTime(long txTime) {
        this.txTime = txTime;
    }

    public TokenTxInfo(String txAccountAddress, String txDate,long txTime,String txAmount, String txStatus, String optNo) {
        this.txAccountAddress = txAccountAddress;
        this.txTime=txTime;
        this.txDate = txDate;
        this.txAmount = txAmount;
        this.txStatus = txStatus;
        this.optNo = optNo;
    }

    @Generated(hash = 553494332)
    public TokenTxInfo(String txHash, String optNo, String address, String assetCode, String txAccountAddress,
            String txDate, long txTime, String txAmount, String txStatus, String outinType) {
        this.txHash = txHash;
        this.optNo = optNo;
        this.address = address;
        this.assetCode = assetCode;
        this.txAccountAddress = txAccountAddress;
        this.txDate = txDate;
        this.txTime = txTime;
        this.txAmount = txAmount;
        this.txStatus = txStatus;
        this.outinType = outinType;
    }

    @Generated(hash = 1214664233)
    public TokenTxInfo() {
    }

    public String getTxAccountAddress() {
        return txAccountAddress;
    }

    public void setTxAccountAddress(String txAccountAddress) {
        this.txAccountAddress = txAccountAddress;
    }

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(String txAmount) {
        this.txAmount = txAmount;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getOutinType() {
        return outinType;
    }

    public void setOutinType(String outinType) {
        this.outinType = outinType;
    }

    public String getOptNo() {
        return optNo;
    }

    public void setOptNo(String optNo) {
        this.optNo = optNo;
    }
}
