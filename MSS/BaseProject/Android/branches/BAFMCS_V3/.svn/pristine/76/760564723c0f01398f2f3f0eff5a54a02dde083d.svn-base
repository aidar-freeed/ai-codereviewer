package com.adins.mss.base.checkin;

import com.adins.mss.dao.LocationInfo;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonRequestAbsensi extends MssRequestType {
    @SerializedName("locationInfo")
    List<LocationInfo> locationInfo;
    @SerializedName("attd_address")
    String attd_address;

    public String getAttd_address() {
        return this.attd_address;
    }

    public void setAttd_address(String value) {
        this.attd_address = value;
    }

    public List<LocationInfo> getLocationInfo() {
        return this.locationInfo;
    }

    public void setLocationInfo(List<LocationInfo> value) {
        this.locationInfo = value;
    }
}
