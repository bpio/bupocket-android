package com.bupocket.voucher.model;

import java.util.List;

public class VoucherListModel {


    private PageBean page;
    private List<VoucherDetailModel> voucherList;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<VoucherDetailModel> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(List<VoucherDetailModel> voucherList) {
        this.voucherList = voucherList;
    }

    public static class PageBean {
        /**
         * pageSize : 10
         * pageStart : 1
         * pageTotal : 2
         */

        private int pageSize;
        private int pageStart;
        private int pageTotal;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageStart() {
            return pageStart;
        }

        public void setPageStart(int pageStart) {
            this.pageStart = pageStart;
        }

        public int getPageTotal() {
            return pageTotal;
        }

        public void setPageTotal(int pageTotal) {
            this.pageTotal = pageTotal;
        }
    }


}
