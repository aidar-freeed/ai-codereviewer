package com.adins.mss.base.dukcapil;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class ImageDkcpBean {
    @SerializedName("status")
    String generatedStatus;

    @SerializedName("read")
    HashMap<String, String> read;

    public HashMap<String, String> getValue() {
        return read;
    }

    public void setValue(HashMap<String, String> value) {
        this.read = value;
    }

    public String getGeneratedStatus() {
        return generatedStatus;
    }

    public void setGeneratedStatus(String generatedStatus) {
        this.generatedStatus = generatedStatus;
    }
    public String getValueRead(String key){
        return read.get(key);
    }
}
