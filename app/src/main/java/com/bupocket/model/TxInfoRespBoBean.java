package com.bupocket.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public  class TxInfoRespBoBean {
    /**
     * amount : 1
     * destAddress : buQeZjdQLCoBCwbVYFnMvXByjMDYm9Hwhkgv
     * fee : 0.00246
     * hash : ea379540e73f06056ec02a77468bfe2657f9724bbeea8236763d6c1a675ebc14
     * ledgerSeq : 153532
     * nonce : 73
     * signatureStr : [{"publicKey":"b001a80ca91e3d6a08214aedc81a0ed550cd40dda42b99bf9dccd6ee5d59d7a9f69d0fa1756f","signData":"418dd3b1820ef61bbeaf95ca4608739e13b186490b494500a7efbd5bd0ed7d27c99f7e81c565143a60d97455a027a885b466439c1466cdde28b03d90c312480e"}]
     * sourceAddress : buQfxCc35fLqX95dAPztyH4aneb8GDx8Sy4i
     */


    private String address;
    private String optNo;
    private String amount;
    private String destAddress;
    private String fee;
    private String hash;
    private int ledgerSeq;
    private int nonce;
    private String signatureStr;
    private String sourceAddress;


    @Generated(hash = 1772331965)
    public TxInfoRespBoBean(String address, String optNo, String amount, String destAddress, String fee, String hash, int ledgerSeq, int nonce, String signatureStr, String sourceAddress) {
        this.address = address;
        this.optNo = optNo;
        this.amount = amount;
        this.destAddress = destAddress;
        this.fee = fee;
        this.hash = hash;
        this.ledgerSeq = ledgerSeq;
        this.nonce = nonce;
        this.signatureStr = signatureStr;
        this.sourceAddress = sourceAddress;
    }

    @Generated(hash = 906463736)
    public TxInfoRespBoBean() {
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getLedgerSeq() {
        return ledgerSeq;
    }

    public void setLedgerSeq(int ledgerSeq) {
        this.ledgerSeq = ledgerSeq;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getSignatureStr() {
        return signatureStr;
    }

    public void setSignatureStr(String signatureStr) {
        this.signatureStr = signatureStr;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getOptNo() {
        return this.optNo;
    }

    public void setOptNo(String optNo) {
        this.optNo = optNo;
    }

}