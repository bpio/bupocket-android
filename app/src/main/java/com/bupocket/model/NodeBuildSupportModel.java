package com.bupocket.model;

public class NodeBuildSupportModel {


    private String  initiatorAddress;
    private String  amount;
    private String  createTime;
    private String  type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInitiatorAddress() {
        return initiatorAddress;
    }

    public void setInitiatorAddress(String initiatorAddress) {
        this.initiatorAddress = initiatorAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
