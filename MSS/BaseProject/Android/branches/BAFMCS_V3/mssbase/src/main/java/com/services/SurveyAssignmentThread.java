package com.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import android.text.format.DateFormat;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.logger.Logger;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.services.models.JsonRequestCheckOrder;
import com.services.models.JsonResponseServer;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by angga.permadi on 4/29/2016.
 */
public class SurveyAssignmentThread extends Thread {
    public static final int ASSIGNMENT_NOTIFICATION_ID = 123;
    public static final String ASSIGNMENT_NOTIFICATION_KEY = "ASSIGNMENT_NOTIFICATION_KEY";
    public String uuidUser;
    public List<JsonResponseServer.ResponseServer> dummyResults;
    private Context context;
    private int interval; // in miliseconds
    private volatile boolean keepRunning = true;

    public SurveyAssignmentThread(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.dummyResults = new ArrayList<>();

        interval = Global.MINUTE * 10;
        try {
            uuidUser = GlobalData.getSharedGlobalData().getUser()
                    .getUuid_user();
            if (uuidUser != null) {
                if (GeneralParameterDataAccess.getOne(context, uuidUser, "PRM04_F5IN")
                        .getGs_value() != null
                        && !GeneralParameterDataAccess
                        .getOne(context, uuidUser, "PRM04_F5IN").getGs_value()
                        .isEmpty()) {
                    interval = Integer.parseInt(GeneralParameterDataAccess.getOne(
                            context, uuidUser, "PRM04_F5IN").getGs_value()) * 1000; // from
                    // milisecond
                    // to
                    // second
                }
            } else {
                keepRunning = false;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            if (Global.user != null) {
                uuidUser = Global.user.getUuid_user();
                if (uuidUser != null) {
                    if (GeneralParameterDataAccess.getOne(context, uuidUser, "PRM04_F5IN")
                            .getGs_value() != null
                            && !GeneralParameterDataAccess
                            .getOne(context, uuidUser, "PRM04_F5IN").getGs_value()
                            .isEmpty()) {
                        interval = Integer.parseInt(GeneralParameterDataAccess.getOne(
                                context, uuidUser, "PRM04_F5IN").getGs_value()) * 1000; // from
                        // milisecond
                        // to
                        // second
                    }
                }
            } else {
                keepRunning = false;
            }
        }
    }

    @Override
    public void run() {
        super.run();

        while (keepRunning) {
            try {
                String resp;
                JsonResponseServer result = null;

                if (Tool.isInternetconnected(context)) {
                    JsonRequestCheckOrder requestCheckOrder = new JsonRequestCheckOrder();
                    requestCheckOrder.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    requestCheckOrder.setFlag(Global.FLAG_FOR_ORDERASSIGNMENT);
                    String json = GsonHelper.toJson(requestCheckOrder);
                    String url = GlobalData.getSharedGlobalData().getURL_GET_LIST_ASSIGNMENT();
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
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }

                    if (serverResult != null) {
                        resp = serverResult.getResult();
                        try {
                            result = GsonHelper.fromJson(resp, JsonResponseServer.class);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            e.printStackTrace();
                        }
                    }
                }

                String resultString = result == null ? "null" : result.toString();
                Logger.d(this, "response from notif assignment task : " + resultString);

                if (result != null && result.getStatus().getCode() == 0) {
                    List<JsonResponseServer.ResponseServer> listResponse = result.getListResponseServer();
                    if (listResponse != null && listResponse.size() > 0) {
                        showNotification(listResponse.size(), isEquals(dummyResults, listResponse));
                        dummyResults = listResponse;
                    } else {
                        cancelNotification();
                    }
                    GlobalData.getSharedGlobalData().setCounterAssignment(listResponse == null ? 0 : listResponse.size());
                    updateAssignmentCounter(listResponse == null ? 0 : listResponse.size());
                }
            } catch (Exception e) {
                FireCrash.log(e);
                e.printStackTrace();
            }

            doSleep(interval);
        }
    }

    private void showNotification(int notifCount, boolean isSilent) {
        String notifTitle = context.getString(R.string.assignment_task);
        String message = context.getString(R.string.notification_assignment, notifCount);

        Intent resultIntent = new Intent(context, AppContext.getInstance().getHomeClass());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.setAction(ASSIGNMENT_NOTIFICATION_KEY);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, ASSIGNMENT_NOTIFICATION_ID, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.getSharedNotification().setDefaultIcon(
                NotificationThread.getNotificationIcon());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(NotificationThread.getNotificationIcon());
        builder.setContentTitle(notifTitle).setNumber(notifCount);
        builder.setContentText(message).setNumber(notifCount);
        builder.setPriority(NORM_PRIORITY);
        NotificationCompat.BigTextStyle inboxStyle =
                new NotificationCompat.BigTextStyle();

        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(notifTitle);
        inboxStyle.bigText(message);
        inboxStyle.setSummaryText(context.getString(R.string.click_to_open_assignment_task));

        builder.setStyle(inboxStyle);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        if (isSilent) {
            builder.setSound(null);
            builder.setVibrate(new long[]{0L, 0L});
        } else {
            builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(ASSIGNMENT_NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ASSIGNMENT_NOTIFICATION_ID);
    }

    private boolean isEquals(List<JsonResponseServer.ResponseServer> objects1,
                             List<JsonResponseServer.ResponseServer> objects2) {
        if (objects1.size() == 0) return false;
        if (objects1.size() != objects2.size()) return false;

        for (int i = 0; i < objects1.size(); i++) {
            if (!objects1.get(i).getFlag().equals(objects2.get(i).getFlag())) return false;
        }

        return true;
    }

    public void requestStop() {
        keepRunning = false;
    }

    private void doSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void updateAssignmentCounter(final int count) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
                try {
                    if (NewMainActivity.mnSurveyAssign != null)
                        NewMainActivity.mnSurveyAssign.setCounter(String.valueOf(count));
                    NewMainActivity.setCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
                }
            }
        });
    }

}
