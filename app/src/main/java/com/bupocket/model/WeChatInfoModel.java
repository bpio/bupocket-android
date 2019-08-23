package com.bupocket.model;

import com.bupocket.http.api.AddressBookListBean;
import com.google.gson.Gson;

import java.util.List;

public class WeChatInfoModel {

    private String wxNick;
    private String wxHeadImgUrl;
    private String isBindWx;


    public String getIsBindWx() {
        return isBindWx;
    }

    public void setIsBindWx(String isBindWx) {
        this.isBindWx = isBindWx;
    }

    public String getWxNick() {
        return wxNick;
    }

    public void setWxNick(String wxNick) {
        this.wxNick = wxNick;
    }

    public String getWxHeadImgUrl() {
        return wxHeadImgUrl;
    }

    public void setWxHeadImgUrl(String wxHeadImgUrl) {
        this.wxHeadImgUrl = wxHeadImgUrl;
    }
}
