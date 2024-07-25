package com.adins.mss.base.todolist.todayplanrepository;

import com.google.gson.annotations.SerializedName;

public class PlanTaskSequence{
    @SerializedName("uuid_task_h")
    private String uuidTaskH;
    @SerializedName("task_seqno")
    private String taskSeqNo;

    public PlanTaskSequence() {
    }

    public PlanTaskSequence(String uuidTaskH, String taskSeqNo) {
        this.uuidTaskH = uuidTaskH;
        this.taskSeqNo = taskSeqNo;
    }

    public String getUuidTaskH() {
        return uuidTaskH;
    }

    public void setUuidTaskH(String uuidTaskH) {
        this.uuidTaskH = uuidTaskH;
    }

    public String getTaskSeqNo() {
        return taskSeqNo;
    }

    public void setTaskSeqNo(String taskSeqNo) {
        this.taskSeqNo = taskSeqNo;
    }
}
