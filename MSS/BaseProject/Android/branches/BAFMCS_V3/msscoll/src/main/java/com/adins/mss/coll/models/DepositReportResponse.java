package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eric.sn on 6/2/2017.
 */

public class DepositReportResponse extends MssResponseType {

    @SerializedName("batch_id")
    private String batchId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
