package com.bupocket.enums;

public enum CustomNodeTypeEnum {


    STOP(0),
    START(1);

    private int serviceType;

    CustomNodeTypeEnum(int serviceType) {
        this.serviceType = serviceType;
    }


    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
}
