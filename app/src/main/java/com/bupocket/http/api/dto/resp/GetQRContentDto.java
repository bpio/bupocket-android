package com.bupocket.http.api.dto.resp;

import com.google.gson.Gson;

public class GetQRContentDto {

    /**
     * type : 1
     * qrcodeSessionId : 5b3b1ccdf5e64cd590f24f3180ede161
     * destAddress : buQBvtJXGgdUdZka19ABSKpxfAUqUpyS2bZU
     * amount : 50000
     * script :
     * qrRemark :
     */

    private String type;
    private String qrcodeSessionId;
    private String destAddress;
    private String accountTag;
    private String amount;
    private String script;
    private String qrRemark;

    public static GetQRContentDto objectFromData(String str) {

        return new Gson().fromJson(str, GetQRContentDto.class);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQrcodeSessionId() {
        return qrcodeSessionId;
    }

    public void setQrcodeSessionId(String qrcodeSessionId) {
        this.qrcodeSessionId = qrcodeSessionId;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getQrRemark() {
        return qrRemark;
    }

    public void setQrRemark(String qrRemark) {
        this.qrRemark = qrRemark;
    }

    public String getAccountTag() {
        return accountTag;
    }

    public void setAccountTag(String accountTag) {
        this.accountTag = accountTag;
    }
}
