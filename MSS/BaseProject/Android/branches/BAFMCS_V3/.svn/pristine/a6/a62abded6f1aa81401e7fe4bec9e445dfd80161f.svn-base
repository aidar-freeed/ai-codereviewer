package com.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adins.mss.base.Backup;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.JsonRequestSubmitTask;
import com.adins.mss.base.dynamicform.JsonResponseSubmitTask;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.StatusTabFragment;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoSendImageThread extends Thread {
    private static String TAG = "AUTOSENDIMAGE";
    private static int countFalseSent = 0;
    public Notification.BigTextStyle inboxStyle;
    int totalPending = 0;
    int totalimage = 0;
    private Context context;
    private int interval;
    private volatile boolean keepRunning = true;
    private volatile boolean isWait = false;
    private String taskId;
    private NotificationManager mNotifyManager;
    private Notification.Builder mBuilder;
    private String errMessage = "";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static List listTaskUploadManual = new ArrayList<>();
    private final Object threadMonitor;

    public AutoSendImageThread(Context context,Object threadMonitor) {
        this.threadMonitor = threadMonitor;
        this.context = context;
        String uuid_user = "";
        try {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new Notification.Builder(context);
                    mBuilder.setContentTitle(context.getString(R.string.uploading_image))
                            .setContentText(context.getString(R.string.progress_uploading))
                            .setSmallIcon(getNotificationUploadingIcon());
                    inboxStyle = new Notification.BigTextStyle();
                    inboxStyle.setBigContentTitle(context.getString(R.string.uploading_image));
                    inboxStyle.bigText(context.getString(R.string.progress_uploading));
                    mBuilder.setStyle(inboxStyle);
                }
            } catch (Exception e) {
                FireCrash.log(e);

            }

            uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            if (uuid_user == null)
                uuid_user = Global.user.getUuid_user();

            GeneralParameter gp = GeneralParameterDataAccess.getOne(context,
                    uuid_user,
                    Global.GS_INTERVAL_AUTOSEND);
            GeneralParameter gp2 = GeneralParameterDataAccess.getOne(context,
                    uuid_user,
                    Global.GS_PARTIAL_SENT);
            if (gp != null) {
                this.interval = Global.SECOND * Integer.parseInt(gp.getGs_value());
            }
            if (gp2 != null) {
                if (gp2.getGs_value().equals("0")) {
                    requestStop();
                }
            } else {
                return;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            this.interval = 0;
            try {
                requestStop();
            } catch (Exception e2) {
                FireCrash.log(e);
            }
        }

    }

    public static boolean checkFinal(int count) {
        return count <= 1;
    }

    public static int countTotalImageTask(Context context, String uuid_task_h) {
        List<TaskD> totalImageTask = TaskDDataAccess
                .getTaskDWithImageByUuidTaskH(context, uuid_task_h);
        return totalImageTask.size();
    }

    public static int countPendingImageUpload(Context context,
                                              String uuid_task_h) {
        List<TaskD> sendImage = TaskDDataAccess.getUnsentImageByUuidTaskH(
                context, GlobalData.getSharedGlobalData().getUser()
                        .getUuid_user(), uuid_task_h);
        return sendImage.size();
    }

    public static int countPendingImageBeforeUpload(Context context,
                                                    String uuid_task_h) {
        List<TaskD> sendImage = TaskDDataAccess.getPendingImageByUuidTaskH(
                context, GlobalData.getSharedGlobalData().getUser()
                        .getUuid_user(), uuid_task_h);
        return sendImage.size();
    }

    public static int getNotificationUploadingIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_uploading : R.drawable.icon_notif_new;
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                synchronized (threadMonitor) {
                    if (isWait) {
                        threadMonitor.wait();
                    }
                }
                try {
                    String uuidUser = GlobalData.getSharedGlobalData().getUser()
                            .getUuid_user();
                    if (uuidUser != null) {
                        GeneralParameter gp = GeneralParameterDataAccess.getOne(context, uuidUser, Global.GS_INTERVAL_AUTOSEND);
                        if (gp != null && gp.getGs_value() != null
                                && !gp.getGs_value()
                                .isEmpty()) {
                            interval = Integer.parseInt(gp.getGs_value()) * Global.SECOND;
                        }
                        GeneralParameter gp2 = GeneralParameterDataAccess.getOne(context,
                                uuidUser,
                                Global.GS_PARTIAL_SENT);
                        if (gp2 != null && gp2.getGs_value().equals("0")) {
                            requestStop();
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                if (interval > 0) {
                    if (Tool.isInternetconnected(context) && !Global.isIsManualUploading()) {
                        List<String> listKeySendImage = TaskDDataAccess.getUnsentImageListKey(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                        int count = listKeySendImage.size();
                        if (count > 0) {
                            Global.setIsUploading(true);
                            for (int i = 0; i < count; i++) {
                                TaskD taskd = TaskDDataAccess.getOneUnsentImageByUuidTaskH(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), listKeySendImage.get(i));
                                try {
                                    String uuid_task_h = taskd.getTaskH()
                                            .getUuid_task_h();
                                    String task_id = taskd.getTaskH()
                                            .getTask_id();
                                    totalPending = countPendingImageUpload(
                                            context, uuid_task_h);
                                    totalimage = countTotalImageTask(
                                            context, uuid_task_h);
                                    boolean finale =
                                            checkFinal(totalPending);
                                    if (finale) {
                                        taskd.setIs_final(Global.TRUE_STRING);
                                    }

                                    if (taskd.getCount() == null) {
                                        taskd.setCount(Global.FALSE_STRING);
                                    }

                                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                                    task.setAudit(GlobalData
                                            .getSharedGlobalData()
                                            .getAuditData());
                                    task.addImeiAndroidIdToUnstructured();
                                    task.setTaskH(taskd.getTaskH());
                                    List<TaskD> ds = new ArrayList<>();
                                    ds.add(taskd);
                                    task.setTaskD(ds);
                                    String json = GsonHelper.toJson(task);
                                    String url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                                    final TaskH taskH = taskd.getTaskH();
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            handler.post(new Runnable() {
                                                public void run() {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                        int uplod = totalimage - totalPending + 1;
                                                        String counter = context.getString(R.string.uploading_image_counter, uplod, totalimage);
                                                        String title = context.getString(R.string.uploading_image_title, taskH.getCustomer_name());
                                                        mBuilder.setContentTitle(title);
                                                        mBuilder.setContentText(counter)
                                                                // Removes the progress bar
                                                                .setProgress(0, 0, true);
                                                        inboxStyle.setBigContentTitle(title);
                                                        inboxStyle.bigText(counter);
                                                        mBuilder.setStyle(inboxStyle);
                                                        mBuilder.setOngoing(true);

                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                                        {
                                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                                            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                                                            assert mNotifyManager != null;
                                                            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                                                            mNotifyManager.createNotificationChannel(notificationChannel);
                                                        }

                                                        mNotifyManager.notify(4, mBuilder.build());
                                                    }
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        e.printStackTrace();
                                    }
                                    if (taskH.getIs_prepocessed() != null
                                            && taskH.getIs_prepocessed()
                                            .equals(Global.FORM_TYPE_VERIFICATION)) {
                                        url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                                    } else {
                                        url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                                    }
                                    boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                                    boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                                    HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                                    HttpConnectionResult serverResult = null;
                                    Date startSend = Tool.getSystemDateTime();

                                    //Firebase Performance Trace HTTP Request
                                    HttpMetric networkMetric =
                                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                                    Utility.metricStart(networkMetric, json);

                                    try {
                                        if (!Global.isIsManualUploading()) {
                                            serverResult = httpConn
                                                    .requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                                            Utility.metricStop(networkMetric, serverResult);
                                        } else {
                                            errMessage = context.getString(R.string.upload_queue);
                                            break;
                                        }
                                    } catch (Exception ex) {
                                        errMessage = context.getString(R.string.upload_failed);
                                        break;
                                    }
                                    if (serverResult != null && serverResult.isOK()) {
                                        String resultvalue = serverResult
                                                .getResult();
                                        try {
                                            JsonResponseSubmitTask responseSubmitTask = GsonHelper
                                                    .fromJson(
                                                            resultvalue,
                                                            JsonResponseSubmitTask.class);

                                            int statusCode = responseSubmitTask
                                                    .getStatus().getCode();
                                            if (statusCode == 0) {
                                                Date finishSend = Tool
                                                        .getSystemDateTime();
                                                if (Global.IS_DEV)
                                                    Log.i(TAG, "Berhasil terkirim");
                                                if (finale) {
                                                    ToDoList.removeSurveyFromList(task_id);
                                                    if (responseSubmitTask
                                                            .getTaskId() != null)
                                                        taskId = responseSubmitTask
                                                                .getTaskId().toString();
                                                    if (taskId != null && taskId.length() > 0) {
                                                        doAfterFinish(taskd, startSend,
                                                                finishSend);
                                                        if (StatusTabFragment.handler != null)
                                                            StatusTabFragment.handler
                                                                    .sendEmptyMessage(0);
                                                        Utility.freeMemory();
                                                    }
                                                } else {
                                                    totalPending--;
                                                    ToDoList.updateStatusSurvey(
                                                            task_id,
                                                            TaskHDataAccess.STATUS_SEND_UPLOADING,
                                                            totalPending);
                                                    taskd.setIs_sent(Global.TRUE_STRING);
                                                    TaskDDataAccess.addOrReplace(
                                                            this.context, taskd);
                                                    if (StatusTabFragment.handler != null)
                                                        StatusTabFragment.handler
                                                                .sendEmptyMessage(0);
                                                }
                                            }
                                            else if(statusCode == Global.FAILED_DRAFT_TASK_CODE){
                                                errMessage = responseSubmitTask.getStatus().getMessage();
                                                TaskManager.isSubmitFailedDraft(context, taskH, errMessage);
                                                errMessage = context.getString(R.string.task_failed_draft);
                                            }else {
                                                if (Global.IS_DEV)
                                                    Log.i(TAG, "Status Auto Send Image : " + resultvalue);
                                                errMessage = responseSubmitTask.getStatus().getMessage();
                                            }
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                            errMessage = context.getString(R.string.upload_queue);
                                            break;
                                        }

                                    } else {
                                        String statusCode = String.valueOf(serverResult.getStatusCode()).trim();
                                        if (statusCode != null && statusCode.charAt(0) != '5') {
                                            //set counter for taskD
                                            taskd.setCount(Global.TRUE_STRING);
                                            taskd.setIs_sent(Global.FALSE_STRING);
                                            TaskDDataAccess.addOrReplace(this.context, taskd);
                                        }
                                        errMessage = context.getString(R.string.upload_queue);
                                        break;
                                    }

                                    TaskHDataAccess.doBackup(context, taskH);

                                } catch (Exception ex) {
                                    Logger.e(TAG, Log.getStackTraceString(ex));
                                    ex.printStackTrace();
                                    if (countFalseSent >= 3) {
                                        countFalseSent = 0;
                                        // jika 3 kali gagal looping, gak usah
                                        // dilanjutkan kirim lg..
                                        break;
                                    } else {
                                        //set counter for taskD
                                        taskd.setCount(Global.TRUE_STRING);
                                        taskd.setIs_sent(Global.FALSE_STRING);
                                        TaskDDataAccess.addOrReplace(this.context, taskd);
                                        countFalseSent++;
                                        continue;
                                    }
                                }
                            }
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    // UI code goes here
                                    try {
                                        try {
                                            NewMainActivity.setCounter();
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                        if (NewTimelineFragment.getTimelineHandler() != null)
                                            NewTimelineFragment.getTimelineHandler().sendEmptyMessage(0);
                                        if (StatusTabFragment.handler != null)
                                            StatusTabFragment.handler.sendEmptyMessage(0);
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                    }
                                }
                            });
                            try {
                                if (errMessage == null || errMessage.length() == 0) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        Handler handler2 = new Handler(Looper.getMainLooper());
                                        handler2.post(new Runnable() {
                                            public void run() {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    String counter = context.getString(R.string.upload_complete);
                                                    mBuilder.setContentText(counter)
                                                            // Removes the progress bar
                                                            .setProgress(0, 0, false);
                                                    mBuilder.setOngoing(false);
                                                    inboxStyle.bigText(counter);
                                                    mBuilder.setStyle(inboxStyle);

                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                                    {
                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                                                        assert mNotifyManager != null;
                                                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                                                        mNotifyManager.createNotificationChannel(notificationChannel);
                                                    }

                                                    mNotifyManager.notify(4, mBuilder.build());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        Handler handler2 = new Handler(Looper.getMainLooper());
                                        handler2.post(new Runnable() {
                                            public void run() {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    mBuilder.setContentText(errMessage)
                                                            // Removes the progress bar
                                                            .setProgress(0, 0, false);
                                                    mBuilder.setOngoing(false);
                                                    inboxStyle.bigText(errMessage);
                                                    mBuilder.setStyle(inboxStyle);

                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                                    {
                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                                                        assert mNotifyManager != null;
                                                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                                                        mNotifyManager.createNotificationChannel(notificationChannel);
                                                    }

                                                    mNotifyManager.notify(4, mBuilder.build());
                                                }
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                FireCrash.log(e);

                            }
                        }
                        try{
                            //select list taskH yg status masih uploding harus cari sisa image 0
                            List<TaskH> taskHList = TaskHDataAccess.getAllTaskByStatus(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_UPLOADING);
                            if(!taskHList.isEmpty()){
                                for (int i = 0; i < taskHList.size(); i++) {
                                    TaskH taskH = taskHList.get(i);
                                    int countTaskImage = countPendingImageBeforeUpload(context, taskH.getUuid_task_h());
                                    if(countTaskImage == 0){
                                        List<TaskD> taskDList = TaskDDataAccess.getAll(context, taskH.getUuid_task_h(), TaskDDataAccess.IMAGE_ONLY);
                                        if(!taskDList.isEmpty()){
                                            TaskD taskDetail = taskDList.get(0);
                                            sendImageTask(taskDetail);
                                        }
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    Global.setIsUploading(false);
                } else {
                    MainServices.autoSendImageThread.requestStop();
                }
                countFalseSent = 0;

                try {
                    Thread.sleep(interval);
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                Logger.e(TAG, Log.getStackTraceString(ex));
                Global.setIsUploading(false);
                ex.printStackTrace();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void requestWait() {
        isWait = true;
    }

    public synchronized void stopWaiting() {
        isWait = false;
        synchronized (threadMonitor) {
            threadMonitor.notifyAll();
        }
    }

    public synchronized void requestStop() {
        keepRunning = false;
    }

    private void doAfterFinish(TaskD taskd, Date startSend, Date finishSend) {
        long time = finishSend.getTime() - startSend.getTime();
        int sec = (int) Math.ceil((double) time / 1000); // millisecond to second
        byte[] answer = taskd.getImage();
        int size = answer.length;

        TaskH taskH = taskd.getTaskH();
        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
        taskH.setLast_saved_question(1);
        taskH.setTask_id(taskId);
        taskH.setSubmit_date(taskH.getSubmit_date());
        taskH.setSubmit_duration(String.valueOf(sec));
        taskH.setSubmit_size(String.valueOf(size));
        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        if (isRVinFront && Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
            taskH.setStatus_rv(TaskHDataAccess.STATUS_RV_SENT);
        }
        TaskHDataAccess.addOrReplace(this.context, taskH);

        taskd.setIs_sent(Global.TRUE_STRING);
        TaskDDataAccess.addOrReplace(this.context, taskd);

        Handler handler = new Handler(Looper.getMainLooper());

        //today plan repo trigger update after submit success
        if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted() && taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)){
            GlobalData.getSharedGlobalData().getTodayPlanRepo().updatePlanByTaskH(taskH, PlanTaskDataAccess.STATUS_FINISH);
        }

        handler.post(new Runnable() {
            public void run() {
                try {
                    NewMainActivity.setCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        });

        TimelineManager.insertTimeline(context, taskH);
        TaskHDataAccess.doBackup(context, taskH);
    }

    private void doAfterFinish(TaskH taskH, List<TaskD> listTaskD) {
        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
        taskH.setLast_saved_question(1);
        TaskHDataAccess.addOrReplace(this.context, taskH);
        for (TaskD taskD : listTaskD) {
            taskD.setIs_sent(Global.TRUE_STRING);
            TaskDDataAccess.addOrReplace(this.context, taskD);
        }

        try {
            NewMainActivity.setCounter();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        TimelineManager.insertTimeline(context, taskH);
        TaskHDataAccess.doBackup(context, taskH);
    }

    public void sendImageTask(TaskD taskd){
        try {
            String uuid_task_h = taskd.getTaskH()
                    .getUuid_task_h();
            String task_id = taskd.getTaskH()
                    .getTask_id();
            totalPending = countPendingImageUpload(
                    context, uuid_task_h);
            boolean finale = checkFinal(totalPending);
            if (finale) {
                taskd.setIs_final(Global.TRUE_STRING);
            }

            taskd.getTaskH().getUser();
            taskd.getTaskH().getScheme();
            JsonRequestSubmitTask task = new JsonRequestSubmitTask();
            task.setAudit(GlobalData
                    .getSharedGlobalData()
                    .getAuditData());
            task.addImeiAndroidIdToUnstructured();
            task.setTaskH(taskd.getTaskH());
            List<TaskD> ds = new ArrayList<>();
            ds.add(taskd);
            task.setTaskD(ds);
            String json = GsonHelper.toJson(task);
            String url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
            final TaskH taskH = taskd.getTaskH();

            if (taskH.getIs_prepocessed() != null
                    && taskH.getIs_prepocessed()
                    .equals(Global.FORM_TYPE_VERIFICATION)) {
                url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
            } else {
                url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
            }

            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            Date startSend = Tool.getSystemDateTime();

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, json);

            try {
                if(!Global.isIsManualUploading()) {
                    serverResult = httpConn
                            .requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                }
                else{
                    errMessage = context.getString(R.string.upload_queue);
                }
            } catch (Exception ex) {
                errMessage = context.getString(R.string.upload_failed);
            }
            if (serverResult != null && serverResult.isOK()) {
                String resultvalue = serverResult
                        .getResult();
                try {
                    JsonResponseSubmitTask responseSubmitTask = GsonHelper
                            .fromJson(
                                    resultvalue,
                                    JsonResponseSubmitTask.class);

                    int statusCode = responseSubmitTask
                            .getStatus().getCode();
                    if (statusCode == 0) {
                        Date finishSend = Tool
                                .getSystemDateTime();
                        if(Global.IS_DEV)
                            System.out.println("Berhasil terkirim");
                        if (finale) {
                            ToDoList.removeSurveyFromList(task_id);
                            if (responseSubmitTask
                                    .getTaskId() != null)
                                taskId = responseSubmitTask
                                        .getTaskId().toString();
                            if (taskId != null && taskId.length() > 0) {
                                doAfterFinish(taskd, startSend,
                                        finishSend);
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler
                                            .sendEmptyMessage(0);
                                Utility.freeMemory();
                            }
                        } else {
                            ToDoList.updateStatusSurvey(
                                    task_id,
                                    TaskHDataAccess.STATUS_SEND_UPLOADING,
                                    totalPending);
                            taskd.setIs_sent(Global.TRUE_STRING);
                            TaskDDataAccess.addOrReplace(
                                    context, taskd);
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler
                                        .sendEmptyMessage(0);
                        }
                    } else {
                        if (Global.IS_DEV) {
                            Log.i(TAG, "Status Auto Send Image : " + resultvalue);
                        }
                        errMessage = responseSubmitTask.getStatus().getMessage();
                    }
                } catch (Exception e) {
                    errMessage = context.getString(R.string.upload_queue);
                }
            } else if(serverResult != null && !serverResult.isOK()) {
                String statusCode = String.valueOf(serverResult.getStatusCode()).trim();
                if (!statusCode.isEmpty() && statusCode.charAt(0) != '5') {
                    //set counter for taskD
                    taskd.setCount(Global.TRUE_STRING);
                    taskd.setIs_sent(Global.FALSE_STRING);
                    TaskDDataAccess.addOrReplace(context, taskd);
                }
                errMessage = context.getString(R.string.upload_queue);
            }

        } catch (Exception ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
            ex.printStackTrace();
            if (countFalseSent >= 3) {
                countFalseSent = 0;
                // jika 3 kali gagal looping, gak usah
                // dilanjutkan kirim lg..
            } else {
                //set counter for taskD
                taskd.setCount(Global.TRUE_STRING);
                taskd.setIs_sent(Global.FALSE_STRING);
                TaskDDataAccess.addOrReplace(context, taskd);
            }
        }
    }

    public static void addTaskUploadManual(String uuid_task_h) {
        if(!listTaskUploadManual.contains(uuid_task_h)) {
            listTaskUploadManual.add(uuid_task_h);
        }
    }

    public static void removeTaskUploadManual(String uuid_task_h) {
        if(listTaskUploadManual != null){
            if(listTaskUploadManual.contains(uuid_task_h)) {
                listTaskUploadManual.remove(uuid_task_h);
            }
        }
    }
}