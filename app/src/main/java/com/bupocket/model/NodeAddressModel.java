package com.bupocket.model;

public class NodeAddressModel {


    /**
     * error_code : 0
     * result : {"header":{"account_tree_hash":"6bb6fa35c10e7a141f2375d17af467d0877a1cb97b810a2b3a3d0173f3e9f8fa","close_time":1561550080554742,"consensus_value_hash":"63ef7c145ab0de45768130b4665035486a51e6eff97230545d2d2a68ded80b61","fees_hash":"20c5b9453723e21617eddd0432e737a915468ddb61fb6fcfe0a051ba219330ec","hash":"6ca72c19f9fe7b5060812a3f70089c7d924e7e1266496eaea3e3534a4b65b60a","previous_hash":"74fc4e8b3d006b38ba214bc7b9730b66902303dbd746652655e807a6186994ed","seq":548084,"tx_count":997,"validators_hash":"c9eba856082c81844f711af300593c91c7a2c98a9bb43b377941814e7d16ba13","version":1003}}
     */

    private int error_code;
    private ResultBean result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * header : {"account_tree_hash":"6bb6fa35c10e7a141f2375d17af467d0877a1cb97b810a2b3a3d0173f3e9f8fa","close_time":1561550080554742,"consensus_value_hash":"63ef7c145ab0de45768130b4665035486a51e6eff97230545d2d2a68ded80b61","fees_hash":"20c5b9453723e21617eddd0432e737a915468ddb61fb6fcfe0a051ba219330ec","hash":"6ca72c19f9fe7b5060812a3f70089c7d924e7e1266496eaea3e3534a4b65b60a","previous_hash":"74fc4e8b3d006b38ba214bc7b9730b66902303dbd746652655e807a6186994ed","seq":548084,"tx_count":997,"validators_hash":"c9eba856082c81844f711af300593c91c7a2c98a9bb43b377941814e7d16ba13","version":1003}
         */

        private HeaderBean header;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public static class HeaderBean {
            /**
             * account_tree_hash : 6bb6fa35c10e7a141f2375d17af467d0877a1cb97b810a2b3a3d0173f3e9f8fa
             * close_time : 1561550080554742
             * consensus_value_hash : 63ef7c145ab0de45768130b4665035486a51e6eff97230545d2d2a68ded80b61
             * fees_hash : 20c5b9453723e21617eddd0432e737a915468ddb61fb6fcfe0a051ba219330ec
             * hash : 6ca72c19f9fe7b5060812a3f70089c7d924e7e1266496eaea3e3534a4b65b60a
             * previous_hash : 74fc4e8b3d006b38ba214bc7b9730b66902303dbd746652655e807a6186994ed
             * seq : 548084
             * tx_count : 997
             * validators_hash : c9eba856082c81844f711af300593c91c7a2c98a9bb43b377941814e7d16ba13
             * version : 1003
             */

            private String account_tree_hash;
            private long close_time;
            private String consensus_value_hash;
            private String fees_hash;
            private String hash;
            private String previous_hash;
            private int seq;
            private int tx_count;
            private String validators_hash;
            private int version;

            public String getAccount_tree_hash() {
                return account_tree_hash;
            }

            public void setAccount_tree_hash(String account_tree_hash) {
                this.account_tree_hash = account_tree_hash;
            }

            public long getClose_time() {
                return close_time;
            }

            public void setClose_time(long close_time) {
                this.close_time = close_time;
            }

            public String getConsensus_value_hash() {
                return consensus_value_hash;
            }

            public void setConsensus_value_hash(String consensus_value_hash) {
                this.consensus_value_hash = consensus_value_hash;
            }

            public String getFees_hash() {
                return fees_hash;
            }

            public void setFees_hash(String fees_hash) {
                this.fees_hash = fees_hash;
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }

            public String getPrevious_hash() {
                return previous_hash;
            }

            public void setPrevious_hash(String previous_hash) {
                this.previous_hash = previous_hash;
            }

            public int getSeq() {
                return seq;
            }

            public void setSeq(int seq) {
                this.seq = seq;
            }

            public int getTx_count() {
                return tx_count;
            }

            public void setTx_count(int tx_count) {
                this.tx_count = tx_count;
            }

            public String getValidators_hash() {
                return validators_hash;
            }

            public void setValidators_hash(String validators_hash) {
                this.validators_hash = validators_hash;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }
        }
    }
}
