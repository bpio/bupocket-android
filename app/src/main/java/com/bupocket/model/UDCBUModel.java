package com.bupocket.model;

import java.io.PipedReader;

public class UDCBUModel {


    private String dest_address;

    private String  amount;

    private String tx_fee;

    private String input;

    public String getDest_address() {
        return dest_address;
    }

    public void setDest_address(String dest_address) {
        this.dest_address = dest_address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTx_fee() {
        return tx_fee;
    }

    public void setTx_fee(String tx_fee) {
        this.tx_fee = tx_fee;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
