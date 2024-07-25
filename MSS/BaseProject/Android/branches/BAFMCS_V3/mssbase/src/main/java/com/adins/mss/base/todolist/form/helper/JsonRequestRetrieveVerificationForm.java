package com.adins.mss.base.todolist.form.helper;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Kamil on 10/18/17.
 */

public class JsonRequestRetrieveVerificationForm extends MssRequestType {
    @SerializedName("uuid_task_h")
    String uuidTaskH;

    public String getUuidTaskH() {
        return uuidTaskH;
    }

    public void setUuidTaskH(String uuidTaskH) {
        this.uuidTaskH = uuidTaskH;
    }
}
