package com.adins.mss.coll.closingtask.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public class ClosingTaskRequest extends MssRequestType {
    public transient static final String CLOSING_TASK_LIST = "0";
    public transient static final String CLOSING_TASK = "1";

    @SerializedName("flag")
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
