package com.adins.mss.coll;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class GetLogoKompetisiResponse extends MssResponseType {

    @SerializedName("LOGO")
    protected String LOGO;

    public String getLOGO() {
        return LOGO;
    }

    public void setLOGO(String LOGO) {
        this.LOGO = LOGO;
    }
}
