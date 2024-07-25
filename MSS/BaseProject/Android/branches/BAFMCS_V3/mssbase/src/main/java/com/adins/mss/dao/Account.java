package com.adins.mss.dao;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MS_ACCOUNT".
 */
public class Account {

    /** Not-null value. */
     @SerializedName("uuid_account")
    private String uuid_account;
     @SerializedName("account_name")
    private String account_name;
     @SerializedName("account_address")
    private String account_address;
     @SerializedName("account_phone_1")
    private String account_phone_1;
     @SerializedName("account_phone_2")
    private String account_phone_2;
     @SerializedName("account_latitude")
    private String account_latitude;
     @SerializedName("account_longitude")
    private String account_longitude;
     @SerializedName("usr_crt")
    private String usr_crt;
     @SerializedName("dtm_crt")
    private java.util.Date dtm_crt;

    public Account() {
    }

    public Account(String uuid_account) {
        this.uuid_account = uuid_account;
    }

    public Account(String uuid_account, String account_name, String account_address, String account_phone_1, String account_phone_2, String account_latitude, String account_longitude, String usr_crt, java.util.Date dtm_crt) {
        this.uuid_account = uuid_account;
        this.account_name = account_name;
        this.account_address = account_address;
        this.account_phone_1 = account_phone_1;
        this.account_phone_2 = account_phone_2;
        this.account_latitude = account_latitude;
        this.account_longitude = account_longitude;
        this.usr_crt = usr_crt;
        this.dtm_crt = dtm_crt;
    }

    /** Not-null value. */
    public String getUuid_account() {
        return uuid_account;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid_account(String uuid_account) {
        this.uuid_account = uuid_account;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getAccount_address() {
        return account_address;
    }

    public void setAccount_address(String account_address) {
        this.account_address = account_address;
    }

    public String getAccount_phone_1() {
        return account_phone_1;
    }

    public void setAccount_phone_1(String account_phone_1) {
        this.account_phone_1 = account_phone_1;
    }

    public String getAccount_phone_2() {
        return account_phone_2;
    }

    public void setAccount_phone_2(String account_phone_2) {
        this.account_phone_2 = account_phone_2;
    }

    public String getAccount_latitude() {
        return account_latitude;
    }

    public void setAccount_latitude(String account_latitude) {
        this.account_latitude = account_latitude;
    }

    public String getAccount_longitude() {
        return account_longitude;
    }

    public void setAccount_longitude(String account_longitude) {
        this.account_longitude = account_longitude;
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

}
