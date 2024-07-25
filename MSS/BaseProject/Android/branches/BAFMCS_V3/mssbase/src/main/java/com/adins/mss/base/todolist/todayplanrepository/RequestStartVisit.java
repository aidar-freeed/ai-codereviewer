package com.adins.mss.base.todolist.todayplanrepository;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestStartVisit extends MssRequestType {
    @SerializedName("is_start_visit")
    private String isStartVisit;
    @SerializedName("activity_list")
    private List<PlanTaskSequence> activityList;

    public List<PlanTaskSequence> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<PlanTaskSequence> activityList) {
        this.activityList = activityList;
    }

    public String getIsStartVisit() {
        return isStartVisit;
    }

    public void setIsStartVisit(boolean isStartVisit) {
        this.isStartVisit = isStartVisit?"1":"0";
    }
}
