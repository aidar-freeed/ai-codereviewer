package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class JsonRequestQuestionSet extends MssRequestType {
    /**
     * Property uuid_scheme
     */
    @SerializedName("uuid_scheme")
    String uuid_scheme;

    @SerializedName("form_version")
    String form_version;

    /**
     * Gets the uuid_scheme
     */
    public String getUuid_scheme() {
        return this.uuid_scheme;
    }

    /**
     * Sets the uuid_scheme
     */
    public void setUuid_scheme(String value) {
        this.uuid_scheme = value;
    }

    public String getForm_version() {
        return form_version;
    }

    public void setForm_version(String form_version) {
        this.form_version = form_version;
    }
}
