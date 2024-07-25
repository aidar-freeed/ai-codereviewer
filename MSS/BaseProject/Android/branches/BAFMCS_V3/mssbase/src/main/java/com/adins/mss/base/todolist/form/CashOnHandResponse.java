package com.adins.mss.base.todolist.form;

import com.adins.mss.foundation.http.MssResponseType;

import java.util.List;

/**
 * Created by gigin.ginanjar on 29/09/2016.
 */

public class CashOnHandResponse extends MssResponseType {
    private String userId;
    private String cashLimit;
    private String cashOnHand;
    private List batchIdList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCashLimit() {
        return cashLimit;
    }

    public void setCashLimit(String cashLimit) {
        this.cashLimit = cashLimit;
    }

    public String getCashOnHand() {
        return cashOnHand;
    }

    public void setCashOnHand(String cashOnHand) {
        this.cashOnHand = cashOnHand;
    }

    public List getBatchIdList() {
        return batchIdList;
    }

    public void setBatchIdList(List batchIdList) {
        this.batchIdList = batchIdList;
    }
}
