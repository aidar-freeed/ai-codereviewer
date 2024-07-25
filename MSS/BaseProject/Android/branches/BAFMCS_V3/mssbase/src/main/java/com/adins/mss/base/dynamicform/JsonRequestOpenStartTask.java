package com.adins.mss.base.dynamicform;

import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestOpenStartTask extends MssRequestType {
    @SerializedName("taskH")
    TaskH taskH;

    public TaskH getTaskH() {
        return this.taskH;
    }

    public void setTaskH(TaskH value) {
        this.taskH = value;
    }
}
