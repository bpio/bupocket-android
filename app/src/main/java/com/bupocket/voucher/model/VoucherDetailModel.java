package com.bupocket.voucher.model;

import com.bupocket.model.Voucher_Acceptance;
import com.bupocket.model.Voucher_Issuer;
import com.bupocket.model.Voucher_Properties;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;

import java.io.Serializable;
import java.util.List;
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
    private VoucherAcceptanceBean voucherAcceptance;

    @Convert(columnType = String.class, converter = Voucher_Issuer.class)
    private VoucherIssuerBean voucherIssuer;

    @Convert(columnType = String.class, converter = Voucher_Properties.class)
    private List<VoucherPropertiesBean> voucherProperties;


    private String voucherIcon;
    private String voucherId;
    private String voucherName;


    @Generated(hash = 1087373310)
    public VoucherDetailModel(String address, String balance, String contractAddress,
            String endTime, String faceValue, String startTime, String trancheId,
            String spuId, VoucherAcceptanceBean voucherAcceptance,
            VoucherIssuerBean voucherIssuer, List<VoucherPropertiesBean> voucherProperties,
            String voucherIcon, String voucherId, String voucherName) {
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
        this.voucherProperties = voucherProperties;
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

    public List<VoucherPropertiesBean> getVoucherProperties() {
        return voucherProperties;
    }

    public void setVoucherProperties(List<VoucherPropertiesBean> voucherProperties) {
        this.voucherProperties = voucherProperties;
    }

    public static class VoucherAcceptanceBean implements Serializable {

        private static final long serialVersionUID = -4621715087305100002L;
        private String address;
        private String icon;
        private String name;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class VoucherIssuerBean implements Serializable {

        private static final long serialVersionUID = -4621715087305100003L;
        private String address;
        private String icon;
        private String name;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class VoucherPropertiesBean implements Serializable {

        private static final long serialVersionUID = -4621715087305100004L;
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
