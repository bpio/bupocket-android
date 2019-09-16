package com.bupocket.http.api.dto.resp;

import com.bupocket.model.BlockInfoRespBoBean;
import com.bupocket.model.TxDetailRespBoBean;
import com.bupocket.model.TxInfoRespBoBean;


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


}
