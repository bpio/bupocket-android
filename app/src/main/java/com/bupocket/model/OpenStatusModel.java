package com.bupocket.model;

public class OpenStatusModel {

    private String type;
    private String activityId;
    private String bonusId;
    private String bonusEntranceImage;
    private String pageUrl;

    public String getBonusId() {
        return bonusId;
    }

    public void setBonusId(String bonusId) {
        this.bonusId = bonusId;
    }

    public String getBonusEntranceImage() {
        return bonusEntranceImage;
    }

    public void setBonusEntranceImage(String bonusEntranceImage) {
        this.bonusEntranceImage = bonusEntranceImage;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
