package com.adins.mss.base.dynamicform;

import android.content.Context;

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

public class SubmitTaskApi {
    private Context context;
    private TaskH taskH;
    private JsonRequestSubmitTask task;

    public SubmitTaskApi(Context context, JsonRequestSubmitTask task, TaskH taskH) {
        this.context = context;
        this.taskH = taskH;
        this.task = task;
    }

    public HttpConnectionResult request() {
        String json = GsonHelper.toJson(task);
        String url;

        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
            url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
        } else {
            url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
        }

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
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
