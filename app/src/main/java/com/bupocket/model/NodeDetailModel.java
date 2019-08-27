package com.bupocket.model;

import java.util.List;

public class NodeDetailModel {


    /**
     * nodeInfo : {"nodeId":"2c7118d1ba0f43e0b508e6026c820ce0","name":"测试联名生态节点","logo":"8e/9c/9d/ea076f43ec13bf87b525214f23f4496b.jpg","deposit":"3000000","capitalAddress":"buQozS5zX62Wiso7GU6Fh9xQzDRB4vQh33Lj","identityType":"2","status":"72","identityStatus":"4","introduce":"","communityPlan":"","createTime":"2019-06-26 19:14:20","region":"","team":"","communityPlanning":"","serverConfig":"","estimatedCost":"","slogan":"","budgetaryCost":"","contributionPromise":"","timeline":[{"title":"退出节点","enTitle":"withdraw node","content":"申请退出节点竞选，停止记账和奖励分配","enContent":"apply for withdrawal from the election of validator node, stop out block and reward distribution","type":"0","createTime":1561619085000},{"title":"生态节点","enTitle":"Kol node","content":"通过委员会审核，成为生态节点","enContent":"Become an ecological node after passing reviews by the committee","type":"1","createTime":1561548008000},{"title":"","enTitle":"","content":"编辑资质审核资料","enContent":"Edit materials of qualification review","type":"null","createTime":1561547849000}],"order":""}
     * nodeData : {"ratio":"8","equityValue":"3000010","deposit":"3000000","totalVoteCount":"10","totalRewardAmount":"5879.58018799","receiveRewardAmount":"5879.58018799"}
     */

    private NodeInfoBean nodeInfo;
    private NodeDataBean nodeData;

    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfoBean nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public NodeDataBean getNodeData() {
        return nodeData;
    }

    public void setNodeData(NodeDataBean nodeData) {
        this.nodeData = nodeData;
    }

    public static class NodeInfoBean {
        /**
         * nodeId : 2c7118d1ba0f43e0b508e6026c820ce0
         * name : 测试联名生态节点
         * logo : 8e/9c/9d/ea076f43ec13bf87b525214f23f4496b.jpg
         * deposit : 3000000
         * capitalAddress : buQozS5zX62Wiso7GU6Fh9xQzDRB4vQh33Lj
         * identityType : 2
         * status : 72
         * identityStatus : 4
         * introduce :
         * communityPlan :
         * createTime : 2019-06-26 19:14:20
         * region :
         * team :
         * communityPlanning :
         * serverConfig :
         * estimatedCost :
         * slogan :
         * budgetaryCost :
         * contributionPromise :
         * timeline : [{"title":"退出节点","enTitle":"withdraw node","content":"申请退出节点竞选，停止记账和奖励分配","enContent":"apply for withdrawal from the election of validator node, stop out block and reward distribution","type":"0","createTime":1561619085000},{"title":"生态节点","enTitle":"Kol node","content":"通过委员会审核，成为生态节点","enContent":"Become an ecological node after passing reviews by the committee","type":"1","createTime":1561548008000},{"title":"","enTitle":"","content":"编辑资质审核资料","enContent":"Edit materials of qualification review","type":"null","createTime":1561547849000}]
         * order :
         */

        private String nodeId;
        private String name;
        private String logo;
        private String deposit;
        private String capitalAddress;
        private String identityType;
        private String status;
        private String identityStatus;
        private String introduce;
        private String communityPlan;
        private String createTime;
        private String region;
        private String team;
        private String communityPlanning;
        private String serverConfig;
        private String estimatedCost;
        private String slogan;
        private String budgetaryCost;
        private String contributionPromise;
        private String order;
        private List<TimelineBean> timeline;

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDeposit() {
            return deposit;
        }

        public void setDeposit(String deposit) {
            this.deposit = deposit;
        }

        public String getCapitalAddress() {
            return capitalAddress;
        }

        public void setCapitalAddress(String capitalAddress) {
            this.capitalAddress = capitalAddress;
        }

        public String getIdentityType() {
            return identityType;
        }

        public void setIdentityType(String identityType) {
            this.identityType = identityType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIdentityStatus() {
            return identityStatus;
        }

        public void setIdentityStatus(String identityStatus) {
            this.identityStatus = identityStatus;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getCommunityPlan() {
            return communityPlan;
        }

        public void setCommunityPlan(String communityPlan) {
            this.communityPlan = communityPlan;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        public String getCommunityPlanning() {
            return communityPlanning;
        }

        public void setCommunityPlanning(String communityPlanning) {
            this.communityPlanning = communityPlanning;
        }

        public String getServerConfig() {
            return serverConfig;
        }

        public void setServerConfig(String serverConfig) {
            this.serverConfig = serverConfig;
        }

        public String getEstimatedCost() {
            return estimatedCost;
        }

        public void setEstimatedCost(String estimatedCost) {
            this.estimatedCost = estimatedCost;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }

        public String getBudgetaryCost() {
            return budgetaryCost;
        }

        public void setBudgetaryCost(String budgetaryCost) {
            this.budgetaryCost = budgetaryCost;
        }

        public String getContributionPromise() {
            return contributionPromise;
        }

        public void setContributionPromise(String contributionPromise) {
            this.contributionPromise = contributionPromise;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public List<TimelineBean> getTimeline() {
            return timeline;
        }

        public void setTimeline(List<TimelineBean> timeline) {
            this.timeline = timeline;
        }

        public static class TimelineBean {
            /**
             * title : 退出节点
             * enTitle : withdraw node
             * content : 申请退出节点竞选，停止记账和奖励分配
             * enContent : apply for withdrawal from the election of validator node, stop out block and reward distribution
             * type : 0
             * createTime : 1561619085000
             */

            private String title;
            private String enTitle;
            private String content;
            private String enContent;
            private String type;
            private long createTime;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getEnTitle() {
                return enTitle;
            }

            public void setEnTitle(String enTitle) {
                this.enTitle = enTitle;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getEnContent() {
                return enContent;
            }

            public void setEnContent(String enContent) {
                this.enContent = enContent;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }
        }
    }

    public static class NodeDataBean {
        /**
         * ratio : 8
         * equityValue : 3000010
         * deposit : 3000000
         * totalVoteCount : 10
         * totalRewardAmount : 5879.58018799
         * receiveRewardAmount : 5879.58018799
         */

        private String ratio;
        private String equityValue;
        private String deposit;
        private String totalVoteCount;
        private String totalRewardAmount;
        private String receiveRewardAmount;

        public String getRatio() {
            return ratio;
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
        }

        public String getEquityValue() {
            return equityValue;
        }

        public void setEquityValue(String equityValue) {
            this.equityValue = equityValue;
        }

        public String getDeposit() {
            return deposit;
        }

        public void setDeposit(String deposit) {
            this.deposit = deposit;
        }

        public String getTotalVoteCount() {
            return totalVoteCount;
        }

        public void setTotalVoteCount(String totalVoteCount) {
            this.totalVoteCount = totalVoteCount;
        }

        public String getTotalRewardAmount() {
            return totalRewardAmount;
        }

        public void setTotalRewardAmount(String totalRewardAmount) {
            this.totalRewardAmount = totalRewardAmount;
        }

        public String getReceiveRewardAmount() {
            return receiveRewardAmount;
        }

        public void setReceiveRewardAmount(String receiveRewardAmount) {
            this.receiveRewardAmount = receiveRewardAmount;
        }
    }
}
