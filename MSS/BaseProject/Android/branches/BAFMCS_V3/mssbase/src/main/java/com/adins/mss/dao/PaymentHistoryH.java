package com.adins.mss.dao;

import com.adins.mss.base.util.ExcludeFromGson;
import com.google.gson.annotations.SerializedName;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TR_PAYMENTHISTORY_H".
 */
public class PaymentHistoryH {

    /** Not-null value. */
     @SerializedName("uuid_payment_history_h")
    private String uuid_payment_history_h;
     @SerializedName("uuid_task_h")
    private String uuid_task_h;
     @SerializedName("agreement_no")
    private String agreement_no;
     @SerializedName("branch_code")
    private String branch_code;
     @SerializedName("value_date")
    private java.util.Date value_date;
     @SerializedName("payment_amount")
    private String payment_amount;
     @SerializedName("installment_amount")
    private String installment_amount;
     @SerializedName("installment_number")
    private String installment_number;
     @SerializedName("transaction_type")
    private String transaction_type;
     @SerializedName("wop_code")
    private String wop_code;
     @SerializedName("receipt_no")
    private String receipt_no;
     @SerializedName("post_date")
    private java.util.Date post_date;
     @SerializedName("dtm_upd")
    private java.util.Date dtm_upd;
     @SerializedName("usr_upd")
    private String usr_upd;
     @SerializedName("dtm_crt")
    private java.util.Date dtm_crt;
     @SerializedName("usr_crt")
    private String usr_crt;

    public PaymentHistoryH() {
    }

    public PaymentHistoryH(String uuid_payment_history_h) {
        this.uuid_payment_history_h = uuid_payment_history_h;
    }

    public PaymentHistoryH(String uuid_payment_history_h, String uuid_task_h, String agreement_no, String branch_code, java.util.Date value_date, String payment_amount, String installment_amount, String installment_number, String transaction_type, String wop_code, String receipt_no, java.util.Date post_date, java.util.Date dtm_upd, String usr_upd, java.util.Date dtm_crt, String usr_crt) {
        this.uuid_payment_history_h = uuid_payment_history_h;
        this.uuid_task_h = uuid_task_h;
        this.agreement_no = agreement_no;
        this.branch_code = branch_code;
        this.value_date = value_date;
        this.payment_amount = payment_amount;
        this.installment_amount = installment_amount;
        this.installment_number = installment_number;
        this.transaction_type = transaction_type;
        this.wop_code = wop_code;
        this.receipt_no = receipt_no;
        this.post_date = post_date;
        this.dtm_upd = dtm_upd;
        this.usr_upd = usr_upd;
        this.dtm_crt = dtm_crt;
        this.usr_crt = usr_crt;
    }

    /** Not-null value. */
    public String getUuid_payment_history_h() {
        return uuid_payment_history_h;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid_payment_history_h(String uuid_payment_history_h) {
        this.uuid_payment_history_h = uuid_payment_history_h;
    }

    public String getUuid_task_h() {
        return uuid_task_h;
    }

    public void setUuid_task_h(String uuid_task_h) {
        this.uuid_task_h = uuid_task_h;
    }

    public String getAgreement_no() {
        return agreement_no;
    }

    public void setAgreement_no(String agreement_no) {
        this.agreement_no = agreement_no;
    }

    public String getBranch_code() {
        return branch_code;
    }

    public void setBranch_code(String branch_code) {
        this.branch_code = branch_code;
    }

    public java.util.Date getValue_date() {
        return value_date;
    }

    public void setValue_date(java.util.Date value_date) {
        this.value_date = value_date;
    }

    public String getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getInstallment_amount() {
        return installment_amount;
    }

    public void setInstallment_amount(String installment_amount) {
        this.installment_amount = installment_amount;
    }

    public String getInstallment_number() {
        return installment_number;
    }

    public void setInstallment_number(String installment_number) {
        this.installment_number = installment_number;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getWop_code() {
        return wop_code;
    }

    public void setWop_code(String wop_code) {
        this.wop_code = wop_code;
    }

    public String getReceipt_no() {
        return receipt_no;
    }

    public void setReceipt_no(String receipt_no) {
        this.receipt_no = receipt_no;
    }

    public java.util.Date getPost_date() {
        return post_date;
    }

    public void setPost_date(java.util.Date post_date) {
        this.post_date = post_date;
    }

    public java.util.Date getDtm_upd() {
        return dtm_upd;
    }

    public void setDtm_upd(java.util.Date dtm_upd) {
        this.dtm_upd = dtm_upd;
    }

    public String getUsr_upd() {
        return usr_upd;
    }

    public void setUsr_upd(String usr_upd) {
        this.usr_upd = usr_upd;
    }

    public java.util.Date getDtm_crt() {
        return dtm_crt;
    }

    public void setDtm_crt(java.util.Date dtm_crt) {
        this.dtm_crt = dtm_crt;
    }

    public String getUsr_crt() {
        return usr_crt;
    }

    public void setUsr_crt(String usr_crt) {
        this.usr_crt = usr_crt;
    }

}
