package com.adins.mss.dao;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MS_PRODUCT".
 */
public class Product {

    /** Not-null value. */
     @SerializedName("uuid_product")
    private String uuid_product;
     @SerializedName("product_code")
    private String product_code;
     @SerializedName("product_name")
    private String product_name;
     @SerializedName("product_desc")
    private String product_desc;
     @SerializedName("product_value")
    private Integer product_value;
     @SerializedName("product_inctv_prctg")
    private Integer product_inctv_prctg;
     @SerializedName("lob_image")
    private byte[] lob_image;
     @SerializedName("usr_crt")
    private String usr_crt;
     @SerializedName("dtm_crt")
    private java.util.Date dtm_crt;
     @SerializedName("is_active")
    private String is_active;
     @SerializedName("brand")
    private String brand;
     @SerializedName("type")
    private String type;
     @SerializedName("model")
    private String model;
     @SerializedName("product_file")
    private String product_file;

    public Product() {
    }

    public Product(String uuid_product) {
        this.uuid_product = uuid_product;
    }

    public Product(String uuid_product, String product_code, String product_name, String product_desc, Integer product_value, Integer product_inctv_prctg, byte[] lob_image, String usr_crt, java.util.Date dtm_crt, String is_active, String brand, String type, String model, String product_file) {
        this.uuid_product = uuid_product;
        this.product_code = product_code;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_value = product_value;
        this.product_inctv_prctg = product_inctv_prctg;
        this.lob_image = lob_image;
        this.usr_crt = usr_crt;
        this.dtm_crt = dtm_crt;
        this.is_active = is_active;
        this.brand = brand;
        this.type = type;
        this.model = model;
        this.product_file = product_file;
    }

    /** Not-null value. */
    public String getUuid_product() {
        return uuid_product;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid_product(String uuid_product) {
        this.uuid_product = uuid_product;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public Integer getProduct_value() {
        return product_value;
    }

    public void setProduct_value(Integer product_value) {
        this.product_value = product_value;
    }

    public Integer getProduct_inctv_prctg() {
        return product_inctv_prctg;
    }

    public void setProduct_inctv_prctg(Integer product_inctv_prctg) {
        this.product_inctv_prctg = product_inctv_prctg;
    }

    public byte[] getLob_image() {
        return lob_image;
    }

    public void setLob_image(byte[] lob_image) {
        this.lob_image = lob_image;
    }

    public String getUsr_crt() {
        return usr_crt;
    }

    public void setUsr_crt(String usr_crt) {
        this.usr_crt = usr_crt;
    }

    public java.util.Date getDtm_crt() {
        return dtm_crt;
    }

    public void setDtm_crt(java.util.Date dtm_crt) {
        this.dtm_crt = dtm_crt;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct_file() {
        return product_file;
    }

    public void setProduct_file(String product_file) {
        this.product_file = product_file;
    }

}
