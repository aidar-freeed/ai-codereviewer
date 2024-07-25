package com.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.TaskManager;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintDate;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintDateDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.http.ForceLogoutResponse;
import com.adins.mss.foundation.print.rv.ApiCodes;
import com.adins.mss.foundation.print.rv.RVNumberResponse;
import com.adins.mss.logger.Logger;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MainServices extends Service {
    public static final String ACTION_GET_NOTIFICATION_THREAD = "ACTION_GET_NOTIFICATION_THREAD";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_TASKLIST = "com.services.action.TaskList";
    private static final String ACTION_AUTO_SEND_TASK = "com.services.action.AutoSendTask";
    private static final String ACTION_AUTO_SEND_IMAGE = "com.services.action.AutoSendImage";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.services.extra.PARAM2";

    public static NotificationThread taskListThread;
    public static AutoSendTaskThread autoSendTaskThread;
    public static AutoSendImageThread autoSendImageThread;
    public static AutoSendRVNumberThread autoSendRVNumberThread;
    public static SurveyAssignmentThread assignmentThread;
    public static PrintSubmitThread printSubmitThread;
    public static Class mainClass;

    public MainServices() {
    }

    public static void stopAllThread() {
        stopTaskListThread();
        stopAutoSendTaskThread();
        stopAutoSendImageThread();
        stopAssignmentThread();
        stopPrintSubmitThread();
        stopSendRVNumberThread();
    }

    private static synchronized void stopTaskListThread() {
        if (taskListThread != null) {
            taskListThread.requestStop();
            taskListThread = null;
        }
    }

    private static synchronized void stopAutoSendTaskThread() {
        if (autoSendTaskThread != null) {
            autoSendTaskThread.requestStop();
            autoSendTaskThread = null;
        }
    }

    private static synchronized void stopAutoSendImageThread() {
        if (autoSendImageThread != null) {
            autoSendImageThread.requestStop();
            autoSendImageThread = null;
        }
    }

    public static synchronized void stopAssignmentThread() {
        if (assignmentThread != null) {
            assignmentThread.requestStop();
            assignmentThread.interrupt();
        }

        assignmentThread = null;
    }

    public static synchronized void stopSendRVNumberThread() {
        if (autoSendRVNumberThread != null) {
            autoSendRVNumberThread.interrupt();
        }

        autoSendRVNumberThread = null;
    }

    public static synchronized void stopPrintSubmitThread() {
        if (printSubmitThread != null) {
            printSubmitThread.requestStop();
            printSubmitThread.interrupt();
            printSubmitThread = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusHelper.registerEventBus(this);
        GlobalData.getSharedGlobalData();
        if (GlobalData.getSharedGlobalData().getUser() == null) {
            NewMainActivity.InitializeGlobalDataIfError(getApplicationContext());
        }
        try {
            handleActionTaskList();
            if (!GeneralParameterDataAccess.getOne(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), "PRM03_ASIN").getGs_value().equals("0")) {
                handleActionAutoSendTask();
                if (TaskManager.isPartial(getApplicationContext()) == true) {
                    handleActionAutoSendImage();
                }
                startAssignmentThread();
                startPrintSubmitThread();
                boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
                if (isRVinFront && Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                    startSendRVNumberThread();
                }

            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
    }

    /**
     * Handle action TaskList in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTaskList() {
        // TODO: Handle action TaskList
        startTaskListThread();
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action AutoSendTask in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAutoSendTask() {
        // TODO: Handle action AutoSendTask
        startAutoSendTaskThread();
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action AutoSendTask in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAutoSendImage() {
        // TODO: Handle action AutoSendTask
        startAutoSendImageThread();
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Keep // subcribe
    public void onEvent(@NonNull MainServiceEvent event) {
        Logger.d(this, "onEvent : " + event.getClass().getSimpleName() + " : " + event.toString());

        switch (event.getmState()) {
            case TASK_DATA_ACCESS_STOP_WAIT:
                if (taskListThread != null) taskListThread.stopWaiting();
                if (autoSendTaskThread != null) autoSendTaskThread.stopWaiting();
                if (autoSendImageThread != null) autoSendImageThread.stopWaiting();
                break;
            case TASK_DATA_ACCESS_WAIT:
                if (taskListThread != null) taskListThread.requestWait();
                if (autoSendTaskThread != null) autoSendTaskThread.requestWait();
                if (autoSendImageThread != null) autoSendImageThread.requestWait();
                break;
            default:
                break;
        }
    }

    @Keep // subcribe
    public void onEvent(PrintDate task) {
        Logger.d(this, "onEvent: " + task);

        startPrintSubmitThread();
    }

    @Keep // subcribe
    public void onEvent(RVNumberResponse response) {
        Logger.d(this, "onEvent : " + response.toString());

        if (response.getReqCode() == ApiCodes.RV_NUMBER_AUTO_SEND) {
            boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(getApplicationContext(), GlobalData.getSharedGlobalData().getUser().getUuid_user());
            if (isRVinFront)
                startSendRVNumberThread();
        }
    }

    @Keep // subcribe
    public void onEvent(ForceLogoutResponse response) {
        stopAllThread();
        stopSelf();
    }

    @Keep
    public void onEvent(String action) {
        if (action.equals(ACTION_GET_NOTIFICATION_THREAD)) {
            startTaskListThread();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregisterEventBus(this);
        stopAllThread();
    }

    public void stopMainServices() {
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void startTaskListThread() {
        if (taskListThread == null) {
            taskListThread = new NotificationThread(getApplicationContext(),this);
            taskListThread.start();
        } else {
            if (!taskListThread.isAlive()) {
                taskListThread.start();
            }
        }
    }

    private synchronized void startAutoSendTaskThread() {
        if (autoSendTaskThread == null) {
            autoSendTaskThread = new AutoSendTaskThread(getApplicationContext(),this);
            autoSendTaskThread.start();
        } else {
            if (!autoSendTaskThread.isAlive())
                autoSendTaskThread.start();
        }
    }

    private synchronized void startAutoSendImageThread() {
        if (autoSendImageThread == null) {
            autoSendImageThread = new AutoSendImageThread(getApplicationContext(),this);
            autoSendImageThread.start();
        } else {
            if (!autoSendImageThread.isAlive())
                autoSendImageThread.start();
        }
    }

    public synchronized void startAssignmentThread() {
        if (GlobalData.getSharedGlobalData() != null && GlobalData.getSharedGlobalData().getApplication() != null &&
                GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_SURVEY)) {
            if (NewMainActivity.mnSurveyAssign != null) {
                if (assignmentThread == null) {
                    assignmentThread = new SurveyAssignmentThread(getApplicationContext());
                    assignmentThread.start();
                }

                if (!assignmentThread.isAlive()) {
                    assignmentThread.start();
                    Logger.d(this, "start assignmentThread");
                }
            }
        }
    }

    public synchronized void startSendRVNumberThread() {
        if (GlobalData.getSharedGlobalData() != null && GlobalData.getSharedGlobalData().getApplication() != null &&
                GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
            String uuidUser = GlobalData.getSharedGlobalData().getUser()
                    .getUuid_user();
            TaskH taskH = TaskHDataAccess.getOneTaskByStatusRV(getApplicationContext(),
                    uuidUser, TaskHDataAccess.STATUS_RV_PENDING);
            if (taskH != null) {
                if (autoSendRVNumberThread == null) {
                    autoSendRVNumberThread = new AutoSendRVNumberThread(getApplicationContext());
                    autoSendRVNumberThread.start();
                } else {
                    try {
                        if (!autoSendRVNumberThread.isAlive()) {
                            autoSendRVNumberThread.start();
                            Logger.d(this, "start autoSendRVNumberThread");
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                        Logger.printStackTrace(e);
                    }
                }
            }
        }
    }

    public synchronized void startPrintSubmitThread() {
        if (GlobalData.getSharedGlobalData() != null && GlobalData.getSharedGlobalData().getApplication() != null &&
                GlobalData.getSharedGlobalData().getApplication().equals("MC")) {
            stopPrintSubmitThread();
            if (PrintDateDataAccess.getAll(getApplicationContext()) != null) {
                printSubmitThread = new PrintSubmitThread(getApplicationContext());
                printSubmitThread.start();
                Logger.d(this, "start printSubmitThread");
            }
        }
    }
}
