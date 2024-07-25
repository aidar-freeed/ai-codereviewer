package com.adins.mss.odr.followup.api;

import com.adins.mss.dao.GroupTask;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by olivia.dg on 11/24/2017.
 */

public class GetFollowUpResponse extends MssResponseType {
    @SerializedName("listFollowUp")
    private List<GroupTask> listFollowUp;

    public List<GroupTask> getListFollowUp() {
        return listFollowUp;
    }

    public void setListFollowUp(List<GroupTask> listFollowUp) {
        this.listFollowUp = listFollowUp;
    }
}
