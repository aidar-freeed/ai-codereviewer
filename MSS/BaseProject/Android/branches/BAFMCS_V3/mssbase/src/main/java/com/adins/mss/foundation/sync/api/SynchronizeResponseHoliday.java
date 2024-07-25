package com.adins.mss.foundation.sync.api;

import com.adins.mss.dao.Holiday;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SynchronizeResponseHoliday extends MssResponseType {
    @SerializedName("listSync")
    private List<Holiday> listSync;

    public List<Holiday> getListSync() {
        return listSync;
    }

    public void setListSync(List<Holiday> listSync) {
        this.listSync = listSync;
    }
}
