package com.bupocket.model;

import java.util.ArrayList;

public class NodeBuildDetailModel {


    private String nodeId;
    private String title;
    private String perAmount;
    private String deposit;
    private int copies;
    private int leftCopies;
    private String rewardRate;
    private String totalAmount;
    private String status;
    private String supportCopies;
    private String supportPerson;
    private String initiatorAmount;
    private String supportAmount;
    private ArrayList<NodeBuildSupportModel> supportList;

    public String getSupportCopies() {
        return supportCopies;
    }

    public String getInitiatorAmount() {
        return initiatorAmount;
    }

    public void setInitiatorAmount(String initiatorAmount) {
        this.initiatorAmount = initiatorAmount;
    }

    public String getSupportAmount() {
        return supportAmount;
    }

    public void setSupportAmount(String supportAmount) {
        this.supportAmount = supportAmount;
    }

    public void setSupportCopies(String supportCopies) {
        this.supportCopies = supportCopies;
    }

    public String getSupportPerson() {
        return supportPerson;
    }

    public void setSupportPerson(String supportPerson) {
        this.supportPerson = supportPerson;
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

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<NodeBuildSupportModel> getSupportList() {
        return supportList;
    }

    public void setSupportList(ArrayList<NodeBuildSupportModel> supportList) {
        this.supportList = supportList;
    }
}
