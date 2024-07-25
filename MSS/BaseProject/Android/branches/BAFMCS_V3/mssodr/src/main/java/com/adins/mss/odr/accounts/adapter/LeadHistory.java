package com.adins.mss.odr.accounts.adapter;

/**
 * Created by muhammad.aap on 11/30/2018.
 */

public class LeadHistory {
    private String leadStatus;
    private String leadProduct;
    private String leadLastDate;

    public LeadHistory(String leadStatus, String leadProduct, String leadLastDate) {
        this.leadStatus = leadStatus;
        this.leadProduct = leadProduct;
        this.leadLastDate = leadLastDate;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getLeadProduct() {
        return leadProduct;
    }

    public void setLeadProduct(String leadProduct) {
        this.leadProduct = leadProduct;
    }

    public String getLeadLastDate() {
        return leadLastDate;
    }

    public void setLeadLastDate(String leadLastDate) {
        this.leadLastDate = leadLastDate;
    }
}
