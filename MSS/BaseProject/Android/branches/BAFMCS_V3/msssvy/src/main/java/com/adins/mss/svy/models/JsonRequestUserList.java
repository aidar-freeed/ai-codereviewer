package com.adins.mss.svy.models;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by olivia.dg on 6/2/2017.
 */

public class JsonRequestUserList extends MssRequestType {
    /** Property uuid_task_h */
    @SerializedName("uuid_task_h")
    public String uuid_task_h;
    @SerializedName("access_mode")
    private String access_mode;

    /**
     * Gets the uuid_task_h
     */
    public String getUuid_task_h() {
        return this.uuid_task_h;
    }

    /**
     * Sets the uuid_task_h
     */
    public void setUuid_task_h(String value) {
        this.uuid_task_h = value;
    }

    public String getAccess_mode() {
        return this.access_mode;
    }

    public void setAccess_mode(String value) {
        this.access_mode = value;
    }
}
