package com.bupocket.model;

public class CallVoucherBalanceModel {


    /**
     * result : {"type":"string","value":"{\"total\":\"4\",\"available\":\"4\"}"}
     */

    private ResultBean result;
    /**
     * total : 4
     * available : 4
     */

    private String total;
    private String available;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public static class ResultBean {
        /**
         * type : string
         * value : {"total":"4","available":"4"}
         */

        private String type;
        private ValueBean value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ValueBean getValue() {
            return value;
        }

        public void setValue(ValueBean value) {
            this.value = value;
        }
    }
    public static class ValueBean {
        /**
         * type : string
         * value : {"total":"4","available":"4"}
         */

        private String total;
        private String available;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getAvailable() {
            return available;
        }

        public void setAvailable(String available) {
            this.available = available;
        }
    }

}
