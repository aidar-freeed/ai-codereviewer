package com.services.models;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class JsonResponseLastSync extends MssResponseType {
    @SerializedName("dtm_lastsync")
    private String dtm_lastSync;

    public String getDtm_lastSync() {
        return dtm_lastSync;
    }

    public void setDtm_lastSync(String dtm_lastSync) {
        this.dtm_lastSync = dtm_lastSync;
    }
}
