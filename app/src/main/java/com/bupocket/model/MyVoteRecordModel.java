package com.bupocket.model;

import java.util.ArrayList;

public class MyVoteRecordModel {


    NodeInfoModel  nodeInfo;

    ArrayList<MyVoteInfoModel>  list;

    public NodeInfoModel getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfoModel nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public ArrayList<MyVoteInfoModel> getList() {
        return list;
    }

    public void setList(ArrayList<MyVoteInfoModel> list) {
        this.list = list;
    }
}
