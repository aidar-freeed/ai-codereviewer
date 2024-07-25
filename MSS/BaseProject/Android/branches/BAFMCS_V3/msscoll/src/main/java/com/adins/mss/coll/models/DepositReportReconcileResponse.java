package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dian.ina on 02/09/2015.
 */
public class DepositReportReconcileResponse extends MssResponseType {
    @SerializedName("taskId")
    private List<String> taskId;

    public List<String> getTaskId() {
        return taskId;
    }

    public void setTaskId(List<String> taskId) {
        this.taskId = taskId;
    }
}
