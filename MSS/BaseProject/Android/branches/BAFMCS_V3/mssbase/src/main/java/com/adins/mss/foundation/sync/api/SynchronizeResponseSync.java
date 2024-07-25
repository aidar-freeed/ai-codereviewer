package com.adins.mss.foundation.sync.api;

import com.adins.mss.dao.Sync;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SynchronizeResponseSync extends MssResponseType {
    @SerializedName("listSync")
    private List<Sync> listSync;

    public List<Sync> getListSync() {
        return listSync;
    }

    public void setListSync(List<Sync> listSync) {
        this.listSync = listSync;
    }
}