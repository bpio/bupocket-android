package com.bupocket.voucher.model;

import java.io.Serializable;

public class VoucherAcceptanceBean implements Serializable {

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
