package com.adins.mss.odr.followup.api;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by olivia.dg on 11/24/2017.
 */

public class DoFollowUpRequest extends MssRequestType {
    @SerializedName("group_task_id")
    private List<String> groupTaskId;

    public List<String> getGroupTaskId() {
        return groupTaskId;
    }

    public void setGroupTaskId(List<String> groupTaskId) {
        this.groupTaskId = groupTaskId;
    }
}
