package com.adins.mss.coll.models;

import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adityapurwa on 22/04/15.
 */
public class ReportSummaryResponse extends MssResponseType {
    @SerializedName("current_date")
    private String current_date;
    @SerializedName("collector")
    private String collector;
    @SerializedName("total_to_be_paid")
    private Double total_to_be_paid;
    @SerializedName("total_received")
    private Double total_received;
    @SerializedName("listTask")
    private List<ReportSummaryResponse> listTask;
    @SerializedName("agreementNo")
    private String agreementNo;
    @SerializedName("statusTask")
    private String statusTask;
    @SerializedName("amountPaid")
    private String amountPaid;
    @SerializedName("ptp")
    private String ptp;
    @SerializedName("note")
    private String note;
    @SerializedName("customerName")
    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public Double getTotal_to_be_paid() {
        return total_to_be_paid;
    }

    public void setTotal_to_be_paid(Double total_to_be_paid) {
        this.total_to_be_paid = total_to_be_paid;
    }

    public Double getTotal_received() {
        return total_received;
    }

    public void setTotal_received(Double total_received) {
        this.total_received = total_received;
    }

    public List<ReportSummaryResponse> getList_task(){
        return this.listTask;
    }

    public void setList_task(List<ReportSummaryResponse> listTask){
        this.listTask = listTask;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPtp() {
        return ptp;
    }

    public void setPtp(String ptp) {
        this.ptp = ptp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
