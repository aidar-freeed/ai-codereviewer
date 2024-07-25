package com.adins.mss.base.todolist;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class RequestOneTaskBean extends MssRequestType {
    @SerializedName("schemeId")
    String schemeId;

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }


}
