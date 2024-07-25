package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dian.ina on 02/09/2015.
 */
public class DepositReportReconcileRequest extends MssRequestType {
    @SerializedName("taskId")
    private List<String> taskId;
    @SerializedName("flag")
    private List<String> flag;

    public List<String> getTaskId() {
        return taskId;
    }

    public void setTaskId(List<String> taskId) {
        this.taskId = taskId;
    }

    public List<String> getFlag() {
        return flag;
    }

    public void setFlag(List<String> flag) {
        this.flag = flag;
    }
}
