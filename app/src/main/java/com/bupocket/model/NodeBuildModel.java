package com.bupocket.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class NodeBuildModel implements Serializable {

    private String name;

    protected NodeBuildModel(Parcel in) {
        name = in.readString();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public NodeBuildModel() {
    }

}
