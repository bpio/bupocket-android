package com.bupocket.model;

import java.util.PriorityQueue;

public class NodeInfoModel {

    private String nodeName;
    private String logo;
    private String identityType;
    private String voteCount;
    private String myVoteCount;


    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getMyVoteCount() {
        return myVoteCount;
    }

    public void setMyVoteCount(String myVoteCount) {
        this.myVoteCount = myVoteCount;
    }
}
