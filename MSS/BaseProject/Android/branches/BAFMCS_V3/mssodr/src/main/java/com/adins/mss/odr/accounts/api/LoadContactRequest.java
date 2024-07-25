package com.adins.mss.odr.accounts.api;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by olivia.dg on 11/17/2017.
 */

public class LoadContactRequest extends MssRequestType {
    @SerializedName("uuid_account")
    private String uuid_account;

    @SerializedName("uuid_product")
    private String uuid_product;

    public String getUuid_account() {
        return uuid_account;
    }

    public void setUuid_account(String uuid_account) {
        this.uuid_account = uuid_account;
    }

    public String getUuid_product() {
        return uuid_product;
    }

    public void setUuid_product(String uuid_product) {
        this.uuid_product = uuid_product;
    }
}
