package com.adins.mss.foundation.print.paymentchannel;

import com.adins.mss.dao.PaymentChannel;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncChannelResponse extends MssResponseType {
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("listPaymentChannel")
    private List<PaymentChannel> listPaymentChannel;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<PaymentChannel> getListPaymentChannel() {
        return listPaymentChannel;
    }

    public void setListPaymentChannelr(List<PaymentChannel> listPaymentChannel) {
        this.listPaymentChannel = listPaymentChannel;
    }

}
