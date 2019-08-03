package com.bupocket.model;

import java.io.Serializable;
import java.util.List;

public class RedPacketDetailModel implements Serializable {


    private BonusInfoBean bonusInfo;
    private ActivityRulesBean activityRules;
    private LatelyDataBean latelyData;

    public BonusInfoBean getBonusInfo() {
        return bonusInfo;
    }

    public void setBonusInfo(BonusInfoBean bonusInfo) {
        this.bonusInfo = bonusInfo;
    }

    public ActivityRulesBean getActivityRules() {
        return activityRules;
    }

    public void setActivityRules(ActivityRulesBean activityRules) {
        this.activityRules = activityRules;
    }

    public LatelyDataBean getLatelyData() {
        return latelyData;
    }

    public void setLatelyData(LatelyDataBean latelyData) {
        this.latelyData = latelyData;
    }



    public static class ActivityRulesBean {

        private String label;
        private String data;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class LatelyDataBean {


        private String label;
        private List<LuckRedModel> data;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<LuckRedModel> getData() {
            return data;
        }

        public void setData(List<LuckRedModel> data) {
            this.data = data;
        }



    }
}
