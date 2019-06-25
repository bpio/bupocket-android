package com.bupocket.model;

import com.google.gson.Gson;

public class RegisterTokenInfo {


    private String type;
    private String name;
    private String code;
    private String amount;
    private String decimals;
    private String desc;

    public static RegisterTokenInfo objectFromData(String str) {

        return new Gson().fromJson(str, RegisterTokenInfo.class);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
