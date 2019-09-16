package com.bupocket.model;

import java.util.List;

public class VersionLogModel {

    /**
     * logList : [{"createTime":"1550824464000","englishVerContents":"1.Optimized the entry to back up mnemonics for the identity wallet.\n2.Added an address book to ease the selection of a destination address to transfer.","verContents":"1.优化身份钱包助记词备份入口\n2.增加地址簿功能，方便选择转账目标地址","verNumber":"1.5.2"},{"createTime":"1547716012000","englishVerContents":"1. Add functions that include importing mnemonics, Keystore, and private key.\n2. Add functions that include exporting Keystore and private key.\n3. Add Wallet Management.\n4. Optimize the UI of transaction history.","verContents":"1. 增加导入助记词、Keystore、私钥功能\n2. 增加导出Keystore、私钥功能\n3. 增加钱包管理功能\n4. 优化交易历史页面UI","verNumber":"1.5.0"},{"createTime":"1546915213000","englishVerContents":"1.Fuzzy matching is supported when you search for tokens.\n2.Optimized the web UI of searching for tokens.","verContents":"1.搜索资产时，支持模糊匹配资产。\n2.优化了搜索资产页面的UI。","verNumber":"1.4.2"},{"createTime":"1546860550000","englishVerContents":"1.Fuzzy matching is supported when you search for tokens.\n2.Optimized the web UI of searching for tokens.","verContents":"1.搜索资产时，支持模糊匹配资产。\n2.优化了搜索资产页面的UI。","verNumber":"1.4.2"},{"createTime":"1537413064000","englishVerContents":"1，添加删除信用卡接口\n 2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成","verContents":"1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制\n4，添加放弃任务接口，小时内不生成\n5，消费任务手动生成","verNumber":"1.0.0"}]
     * page : {"count":1,"curSize":5,"endOfGroup":1,"firstResultNumber":0,"nextFlag":false,"queryTotal":true,"size":10,"start":1,"startOfGroup":1,"total":5}
     */

    private PageBean page;
    private List<LogListModel> logList;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<LogListModel> getLogList() {
        return logList;
    }

    public void setLogList(List<LogListModel> logList) {
        this.logList = logList;
    }

    public static class PageBean {
        /**
         * count : 1
         * curSize : 5
         * endOfGroup : 1
         * firstResultNumber : 0
         * nextFlag : false
         * queryTotal : true
         * size : 10
         * start : 1
         * startOfGroup : 1
         * total : 5
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
