package com.adins.mss.odr.products.api;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

public class ProductDetailViewPdfRequest extends MssRequestType {

    @SerializedName("uuid_product")
    private String uuid_product;

    @SerializedName("dtm_upd")
    private String dtm_upd;

    public String getUuid_product() {
        return uuid_product;
    }

    public void setUuid_product(String uuid_product) {
        this.uuid_product = uuid_product;
    }

    public String getDtm_upd() {
        return dtm_upd;
    }

    public void setDtm_upd(String dtm_upd) {
        this.dtm_upd = dtm_upd;
    }
}
