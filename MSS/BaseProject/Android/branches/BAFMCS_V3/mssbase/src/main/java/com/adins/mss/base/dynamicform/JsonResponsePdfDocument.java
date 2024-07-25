package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class JsonResponsePdfDocument extends MssResponseType {

    @SerializedName("success")
    private String success;
    @SerializedName("status_code")
    private String statusCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private String data;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
