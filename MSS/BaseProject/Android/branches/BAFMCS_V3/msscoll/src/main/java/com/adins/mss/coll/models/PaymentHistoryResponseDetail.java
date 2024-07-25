package com.adins.mss.coll.models;

import com.adins.mss.dao.PaymentHistoryD;
import com.adins.mss.foundation.http.MssResponseType;

/**
 * Created by adityapurwa on 20/03/15.
 */
public class PaymentHistoryResponseDetail extends PaymentHistoryD {
    public PaymentHistoryResponseDetail(PaymentHistoryD detail){
    	setDtm_crt(detail.getDtm_crt());
        setDtm_upd(detail.getDtm_upd());
        setOs_amount_od(detail.getOs_amount_od());
        setPayment_allocation_name(detail.getPayment_allocation_name());
        setReceive_amount(detail.getReceive_amount());
        setUsr_crt(detail.getUsr_crt());
        setUsr_upd(detail.getUsr_upd());
        setUuid_task_h(detail.getUuid_task_h());
        setUuid_payment_history_d(detail.getUuid_payment_history_d());        
    }
}
