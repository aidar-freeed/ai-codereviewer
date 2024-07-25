package com.adins.mss.base.todolist.form.followup;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

public class TaskListResponse extends MssResponseType {
    @SerializedName("uuidTaskH")
    private String uuidTaskH;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("agreementNo")
    private String agreementNo;

    @SerializedName("flagTask")
    private String flagTask;

    @SerializedName("customerAddress")
    private String customerAddress;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("tglJanjiBayar")
    private String tglJanjiBayar;

    @SerializedName("overdueDays")
    private String overdueDays;

    @SerializedName("installmentNo")
    private String installmentNo;

    @SerializedName("amountDue")
    private String amountDue;

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }


    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTglJanjiBayar() {
        return tglJanjiBayar;
    }

    public void setTglJanjiBayar(String tglJanjiBayar) {
        this.tglJanjiBayar = tglJanjiBayar;
    }

    public String getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(String overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getInstallmentNo() {
        return installmentNo;
    }

    public void setInstallmentNo(String installmentNo) {
        this.installmentNo = installmentNo;
    }

    public String getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(String amountDue) {
        this.amountDue = amountDue;
    }



    public String getUuidTaskH() {
        return uuidTaskH;
    }

    public void setUuidTaskH(String uuidTaskH) {
        this.uuidTaskH = uuidTaskH;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getFlagTask() {
        return flagTask;
    }

    public void setFlagTask(String flagTask) {
        this.flagTask = flagTask;
    }


}
