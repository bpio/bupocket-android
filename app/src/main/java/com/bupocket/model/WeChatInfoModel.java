package com.bupocket.model;

import com.bupocket.http.api.AddressBookListBean;
import com.google.gson.Gson;

import java.util.List;

public class WeChatInfoModel {

    private String nick;
    private String headImgUrl;


    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
