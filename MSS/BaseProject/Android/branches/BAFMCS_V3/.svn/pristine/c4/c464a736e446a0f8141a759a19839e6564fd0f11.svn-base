package com.adins.mss.foundation.print.rv.syncs;

import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncRVResponse extends MssResponseType {
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("listRvNumber")
    private List<ReceiptVoucher> listReceiptVoucher;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<ReceiptVoucher> getListReceiptVoucher() {
        return listReceiptVoucher;
    }

    public void setListReceiptVoucher(List<ReceiptVoucher> listReceiptVoucher) {
        this.listReceiptVoucher = listReceiptVoucher;
    }
}
