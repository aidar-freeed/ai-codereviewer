package com.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.Backup;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.authentication.Authentication;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.JsonRequestTaskD;
import com.adins.mss.base.dynamicform.JsonResponseTaskD;
import com.adins.mss.base.networkmonitor.NetworkMonitorDataUsage;
import com.adins.mss.base.rv.SyncRVNumberTask;
import com.adins.mss.base.scheme.SyncQuestionSetTask;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.TasklistInterface;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskSummary;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskSummaryDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.tracking.LocationTrackingService;

import org.acra.ACRA;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NotificationThread extends Thread {
    private static String TAG = "NotificationThread";
    public static String TASKLIST_NOTIFICATION_KEY = "TASKLIST_NOTIFICATION_KEY";
    public static long lastAssigmentDate = 0;
    private static List<TaskH> taskHList = new ArrayList<>();
    public static int notifCount = 0;
    public String uuidUser;
    public boolean firstNotif = true;
    NetworkMonitorDataUsage monitorDU = new NetworkMonitorDataUsage();
    private Context context;
    private int interval; // in miliseconds
    private volatile boolean keepRunning = true;
    private volatile boolean isWait = false;
    private long dataUsage;
    private final Object threadMonitor;

    public NotificationThread(Context context,Object threadMonitor) {
        this.threadMonitor = threadMonitor;
        this.context = context;
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
                            context, uuidUser, "PRM04_F5IN").getGs_value()) * 1000;
                }
            } else {
                keepRunning = false;
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            if (Global.user != null) {
                uuidUser = Global.user.getUuid_user();
                if (uuidUser != null) {
                    if (GeneralParameterDataAccess.getOne(context, uuidUser, "PRM04_F5IN")
                            .getGs_value() != null
                            && !GeneralParameterDataAccess
                            .getOne(context, uuidUser, "PRM04_F5IN").getGs_value()
                            .isEmpty()) {
                        interval = Integer.parseInt(GeneralParameterDataAccess.getOne(
                                context, uuidUser, "PRM04_F5IN").getGs_value()) * 1000;
                    }
                }
            } else {
                keepRunning = false;
            }
        }
    }

    public static List<TaskH> getTaskHList() {
        return taskHList;
    }

    public static int getNotificationIcon() {
//        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//        return useWhiteIcon ? R.drawable.icon_notif_new_white : R.drawable.icon_notif_new;
        return R.drawable.icon_notif_new;
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
                    uuidUser = GlobalData.getSharedGlobalData().getUser()
                            .getUuid_user();
                    if (uuidUser != null) {
                        GeneralParameter gp = GeneralParameterDataAccess.getOne(context, uuidUser, Global.GS_INTERVAL_TASKREFRESH);
                        if (gp != null && gp.getGs_value() != null
                                && !gp.getGs_value()
                                .isEmpty()) {
                            interval = Integer.parseInt(gp.getGs_value()) * Global.SECOND;
                            if (Integer.parseInt(gp.getGs_value()) == 0) {
                                keepRunning = false;
                            }
                            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                            if (!Global.APPLICATION_ORDER.equals(application)) {
                                checkFlagTracking();
                            }
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }

                if (Tool.isInternetconnected(context)) {
                    //Start Kelola Data Usage
                    long save;
                    dataUsage = monitorDU.getDataUsage(context);
                    save = dataUsage - monitorDU.getDataLastDay(context);
                    if (monitorDU.getDataLastDay(context) == 0) {//Data usage hari sebelumnya belum ada
                        monitorDU.update(context, dataUsage);
                    } else {
                        if (save < 0) {
                            save = 0;
                        }
                        monitorDU.update(context, save);
                    }
                    //End Kelola Data Usage

                    List<TaskH> taskHList = TaskHDataAccess.getAllWithAssignmentDate(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    if (taskHList == null)
                        taskHList = new ArrayList<>();
                    for (TaskH taskH : taskHList) {
                        long taskAssignmentDate = taskH.getAssignment_date().getTime();
                        if (taskAssignmentDate >= lastAssigmentDate) {
                            lastAssigmentDate = taskH.getAssignment_date().getTime();
                        }
                    }
                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                    List<String> listUuidTaskH = null;
                    /*if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                        try {
                            SyncRVNumberTask.syncRvNumberInBackground(context);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }*/
                    notifCount = 0;
                    taskHList.clear();
                    taskHList.addAll(getServerNewTask());


                    //generate plan task if needed
                    if(Global.PLAN_TASK_ENABLED){
                        TodayPlanRepository todayPlanRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                        todayPlanRepository.generatePlansFromTaskList(taskHList);
                    }

                    if (!Global.APPLICATION_ORDER.equalsIgnoreCase(application) && taskHList != null) {
                        listUuidTaskH = new ArrayList<>();
                        notifCount = taskHList.size();
                        // save to local
                        if (!taskHList.isEmpty()) {
                            for (TaskH taskH : taskHList) {
                                taskH.setUser(GlobalData.getSharedGlobalData()
                                        .getUser());
                                taskH.setUuid_user(GlobalData
                                        .getSharedGlobalData().getUser()
                                        .getUuid_user());
                                taskH.setIs_verification(Global.TRUE_STRING);
                                listUuidTaskH.add(taskH.getUuid_task_h());
                                String uuid_timelineType = TimelineTypeDataAccess
                                        .getTimelineTypebyType(context,
                                                Global.TIMELINE_TYPE_TASK)
                                        .getUuid_timeline_type();
                                boolean wasInTimeline = TimelineDataAccess
                                        .getOneTimelineByTaskH(context,
                                                GlobalData
                                                        .getSharedGlobalData()
                                                        .getUser()
                                                        .getUuid_user(),
                                                taskH.getUuid_task_h(),
                                                uuid_timelineType) != null;

                                String uuid_scheme = taskH.getUuid_scheme();
                                Scheme scheme = SchemeDataAccess.getOne(
                                        context, uuid_scheme);
                                if (scheme != null) {
                                    taskH.setScheme(scheme);
                                    TaskH h = TaskHDataAccess.getOneTaskHeader(
                                            context, taskH.getTask_id());
                                    if (h != null && h.getStatus() != null) {
                                        if (!ToDoList.isOldTask(h)) {
                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                            TaskHDataAccess.addOrReplace(
                                                    context, taskH);
                                            if (!wasInTimeline) {
                                                TimelineManager.insertTimeline(
                                                        context, taskH);
                                            }
                                        } else if (ToDoList.isOldTask(h) && h.getUuid_user() != null &&
                                                !h.getUuid_user().equals(GlobalData.getSharedGlobalData().getUser().getUuid_user())) {
                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                            TaskHDataAccess.addOrReplace(context, taskH);
                                            if (!wasInTimeline) {
                                                TimelineManager.insertTimeline(
                                                        context, taskH);
                                            }
                                        } else {
                                            notifCount--;
                                        }
                                    } else {
                                        GlobalData.setNewTaskAvailable(true);
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                        TaskHDataAccess.addOrReplace(context, taskH);
                                        if (!wasInTimeline) {
                                            TimelineManager.insertTimeline(
                                                    context, taskH);
                                        }
                                    }

                                    // 2017/10/02 | olivia : simpan task ke table TaskSummary
                                    if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                                        TaskSummary taskSummary = TaskSummaryDataAccess.getOne(context, taskH.getUuid_task_h(), uuidUser);
                                        String uuidTaskSum = Tool.getUUID();
                                        Date dtm_crt = Tool.getSystemDateTime();
                                        if (taskSummary != null) {
                                            if (!CommonImpl.dateIsToday(taskSummary.getDtm_crt())) {
                                                taskSummary.setDtm_crt(dtm_crt);
                                                TaskSummaryDataAccess.addOrReplace(context, taskSummary);
                                            }
                                        } else {
                                            TaskSummary tempSummary = new TaskSummary(uuidTaskSum, taskH.getUuid_task_h(), uuidUser, null, dtm_crt);
                                            TaskSummaryDataAccess.add(context, tempSummary);
                                        }
                                    }

                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                NewMainActivity.setCounter();
                                            } catch (Exception e) {
                                                FireCrash.log(e);

                                            }
                                        }
                                    });

                                    TaskH task = TaskHDataAccess.getOneHeader(context, taskH.getUuid_task_h());
                                    if ("New".equalsIgnoreCase(task.getStatus())) {
                                        JsonRequestTaskD request = new JsonRequestTaskD();
                                        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                                        request.setuuid_task_h(taskH.getUuid_task_h());

                                        String json = GsonHelper.toJson(request);
                                        String url = GlobalData.getSharedGlobalData().getURL_GET_VERIFICATION();
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
                                        if (serverResult.isOK()) {
                                            try {
                                                String result = serverResult.getResult();

                                                JsonResponseTaskD response = GsonHelper.fromJson(result, JsonResponseTaskD.class);
                                                if (response.getStatus().getCode() == 0) {
                                                    List<TaskD> taskDs = response.getListTask();

                                                    if (!taskDs.isEmpty()) {

                                                        TaskH h2 = TaskHDataAccess.getOneTaskHeader(
                                                                context, taskH.getTask_id());
                                                        if (h2 != null && h2.getStatus() != null) {
                                                            if (!ToDoList.isOldTask(h2)) {
                                                                taskH.setScheme(scheme);
                                                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                                                                TaskHDataAccess.addOrReplace(context, taskH);
                                                                for (TaskD taskD : taskDs) {
                                                                    taskD.setTaskH(taskH);
                                                                }
                                                                if (taskDs != null && !taskDs.isEmpty())
                                                                    TaskDDataAccess.addOrReplace(context, taskDs);
                                                            }
                                                        } else {
                                                            taskH.setScheme(scheme);
                                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                                                            TaskHDataAccess.addOrReplace(context, taskH);
                                                            for (TaskD taskD : taskDs) {
                                                                taskD.setTaskH(taskH);
                                                            }
                                                            if (taskDs != null && !taskDs.isEmpty())
                                                                TaskDDataAccess.addOrReplace(context, taskDs);
                                                        }
                                                    } else {
                                                        TaskH h2 = TaskHDataAccess.getOneTaskHeader(
                                                                context, taskH.getTask_id());
                                                        if (h2 != null && h2.getStatus() != null) {
                                                            if (!ToDoList.isOldTask(h2)) {
                                                                taskH.setScheme(scheme);
                                                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                                                                TaskHDataAccess.addOrReplace(context, taskH);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    if (Global.IS_DEV)
                                                        Log.i(TAG,result);
                                                }
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else {
                                    notifCount--;
                                }
                            }
                            showNotif();
                        }
                    }
                    SyncQuestionSetTask.syncQuestionSetInBackground(context);
                    List<TaskH> taskHs = TaskHDataAccess.getAllTaskByStatus(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                    taskHs.addAll(TaskHDataAccess.getAllTaskByStatus(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_SAVEDRAFT));

                    List<TaskH> needRemoveFromBackup = new ArrayList<>();
                    if (listUuidTaskH != null) {
                        for (TaskH h : taskHs) {
                            String uuid_task_h = h.getUuid_task_h();
                            boolean isSame = false;
                            for (String uuid_from_server : listUuidTaskH) {
                                if (uuid_task_h.equals(uuid_from_server)) {
                                    isSame = true;
                                    break;
                                }
                            }
                            if (!isSame) {
                                // 2017/10/02 | olivia : task yang delete/close dianggap fail
                                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application)) {
                                    TaskSummary taskSummary = TaskSummaryDataAccess.getOne(context, h.getUuid_task_h(), uuidUser);
                                    if (taskSummary != null) {
                                        taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_FAIL);
                                        TaskSummaryDataAccess.addOrReplace(context, taskSummary);
                                    }
                                }
                                TaskHDataAccess.deleteWithRelation(context, h);
                                if(h.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                                    needRemoveFromBackup.add(h);
                                }
                            }
                        }

                        Backup backup = new Backup(context);
                        backup.removeTask(needRemoveFromBackup);

                        if (PriorityTabFragment.mHandler != null) {
                            PriorityTabFragment.mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Update your UI
                                    try {
                                        PriorityTabFragment.getListTaskH().clear();
                                        PriorityTabFragment.getHandler().sendEmptyMessage(0);
                                        PriorityTabFragment.viewAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        FireCrash.log(e);
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }

                if (interval == 0) {
                    keepRunning = false;
                }

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            NewMainActivity.setCounter();
                            if (NewTimelineFragment.getTimelineHandler() != null)
                                NewTimelineFragment.getTimelineHandler().sendEmptyMessage(0);
                            if (PriorityTabFragment.getHandler() != null)
                                PriorityTabFragment.getHandler().sendEmptyMessage(0);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                            ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
                        }
                    }
                });

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
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
        Notification.getSharedNotification().clearNotifAll(context);
        keepRunning = false;
    }

    private List<TaskH> getServerNewTask() {
        JsonResponseRetrieveTaskList jrsrtl = new JsonResponseRetrieveTaskList();
        MssRequestType mrt = new MssRequestType();
        mrt.setAudit(GlobalData.getSharedGlobalData().getAuditData());

        String json = GsonHelper.toJson(mrt);

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;
        String url = GlobalData.getSharedGlobalData().getURL_GET_TASKLIST();

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            serverResult = httpConn
                    .requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception ex) {
            FireCrash.log(ex);
        }
        if (serverResult != null && serverResult.isOK()) {
            String sentStatus = serverResult.getResult();
            Logger.d(NotificationThread.class.getSimpleName(), "Here is response Tasklist from notif : " + sentStatus);
            jrsrtl = GsonHelper.fromJson(sentStatus, JsonResponseRetrieveTaskList.class);

            if (jrsrtl.getStatus().getCode() == Global.STATUS_CODE_APPL_CLEANSING) {
                NewMainActivity.Force_Uninstall = true;
                ObscuredSharedPreferences.Editor editor = ObscuredSharedPreferences.getPrefs(context, Authentication.LOGIN_PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Authentication.LOGIN_PREFERENCES_APPLICATION_CLEANSING, "uninstall");
                editor.apply();
                //bong 10 apr 15 - Gigin minta untuk uninstall aplikasi jika user tidak aktif
                DialogManager.uninstallAPK(context);
            } else {
                NewMainActivity.Force_Uninstall = false;
				              //delete preferences uninstall ketika user active dan sebelumnya inactive
                ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(context,
                        Authentication.LOGIN_PREFERENCES, Context.MODE_PRIVATE);
                String restoredText = prefs.getString(Authentication.LOGIN_PREFERENCES_APPLICATION_CLEANSING, null);
                if (restoredText != null && restoredText.equalsIgnoreCase("uninstall")) {
                    ObscuredSharedPreferences.Editor editor = prefs.edit();
                    try {
                        editor.remove(Authentication.LOGIN_PREFERENCES_APPLICATION_CLEANSING);
                        editor.apply();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
            }
        }


        // new task must be saved to database
        if (jrsrtl.getListTaskList() != null){
            return jrsrtl.getListTaskList();
        }
        else
            return null;
    }

    public void showNotif() {
        if (notifCount > 0) {
            String notifTitle = context.getString(R.string.outstanding_task);
            String message = context.getString(R.string.notification, notifCount);

            /* Start Prepare Pending Intent */
            Intent intent = new Intent(context, AppContext.getInstance().getHomeClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setAction(TASKLIST_NOTIFICATION_KEY);

            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            /* End Prepare Pending Intent */

            Notification.getSharedNotification().setDefaultIcon(
                    R.drawable.icon_notif_new);

            //Nendi: 2019.06.20 | Update to support Android >= 8.0
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_new_task));
            builder.setSmallIcon(getNotificationIcon());
            builder.setContentTitle(notifTitle).setNumber(notifCount);
            builder.setContentText(message).setNumber(notifCount);
            builder.setPriority(NORM_PRIORITY);
            NotificationCompat.BigTextStyle inboxStyle =
                    new NotificationCompat.BigTextStyle();
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle(notifTitle);
            inboxStyle.bigText(message);
            inboxStyle.setSummaryText(context.getString(R.string.click_to_open_tasklist));


            builder.setDefaults(android.app.Notification.DEFAULT_ALL);
            builder.setStyle(inboxStyle);
            builder.setAutoCancel(true);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setContentIntent(pendingIntent);

            firstNotif = false;
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(2019, builder.build());
        }
    }

    public void checkFlagTracking() {
        try {
            User user = GlobalData.getSharedGlobalData().getUser();
            if (null != user && null != user.getIs_tracking() && user.getIs_tracking().equals("1")) {
                String trackingDays;
                List trackingDaysList;
                int thisDayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                String thisDay;
                thisDayInt -= 1;

                thisDay = String.valueOf(thisDayInt);

                trackingDays = user.getTracking_days();
                if (null != trackingDays && !"".equalsIgnoreCase(trackingDays)) {
                    trackingDaysList = Arrays.asList(trackingDays.split(";"));
                } else {
                    return;
                }

                String hourFromWebStart = user.getStart_time();
                Calendar calStart = Calendar.getInstance();
                if (null != hourFromWebStart && !"".equalsIgnoreCase(hourFromWebStart)) {
                    String hourSplitStart[] = hourFromWebStart.split(":");
                    int hourStart = Integer.parseInt(hourSplitStart[0]);
                    int minuteStart = Integer.parseInt(hourSplitStart[1]);

                    calStart.set(Calendar.HOUR_OF_DAY, hourStart);
                    calStart.set(Calendar.MINUTE, minuteStart);
                } else {
                    return;
                }

                String hourFromWeb = user.getEnd_time();
                Calendar cal = Calendar.getInstance();
                if (null != hourFromWeb && !"".equalsIgnoreCase(hourFromWeb)) {
                    String hourSplit[] = hourFromWeb.split(":");
                    int hour = Integer.parseInt(hourSplit[0]);
                    int minute = Integer.parseInt(hourSplit[1]);

                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                } else {
                    return;
                }

                if (trackingDaysList.contains(thisDay)) {
                    if (Calendar.getInstance().after(calStart) && Calendar.getInstance().before(cal)) {

                    } else {
                        if (!LocationTrackingService.isKeepRunning() && LocationTrackingService.isKeepRunningBefore()) {
                            long dataUsageThis = monitorDU.getDataThisDay(context);
                            long dataUsageLastDay = monitorDU.getDataLastDay(context);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                            String eightDigit1 = String.valueOf(calendar.getTimeInMillis()).substring(0, 8);
                            String eightDigit2 = String.valueOf(monitorDU.getLastDate(context)).substring(0, 8);
                            Logger.d("NotificationThread", "" + calendar.getTimeInMillis() + " , " + eightDigit1);
                            Logger.d("NotificationThread", "" + monitorDU.getLastDate(context) + " , " + eightDigit2);
                            if (!eightDigit1.equalsIgnoreCase(eightDigit2)) {
                                monitorDU.setDataLastDay(context, dataUsageLastDay + dataUsageThis);
                                monitorDU.resetDataThisDay(context);
                                monitorDU.setDateLastDay(context);
                                LocationTrackingService.setKeepRunningBefore(false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
            ACRA.getErrorReporter().putCustomData("NotificationThread", e.getMessage());
            ACRA.getErrorReporter().putCustomData("NotificationThread", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
            ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat checkFlagTacking"));
        }
    }
}
