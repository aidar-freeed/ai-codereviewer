package com.adins.mss.base.depositreport;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by angga.permadi on 8/30/2016.
 */
public class DetailTaskHRequest extends MssRequestType {

    @SerializedName("uuid_task_h")
    private String uuidTaskH;
    @SerializedName("flag")
    private String flag;

    public String getUuidTaskH() {
        return uuidTaskH;
    }

    public void setUuidTaskH(String uuidTaskH) {
        this.uuidTaskH = uuidTaskH;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
