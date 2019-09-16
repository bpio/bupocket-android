package com.bupocket.model;

public class UserInfoModel {


    /**
     * isBindWx : 0
     * wxInfo : {"nick":"","headImgUrl":""}
     */

    private String isBindWx;
    private WeChatInfoModel wxInfo;

    public String getIsBindWx() {
        return isBindWx;
    }

    public void setIsBindWx(String isBindWx) {
        this.isBindWx = isBindWx;
    }

    public WeChatInfoModel getWxInfo() {
        return wxInfo;
    }

    public void setWxInfo(WeChatInfoModel wxInfo) {
        this.wxInfo = wxInfo;
    }
}
