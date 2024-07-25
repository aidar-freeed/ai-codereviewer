package com.adins.mss.coll.fragments;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class GetLogoRequest extends MssRequestType {

    @SerializedName("MEMBERSHIP_PROGRAM_CODE")
    protected String MEMBERSHIP_PROGRAM_CODE;

    public String getMEMBERSHIP_PROGRAM_CODE() {
        return MEMBERSHIP_PROGRAM_CODE;
    }

    public void setMEMBERSHIP_PROGRAM_CODE(String MEMBERSHIP_PROGRAM_CODE) {
        this.MEMBERSHIP_PROGRAM_CODE = MEMBERSHIP_PROGRAM_CODE;
    }
}
