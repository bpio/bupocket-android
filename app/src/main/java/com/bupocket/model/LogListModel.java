package com.bupocket.model;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LogListModel {

    @Id
    private String createTime;
    private String englishVerContents;
    private String verContents;
    private String verNumber;

    @Generated(hash = 589878881)
    public LogListModel(String createTime, String englishVerContents,
            String verContents, String verNumber) {
        this.createTime = createTime;
        this.englishVerContents = englishVerContents;
        this.verContents = verContents;
        this.verNumber = verNumber;
    }

    @Generated(hash = 800070479)
    public LogListModel() {
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEnglishVerContents() {
        return englishVerContents;
    }

    public void setEnglishVerContents(String englishVerContents) {
        this.englishVerContents = englishVerContents;
    }

    public String getVerContents() {
        return verContents;
    }

    public void setVerContents(String verContents) {
        this.verContents = verContents;
    }

    public String getVerNumber() {
        return verNumber;
    }

    public void setVerNumber(String verNumber) {
        this.verNumber = verNumber;
    }
}
