package com.adins.mss.foundation.print.rv.syncs;

import com.adins.mss.foundation.http.MssRequestType;

import java.util.Date;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncRVRequest extends MssRequestType {
    private Date lastDtmCrt;

    public SyncRVRequest() {

    }

    public Date getLastDtmCrt() {
        return lastDtmCrt;
    }

    public void setLastDtmCrt(Date lastDtmCrt) {
        this.lastDtmCrt = lastDtmCrt;
    }
}
