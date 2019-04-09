package com.bupocket.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SuperNodeModel implements Parcelable {

    private String nodeId;
    private String nodeName;
    private String nodeLogo;
    private String nodeCapitalAddress;
    private String identityType;
    private String identityStatus;
    private String nodeVote;
    private String myVoteCount;

    protected SuperNodeModel(Parcel in) {
        nodeId = in.readString();
        nodeName = in.readString();
        nodeLogo = in.readString();
        nodeCapitalAddress = in.readString();
        identityType = in.readString();
        identityStatus = in.readString();
        nodeVote = in.readString();
        myVoteCount = in.readString();
    }

    public static final Creator<SuperNodeModel> CREATOR = new Creator<SuperNodeModel>() {
        @Override
        public SuperNodeModel createFromParcel(Parcel in) {
            return new SuperNodeModel(in);
        }

        @Override
        public SuperNodeModel[] newArray(int size) {
            return new SuperNodeModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(nodeName);
        dest.writeString(nodeLogo);
        dest.writeString(nodeCapitalAddress);
        dest.writeString(identityType);
        dest.writeString(identityStatus);
        dest.writeString(nodeVote);
        dest.writeString(myVoteCount);
    }
}