package com.bupocket.http.api.dto.resp;

public class TxDetailRespDto {

    private BlockInfoRespBoBean blockInfoRespBo;
    private TxDetailRespBoBean txDeatilRespBo;
    private TxInfoRespBoBean txInfoRespBo;

    public BlockInfoRespBoBean getBlockInfoRespBo() {
        return blockInfoRespBo;
    }

    public void setBlockInfoRespBo(BlockInfoRespBoBean blockInfoRespBo) {
        this.blockInfoRespBo = blockInfoRespBo;
    }

    public TxDetailRespBoBean getTxDeatilRespBo() {
        return txDeatilRespBo;
    }

    public void setTxDeatilRespBo(TxDetailRespBoBean txDeatilRespBo) {
        this.txDeatilRespBo = txDeatilRespBo;
    }

    public TxInfoRespBoBean getTxInfoRespBo() {
        return txInfoRespBo;
    }

    public void setTxInfoRespBo(TxInfoRespBoBean txInfoRespBo) {
        this.txInfoRespBo = txInfoRespBo;
    }

    public static class BlockInfoRespBoBean {
        /**
         * closeTimeDate : 1526544917898299
         * hash : 68100a81ea74276de23ed91f8a07973094ff7bf3b91b9bb9302ac5134be7d586
         * previousHash : fe77b9ddefc2d9071f5bf18f6be1b4aac7933219aa2c3d9a4e97e7277db904f6
         * seq : 153532
         * txCount : 1
         * validatorsHash : 4d1a95bd634df20c179ba152a41a72277392d0a9835460a4db7882da31169b8d
         */

        private String closeTimeDate;
        private String hash;
        private String previousHash;
        private int seq;
        private int txCount;
        private String validatorsHash;

        public String getCloseTimeDate() {
            return closeTimeDate;
        }

        public void setCloseTimeDate(String closeTimeDate) {
            this.closeTimeDate = closeTimeDate;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getPreviousHash() {
            return previousHash;
        }

        public void setPreviousHash(String previousHash) {
            this.previousHash = previousHash;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public int getTxCount() {
            return txCount;
        }

        public void setTxCount(int txCount) {
            this.txCount = txCount;
        }

        public String getValidatorsHash() {
            return validatorsHash;
        }

        public void setValidatorsHash(String validatorsHash) {
            this.validatorsHash = validatorsHash;
        }
    }

    public static class TxDetailRespBoBean {
        /**
         * amount : 1
         * applyTimeDate : 1526544917898299
         * destAddress : buQeZjdQLCoBCwbVYFnMvXByjMDYm9Hwhkgv
         * fee : 0.00246
         * sourceAddress : buQfxCc35fLqX95dAPztyH4aneb8GDx8Sy4i
         */

        private String amount;
        private String applyTimeDate;
        private String destAddress;
        private String fee;
        private String sourceAddress;
        private Integer status;
        private String originalMetadata;
        private String txMetadata;
        private String operaMetadata;
        private String errorMsg;
        private String errorCode;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getApplyTimeDate() {
            return applyTimeDate;
        }

        public void setApplyTimeDate(String applyTimeDate) {
            this.applyTimeDate = applyTimeDate;
        }

        public String getDestAddress() {
            return destAddress;
        }

        public void setDestAddress(String destAddress) {
            this.destAddress = destAddress;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getSourceAddress() {
            return sourceAddress;
        }

        public void setSourceAddress(String sourceAddress) {
            this.sourceAddress = sourceAddress;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getOriginalMetadata() {
            return originalMetadata;
        }

        public void setOriginalMetadata(String originalMetadata) {
            this.originalMetadata = originalMetadata;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getTxMetadata() {
            return txMetadata;
        }

        public void setTxMetadata(String txMetadata) {
            this.txMetadata = txMetadata;
        }

        public String getOperaMetadata() {
            return operaMetadata;
        }

        public void setOperaMetadata(String operaMetadata) {
            this.operaMetadata = operaMetadata;
        }
    }

    public static class TxInfoRespBoBean {
        /**
         * amount : 1
         * destAddress : buQeZjdQLCoBCwbVYFnMvXByjMDYm9Hwhkgv
         * fee : 0.00246
         * hash : ea379540e73f06056ec02a77468bfe2657f9724bbeea8236763d6c1a675ebc14
         * ledgerSeq : 153532
         * nonce : 73
         * signatureStr : [{"publicKey":"b001a80ca91e3d6a08214aedc81a0ed550cd40dda42b99bf9dccd6ee5d59d7a9f69d0fa1756f","signData":"418dd3b1820ef61bbeaf95ca4608739e13b186490b494500a7efbd5bd0ed7d27c99f7e81c565143a60d97455a027a885b466439c1466cdde28b03d90c312480e"}]
         * sourceAddress : buQfxCc35fLqX95dAPztyH4aneb8GDx8Sy4i
         */

        private String amount;
        private String destAddress;
        private String fee;
        private String hash;
        private int ledgerSeq;
        private int nonce;
        private String signatureStr;
        private String sourceAddress;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDestAddress() {
            return destAddress;
        }

        public void setDestAddress(String destAddress) {
            this.destAddress = destAddress;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public int getLedgerSeq() {
            return ledgerSeq;
        }

        public void setLedgerSeq(int ledgerSeq) {
            this.ledgerSeq = ledgerSeq;
        }

        public int getNonce() {
            return nonce;
        }

        public void setNonce(int nonce) {
            this.nonce = nonce;
        }

        public String getSignatureStr() {
            return signatureStr;
        }

        public void setSignatureStr(String signatureStr) {
            this.signatureStr = signatureStr;
        }

        public String getSourceAddress() {
            return sourceAddress;
        }

        public void setSourceAddress(String sourceAddress) {
            this.sourceAddress = sourceAddress;
        }

    }
}
