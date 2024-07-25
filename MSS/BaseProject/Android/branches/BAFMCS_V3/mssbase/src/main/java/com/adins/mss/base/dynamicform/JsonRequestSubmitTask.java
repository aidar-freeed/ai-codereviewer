package com.adins.mss.base.dynamicform;

import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonRequestSubmitTask extends MssRequestType {
    /**
     * Property taskH
     */
    @SerializedName("taskH")
    TaskH taskH;

    /**
     * Property taskD
     */
    @SerializedName("taskD")
    List<TaskD> taskD;

    @SerializedName("uuidTaskHNext")
    String uuidTaskHNext;

    /**
     * Gets the taskH
     */
    public TaskH getTaskH() {
        return this.taskH;
    }

    /**
     * Sets the taskH
     */
    public void setTaskH(TaskH value) {
        this.taskH = value;
    }

    /**
     * Gets the taskD
     */
    public List<TaskD> getTaskD() {
        return this.taskD;
    }

    /**
     * Sets the taskD
     */
    public void setTaskD(List<TaskD> value) {
        this.taskD = value;
    }

    public String getUuidTaskHNext() {
        return uuidTaskHNext;
    }

    public void setUuidTaskHNext(String uuidTaskHNext) {
        this.uuidTaskHNext = uuidTaskHNext;
    }
}
