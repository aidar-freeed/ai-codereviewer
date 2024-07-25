package com.adins.mss.foundation.print.rv;

import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by angga.permadi on 4/20/2016.
 */
public class RVNumberResponse extends MssResponseType {
    @SerializedName("reqCode")
    private int reqCode;
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("errorCode")
    private String errorCode;

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return GsonHelper.toJson(this);
    }
}
