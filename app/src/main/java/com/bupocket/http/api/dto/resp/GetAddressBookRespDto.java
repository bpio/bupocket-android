package com.bupocket.http.api.dto.resp;

import com.bupocket.http.api.AddressBookListBean;
import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

public class GetAddressBookRespDto {

    /**
     * page : {"count":1,"curSize":1,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":1}
     * addressBookList : [{"nickName":"张三","linkmanAddress":"buQtL9dwfFj4BWGRsMri7GX9nGv4GdjpvAeN","remark":"备注"}]
     */

    private PageBean page;
    private List<AddressBookListBean> addressBookList;

    public static GetAddressBookRespDto objectFromData(String str) {

        return new Gson().fromJson(str, GetAddressBookRespDto.class);
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<AddressBookListBean> getAddressBookList() {
        return addressBookList;
    }

    public void setAddressBookList(List<AddressBookListBean> addressBookList) {
        this.addressBookList = addressBookList;
    }

    public static class PageBean {
        /**
         * count : 1
         * curSize : 1
         * endOfGroup : 1
         * firstResultNumber : 0
         * nextFlag : false
         * queryTotal : true
         * size : 10
         * start : 1
         * startOfGroup : 1
         * total : 1
         */

        private int count;
        private int curSize;
        private int endOfGroup;
        private int firstResultNumber;
        private boolean nextFlag;
        private boolean queryTotal;
        private int size;
        private int start;
        private int startOfGroup;
        private int total;

        public static PageBean objectFromData(String str) {

            return new Gson().fromJson(str, PageBean.class);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCurSize() {
            return curSize;
        }

        public void setCurSize(int curSize) {
            this.curSize = curSize;
        }

        public int getEndOfGroup() {
            return endOfGroup;
        }

        public void setEndOfGroup(int endOfGroup) {
            this.endOfGroup = endOfGroup;
        }

        public int getFirstResultNumber() {
            return firstResultNumber;
        }

        public void setFirstResultNumber(int firstResultNumber) {
            this.firstResultNumber = firstResultNumber;
        }

        public boolean isNextFlag() {
            return nextFlag;
        }

        public void setNextFlag(boolean nextFlag) {
            this.nextFlag = nextFlag;
        }

        public boolean isQueryTotal() {
            return queryTotal;
        }

        public void setQueryTotal(boolean queryTotal) {
            this.queryTotal = queryTotal;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getStartOfGroup() {
            return startOfGroup;
        }

        public void setStartOfGroup(int startOfGroup) {
            this.startOfGroup = startOfGroup;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }



}
