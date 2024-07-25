package com.adins.mss.base.dynamicform;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by noerhayati.dm on 8/2/2018.
 */

public class JsonResponseDigitalReceipt extends MssResponseType {

    @SerializedName("rvNumberMobile")
    private String rvNumberMobile;

    public String getRvNumberMobile() {
        return rvNumberMobile;
    }

    public void setRvNumberMobile(String rvNumberMobile) {
        this.rvNumberMobile = rvNumberMobile;
    }
}
