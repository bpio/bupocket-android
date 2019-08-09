package com.bupocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class SuperNodeModel implements Serializable {

    private static final long serialVersionUID = -4621715087305120000L;

    @Id
    private String nodeId;
    private String nodeName;
    private String nodeLogo;
    private String nodeCapitalAddress;
    private String identityType;
    private String identityStatus;
    private String nodeVote;
    private String myVoteCount;
    private String introduce;
    private String support;
    private String shareStartTime;
    private String status;



    @Generated(hash = 1858321937)
    public SuperNodeModel(String nodeId, String nodeName, String nodeLogo,
            String nodeCapitalAddress, String identityType, String identityStatus,
            String nodeVote, String myVoteCount, String introduce, String support,
            String shareStartTime, String status) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeLogo = nodeLogo;
        this.nodeCapitalAddress = nodeCapitalAddress;
        this.identityType = identityType;
        this.identityStatus = identityStatus;
        this.nodeVote = nodeVote;
        this.myVoteCount = myVoteCount;
        this.introduce = introduce;
        this.support = support;
        this.shareStartTime = shareStartTime;
        this.status = status;
    }

    @Generated(hash = 664471356)
    public SuperNodeModel() {
    }


    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShareStartTime() {
        return shareStartTime;
    }

    public void setShareStartTime(String shareStartTime) {
        this.shareStartTime = shareStartTime;
    }




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

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

}