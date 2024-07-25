package com.adins.mss.coll.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.models.CollectionActivityRequest;
import com.adins.mss.coll.models.CollectionActivityResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;

/**
 * Created by dian.ina on 08/05/2015.
 */
public class CollectionActivityApi {
    private final Context context;

    public CollectionActivityApi(Context context)
    {
        this.context = context;
    }

    public CollectionActivityResponse request(String taskId) throws IOException {
        CollectionActivityRequest request = new CollectionActivityRequest();
        request.setTaskId(taskId);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String requestJson = GsonHelper.toJson(request);

        /*HttpClient client = new HttpClient(context);
        String url = GlobalData.getSharedGlobalData().getURL_RETRIECECOLLECTIONTASK();
        Request httpRequest = client.request(url)
                .post(RequestBody.create(MediaType.parse("application/json"), requestJson))
                .build();

        Response response = client.execute(httpRequest);*/
        String url = GlobalData.getSharedGlobalData().getURL_RETRIECECOLLECTIONTASK();
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        //Firebase Performance Trace HTTP Request
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
        CollectionActivityResponse collActResp = GsonHelper.fromJson(responseJson, CollectionActivityResponse.class);
//        List<CollectionActivity> collectionActivityList = collActResp.getCollectionHistoryList();
//        if(collectionActivityList!=null&&collectionActivityList.size()>0){
//        	TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
////        	List<CollectionActivity> collectionActivityLocalList = null;
////        	
//        	for(CollectionActivity collectionActivity : collectionActivityList){
//        		String agreementNo = collectionActivity.getAgreement_no();
//        		if(agreementNo!=null &&agreementNo.length()>0){
////        			collectionActivityLocalList = CollectionActivityDataAccess.getAll(context, agreementNo);
//        			CollectionActivityDataAccess.delete(context, collectionActivity.getUuid_task_h());
//        			taskH.setAppl_no(agreementNo);
//        			TaskHDataAccess.addOrReplace(context, taskH);
//        		}
//        		if(collectionActivity.getUuid_collection_activity()==null){
//        			collectionActivity.setUuid_collection_activity(Tool.getUUID());
//        		}
//        		CollectionActivityDataAccess.add(context, collectionActivity);
//        	}
//        }
        
        //return gson.fromJson(responseJson, CollectionActivityResponse.class);
        return collActResp;
    }
}
