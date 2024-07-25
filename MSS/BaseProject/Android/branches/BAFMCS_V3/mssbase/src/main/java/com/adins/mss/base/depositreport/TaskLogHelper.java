package com.adins.mss.base.depositreport;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.tasklog.TaskLogImpl;
import com.adins.mss.base.tasklog.TaskLogListTask;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

/**
 * Created by angga.permadi on 8/30/2016.
 */
public class TaskLogHelper {

    public static List<TaskH> getTaskLog(Context context) {
        TaskLogResponse response = null;
        if (Tool.isInternetconnected(context)) {
            TaskLogRequest request = new TaskLogRequest();
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            request.addImeiAndroidIdToUnstructured();

            HttpCryptedConnection httpConn = new HttpCryptedConnection(context,
                    GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
            String url = GlobalData.getSharedGlobalData().getURL_GET_TASK_LOG();
            HttpConnectionResult serverResult;

            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, GsonHelper.toJson(request));

            try {
                serverResult = httpConn.requestToServer(url, GsonHelper.toJson(request), Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);

                if (serverResult != null && serverResult.isOK()) {
                    try {
                        response = GsonHelper.fromJson(serverResult.getResult(), TaskLogResponse.class);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }
        }

        if (response != null && response.getTaskHList() != null && response.getTaskHList().size() > 0) {
            return response.getTaskHList();
        } else {
            return null;
        }
    }
}
