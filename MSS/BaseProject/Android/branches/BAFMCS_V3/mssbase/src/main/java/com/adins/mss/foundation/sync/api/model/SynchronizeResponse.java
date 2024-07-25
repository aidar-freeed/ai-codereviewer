package com.adins.mss.foundation.sync.api.model;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adityapurwa on 11/03/15.
 */
public class SynchronizeResponse<T> extends MssResponseType {
    @SerializedName("listSync")
    private List<T> listSync;

    public List<T> getListSync() {
        return listSync;
    }

    public void setListSync(List<T> listSync) {
        this.listSync = listSync;
    }
}
