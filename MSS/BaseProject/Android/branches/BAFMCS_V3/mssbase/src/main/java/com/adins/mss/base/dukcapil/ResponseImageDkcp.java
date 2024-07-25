package com.adins.mss.base.dukcapil;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class ResponseImageDkcp extends MssResponseType {
    @SerializedName("dataDkcp")
    private ImageDkcpBean dataDkcp;

    public ImageDkcpBean getDataDkcp() {
        return dataDkcp;
    }

    public void setDataDkcp(ImageDkcpBean dataDkcp) {
        this.dataDkcp = dataDkcp;
    }
}
