package com.adins.mss.base.tasklog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.depositreport.TaskLogHelper;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.log.Log;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskLogImpl extends Log implements TaskLogInterface, IShowError {
    private static Context context;
    private static List<TaskH> listTask;
    private GetOnlineLog onlineLog;
    protected ErrorMessageHandler errorMessageHandler;
    //private static String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

    public TaskLogImpl(Context context) {
        super(context);
        this.context = context;
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    public static long getCounterLog(Context context) {
        long counter = 0;
        try {
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            counter = TaskHDataAccess.getSentTaskCounter(context, uuidUser);
        } catch (Exception e) {
            FireCrash.log(e);
            // TODO: handle exception
        }
        int MAXIMUM_DATA_KEEP = GlobalData.getSharedGlobalData().getMaxDataInLog();
        if (counter > MAXIMUM_DATA_KEEP && MAXIMUM_DATA_KEEP != 0) counter = MAXIMUM_DATA_KEEP;
        return counter;
    }

    public List<TaskH> getListTaskLog() {
        listTask = getAllSentTaskWithLimited();
        Global.setListOfSentTask(listTask);
        return listTask;
    }

    @Override
    public void callOnlineLog(TaskListener listener) {
        cancelOnlineLog();
        onlineLog = new GetOnlineLog(listener);
        onlineLog.execute();
    }

    @Override
    public void cancelOnlineLog() {
        if (onlineLog != null) {
            onlineLog.cancel(true);
            onlineLog = null;
        }
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(GlobalData.isRequireRelogin()){
            return;
        }
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .show();
        }
    }

    public class GetOnlineLog extends AsyncTask<Void, Void, List<TaskH>> {
        private ProgressDialog progressDialog;
        private TaskListener listener;
        private String errMessage = null;

        public GetOnlineLog(TaskListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.progressWait), true);
        }

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            List<TaskH> result = null;
            if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
                try {
                    TaskLogImpl log = new TaskLogImpl(context);
                    result = log.getListTaskLog();
                    if (Tool.isInternetconnected(context)) {
                        List<TaskH> onlineLog = TaskLogHelper.getTaskLog(context);
                        if (onlineLog != null) {
                            if (result == null) result = new ArrayList<>();
                            List<String> uuidListTaskH = new ArrayList<>();

                            for (TaskH taskH : result) {
                                uuidListTaskH.add(taskH.getUuid_task_h());
                            }

                            Iterator<TaskH> iterator = onlineLog.iterator();
                            List<TaskH> listTaskHWithFlag = new ArrayList<>();

                            while (iterator.hasNext()) {
                                TaskH taskH = iterator.next();

                                if (uuidListTaskH.contains(taskH.getUuid_task_h())) {
                                    try {
                                        TaskH taskHWithFlag = TaskHDataAccess.getOneHeader(context, taskH.getUuid_task_h());
                                        taskHWithFlag.setFlag(taskH.getFlag());
                                        listTaskHWithFlag.add(taskHWithFlag);
                                    } catch (Exception e) {
                                        FireCrash.log(e);

                                    }
                                    iterator.remove();
                                }

                            }
                            if (listTaskHWithFlag.size() > 0) {
                                TaskHDataAccess.addOrReplace(context, listTaskHWithFlag);
                            }

                            if (onlineLog.size() > 0) {
                                for (TaskH taskH : onlineLog) {
                                    if (uuidListTaskH.contains(taskH.getUuid_task_h())) {

                                    } else {
                                        taskH.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                    }
                                    TaskHDataAccess.addOrReplace(context, taskH);
                                    result.add(taskH);
                                }
                            }
                        }
                    } else {
                        errMessage = context.getString(R.string.no_internet_connection);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<TaskH> taskHs) {
            super.onPostExecute(taskHs);
            if (context != null) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if (errMessage != null) {
                    errorMessageHandler.processError(context.getString(R.string.info_capital)
                            ,errMessage,ErrorMessageHandler.DIALOG_TYPE);
                /*    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                    dialogBuilder.withTitle(context.getString(R.string.info_capital))
                            .withMessage(errMessage)
                            .show();*/
                } else {
                    if (GlobalData.isRequireRelogin()) {

                    } else if (taskHs == null || taskHs.size() == 0) {
                        errorMessageHandler.processError(context.getString(R.string.info_capital)
                                ,context.getString(R.string.msgNoSent)
                                ,ErrorMessageHandler.DIALOG_TYPE);
                        /*NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                        dialogBuilder.withTitle(context.getString(R.string.info_capital))
                                .withMessage(context.getString(R.string.msgNoSent))
                                .show();*/
                    }
                }
                listener.onCompleteTask(taskHs);
            }
        }
    }
}
