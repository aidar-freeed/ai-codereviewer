package com.services;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.PrintDate;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintDateDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.print.SubmitPrintRequest;
import com.adins.mss.foundation.print.SubmitPrintResponse;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

/**
 * Created by angga.permadi on 3/8/2016.
 */
public class PrintSubmitThread extends Thread {
    private Context context;
    private volatile boolean keepRunning = true;
    private int interval;

    public PrintSubmitThread(Context context) {
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

        while (keepRunning) {
            if (Tool.isInternetconnected(context)) {
                System.gc();
                List<PrintDate> listPrint = PrintDateDataAccess.getAll(context);
                if (listPrint != null && listPrint.size() > 0) {

                    SubmitPrintRequest request = new SubmitPrintRequest();
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    request.setListPrint(listPrint);

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_PRINT_COUNT();
                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                    HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                    HttpConnectionResult serverResult = null;

                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    try {
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                        Utility.metricStop(networkMetric, serverResult);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }

                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            String result = serverResult.getResult();
                            SubmitPrintResponse response = GsonHelper.fromJson(result, SubmitPrintResponse.class);

                            if (response.getStatus().getCode() == 0) {
//                                PrintSubmitDataAccess.delete(context, listPrint);
//                                PrintDateDataAccess.clean(context);
                                for(PrintDate print : listPrint) {
                                    PrintDateDataAccess.delete(context, print.getDtm_print().getTime());
                                }
                                keepRunning = false;
                                break;

                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void requestStop() {
        keepRunning = false;
    }
}
