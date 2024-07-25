package com.adins.mss.foundation.sync.api;

import com.adins.mss.dao.User;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SynchronizaResponseCompetition extends MssResponseType {
    @SerializedName("listSync")
    private List<BeanResp> listSync;

    public List<BeanResp> getListSync() {
        return listSync;
    }

    public void setListSync(List<BeanResp> listSync) {
        this.listSync = listSync;
    }
}
