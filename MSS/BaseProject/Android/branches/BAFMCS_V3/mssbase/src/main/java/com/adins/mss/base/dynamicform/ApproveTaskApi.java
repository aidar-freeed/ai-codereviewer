package com.adins.mss.base.dynamicform;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

public class ApproveTaskApi {
    private Context activity;
    private String uuidTaskH, flag, notes;
    private boolean isApprovalTask;

    public ApproveTaskApi(Context activity, String uuidTaskH, String flag, boolean isApprovalTask, String notes) {
        this.activity = activity;
        this.uuidTaskH = uuidTaskH;
        this.flag = flag;
        this.isApprovalTask = isApprovalTask;
        this.notes = notes;
    }

    public HttpConnectionResult request() {
        JsonRequestApprovalTask request = new JsonRequestApprovalTask();
        request.setUuid_task_h(uuidTaskH);
        request.setFlag(flag);
        request.setNotes(notes);
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.addImeiAndroidIdToUnstructured();

        String json = GsonHelper.toJson(request);
        String url = GlobalData.getSharedGlobalData().getURL_SUBMITAPPROVALTASK();
        if (!isApprovalTask)
            url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
            return serverResult;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            return null;
        }
    }
}
