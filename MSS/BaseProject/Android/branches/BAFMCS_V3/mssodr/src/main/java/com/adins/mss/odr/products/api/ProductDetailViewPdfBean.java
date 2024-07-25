package com.adins.mss.odr.products.api;

import com.google.gson.annotations.SerializedName;

public class ProductDetailViewPdfBean {

    @SerializedName("product_file")
    private String product_file;

    public String getProduct_file() {
        return product_file;
    }

    public void setProduct_file(String product_file) {
        this.product_file = product_file;
    }

}
