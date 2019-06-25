package com.bupocket.model;

import com.google.gson.Gson;

public class IssueTokenInfo {


    private String code;
    private String amount;

    public static IssueTokenInfo objectFromData(String str) {

        return new Gson().fromJson(str, IssueTokenInfo.class);
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
}
