package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by noerhayati.dm on 8/2/2018.
 */

public class JsonRequestValidationQuestion extends MssRequestType {

    @SerializedName("taskId")
    private String taskId;
    @SerializedName("phoneNumber")
    private String phoneNumber;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
