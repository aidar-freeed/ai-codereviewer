package com.adins.mss.base.loyalti.mypointdashboard;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

class DetailKompetisiRequest extends MssRequestType {

    @SerializedName("LOGIN_ID")
    protected String LOGIN_ID;

    public String getLOGIN_ID() {
        return LOGIN_ID;
    }

    public void setLOGIN_ID(String LOGIN_ID) {
        this.LOGIN_ID = LOGIN_ID;
    }

//    @SerializedName("MEMBERSHIP_PROGRAM_CODE")
//    protected ArrayList<String> MEMBERSHIP_PROGRAM_CODE;
//
//    public ArrayList<String> getMEMBERSHIP_PROGRAM_CODE() {
//        return MEMBERSHIP_PROGRAM_CODE;
//    }
//
//    public void setMEMBERSHIP_PROGRAM_CODE(ArrayList<String> MEMBERSHIP_PROGRAM_CODE) {
//        this.MEMBERSHIP_PROGRAM_CODE = MEMBERSHIP_PROGRAM_CODE;
//    }
}

