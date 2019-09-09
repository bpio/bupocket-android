package com.bupocket.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public  class BlockInfoRespBoBean {
    /**
     * closeTimeDate : 1526544917898299
     * hash : 68100a81ea74276de23ed91f8a07973094ff7bf3b91b9bb9302ac5134be7d586
     * previousHash : fe77b9ddefc2d9071f5bf18f6be1b4aac7933219aa2c3d9a4e97e7277db904f6
     * seq : 153532
     * txCount : 1
     * validatorsHash : 4d1a95bd634df20c179ba152a41a72277392d0a9835460a4db7882da31169b8d
     */

    private String address;
    @Id
    private String optNo;
    private String closeTimeDate;
    private String hash;
    private String previousHash;
    private int seq;
    private int txCount;
    private String validatorsHash;


    @Generated(hash = 183617611)
    public BlockInfoRespBoBean(String address, String optNo, String closeTimeDate,
            String hash, String previousHash, int seq, int txCount, String validatorsHash) {
        this.address = address;
        this.optNo = optNo;
        this.closeTimeDate = closeTimeDate;
        this.hash = hash;
        this.previousHash = previousHash;
        this.seq = seq;
        this.txCount = txCount;
        this.validatorsHash = validatorsHash;
    }

    @Generated(hash = 2065838449)
    public BlockInfoRespBoBean() {
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

    public String getCloseTimeDate() {
        return closeTimeDate;
    }

    public void setCloseTimeDate(String closeTimeDate) {
        this.closeTimeDate = closeTimeDate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getTxCount() {
        return txCount;
    }

    public void setTxCount(int txCount) {
        this.txCount = txCount;
    }

    public String getValidatorsHash() {
        return validatorsHash;
    }

    public void setValidatorsHash(String validatorsHash) {
        this.validatorsHash = validatorsHash;
    }
}