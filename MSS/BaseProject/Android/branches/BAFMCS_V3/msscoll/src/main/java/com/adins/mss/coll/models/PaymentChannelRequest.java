package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aditya Purwa on 3/4/2015.
 */
public class PaymentChannelRequest extends MssRequestType {
    @SerializedName("batchID")
    private String batchID;
    @SerializedName("codeChannel")
    private String codeChannel;

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public String getCodeChannel() {
        return codeChannel;
    }

    public void setCodeChannel(String codeChannel) {
        this.codeChannel = codeChannel;
    }

}
