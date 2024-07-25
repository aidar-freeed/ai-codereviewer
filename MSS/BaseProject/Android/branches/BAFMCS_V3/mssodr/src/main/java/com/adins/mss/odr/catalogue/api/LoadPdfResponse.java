package com.adins.mss.odr.catalogue.api;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by olivia.dg on 11/30/2017.
 */

public class LoadPdfResponse extends MssResponseType {
    @SerializedName("catalogue_file")
    private String cataloguePdf;

    public String getCataloguePdf() {
        return cataloguePdf;
    }

    public void setCataloguePdf(String cataloguePdf) {
        this.cataloguePdf = cataloguePdf;
    }
}
