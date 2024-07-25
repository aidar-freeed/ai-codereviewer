    package com.services;

    import android.content.Context;
    import android.os.AsyncTask;
    import android.util.Log;

    import com.adins.mss.base.GlobalData;
    import com.adins.mss.base.crashlytics.FireCrash;
    import com.adins.mss.base.dynamicform.TaskManager;
    import com.adins.mss.base.util.GsonHelper;
    import com.adins.mss.base.util.Utility;
    import com.adins.mss.constant.Global;
    import com.adins.mss.dao.GeneralParameter;
    import com.adins.mss.dao.LastSync;
    import com.adins.mss.dao.TaskH;
    import com.adins.mss.foundation.camerainapp.helper.Logger;
    import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
    import com.adins.mss.foundation.db.dataaccess.LastSyncDataAccess;
    import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
    import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
    import com.adins.mss.foundation.formatter.Formatter;
    import com.adins.mss.foundation.formatter.Tool;
    import com.adins.mss.foundation.http.HttpConnectionResult;
    import com.adins.mss.foundation.http.HttpCryptedConnection;
    import com.google.firebase.perf.FirebasePerformance;
    import com.google.firebase.perf.metrics.HttpMetric;
    import com.services.models.JsonRequestLastSync;
    import com.services.models.JsonResponseLastSync;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    public class AutoSendTaskThread extends Thread {
        private static String TAG = "AUTOSEND";
        private Context context;
        private int interval = 0;
        private volatile boolean keepRunning = true;
        private volatile boolean isWait = false;
        private boolean debug;
        private static List<String> listTaskSubmitManual = new ArrayList<>();
        private final Object threadMonitor;

        public AutoSendTaskThread(Context context,Object threadMonitor) {
            this.threadMonitor = threadMonitor;
            this.context = context;
            this.debug = Global.IS_DEV;

            try {
                GeneralParameter gp = GeneralParameterDataAccess.getOne(context,
                        GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                        Global.GS_INTERVAL_AUTOSEND);
                if (gp != null) {

                    interval = Global.SECOND * Integer.parseInt(gp.getGs_value());
                }
            } catch (Exception e) {
                FireCrash.log(e);
                interval = 0;
            }
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
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }

                    if (interval > 0) {
                        if (Tool.isInternetconnected(context)) {
                            //Check unsent last sync from db
                            List<LastSync> sendlistsync = LastSyncDataAccess.getAllBySentStatus(context, 0);
                            int count = sendlistsync.size();
                            if (count > 0) {
                                //loop unsent last sync and send to server
                                for (LastSync lastSync : sendlistsync) {
                                    try {
                                        SendLastSync sendLastSync = new SendLastSync(context, lastSync);
                                        sendLastSync.execute();
                                    } catch (Exception exc) {
                                        FireCrash.log(exc);
                                    }
                                }

                            }
                        }
                        if (Tool.isInternetconnected(context) && !Global.isIsManualSubmit()) {
                            List<TaskH> sendTask = TaskHDataAccess
                                    .getAllTaskByStatus(context, GlobalData
                                                    .getSharedGlobalData().getUser()
                                                    .getUuid_user(),
                                            TaskHDataAccess.STATUS_SEND_PENDING);
                            int count = sendTask.size();
                            if (count > 0) {
                                for (TaskH taskH : sendTask) {
                                    try {
                                        boolean isPrintable = false;
                                        try {
                                            isPrintable = Formatter.stringToBoolean(taskH.getScheme().getIs_printable());
                                        } catch (Exception e) {
                                            FireCrash.log(e);
                                        }
                                        boolean isTaskPaid = TaskDDataAccess.isTaskPaid(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(),
                                                taskH.getUuid_task_h());

                                        if (!isPrintable && !isTaskPaid) {
                                            try {
                                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                                    if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                        new TaskManager.ApproveTaskOnBackground(context, taskH, Global.FLAG_FOR_REJECTEDTASK, false, taskH.getVerification_notes()).execute();
                                                    else if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                        new TaskManager.RejectWithReSurveyTaskOnBackground(context, taskH, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.VERIFICATION_FLAG).execute();
                                                    else
                                                        new TaskManager().saveAndSendTaskOnBackground(context, taskH.getTask_id(), false, false);
                                                } else if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_APPROVAL)) {
                                                    if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                                        new TaskManager.ApproveTaskOnBackground(context, taskH, Global.FLAG_FOR_REJECTEDTASK, true, taskH.getVerification_notes()).execute();
                                                    else if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY))
                                                        new TaskManager.RejectWithReSurveyTaskOnBackground(context, taskH, Global.FLAG_FOR_REJECTEDTASK_WITHRESURVEY, Global.APPROVAL_FLAG).execute();
                                                    else
                                                        new TaskManager.ApproveTaskOnBackground(context, taskH, Global.FLAG_FOR_APPROVALTASK, true, taskH.getVerification_notes()).execute();
                                                } else
                                                    new TaskManager().saveAndSendTaskOnBackground(context, taskH.getUuid_task_h(), false, false);
                                            } catch (Exception e) {
                                                FireCrash.log(e);
                                            }

                                        } else {
                                            if (!isTaskPaid) {
                                                //Nendi: 2019.06.13 | Bugfix PRDAITMSS-660 (CollAct - Autosend untuk result gagal collect tidak jalan)
                                                new TaskManager().saveAndSendTaskOnBackground(context, taskH.getUuid_task_h(), false, false);
                                            }
                                        }
                                        TaskHDataAccess.doBackup(context, taskH);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        try {
                            MainServices.autoSendTaskThread.requestStop();
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                } catch (Exception ex) {
                    Logger.e(TAG, Log.getStackTraceString(ex));
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

        public static void addTaskSubmitManual(String uuid_task_h) {
            if(!listTaskSubmitManual.contains(uuid_task_h))
                listTaskSubmitManual.add(uuid_task_h);
        }

        public static void removeTaskSubmitManual(String uuid_task_h) {
            listTaskSubmitManual.remove(uuid_task_h);
        }

        class SendLastSync extends AsyncTask<Void, Void, Void> {
            private Context context;
            private LastSync lastSync;

            public SendLastSync(Context context, LastSync lastSync) {
                this.context = context;
                this.lastSync = lastSync;
            }


            @Override
            protected Void doInBackground(Void... params) {
                String uuidLastSync = lastSync.getUuid_last_sync();
                JsonRequestLastSync request = new JsonRequestLastSync();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setDtm_req(lastSync.getDtm_req());
                request.setData(lastSync.getData());
                request.setFlag(lastSync.getFlag());
                String[] arr = null;
                if (lastSync.getListOfLOV() != null) {
                    arr = lastSync.getListOfLOV().substring(
                            1, lastSync.getListOfLOV().length() - 1).split(",");
                    List<String> list = Arrays.asList(arr);
                } else {
                    arr = null;
                }
                List<String> list = new ArrayList<>();
                if (arr == null) {
                    list.add("");
                } else {
                    for (int i = 0; i < arr.length; i++) {
                        list.add(arr[i].trim());
                    }
                }
                request.setListOfLOV(list);
                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getURL_LAST_SYNC();
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
                    e.printStackTrace();
                }
                if(serverResult == null){
                    return null;
                }

                if (serverResult.isOK()) {
                    try {
                        String result = serverResult.getResult();
                        JsonResponseLastSync responseLastSync = GsonHelper.fromJson(result, JsonResponseLastSync.class);
                        if (responseLastSync.getStatus().getCode() == 1) {
                            LastSyncDataAccess.delete(context, uuidLastSync);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }
    }
