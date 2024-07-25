package com.adins.mss.coll.models.loyaltymodels;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class GuidelineFaqResponse extends MssResponseType {
    @SerializedName("base64Pdf")
    String base64pdf;

    public String getBase64pdf() {
        return base64pdf;
    }

    public void setBase64pdf(String base64pdf) {
        this.base64pdf = base64pdf;
    }


}
