package com.adins.mss.coll.models;

import com.adins.mss.dao.ReceiptHistory;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceiptHistoryResponse extends MssResponseType {

    @SerializedName("agreementNo")
    private String agreementNo;
    @SerializedName("listPaymentHistory")
    private List<ReceiptHistory> receiptHistoryList;
    @SerializedName("statusCode")
    private String statusCode;
    @SerializedName("message")
    private String message;

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public List<ReceiptHistory> getReceiptHistoryList() {
        return receiptHistoryList;
    }

    public void setReceiptHistoryList(List<ReceiptHistory> receiptHistoryList) {
        this.receiptHistoryList = receiptHistoryList;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
