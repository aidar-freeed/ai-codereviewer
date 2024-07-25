package com.adins.mss.odr.followup.api;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by olivia.dg on 11/24/2017.
 */

public class DoFollowUpResponse extends MssResponseType {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
