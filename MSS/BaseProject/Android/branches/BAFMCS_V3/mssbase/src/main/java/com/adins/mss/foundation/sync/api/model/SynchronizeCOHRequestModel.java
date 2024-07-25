package com.adins.mss.foundation.sync.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gigin.ginanjar on 29/09/2016.
 */

public class SynchronizeCOHRequestModel extends SynchronizeRequestModel {
    @SerializedName("cashOnHand")
    private String cashOnHand;
    @SerializedName("cashLimit")
    private String cashLimit;
    @SerializedName("login_ID")
    private String login_ID;

    public SynchronizeCOHRequestModel() {
    }

    public SynchronizeCOHRequestModel(SynchronizeRequestModel requestModel) {
        super(requestModel);
    }

    public String getCashOnHand() {
        return cashOnHand;
    }

    public void setCashOnHand(String cashOnHand) {
        this.cashOnHand = cashOnHand;
    }

    public String getCashLimit() {
        return cashLimit;
    }

    public void setCashLimit(String cashLimit) {
        this.cashLimit = cashLimit;
    }

    public String getLogin_ID() {
        return login_ID;
    }

    public void setLogin_ID(String login_ID) {
        this.login_ID = login_ID;
    }
}
