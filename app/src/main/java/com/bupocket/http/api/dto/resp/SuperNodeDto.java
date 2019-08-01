package com.bupocket.http.api.dto.resp;

import com.bupocket.model.SuperNodeModel;

import java.util.ArrayList;

public class SuperNodeDto {

//     "nodeId": "{nodeId}",
//             "nodeName": "{nodeName}",
//             "nodeLogo": "{nodeLogo}",
//             "nodeCapitalAddress": "{nodeCapitalAddress}",
//             "identityType": "{identityType}",
//             "identityStatus": "{nodeBestRankingName}",
//             "nodeVote": "{nodeVote}",
//             "myVoteCount": "20"

    private ArrayList<SuperNodeModel>  nodeList;
    private String accountTag;
    private String accountTagEn;

    public String getAccountTag() {
        return accountTag;
    }

    public void setAccountTag(String accountTag) {
        this.accountTag = accountTag;
    }

    public String getAccountTagEn() {
        return accountTagEn;
    }

    public void setAccountTagEn(String accountTagEn) {
        this.accountTagEn = accountTagEn;
    }

    public ArrayList<SuperNodeModel> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<SuperNodeModel> nodeList) {
        this.nodeList = nodeList;
    }

}
