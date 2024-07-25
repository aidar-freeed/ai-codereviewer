package com.adins.mss.foundation.print.paymentchannel;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncChennelRequest extends MssRequestType {
    @SerializedName("lastDtmUpd")
    private Date lastDtmUpd;

    public SyncChennelRequest() {

    }

    public Date getLastDtmUpd() {
        return lastDtmUpd;
    }

    public void setLastDtmUpd(Date lastDtmUpd) {
        this.lastDtmUpd = lastDtmUpd;
    }
}
