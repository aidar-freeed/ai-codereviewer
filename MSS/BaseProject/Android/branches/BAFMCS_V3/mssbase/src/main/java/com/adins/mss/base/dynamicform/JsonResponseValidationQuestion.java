package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by noerhayati.dm on 8/2/2018.
 */

public class JsonResponseValidationQuestion extends MssResponseType {

    @SerializedName("otp")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
