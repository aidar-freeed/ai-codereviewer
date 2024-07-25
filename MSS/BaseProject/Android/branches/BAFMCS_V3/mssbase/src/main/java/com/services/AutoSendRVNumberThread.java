package com.services;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.print.rv.RVEntity;
import com.adins.mss.foundation.print.rv.RVNumberRequest;
import com.adins.mss.foundation.print.rv.RVNumberResponse;
import com.adins.mss.logger.Logger;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.JsonParseException;

/**
 * Created by angga.permadi on 4/20/2016.
 */
public class AutoSendRVNumberThread extends Thread {
    private Context context;
    private volatile boolean keepRunning;
    private int interval;

    public AutoSendRVNumberThread(Context context) {
        this.context = context;

        try {
            GeneralParameter gp = GeneralParameterDataAccess.getOne(context,
                    GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                    Global.GS_INTERVAL_AUTOSEND);
            if (gp != null) {
                interval = Global.SECOND * Integer.parseInt(gp.getGs_value());
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            interval = Global.MINUTE * 2;
        }
    }

    @Override
    public void run() {
        super.run();
        keepRunning = true;

        while (keepRunning) {
            RVNumberResponse resultBean = null;
            boolean isError = false;

            String uuidUser = GlobalData.getSharedGlobalData().getUser()
                    .getUuid_user();
            TaskH taskH = TaskHDataAccess.getOneTaskByStatusRV(context, uuidUser, TaskHDataAccess.STATUS_RV_PENDING);

            if (taskH != null) {
                System.gc();

                if (Tool.isInternetconnected(context)) {
                    RVEntity rv = new RVEntity();
                    rv.setUuid_task_h(taskH.getUuid_task_h());

                    ReceiptVoucher rvNumber = ReceiptVoucherDataAccess.getOne(context, uuidUser, taskH.getRv_number());
                    if (rvNumber != null) {
                        rv.setRv_number(rvNumber.getRv_number());
                        rv.setDtm_use(rvNumber.getDtm_use());
                    }

                    RVNumberRequest entity = new RVNumberRequest();
                    entity.setRvBlank(rv);
                    entity.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    HttpCryptedConnection httpConn = new HttpCryptedConnection(context,
                            GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
                    HttpConnectionResult serverResult = null;
                    String url = GlobalData.getSharedGlobalData().getURL_RV_NUMBER();

                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, GsonHelper.toJson(entity));

                    try {
                        serverResult = httpConn.requestToServer(url,
                                GsonHelper.toJson(entity), Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                        isError = true;
                    }

                    if (serverResult != null) {
                        Logger.d(this, serverResult.getResult());
                        if (serverResult.isOK()) {
                            try {
                                resultBean = GsonHelper.fromJson(serverResult.getResult(), RVNumberResponse.class);
                            } catch (JsonParseException e) {
                                e.printStackTrace();
                                isError = true;
                            }

                            if (resultBean != null) {
                                if (resultBean.getStatus() != null &&
                                        resultBean.getStatus().getCode() == 0) {
                                    taskH.setStatus_rv(TaskHDataAccess.STATUS_RV_SENT);
                                    TaskHDataAccess.addOrReplace(context, taskH);
                                } else {
                                    isError = true;
                                }
                            }
                        } else {
                            isError = true;
                        }
                    }
                } else {
                    isError = true;
                }
            } else {
                keepRunning = false;
            }

            int interval2 = isError ? interval : 1000;
            try {
                sleep(interval2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isRunning() {
        return keepRunning;
    }
}
