package com.adins.mss.coll.closingtask.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angga.permadi on 6/6/2016.
 */
public class ClosingTaskEntity {

    @SerializedName("noKontrak")
    private String noKontrak;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("noLkp")
    private String noLkp;

    public ClosingTaskEntity() {

    }

    public String getNoKontrak() {
        return noKontrak;
    }

    public void setNoKontrak(String noKontrak) {
        this.noKontrak = noKontrak;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNoLkp() {
        return noLkp;
    }

    public void setNoLkp(String noLkp) {
        this.noLkp = noLkp;
    }

}
