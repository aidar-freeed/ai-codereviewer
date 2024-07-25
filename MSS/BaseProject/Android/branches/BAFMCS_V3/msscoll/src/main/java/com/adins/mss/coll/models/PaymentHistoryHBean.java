package com.adins.mss.coll.models;

import com.adins.mss.dao.PaymentHistoryD;
import com.adins.mss.dao.PaymentHistoryH;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentHistoryHBean {
	@SerializedName("paymentHistoryH")
	private PaymentHistoryH paymentHistoryH;
	@SerializedName("paymentHistoryDList")
	private List<PaymentHistoryD> paymentHistoryDList;

	public PaymentHistoryH getPaymentHistoryH() {
		return paymentHistoryH;
	}

	public void setPaymentHistoryH(PaymentHistoryH paymentHistoryH) {
		this.paymentHistoryH = paymentHistoryH;
	}

	public List<PaymentHistoryD> getPaymentHistoryDList() {
		return paymentHistoryDList;
	}

	public void setPaymentHistoryDList(List<PaymentHistoryD> paymentHistoryDList) {
		this.paymentHistoryDList = paymentHistoryDList;
	}
	
	
}
