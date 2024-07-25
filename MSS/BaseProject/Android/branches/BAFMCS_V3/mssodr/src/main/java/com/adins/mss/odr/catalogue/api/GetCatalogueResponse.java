package com.adins.mss.odr.catalogue.api;

import com.adins.mss.dao.Catalogue;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by olivia.dg on 11/30/2017.
 */

public class GetCatalogueResponse extends MssResponseType {
    @SerializedName("listProductCatalog")
    private List<Catalogue> listProductCatalogue;

    public List<Catalogue> getListProductCatalogue() {
        return listProductCatalogue;
    }

    public void setListProductCatalogue(List<Catalogue> listProductCatalogue) {
        this.listProductCatalogue = listProductCatalogue;
    }
}
