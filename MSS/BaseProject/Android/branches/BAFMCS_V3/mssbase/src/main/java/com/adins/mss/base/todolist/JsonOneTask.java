package com.adins.mss.base.todolist;

import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class JsonOneTask extends MssResponseType {
    @SerializedName("taskH")
    private TaskH taskH;

    public TaskH getOneTaskH() {
        return taskH;
    }

    public void setOneTaskH(TaskH OneTaskH) {
        this.taskH = OneTaskH;
    }
}
