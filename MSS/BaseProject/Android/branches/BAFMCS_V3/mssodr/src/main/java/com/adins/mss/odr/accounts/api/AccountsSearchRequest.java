package com.adins.mss.odr.accounts.api;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by olivia.dg on 11/16/2017.
 */

public class AccountsSearchRequest extends MssRequestType {

    @SerializedName("uuid_account")
    private String uuid_account;
    @SerializedName("uuid_product")
    private String uuid_product;
    @SerializedName("uuid_status")
    private String uuid_status;

    public String getUuid_account() {
        return uuid_account;
    }

    public String getUuid_product() {
        return uuid_product;
    }

    public void setUuid_product(String uuid_product) {
        this.uuid_product = uuid_product;
    }

    public String getUuid_status() {
        return uuid_status;
    }

    public void setUuid_status(String uuid_status) {
        this.uuid_status = uuid_status;
    }

    public void setUuid_account(String uuid_account) {
        this.uuid_account = uuid_account;
    }
}
