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


    public ArrayList<SuperNodeModel> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<SuperNodeModel> nodeList) {
        this.nodeList = nodeList;
    }

}
