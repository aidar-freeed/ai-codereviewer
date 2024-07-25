package com.adins.mss.base.todolist.form.followup;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class SubmitFormRequest extends MssRequestType {
    @SerializedName("uuidTaskH")
    private String uuidTaskH;

    @SerializedName("followUpNotes")
    private String followUpNotes;

    @SerializedName("flagtask")
    private String flagTask;

    public String getUuidTaskH() {
        return uuidTaskH;
    }

    public void setUuidTaskH(String uuidTaskH) {
        this.uuidTaskH = uuidTaskH;
    }

    public String getFollowUpNotes() {
        return followUpNotes;
    }

    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }

    public String getFlagTask() {
        return flagTask;
    }

    public void setFlagTask(String flagTask) {
        this.flagTask = flagTask;
    }
}
