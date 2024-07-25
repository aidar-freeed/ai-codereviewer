package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dian.ina on 08/05/2015.
 */
public class CollectionActivityRequest extends MssRequestType {
    @SerializedName("taskId")
    public String taskId;

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskId()
    {
        return taskId;
    }
}
