package com.bupocket.model;

import java.io.Serializable;

public class BonusInfoBean  implements Serializable {
    private String topImage;
    private String bottomImage;
    private String issuerPhoto;
    private String issuerNick;
    private String mvpFlag;
    private String amount;
    private String tokenSymbol;
    private String receiver;
    private String BonusOverImage;

    public String getBonusOverImage() {
        return BonusOverImage;
    }

    public void setBonusOverImage(String bonusOverImage) {
        BonusOverImage = bonusOverImage;
    }

    public String getTopImage() {
        return topImage;
    }

    public void setTopImage(String topImage) {
        this.topImage = topImage;
    }

    public String getBottomImage() {
        return bottomImage;
    }

    public void setBottomImage(String bottomImage) {
        this.bottomImage = bottomImage;
    }

    public String getIssuerPhoto() {
        return issuerPhoto;
    }

    public void setIssuerPhoto(String issuerPhoto) {
        this.issuerPhoto = issuerPhoto;
    }

    public String getIssuerNick() {
        return issuerNick;
    }

    public void setIssuerNick(String issuerNick) {
        this.issuerNick = issuerNick;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
