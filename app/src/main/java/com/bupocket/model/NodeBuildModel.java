package com.bupocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class NodeBuildModel implements Serializable {

    private String nodeId;
    private String title;
    private String perAmount;
    private String deposit;
    private int totalCopies;
    private int leftCopies;
    private String rewardRate;
    private String totalAmount;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPerAmount() {
        return perAmount;
    }

    public void setPerAmount(String perAmount) {
        this.perAmount = perAmount;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getLeftCopies() {
        return leftCopies;
    }

    public void setLeftCopies(int leftCopies) {
        this.leftCopies = leftCopies;
    }

    public String getRewardRate() {
        return rewardRate;
    }

    public void setRewardRate(String rewardRate) {
        this.rewardRate = rewardRate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
