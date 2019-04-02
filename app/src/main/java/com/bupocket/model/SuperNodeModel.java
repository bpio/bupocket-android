package com.bupocket.model;

public class SuperNodeModel {

    private String nodeId;
    private String nodeName;
    private String nodeLogo;
    private String nodeCapitalAddress;
    private String identityType;
    private String identityStatus;
    private String nodeVote;
    private String myVoteCount;

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setNodeLogo(String nodeLogo) {
        this.nodeLogo = nodeLogo;
    }

    public void setNodeCapitalAddress(String nodeCapitalAddress) {
        this.nodeCapitalAddress = nodeCapitalAddress;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public void setIdentityStatus(String identityStatus) {
        this.identityStatus = identityStatus;
    }

    public void setNodeVote(String nodeVote) {
        this.nodeVote = nodeVote;
    }

    public void setMyVoteCount(String myVoteCount) {
        this.myVoteCount = myVoteCount;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeLogo() {
        return nodeLogo;
    }

    public String getNodeCapitalAddress() {
        return nodeCapitalAddress;
    }

    public String getIdentityType() {
        return identityType;
    }

    public String getIdentityStatus() {
        return identityStatus;
    }

    public String getNodeVote() {
        return nodeVote;
    }

    public String getMyVoteCount() {
        return myVoteCount;
    }
}