package com.adins.mss.coll.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.models.DepositReportReconcileRequest;
import com.adins.mss.coll.models.DepositReportReconcileResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;
import java.util.List;

/**
 * Created by dian.ina on 02/09/2015.
 */
public class DepositReportReconcileApi {
    private final Context context;

    public DepositReportReconcileApi(Context context) {
        this.context = context;
    }

    public DepositReportReconcileResponse request(List<String> taskId, List<String> flag) throws IOException {
        DepositReportReconcileRequest request = new DepositReportReconcileRequest();
        request.setTaskId(taskId);
        request.setFlag(flag);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String requestJson = GsonHelper.toJson(request);

//        HttpClient client = new HttpClient(context);
        String url = GlobalData.getSharedGlobalData().getURL_GET_RECAPITULATE();
//        Request httpRequest = client.request(url)
//                .post(RequestBody.create(MediaType.parse("application/json"), requestJson))
//                .build();
//
//        Response response = client.execute(httpRequest);
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
        //String responseJson = "{\"taskId\":[\"COL150000243\"],\"status\":{\"code\":0}}";
        DepositReportReconcileResponse reconcileResponse = GsonHelper.fromJson(responseJson, DepositReportReconcileResponse.class);

        return reconcileResponse;
    }
}
