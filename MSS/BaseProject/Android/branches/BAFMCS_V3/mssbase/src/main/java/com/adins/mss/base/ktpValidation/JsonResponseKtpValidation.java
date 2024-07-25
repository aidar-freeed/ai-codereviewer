package com.adins.mss.base.ktpValidation;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class JsonResponseKtpValidation extends MssResponseType {

    @SerializedName("result")
    public String result;
    @SerializedName("message")
    public String message;
    @SerializedName("custName")
    public String custName;
    @SerializedName("custAddr")
    public String custAddr;
    @SerializedName("mapValue")
    public Map<String, Object> mapValue;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddr() {
        return custAddr;
    }

    public void setCustAddr(String custAddr) {
        this.custAddr = custAddr;
    }

    public Map<String, Object> getMapValue() {
        return mapValue;
    }

    public void setMapValue(Map<String, Object> mapValue) {
        this.mapValue = mapValue;
    }
}
