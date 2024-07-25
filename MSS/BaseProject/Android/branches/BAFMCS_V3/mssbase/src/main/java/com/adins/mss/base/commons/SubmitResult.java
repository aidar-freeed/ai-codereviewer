package com.adins.mss.base.commons;

import com.adins.mss.dao.TaskH;

import java.io.Serializable;

/**
 * Created by shaladin on 9/22/17.
 */

public class SubmitResult implements Serializable {
    private String taskId;
    private String Date;
    private String Size;
    private String Result;
    private String Duration;
    private TaskH taskH;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public TaskH getTaskH() {
        return taskH;
    }

    public void setTaskH(TaskH taskH) {
        this.taskH = taskH;
    }
}
