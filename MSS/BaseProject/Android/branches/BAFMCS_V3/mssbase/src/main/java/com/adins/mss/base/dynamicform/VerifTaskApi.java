package com.adins.mss.base.dynamicform;

import android.app.Activity;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

public class VerifTaskApi {
    private Activity activity;
    private TaskH taskH;
    private List<TaskD> taskDs;
    private String notes;

    public VerifTaskApi(Activity activity, TaskH taskH, List<TaskD> taskDs, String notes) {
        this.activity = activity;
        this.taskH = taskH;
        this.taskDs = taskDs;
        this.notes = notes;
    }

    public HttpConnectionResult request() {
        JsonRequestVerificationTask task = new JsonRequestVerificationTask();
        task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        task.addImeiAndroidIdToUnstructured();
        task.setTaskH(taskH);
        task.setTaskD(taskDs);
        task.setNotes(notes);

        String json = GsonHelper.toJson(task);
        String url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();

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
            return null;
        }
    }
}
