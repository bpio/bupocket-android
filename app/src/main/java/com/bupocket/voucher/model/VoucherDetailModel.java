package com.bupocket.voucher.model;

import java.io.Serializable;
import java.util.List;

public class VoucherDetailModel implements Serializable {

        private String balance;
        private String contractAddress;
        private String endTime;
        private String faceValue;
        private String startTime;
        private String trancheId;
        private String spuId;
        private VoucherAcceptanceBean voucherAcceptance;
        private String voucherIcon;
        private String voucherId;
        private VoucherIssuerBean voucherIssuer;
        private String voucherName;
        private List<VoucherPropertiesBean> voucherProperties;

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
