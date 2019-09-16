package com.bupocket.wallet.utils.keystore;


import io.bumo.encryption.crypto.keystore.entity.ScryptParamsEty;

public class BaseKeyStoreWalletEntity {

    private String address;
    private String aesctr_iv;
    private String cypher_text;
    private ScryptParamsEty scrypt_params;
    private int version;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAesctr_iv() {
        return aesctr_iv;
    }

    public void setAesctr_iv(String aesctr_iv) {
        this.aesctr_iv = aesctr_iv;
    }

    public String getCypher_text() {
        return cypher_text;
    }

    public void setCypher_text(String cypher_text) {
        this.cypher_text = cypher_text;
    }

    public ScryptParamsEty getScrypt_params() {
        return scrypt_params;
    }

    public void setScrypt_params(ScryptParamsEty scrypt_params) {
        this.scrypt_params = scrypt_params;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
