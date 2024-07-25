package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class GuidelineFaqRequest extends MssRequestType {
    @SerializedName("uuidGuideline")
    String uuidGudeline;

    public String getUuidGudeline() {
        return uuidGudeline;
    }

    public void setUuidGudeline(String uuidGudeline) {
        this.uuidGudeline = uuidGudeline;
    }




}
