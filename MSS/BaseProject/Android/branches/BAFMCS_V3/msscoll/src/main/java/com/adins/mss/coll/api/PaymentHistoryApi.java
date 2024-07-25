package com.adins.mss.coll.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.models.PaymentHistoryRequest;
import com.adins.mss.coll.models.PaymentHistoryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

/**
 * Created by adityapurwa on 20/03/15.
 */
public class PaymentHistoryApi {
    private final Context context;

    public PaymentHistoryApi(Context context) {
        this.context = context;
    }

    public PaymentHistoryResponse request(String taskId) throws Exception {
		boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
		boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
		HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        PaymentHistoryRequest request = new PaymentHistoryRequest();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.setTask_id(taskId);
        String data = GsonHelper.toJson(request);

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_GET_PAYMENTHISTORY(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, data);

        HttpConnectionResult result = httpConn.requestToServer(
        		GlobalData.getSharedGlobalData().getURL_GET_PAYMENTHISTORY(),
                data, Global.DEFAULTCONNECTIONTIMEOUT);
        try {
            Utility.metricStop(networkMetric, result);
        }catch (Exception e){
            e.printStackTrace();
        }

        String jsonResult = result.getResult();
        
        // bong 21 mei 15 - add or replace to local database
  /*      PaymentHistoryResponse paymentHistoryResp = gson.fromJson(jsonResult, PaymentHistoryResponse.class);
        List<PaymentHistoryHBean> paymentHistoryHList = paymentHistoryResp.getPaymentHistoryHList();
        //List<PaymentHistoryH> paymentHistoryHList = paymentHistoryResp.getPaymentHistoryHList();
        //List<PaymentHistoryD> paymentHistoryDList = paymentHistoryResp.getPaymentHistoryDList();
        List<PaymentHistoryH> paymentHistoryHLocalList = null;
        //List<PaymentHistoryD> paymentHistoryDLocalList = null;
        if(paymentHistoryHList!=null){
        	
        	TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        	for(PaymentHistoryHBean paymentHistoryHBean : paymentHistoryHList){
        		String agreementNo = paymentHistoryHBean.getPaymentHistoryH().getAgreement_no();;
        		if(agreementNo!=null && agreementNo.length()>0){        			
        			paymentHistoryHLocalList = PaymentHistoryHDataAccess.getAll(context, agreementNo);
        			//paymentHistoryDLocalList = PaymentHistoryDDataAccess.getAll(context, uuidTaskH);
        			//if(paymentHistoryDLocalList.size()>0){
        			//	PaymentHistoryDDataAccess.delete(context, uuidTaskH);
        			//}
        			taskH.setAppl_no(agreementNo);
        			TaskHDataAccess.addOrReplace(context, taskH);
        			if(paymentHistoryHBean.getPaymentHistoryDList()!=null){
        				if(paymentHistoryHBean.getPaymentHistoryDList().size()>0){
        					for(PaymentHistoryD paymentHistoryD : paymentHistoryHBean.getPaymentHistoryDList()){
        						PaymentHistoryDDataAccess.delete(context, paymentHistoryD);
        					}
        					PaymentHistoryDDataAccess.delete(context, paymentHistoryHBean.getPaymentHistoryH().getUuid_task_h());
        					
        				}
        			}
        			if(paymentHistoryHLocalList!=null){
	        			if(paymentHistoryHLocalList.size()>0){
	        				PaymentHistoryHDataAccess.delete(context, paymentHistoryHBean.getPaymentHistoryH().getUuid_task_h());
	        			}
        			}
        			
        			if(paymentHistoryHBean.getPaymentHistoryH().getUuid_payment_history_h()==null){
        				paymentHistoryHBean.getPaymentHistoryH().setUuid_payment_history_h(Tool.getUUID());
        			}
        			PaymentHistoryHDataAccess.add(context, paymentHistoryHBean.getPaymentHistoryH());
        			
        			if(paymentHistoryHBean.getPaymentHistoryDList()!=null){
        				if(paymentHistoryHBean.getPaymentHistoryDList().size()>0){
        					PaymentHistoryDDataAccess.add(context, paymentHistoryHBean.getPaymentHistoryDList());
        				}
        			}
        		}
        	}
        	
        }*/
        
//        if(paymentHistoryHBean.getPaymentHistoryDList().size()>0){
//        	//String uuidTaskH = "";
//        	//List<PaymentHistoryD> paymentHistoryDLocalList = null;
//        	PaymentHistoryDDataAccess.add(context, paymentHistoryDList);
//        }
        
        return GsonHelper.fromJson(jsonResult, PaymentHistoryResponse.class);


    }

}
