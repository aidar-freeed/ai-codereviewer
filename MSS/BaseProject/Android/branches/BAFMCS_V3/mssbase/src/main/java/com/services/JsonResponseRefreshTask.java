package com.services;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class JsonResponseRefreshTask extends MssResponseType {
    @SerializedName("newTask")
    private int newTask;

    public int getNewTask() {
        return newTask;
    }

    public void setNewTask(int newTask) {
        this.newTask = newTask;
    }

}
