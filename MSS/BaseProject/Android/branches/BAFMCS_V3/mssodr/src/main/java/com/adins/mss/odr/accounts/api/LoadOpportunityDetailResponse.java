package com.adins.mss.odr.accounts.api;

import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by olivia.dg on 11/21/2017.
 */

public class LoadOpportunityDetailResponse extends MssResponseType {
    @SerializedName("listOpporDetail")
    List<TaskH> listOpporDetail;

    public List<TaskH> getListOpporDetail() {
        return listOpporDetail;
    }

    public void setListOpporDetail(List<TaskH> listOpporDetail) {
        this.listOpporDetail = listOpporDetail;
    }
}
