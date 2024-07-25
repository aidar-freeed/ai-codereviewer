package com.services;

import com.adins.mss.base.GlobalData;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestRefreshTask extends MssRequestType {
    @SerializedName("dtm_crt")
    private long dtm_crt;

    public JsonRequestRefreshTask(long dtm_crt) {
        setAudit(GlobalData.getSharedGlobalData().getAuditData());
        setDtm_crt(dtm_crt);
    }

    public long getDtm_crt() {
        return dtm_crt;
    }

    public void setDtm_crt(long dtm_crt) {
        this.dtm_crt = dtm_crt;
    }
}
