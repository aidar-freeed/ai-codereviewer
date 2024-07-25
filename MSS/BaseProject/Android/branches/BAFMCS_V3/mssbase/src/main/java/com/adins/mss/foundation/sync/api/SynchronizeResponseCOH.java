package com.adins.mss.foundation.sync.api;

import com.adins.mss.dao.User;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SynchronizeResponseCOH extends MssResponseType {
    @SerializedName("listSync")
    private List<User> listSync;

    public List<User> getListSync() {
        return listSync;
    }

    public void setListSync(List<User> listSync) {
        this.listSync = listSync;
    }
}
