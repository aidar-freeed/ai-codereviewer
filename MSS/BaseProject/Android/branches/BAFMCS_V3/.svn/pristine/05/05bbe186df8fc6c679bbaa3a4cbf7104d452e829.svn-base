package com.adins.mss.coll.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.models.InstallmentScheduleRequest;
import com.adins.mss.coll.models.InstallmentScheduleResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;

/**
 * Created by adityapurwa on 06/05/15.
 */
public class InstallmentScheduleApi {
    private final Context context;

    public InstallmentScheduleApi(Context context) {
        this.context = context;
    }

    public InstallmentScheduleResponse request(String taskId) throws IOException {
        InstallmentScheduleRequest request = new InstallmentScheduleRequest();
        request.setTaskId(taskId);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String requestJson = GsonHelper.toJson(request);

        //HttpClient client = new HttpClient(context);
        String url = GlobalData.getSharedGlobalData().getURL_GET_INSTALLMENTSCHEDULE();
//        Request httpRequest = client.request(url)
//                .post(RequestBody.create(MediaType.parse("application/json"), requestJson))
//                .build();
//
//        Response response = client.execute(httpRequest);
//        String responseJson = response.body().string();
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, requestJson);

        try {
            serverResult = httpConn.requestToServer(url, requestJson, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        String responseJson ="" ;
        if(serverResult!=null && serverResult.isOK()){
            try {
                responseJson = serverResult.getResult();
            } catch (Exception e) {
                FireCrash.log(e); }
        }
        
        // bong 21 mei 15 - add or replace to local database
        InstallmentScheduleResponse installmentSchedResp = GsonHelper.fromJson(responseJson, InstallmentScheduleResponse.class);
//        if(installmentSchedResp!=null){
//	        List<InstallmentSchedule> installmentScheduleList = installmentSchedResp.getInstallmentScheduleList();
//	        if(installmentScheduleList!=null && installmentScheduleList.size()>0){
//	        	List<InstallmentSchedule> installmentScheduleLocalList = null;
//	        	TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
//	        	for(InstallmentSchedule installmentSchedule : installmentScheduleList){
//	        		String agreementNo = installmentSchedule.getAgreement_no();
//	        		if(agreementNo!=null && agreementNo.length()>0){
//	        			installmentScheduleLocalList = InstallmentScheduleDataAccess.getAll(context, agreementNo);
//	        			InstallmentScheduleDataAccess.delete(context, installmentSchedule.getUuid_task_h());
//	        			taskH.setAppl_no(agreementNo);
//	        			TaskHDataAccess.addOrReplace(context, taskH);
//	        		}
//	        		if(installmentSchedule.getUuid_installment_schedule()==null){
//	        			installmentSchedule.setUuid_installment_schedule(Tool.getUUID());
//	        		}
//	        		InstallmentScheduleDataAccess.add(context, installmentSchedule);
//	        	}
//	        }
//        }
        //return gson.fromJson(responseJson, InstallmentScheduleResponse.class);
        return installmentSchedResp;


    }
}
