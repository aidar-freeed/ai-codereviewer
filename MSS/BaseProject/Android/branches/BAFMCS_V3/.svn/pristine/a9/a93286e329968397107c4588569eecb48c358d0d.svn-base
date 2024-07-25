package com.adins.mss.coll.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.Utility;
import com.adins.mss.coll.models.ReportSummaryResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.Gson;

/**
 * Created by adityapurwa on 22/04/15.
 */
public class ReportSummaryApi {
    private final Context context;

    public ReportSummaryApi(Context context) {
        this.context = context;
    }

    public ReportSummaryResponse request() {
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        Gson gson = new Gson();
        MssRequestType request = new MssRequestType();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String data = gson.toJson(request);
        ReportSummaryResponse response = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(  GlobalData.getSharedGlobalData().getURL_GET_REPORTSUMMARY(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, data);

        try {
            HttpConnectionResult result = httpConn.requestToServer(
                    GlobalData.getSharedGlobalData().getURL_GET_REPORTSUMMARY(),
                    data, Global.DEFAULTCONNECTIONTIMEOUT);
            String jsonResult = result.getResult();
            Utility.metricStop(networkMetric, result);
            response = gson.fromJson(jsonResult, ReportSummaryResponse.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }
}
