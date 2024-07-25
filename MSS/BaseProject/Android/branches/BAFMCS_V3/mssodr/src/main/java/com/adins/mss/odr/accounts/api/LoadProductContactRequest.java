package com.adins.mss.odr.accounts.api;

import com.adins.mss.foundation.http.MssRequestType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by olivia.dg on 12/19/2017.
 */

public class LoadProductContactRequest extends MssRequestType {
    @SerializedName("uuid_account")
    private String uuid_account;

    public String getUuid_account() {
        return uuid_account;
    }

    public void setUuid_account(String uuid_account) {
        this.uuid_account = uuid_account;
    }
}
