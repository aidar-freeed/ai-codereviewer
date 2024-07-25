package com.adins.mss.base.api;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.models.CheckResubmitRequest;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.io.IOException;

/**
 * Created by eric.sn on 4/5/2017.
 */

public class CheckResubmitApi {
    private final Context context;

    public CheckResubmitApi(Context context) {
        this.context = context;
    }

    public MssResponseType request(String uuidtaskH) throws IOException {
        CheckResubmitRequest request = new CheckResubmitRequest();
        request.setUuidTaskH(uuidtaskH);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String requestJson = GsonHelper.toJson(request);

        String url = GlobalData.getSharedGlobalData().getURL_CHECK_RESUBMIT();
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
        String responseJson = "";
        if (serverResult != null && serverResult.isOK()) {
            try {
                responseJson = serverResult.getResult();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

        // bong 21 mei 15 - add or replace to local database
        MssResponseType checkResubmitResp = GsonHelper.fromJson(responseJson, MssResponseType.class);
        return checkResubmitResp;
    }

}
