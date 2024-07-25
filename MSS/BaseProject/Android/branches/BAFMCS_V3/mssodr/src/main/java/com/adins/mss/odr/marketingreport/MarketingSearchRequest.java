package com.adins.mss.odr.marketingreport;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class MarketingSearchRequest extends MssRequestType {
    @SerializedName("date1")
    private Date date1;
    @SerializedName("date2")
    private Date date2;
    @SerializedName("month")
    private String month;
    @SerializedName("uuid_user")
    private String uuidUser;

    public String getUuidUser() {
        return uuidUser;
    }

    public void setUuidUser(String uuidUser) {
        this.uuidUser = uuidUser;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
