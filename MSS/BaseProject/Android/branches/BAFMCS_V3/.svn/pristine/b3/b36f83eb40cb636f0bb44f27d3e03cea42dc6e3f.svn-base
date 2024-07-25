package com.adins.mss.base.dynamicform;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.models.RequestRejectedWithResurvey;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

public class RejectWithResurveyApi {
    private Context activity;
    private TaskH taskH;
    private String flag, applicationFlag;

    public RejectWithResurveyApi(Context activity, TaskH taskH, String flag, String applicationFlag) {
        this.activity = activity;
        this.taskH = taskH;
        this.flag = flag;
        this.applicationFlag = applicationFlag;
    }

    public HttpConnectionResult request() {
        RequestRejectedWithResurvey request = new RequestRejectedWithResurvey();
        request.setUuid_task_h(taskH.getUuid_task_h());
        request.setFlag(flag);
        request.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
        request.setUuid_ms_user(taskH.getUuid_resurvey_user());
        request.setIs_suggested(taskH.getResurvey_suggested());
        request.setNotes(taskH.getVerification_notes());
        request.setAudit(GlobalData.getSharedGlobalData()
                .getAuditData());
        request.addImeiAndroidIdToUnstructured();

        String json = GsonHelper.toJson(request);
        String url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
        if (applicationFlag.equals(Global.APPROVAL_FLAG)) {
            url = GlobalData.getSharedGlobalData().getURL_SUBMITAPPROVALTASK();
        }
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
