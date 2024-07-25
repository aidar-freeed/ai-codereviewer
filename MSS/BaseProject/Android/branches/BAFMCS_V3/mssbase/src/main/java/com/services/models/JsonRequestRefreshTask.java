package com.services.models;

import com.adins.mss.base.GlobalData;
import com.adins.mss.foundation.http.MssRequestType;

public class JsonRequestRefreshTask extends MssRequestType {
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
