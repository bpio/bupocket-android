package com.bupocket.http.api;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public  class AddressBookListBean {

    private String nickName;
    @Id
    private String linkmanAddress;
    private String remark;

    @Generated(hash = 16169808)
    public AddressBookListBean(String nickName, String linkmanAddress,
            String remark) {
        this.nickName = nickName;
        this.linkmanAddress = linkmanAddress;
        this.remark = remark;
    }

    @Generated(hash = 1884168891)
    public AddressBookListBean() {
    }

    public static AddressBookListBean objectFromData(String str) {

        return new Gson().fromJson(str, AddressBookListBean.class);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLinkmanAddress() {
        return linkmanAddress;
    }

    public void setLinkmanAddress(String linkmanAddress) {
        this.linkmanAddress = linkmanAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
