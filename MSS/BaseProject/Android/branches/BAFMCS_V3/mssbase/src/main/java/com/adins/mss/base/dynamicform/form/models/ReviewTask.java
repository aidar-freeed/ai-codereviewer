package com.adins.mss.base.dynamicform.form.models;

import com.google.gson.annotations.SerializedName;

public class ReviewTask {
    @SerializedName("uuid_form")
    private String uuidForm;
    @SerializedName("nik")
    private String nik;
    @SerializedName("npwp")
    private String npwp;
    @SerializedName("phone")
    private String phone;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("customer_name")
    private String customerName;

    public String getUuidForm() {
        return uuidForm;
    }

    public void setUuidForm(String uuidForm) {
        this.uuidForm = uuidForm;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
