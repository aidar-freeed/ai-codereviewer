package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class EmergencyRequest extends MssRequestType {
    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("dtm_emergency")
    private Date dtm_emergency;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getDtm_emergency() {
        return dtm_emergency;
    }

    public void setDtm_emergency(Date dtm_emergency) {
        this.dtm_emergency = dtm_emergency;
    }
}
