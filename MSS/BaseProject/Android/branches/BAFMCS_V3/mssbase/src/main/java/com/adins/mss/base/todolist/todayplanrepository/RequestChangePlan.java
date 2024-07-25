package com.adins.mss.base.todolist.todayplanrepository;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class RequestChangePlan extends MssRequestType {
    @SerializedName("new_uuid_task_h")
    private String newUuidTaskHPlan;
    @SerializedName("old_uuid_task_h")
    private String oldUuidTaskHPlan;

    public RequestChangePlan() {
    }

    public RequestChangePlan(String newUuidTaskHPlan, String oldUuidTaskHPlan) {
        this.newUuidTaskHPlan = newUuidTaskHPlan;
        this.oldUuidTaskHPlan = oldUuidTaskHPlan;
    }

    public String getNewUuidTaskHPlan() {
        return newUuidTaskHPlan;
    }

    public void setNewUuidTaskHPlan(String newUuidTaskHPlan) {
        this.newUuidTaskHPlan = newUuidTaskHPlan;
    }

    public String getOldUuidTaskHPlan() {
        return oldUuidTaskHPlan;
    }

    public void setOldUuidTaskHPlan(String oldUuidTaskHPlan) {
        this.oldUuidTaskHPlan = oldUuidTaskHPlan;
    }
}
