package com.bupocket.model;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class NodeBuildModel implements Serializable {


    private static final long serialVersionUID = -4621715087305128172L;

    @Id
    private String nodeId;
    private String title;
    private String perAmount;
    private String deposit;
    private int totalCopies;
    private int leftCopies;
    private String rewardRate;
    private String totalAmount;
    private int cobuildCopies;

    @Generated(hash = 778070315)
    public NodeBuildModel(String nodeId, String title, String perAmount,
            String deposit, int totalCopies, int leftCopies, String rewardRate,
            String totalAmount, int cobuildCopies) {
        this.nodeId = nodeId;
        this.title = title;
        this.perAmount = perAmount;
        this.deposit = deposit;
        this.totalCopies = totalCopies;
        this.leftCopies = leftCopies;
        this.rewardRate = rewardRate;
        this.totalAmount = totalAmount;
        this.cobuildCopies = cobuildCopies;
    }

    @Generated(hash = 518472824)
    public NodeBuildModel() {
    }

    public int getCobuildCopies() {
        return cobuildCopies;
    }

    public void setCobuildCopies(int cobuildCopies) {
        this.cobuildCopies = cobuildCopies;
    }

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
