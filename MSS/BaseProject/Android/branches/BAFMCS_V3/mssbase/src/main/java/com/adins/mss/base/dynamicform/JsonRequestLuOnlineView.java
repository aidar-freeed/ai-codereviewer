package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JsonRequestLuOnlineView extends MssRequestType implements Serializable {

    @SerializedName("AgrNo")
    private String agrNo;
    @SerializedName("RefId")
    private String refId;

    public String getAgrNo() {
        return agrNo;
    }

    public void setAgrNo(String agrNo) {
        this.agrNo = agrNo;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

}
