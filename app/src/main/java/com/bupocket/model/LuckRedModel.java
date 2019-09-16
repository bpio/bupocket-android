package com.bupocket.model;

import java.io.Serializable;

public class LuckRedModel implements Serializable {


    private String date;
    private String receiver;
    private String mvpFlag;
    private String amount;
    private String tokenSymbol;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMvpFlag() {
        return mvpFlag;
    }

    public void setMvpFlag(String mvpFlag) {
        this.mvpFlag = mvpFlag;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }


}
