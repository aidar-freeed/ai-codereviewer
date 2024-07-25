package com.adins.mss.base.dynamicform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.api.CheckResubmitApi;
import com.adins.mss.base.commons.SubmitResult;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dialogfragments.SendResultDialog;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.models.RequestRejectedWithResurvey;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todo.Task;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.todolist.form.PriorityViewAdapter;
import com.adins.mss.base.todolist.form.StatusTabFragment;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.LocationInfo;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.ReceiptVoucher;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskSummary;
import com.adins.mss.dao.Timeline;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.ReceiptVoucherDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskSummaryDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.adins.mss.foundation.rvgenerator.RvGenerator;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.google.gson.Gson;
import com.services.AutoSendImageThread;
import com.services.AutoSendTaskThread;
import com.services.MainServices;

import org.acra.ACRA;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskManager implements Task {
    public static final String STATUS_TASK_NOT_MAPPING = "5002";
    public static final String STATUS_TASK_DELETED = "2051";
    public static final String STATUS_IMEI_FAILED = "8103";
    public static final String STATUS_TASKD_MISSING = "4040";
    public static final String STATUS_SAVE_FAILED = "7878";
    public static final String STATUS_TASK_CHANGED = "2008";
    public static final String IMAGE_FAILED = "failed";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    private static final String TAG = "TaskManager";
    private static final String SUCCESS_SEND = "Berhasil Terkirim";
    private static final String STATIC_AUTO_SEND_IMAGE_EQUALS_TO = "Status Auto Send Image : ";
    private static final String UPLOAD_IMAGE_EXCEPTION = "Saat Upload image status code!= 0, ";
    private static final String ERROR = " - Error (";
    private static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private static final String SUCCESS = "success";
    private static final String BEEN_DELETED = "been deleted";
    private static final String ERROR_SUBMIT = "errorSubmit";
    private static final String AUTO_SEND_TASK_EXCEPTION = "Just for the stacktrace, kena exception saat auto Send Task";
    private static final String AUTO_SEND_TASK_THREAD_SERVER_CODE_EQUALS = "AutoSendTaskThread server code : ";
    private static final String VERIFY_VERIFICATION_TASK =  "VERIFY VERIFICATION TASK";
    private static final String SUBMIT_TASK = "SUBMIT_TASK";
    private static final String APPROVAL_TASK_FAILED = "Approval Task Failed";
    private static final String REJECTION_TASK_FAILED = "Rejection Task Failed";
    private static final String NO_IMAGE = "no image";

    private static String oldTaskH;
    private static PriorityViewAdapter viewAdapter;

    //bong 8 may 15 - add method for generate printResult
    @SuppressLint("NewApi")
    public static void generatePrintResult(Context activity, TaskH taskH) {
        //bong 7 may 15 - make printItemBean in List and save to printResult
        List<PrintItem> printItemList = PrintItemDataAccess.getAll(activity, taskH.getUuid_scheme());
        List<PrintResult> printResultList = new ArrayList<>();

        //delete dulu yang ada di database, karena generate printResult dengan jawaban yang baru
        List<PrintResult> printResultByTaskH = new ArrayList<>();
        try {
            printResultByTaskH = PrintResultDataAccess.getAll(activity, taskH.getUuid_task_h());
        } catch (Exception e) {
            FireCrash.log(e);
        }
        if (!printResultByTaskH.isEmpty()) {
            PrintResultDataAccess.delete(activity, taskH.getUuid_task_h());
        }

        for (PrintItem bean : printItemList) {
            PrintResult printResult = new PrintResult(Tool.getUUID());
            printResult.setPrint_type_id(bean.getPrint_type_id());
            printResult.setUser(GlobalData.getSharedGlobalData().getUser());
            printResult.setUuid_task_h(taskH.getUuid_task_h());
            printResult.setDtm_crt_server(bean.getDtm_crt());
            if (bean.getPrint_type_id().equals(Global.PRINT_ANSWER)) {
                printResult.setLabel(bean.getPrint_item_label());
                TaskD taskD = TaskDDataAccess.getMatchDetailForPrint(activity, taskH.getUuid_task_h(), bean.getQuestion_id());
                if (taskD != null) {
                    String answer = taskD.getText_answer();
                    QuestionSet questionSet = QuestionSetDataAccess.getOne(activity, bean.getUuid_scheme(), bean.getQuestion_id(), bean.getQuestion_group_id());
                    if (questionSet != null) {
                        if (questionSet.getAnswer_type().equals(Global.AT_DATE)) {
                            //convert String to date first
                            DateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss");
                            Date date = null;
                            try {
                                date = format.parse(taskD.getText_answer());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //convert date to string
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                            String stringDate = sdf.format(date);
                            answer = stringDate;
                        } else if (Global.AT_CURRENCY.equals(questionSet.getAnswer_type())) {
                            answer = Tool.separateThousand(answer);
                            answer = GlobalData.getSharedGlobalData().getCurrencyType() + answer;
                        } else if (Global.AT_DROPDOWN.equals(questionSet.getAnswer_type())) {
                            answer = taskD.getLov();
                        }
                    }
                    printResult.setValue(answer);
                }
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BRANCH_ADDRESS)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser().getBranch_address());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BRANCH_NAME)) {
                printResult.setLabel(GlobalData.getSharedGlobalData().getUser().getBranch_name());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_BT_ID)) {
                String btAddr = "?";
                try {
                    btAddr = BluetoothAdapter.getDefaultAdapter().getAddress();
                } catch (Exception e) {
                    FireCrash.log(e);

                }
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(btAddr);
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LABEL) || bean.getPrint_type_id().equals(Global.PRINT_LABEL_BOLD) || bean.getPrint_type_id().equals(Global.PRINT_LABEL_CENTER) || bean.getPrint_type_id().equals(Global.PRINT_LABEL_CENTER_BOLD) || bean.getPrint_type_id().equals(Global.PRINT_TIMESTAMP)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGO)) {
                printResult.setLabel("");
                printResult.setValue("");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_NEW_LINE)) {
                printResult.setLabel("\n");
                printResult.setValue("\n");
            } else if (bean.getPrint_type_id().equals(Global.PRINT_USER_NAME)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser().getFullname());
            } else if (bean.getPrint_type_id().equals(Global.PRINT_LOGIN_ID)) {
                printResult.setLabel(bean.getPrint_item_label());
                printResult.setValue(GlobalData.getSharedGlobalData().getUser().getLogin_id());
            }
            else if(bean.getPrint_type_id().equals(Global.PRINT_UNIQUE_RV)){
                printResult.setLabel(bean.getPrint_item_label());
                RvGenerator rvGenerator = new RvGenerator();
                String rvNumber = rvGenerator.generateRVNum(taskH.getTask_id(),taskH.getAppl_no());
                printResult.setValue(rvNumber);
            }
            printResult.setUuid_print_result(Tool.getUUID());
            printResultList.add(printResult);

            PrintResultDataAccess.add(activity, printResult);
        }
    }

    public static boolean isPartial(Context context) {

        GeneralParameter gp2 = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_PARTIAL_SENT);
        boolean isPartial = true;
        if (gp2 != null) {
            if (gp2.getGs_value().equals("0")) {
                isPartial = false;
            } else {
                isPartial = true;
            }
        }
        GeneralParameter gp1 = GeneralParameterDataAccess.getOne(context, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_INTERVAL_AUTOSEND);
        if (gp1 != null && gp1.getGs_value().equals("0")) {
            return false;
        }
        return isPartial;
    }

    public static String submitImage(final Context context, List<TaskD> taskDs) {
        final Notification.Builder mBuilder = new Notification.Builder(context);
        final Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        final NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String errMessage = "";
        String taskId = null;
        int totalPending = 0;
        int count = taskDs.size();
        if (count > 0) {
            Global.setIsUploading(true);
            Global.setIsManualUploading(true);
            for (TaskD taskd : taskDs) {
                try {
                    String uuid_task_h = taskd.getTaskH().getUuid_task_h();
                    String task_id = taskd.getTaskH().getTask_id();
                    TaskH taskH = taskd.getTaskH();
                    String taskName = taskH.getCustomer_name();
                    totalPending = AutoSendImageThread.countPendingImageUpload(context, uuid_task_h);
                    final int totalimage = AutoSendImageThread.countTotalImageTask(context, uuid_task_h);

                    showDialogUploading((Activity) context, mBuilder, inboxStyle, mNotifyManager, taskName, totalimage, totalPending);

                    boolean finale = AutoSendImageThread.checkFinal(totalPending);
                    if (finale) {
                        taskd.setIs_final(Global.TRUE_STRING);
                    }
                    taskd.setTaskH(null);
                    taskd.setUuid_task_h(uuid_task_h);
                    if (taskd.getCount() == null) {
                        taskd.setCount(Global.FALSE_STRING);
                    }
                    List<TaskD> taskDList = new ArrayList<>();
                    taskd.getTaskH().getUser();
                    taskd.getTaskH().getScheme();
                    taskDList.add(taskd);
                    Date startSend = Tool.getSystemDateTime();
                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);
                    task.setTaskD(taskDList);
                    if(Global.PLAN_TASK_ENABLED){
                        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                        String nextPlan = null;
                        if(todayPlanRepo != null){
                            nextPlan = todayPlanRepo.nextPlanBeforeSubmit(taskH.getTask_id());
                        }
                        if(nextPlan != null){
                            task.setUuidTaskHNext(nextPlan);
                        }
                    }

                    SubmitTaskApi api = new SubmitTaskApi(context, task, taskH);
                    HttpConnectionResult serverResult = api.request();
                    if (serverResult != null && serverResult.isOK()) {
                        String resultvalue = serverResult.getResult();
                        try {
                            JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                            int statusCode = responseSubmitTask.getStatus().getCode();
                            if (statusCode == 0) {
                                Date finishSend = Tool.getSystemDateTime();
                                if (Global.IS_DEV)
                                    Log.i(TAG, SUCCESS_SEND);
                                if (finale) {
                                    ToDoList.removeSurveyFromList(task_id);
                                    if (responseSubmitTask.getTaskId() != null)
                                        taskId = responseSubmitTask.getTaskId().toString();
                                    if (taskId != null && taskId.length() > 0) {
                                        doAfterFinish(context, taskd, startSend, finishSend, taskId);
                                        errMessage = "";
                                        Utility.freeMemory();
                                    }
                                } else {
                                    errMessage = context.getString(R.string.upload_queue);
                                    totalPending--;
                                    ToDoList.updateStatusSurvey(task_id, TaskHDataAccess.STATUS_SEND_UPLOADING, totalPending);
                                    taskd.setIs_sent(Global.TRUE_STRING);
                                    TaskDDataAccess.addOrReplace(context, taskd);
                                    Utility.freeMemory();
                                }
                            }
                            else if(statusCode == Global.FAILED_DRAFT_TASK_CODE){
                                errMessage = responseSubmitTask.getStatus().getMessage();
                                TaskManager.isSubmitFailedDraft(context, taskH, errMessage);
                                errMessage = context.getString(R.string.task_failed_draft);
                            }else {
                                if (resultvalue != null) {
                                    if (Global.IS_DEV)
                                        Log.i(TAG, STATIC_AUTO_SEND_IMAGE_EQUALS_TO + resultvalue);
                                    ACRA.getErrorReporter().handleSilentException(new Exception(UPLOAD_IMAGE_EXCEPTION + resultvalue));
                                }
                                errMessage = String.valueOf(statusCode);
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            errMessage = context.getString(R.string.upload_queue);
                            break;
                        }
                    } else if(serverResult != null && !serverResult.isOK()) {
                        errMessage = context.getString(R.string.upload_queue);
                        String statusCode = String.valueOf(serverResult.getStatusCode()).trim();
                        if (!statusCode.isEmpty() && statusCode.charAt(0) != '5') {
                            taskd.setCount(Global.TRUE_STRING);
                            taskd.setIs_sent(Global.FALSE_STRING);
                            TaskDDataAccess.addOrReplace(context, taskd);
                        }
                        break;
                    }
                } catch (Exception ex) {
                    errMessage = IMAGE_FAILED;
                    ex.printStackTrace();
                    break;
                }
            }
        }

        setCounter();
        Global.setIsUploading(false);
        Global.setIsManualUploading(false);
        return errMessage;
    }


    public static void ManualUploadImage(final Activity activity, final List<TaskD> taskDs) {
        final Notification.Builder mBuilder = new Notification.Builder(activity);
        final Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        final NotificationManager mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        new AsyncTask<Void, Void, String>() {
            private String errMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String message = null;
                pauseThread();
                String taskId = null;
                int totalPending = 0;
                int count = taskDs.size();
                if (count > 0) {
                    Global.setIsUploading(true);
                    Global.setIsManualUploading(true);
                    for (TaskD taskD : taskDs) {
                        try {
                            String uuid_task_h = taskD.getTaskH().getUuid_task_h();
                            String task_id = taskD.getTaskH().getTask_id();

                            TaskH taskH = taskD.getTaskH();
                            String taskName = taskH.getCustomer_name();
                            int totalPendingImageTask = AutoSendImageThread.countPendingImageUpload(activity, uuid_task_h);
                            final int totalimage = AutoSendImageThread.countTotalImageTask(activity, uuid_task_h);
                            final int totPending = totalPendingImageTask;
                            if ((taskH.getUuid_task_h()).equals(uuid_task_h)) {
                                showDialogUploading(activity, mBuilder, inboxStyle, mNotifyManager, taskName, totalimage, totPending);
                            }

                            boolean finale = AutoSendImageThread.checkFinal(totalPendingImageTask);
                            if (finale) {
                                taskD.setIs_final(Global.TRUE_STRING);
                            }
                            taskD.setTaskH(null);
                            taskD.setUuid_task_h(uuid_task_h);
                            if (taskD.getCount() == null) {
                                taskD.setCount(Global.FALSE_STRING);
                            }
                            List<TaskD> taskDList = new ArrayList<>();
                            taskD.getTaskH().getUser();
                            taskD.getTaskH().getScheme();
                            taskDList.add(taskD);
                            Date startSend = Tool.getSystemDateTime();
                            JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                            task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                            task.addImeiAndroidIdToUnstructured();
                            task.setTaskH(taskH);
                            task.setTaskD(taskDList);
                            if(Global.PLAN_TASK_ENABLED){
                                TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                String nextPlan = null;
                                if(todayPlanRepo != null){
                                    nextPlan = todayPlanRepo.nextPlanBeforeSubmit(taskH.getTask_id());
                                }
                                if(nextPlan != null){
                                    task.setUuidTaskHNext(nextPlan);
                                }
                            }

                            SubmitTaskApi api = new SubmitTaskApi(activity, task, taskH);
                            HttpConnectionResult serverResult = api.request();
                            if (serverResult != null && serverResult.isOK()) {
                                String resultvalue = serverResult.getResult();
                                try {
                                    JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                                    int statusCode = responseSubmitTask.getStatus().getCode();
                                    if (statusCode == 0) {
                                        Date finishSend = Tool.getSystemDateTime();
                                        if (Global.IS_DEV)
                                            Log.i(TAG, SUCCESS_SEND);
                                        if (finale) {
                                            ToDoList.removeSurveyFromList(task_id);
                                            if (responseSubmitTask.getTaskId() != null)
                                                taskId = responseSubmitTask.getTaskId();
                                            if (taskId != null && taskId.length() > 0) {
                                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                                TimelineManager.insertTimeline(activity, taskH);
                                                doAfterFinish(activity, taskD, startSend, finishSend, taskId);
                                                message = "";
                                                Utility.freeMemory();
                                            }
                                        } else {
                                            message = activity.getString(R.string.upload_queue);
                                            ToDoList.updateStatusSurvey(task_id, TaskHDataAccess.STATUS_SEND_UPLOADING, totalPending);
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                            TaskDDataAccess.addOrReplace(activity, taskD);
                                        }
                                    }
                                    else if(statusCode == Global.FAILED_DRAFT_TASK_CODE){
                                        errMessage = responseSubmitTask.getStatus().getMessage();
                                        isSubmitFailedDraft(activity, taskH, errMessage);
                                        message = activity.getString(R.string.task_failed_draft);
                                    }
                                    else {
                                        if (resultvalue != null) {
                                            if (Global.IS_DEV)
                                                Log.i(TAG, STATIC_AUTO_SEND_IMAGE_EQUALS_TO + resultvalue);
                                            ACRA.getErrorReporter().handleSilentException(new Exception(UPLOAD_IMAGE_EXCEPTION + resultvalue));
                                        }
                                        errMessage = responseSubmitTask.getStatus().getMessage();
                                        message = errMessage;
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    message = activity.getString(R.string.upload_queue);
                                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                    TimelineManager.insertTimeline(activity, taskH);
                                    break;
                                }
                            } else if(serverResult != null && !serverResult.isOK()) {
                                message = activity.getString(R.string.upload_queue);
                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                TimelineManager.insertTimeline(activity, taskH);
                                String statusCode = String.valueOf(serverResult.getStatusCode()).trim();
                                if (!statusCode.isEmpty() && statusCode.charAt(0) != '5') {
                                    taskD.setCount(Global.TRUE_STRING);
                                    taskD.setIs_sent(Global.FALSE_STRING);
                                    TaskDDataAccess.addOrReplace(activity, taskD);
                                }
                                break;
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            message = activity.getString(R.string.upload_queue);
                            e.printStackTrace();
                        }
                    }
                } else {
                    message = activity.getString(R.string.no_internet_connection);
                }

                setCounter();
                Global.setIsUploading(false);
                Global.setIsManualUploading(false);
                return message;
            }

            @Override
            protected void onPostExecute(final String message) {
                stopThread();
                try {
                    if (message == null || message.length() == 0) {
                        showDialogComplete(activity, mBuilder, inboxStyle, mNotifyManager);
                    } else {
                        showDialogError(message, mBuilder, inboxStyle, mNotifyManager);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    Global.setIsUploading(false);
                    Global.setIsManualUploading(false);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static void doAfterFinish(final Context context, TaskD taskd, Date startSend, Date finishSend, String taskId) {
        long time = finishSend.getTime() - startSend.getTime();
        int sec = (int) Math.ceil((double)time / 1000); // millisecond to second
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
        TaskHDataAccess.addOrReplace(context, taskH);

        taskd.setIs_sent(Global.TRUE_STRING);
        TaskDDataAccess.addOrReplace(context, taskd);

        TaskHDataAccess.doBackup(context, taskH);

        // 2017/10/02 | olivia : update status Task di table TaskSummary
        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
            TaskSummary taskSummary = TaskSummaryDataAccess.getOne(context, taskH.getUuid_task_h(), taskH.getUuid_user());
            if (taskSummary != null) {
                boolean isTaskPaid = TaskDDataAccess.isTaskPaid(context, taskH.getUuid_user(), taskH.getUuid_task_h());
                if (isTaskPaid)
                    taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_PAID);
                else
                    taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_FAIL);
                TaskSummaryDataAccess.addOrReplace(context, taskSummary);
            }

            //today plan repo trigger update after submit success
            if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted() && taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)){
                GlobalData.getSharedGlobalData().getTodayPlanRepo().updatePlanByTaskH(taskH, PlanTaskDataAccess.STATUS_FINISH);
            }
        }

        setCounter();
        TimelineManager.insertTimeline(context, taskH);
        NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
    }

    public static boolean saveTask(Activity activity, int mode, SurveyHeaderBean header,
                                   List<QuestionBean> listOfQuestions, String uuid_last_question, boolean isSend, boolean isDraft, boolean isAutoSave) {
        String uuidRvSelected = "";
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        Date dtm_crt = new Date(System.currentTimeMillis());
        String task_id = null;
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.task_id), Context.MODE_PRIVATE);
        int save_task_id = sharedPref.getInt(activity.getString(R.string.save_task_id), 1);
        if (mode == Global.MODE_NEW_SURVEY) {
            task_id = "N~" + save_task_id;
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            save_task_id++;
            sharedPrefEditor.putInt(activity.getString(R.string.save_task_id), save_task_id);
            sharedPrefEditor.commit();
        } else if (mode == Global.MODE_SURVEY_TASK) {
            task_id = header.getTask_id();
        }
        int last_saved_question = 1;
        try {
            if (uuid_last_question != null && uuid_last_question.length() > 0) {
                int i = 1;
                for (QuestionBean bean : Constant.getListOfQuestion().values()) {
                    if (uuid_last_question.equals(bean.getUuid_question_set())) {
                        last_saved_question = i;
                        break;
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }

        oldTaskH = header.getUuid_task_h();

        if (CustomerFragment.getIsEditable()) {
            header.setUuid_task_h(Tool.getUUID());
            header.setRv_number(null);
            header.setStatus_rv(null);
            header.setSubmit_date(null);
        }

        Scheme scheme = SchemeDataAccess.getOne(activity, header.getUuid_scheme());
        TaskH taskH = header.getTaskH();
        taskH.setTask_id(task_id);
        taskH.setDraft_date(dtm_crt);
        taskH.setUser(GlobalData.getSharedGlobalData().getUser());
        taskH.setScheme(scheme);
        taskH.setLast_saved_question(last_saved_question);
        ToDoList.addSurveyToList(new SurveyHeaderBean(taskH), true);

        boolean isAnImage = false;
        for (QuestionBean bean : listOfQuestions) {
            if (bean.getImgAnswer() != null)
                isAnImage = true;
        }

        int i = 1;
        List<TaskD> listTaskD = new ArrayList<>();
        for (QuestionBean bean : listOfQuestions) {
            if (bean.isVisible()) {
                if (Global.AT_DATE_TIME.equalsIgnoreCase(bean.getAnswer_type()) || Global.AT_DATE.equalsIgnoreCase(bean.getAnswer_type())) {
                    String answer = bean.getAnswer();
                    if (answer != null && answer.contains("/")) {
                        String format = "";
                        if(Global.AT_DATE_TIME.equalsIgnoreCase(bean.getAnswer_type()))
                            format = Global.DATE_TIME_STR_FORMAT;
                        else if(Global.AT_DATE.equalsIgnoreCase(bean.getAnswer_type()))
                            format = Global.DATE_STR_FORMAT;
                        Date date = null;
                        try {
                            date = Formatter.parseDate(answer, format);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String finalAnswer = Formatter.formatDate(date, Global.DATE_STR_FORMAT_GSON);
                        bean.setAnswer(finalAnswer);
                    }
                }

                if (Tool.isOptions(bean.getAnswer_type())
                        || Global.AT_TEXT_WITH_SUGGESTION.equals(bean.getAnswer_type())) {
                    try {
                        List<OptionAnswerBean> optAnsBean = bean.getSelectedOptionAnswers();
                        String answer = bean.getAnswer();
                        String[] finalAnswer = new String[1];
                        if (answer != null && answer.length() > 0) {
                            finalAnswer = Tool.split(answer, Global.DELIMETER_DATA);
                        } else {
                            finalAnswer[0] = "";
                        }
                        int j = 0;
                        if(optAnsBean == null){
                            optAnsBean = new ArrayList<>();
                        }
                        if (optAnsBean.isEmpty()) {
                            OptionAnswerBean opt = new OptionAnswerBean("", "");
                            optAnsBean.add(opt);
                        }
                        for (OptionAnswerBean selectedOption : optAnsBean) {
                            TaskD taskD = new TaskD(Tool.getUUID());
                            taskD.setQuestion_group_id(bean.getQuestion_group_id());
                            taskD.setQuestion_id(bean.getQuestion_id());

                            taskD.setImage(bean.getImgAnswer());
                            taskD.setImage_timestamp(bean.getImgTimestamp());
                            if (isPartial(activity)) {
                                if (isAnImage)
                                    taskD.setIs_final(Global.FALSE_STRING);
                                else {
                                    if (i == listOfQuestions.size()) {
                                        taskD.setIs_final(Global.TRUE_STRING);
                                    } else {
                                        taskD.setIs_final(Global.FALSE_STRING);
                                    }
                                }
                            } else {
                                if (i == listOfQuestions.size()) {
                                    taskD.setIs_final(Global.TRUE_STRING);
                                } else {
                                    taskD.setIs_final(Global.FALSE_STRING);
                                }
                            }
                            taskD.setLov(bean.getLovId());
                            taskD.setUsr_crt(uuid_user);
                            taskD.setDtm_crt(dtm_crt);
                            taskD.setUuid_task_h(header.getUuid_task_h());
                            taskD.setQuestion_label(bean.getQuestion_label());
                            taskD.setIs_visible(Global.TRUE_STRING);
                            taskD.setRegex(bean.getRegex());
                            taskD.setIs_readonly(bean.getIs_readonly());
                            taskD.setTaskH(taskH);

                            String lookUpId = selectedOption.getUuid_lookup();
                            String lovCode = selectedOption.getCode();
                            String lovGroup = selectedOption.getLov_group();
                            if (lookUpId != null && lovCode != null) {
                                OptionAnswerBean selectedOption2 = null;
                                if (Global.TAG_RV_NUMBER.equalsIgnoreCase(bean.getTag())) {
                                    uuidRvSelected = lookUpId;
                                    taskD.setTag(bean.getTag());
                                }
                                Lookup lookup = LookupDataAccess.getOne(activity, lookUpId, lovGroup);
                                selectedOption2 = new OptionAnswerBean(lookup);
                                selectedOption2.setSelected(true);
                                if (bean.getTag() != null && bean.getTag().equalsIgnoreCase("Job MH")) {
                                    taskD.setOption_answer_id(selectedOption2.getCode());
                                    taskD.setTag(bean.getTag());
                                } else {
                                    taskD.setOption_answer_id(selectedOption2.getUuid_lookup());
                                }
                                taskD.setUuid_lookup(selectedOption2.getUuid_lookup());
                            }
                            taskD.setLov(lovCode);

                            if (Tool.isOptionsWithDescription(bean.getAnswer_type())) {
                                taskD.setText_answer(finalAnswer[j]);
                            } else if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type()) ||
                                    Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type()) ||
                                    Global.AT_TEXT_WITH_SUGGESTION.equals(bean.getAnswer_type()))
                                taskD.setText_answer(bean.getAnswer());
                            listTaskD.add(taskD);
                            j++;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    TaskD taskD = new TaskD(Tool.getUUID());
                    taskD.setQuestion_group_id(bean.getQuestion_group_id());
                    taskD.setQuestion_id(bean.getQuestion_id());

                    //2019.06.19
                    //Nendi: Bugfix PRDAITMSS-726 | Answer type Currency value converted to exponential
                    if (Global.AT_CURRENCY.equals(bean.getAnswer_type()) && !bean.getAnswer().equals("")) {
                        String finalAnswer = String.format(Locale.US, "%.0f", Double.parseDouble(bean.getAnswer()));
                        finalAnswer = finalAnswer.replace(".", "");
                        taskD.setText_answer(finalAnswer);
                    } else {
                        taskD.setText_answer(bean.getAnswer());
                    }

                    if(bean.getImgAnswer() != null){
                        taskD.setImage(bean.getImgAnswer());
                        taskD.setImage_timestamp(bean.getImgTimestamp());
                    }
                    if (isPartial(activity)) {
                        if (isAnImage)
                            taskD.setIs_final(Global.FALSE_STRING);
                        else {
                            if (i == listOfQuestions.size()) {
                                taskD.setIs_final(Global.TRUE_STRING);
                            } else {
                                taskD.setIs_final(Global.FALSE_STRING);
                            }
                        }
                    } else {
                        if (i == listOfQuestions.size()) {
                            taskD.setIs_final(Global.TRUE_STRING);
                        } else {
                            taskD.setIs_final(Global.FALSE_STRING);
                        }
                    }
                    taskD.setTag(bean.getTag());
                    taskD.setLov(bean.getLovId());
                    taskD.setUsr_crt(uuid_user);
                    taskD.setDtm_crt(dtm_crt);
                    taskD.setUuid_task_h(header.getUuid_task_h());
                    taskD.setQuestion_label(bean.getQuestion_label());
                    if (bean.isVisible()) {
                        taskD.setIs_visible(Global.TRUE_STRING);
                    } else {
                        taskD.setIs_visible(Global.FALSE_STRING);
                    }
                    if (bean.getAnswer_type().equals(Global.AT_GPS) ||
                            bean.getAnswer_type().equals(Global.AT_GPS_N_LBS) ||
                            bean.getAnswer_type().equals(Global.AT_LOCATION) ||
                            bean.getAnswer_type().equals(Global.AT_IMAGE_W_GPS_ONLY) ||
                            bean.getAnswer_type().equals(Global.AT_IMAGE_W_LOCATION)) {
                        try {
                            LocationInfo info = bean.getLocationInfo();
                            taskD.setLatitude(info.getLatitude());
                            taskD.setLongitude(info.getLongitude());
                            taskD.setCid(info.getCid());
                            taskD.setMcc(info.getMcc());
                            taskD.setMnc(info.getMnc());
                            taskD.setLac(info.getLac());
                            taskD.setAccuracy(info.getAccuracy());
                            taskD.setGps_time(info.getGps_time());
                            taskD.setLocation_image(bean.getImgLocation());
                        } catch (Exception e) {             FireCrash.log(e);

                        }
                    }
                    if(Global.AT_ID_CARD_PHOTO.equals(bean.getAnswer_type())){
                        Gson gson = new Gson();
                        taskD.setData_dukcapil(gson.toJson(bean.getResponseImageDkcp()));
                    }
                    taskD.setRegex(bean.getRegex());
                    taskD.setIs_readonly(bean.getIs_readonly());
                    taskD.setTaskH(taskH);
                    listTaskD.add(taskD);
                }
            } else {
                if (!isSend && QuestionBean.isHaveAnswer(bean)) {
                    if (Tool.isOptions(bean.getAnswer_type())) {
                        try {
                            List<OptionAnswerBean> optAnsBean = bean.getSelectedOptionAnswers();
                            String answer = bean.getAnswer();
                            String[] finalAnswer;
                            if (answer != null && answer.length() > 0) {
                                finalAnswer = Tool.split(answer, Global.DELIMETER_DATA);
                            } else {
                                finalAnswer = new String[0];
                            }
                            int j = 0;
                            for (OptionAnswerBean selectedOption : optAnsBean) {
                                TaskD taskD = new TaskD(Tool.getUUID());
                                taskD.setQuestion_group_id(bean.getQuestion_group_id());
                                taskD.setQuestion_id(bean.getQuestion_id());

                                taskD.setImage(bean.getImgAnswer());
                                taskD.setImage_timestamp(bean.getImgTimestamp());
                                if (isPartial(activity)) {
                                    if (isAnImage)
                                        taskD.setIs_final(Global.FALSE_STRING);
                                    else {
                                        if (i == listOfQuestions.size()) {
                                            taskD.setIs_final(Global.TRUE_STRING);
                                        } else {
                                            taskD.setIs_final(Global.FALSE_STRING);
                                        }
                                    }
                                } else {
                                    if (i == listOfQuestions.size()) {
                                        taskD.setIs_final(Global.TRUE_STRING);
                                    } else {
                                        taskD.setIs_final(Global.FALSE_STRING);
                                    }
                                }
                                taskD.setLov(bean.getLovId());
                                taskD.setUsr_crt(uuid_user);
                                taskD.setDtm_crt(dtm_crt);
                                taskD.setUuid_task_h(header.getUuid_task_h());
                                taskD.setQuestion_label(bean.getQuestion_label());
                                taskD.setIs_visible(Global.TRUE_STRING);
                                taskD.setRegex(bean.getRegex());
                                taskD.setIs_readonly(bean.getIs_readonly());
                                taskD.setTaskH(taskH);

                                String lookUpId = selectedOption.getUuid_lookup();
                                String lovCode = selectedOption.getCode();
                                String lovGroup = selectedOption.getLov_group();
                                if (lookUpId != null && lovCode != null) {
                                    OptionAnswerBean selectedOption2 = null;
                                    if (Global.TAG_RV_NUMBER.equalsIgnoreCase(bean.getTag())) {
                                        uuidRvSelected = lookUpId;
                                        taskD.setTag(bean.getTag());
                                    }
                                    Lookup lookup = LookupDataAccess.getOne(activity, lookUpId, lovGroup);
                                    selectedOption2 = new OptionAnswerBean(lookup);
                                    selectedOption2.setSelected(true);
                                    if (bean.getTag() != null && bean.getTag().equalsIgnoreCase("Job MH")) {
                                        taskD.setOption_answer_id(selectedOption2.getCode());
                                        taskD.setTag(bean.getTag());
                                    } else {
                                        taskD.setOption_answer_id(selectedOption2.getUuid_lookup());
                                    }
                                    taskD.setUuid_lookup(selectedOption2.getUuid_lookup());
                                }
                                taskD.setLov(lovCode);

                                if (Tool.isOptionsWithDescription(bean.getAnswer_type())) {
                                    taskD.setText_answer(finalAnswer[j]);
                                } else if (Global.AT_MULTIPLE_ONE_DESCRIPTION.equals(bean.getAnswer_type()) ||
                                        Global.AT_DROPDOWN_W_DESCRIPTION.equals(bean.getAnswer_type()))
                                    taskD.setText_answer(bean.getAnswer());
                                listTaskD.add(taskD);
                                j++;
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    } else {
                        TaskD taskD = new TaskD(Tool.getUUID());
                        taskD.setQuestion_group_id(bean.getQuestion_group_id());
                        taskD.setQuestion_id(bean.getQuestion_id());
                        taskD.setText_answer(bean.getAnswer());
                        taskD.setImage(bean.getImgAnswer());
                        taskD.setImage_timestamp(bean.getImgTimestamp());
                        if (isPartial(activity)) {
                            if (isAnImage)
                                taskD.setIs_final(Global.FALSE_STRING);
                            else {
                                if (i == listOfQuestions.size()) {
                                    taskD.setIs_final(Global.TRUE_STRING);
                                } else {
                                    taskD.setIs_final(Global.FALSE_STRING);
                                }
                            }
                        } else {
                            if (i == listOfQuestions.size()) {
                                taskD.setIs_final(Global.TRUE_STRING);
                            } else {
                                taskD.setIs_final(Global.FALSE_STRING);
                            }
                        }
                        taskD.setTag(bean.getTag());
                        taskD.setLov(bean.getLovId());
                        taskD.setUsr_crt(uuid_user);
                        taskD.setDtm_crt(dtm_crt);
                        taskD.setUuid_task_h(header.getUuid_task_h());
                        taskD.setQuestion_label(bean.getQuestion_label());
                        if (bean.isVisible()) {
                            taskD.setIs_visible(Global.TRUE_STRING);
                        } else {
                            taskD.setIs_visible(Global.FALSE_STRING);
                        }
                        if (bean.getAnswer_type().equals(Global.AT_GPS) ||
                                bean.getAnswer_type().equals(Global.AT_GPS_N_LBS) ||
                                bean.getAnswer_type().equals(Global.AT_LOCATION) ||
                                bean.getAnswer_type().equals(Global.AT_IMAGE_W_GPS_ONLY) ||
                                bean.getAnswer_type().equals(Global.AT_IMAGE_W_LOCATION)) {
                            try {
                                LocationInfo info = bean.getLocationInfo();
                                taskD.setLatitude(info.getLatitude());
                                taskD.setLongitude(info.getLongitude());
                                taskD.setCid(info.getCid());
                                taskD.setMcc(info.getMcc());
                                taskD.setMnc(info.getMnc());
                                taskD.setLac(info.getLac());
                                taskD.setAccuracy(info.getAccuracy());
                                taskD.setGps_time(info.getGps_time());
                                taskD.setLocation_image(bean.getImgLocation());
                            } catch (Exception e) {
                                FireCrash.log(e);

                            }
                        }
                        taskD.setRegex(bean.getRegex());
                        taskD.setIs_readonly(bean.getIs_readonly());
                        taskD.setTaskH(taskH);
                        listTaskD.add(taskD);
                    }

                }
            }
            i++;
        }

        //if revisit, copy taskds that not showed from old taskh, then set new id and new taskh to them to prevent taskd corrupt
        if(CustomerFragment.getIsEditable()){
            List<TaskD> unshowedTaskDRevisit = new ArrayList<>();
            List<TaskD> oldTaskDs = TaskDDataAccess.getAll(activity, oldTaskH, TaskDDataAccess.ALL_TASK);
            if(oldTaskDs != null && !oldTaskDs.isEmpty()){
                boolean taskDHasShowed;
                for (TaskD _taskD:oldTaskDs){
                    taskDHasShowed = false;
                    for(TaskD newTaskD:listTaskD){
                        if(_taskD.getQuestion_id().equals(newTaskD.getQuestion_id())){
                            taskDHasShowed = true;
                            break;
                        }
                    }
                    if(!taskDHasShowed){
                        _taskD.setUuid_task_d(Tool.getUUID());
                        _taskD.setUuid_task_h(header.getUuid_task_h());
                        unshowedTaskDRevisit.add(_taskD);
                    }
                }
            }

            //join unshowed taskds to showed taskd
            listTaskD.addAll(unshowedTaskDRevisit);
        }

        boolean success = TaskDDataAccess.reInsert(activity, taskH, listTaskD, isSend);
        if (success) {
            try {
                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                    boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(activity, uuid_user);
                    if (isRVinFront && !uuidRvSelected.isEmpty()) {
                        String lastRv = taskH.getRv_number();
                        if ((lastRv != null && !lastRv.isEmpty()) && !lastRv.equals(uuidRvSelected)) {
                            ReceiptVoucherDataAccess.updateToNew(activity, uuid_user,lastRv);
                        }
                        ReceiptVoucherDataAccess.updateToUsed(activity, uuid_user, uuidRvSelected);
                        taskH.setRv_number(uuidRvSelected);
                        taskH.setStatus_rv(TaskHDataAccess.STATUS_RV_PENDING);
                    }
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().handleSilentException(new Exception("Error saat save di update lookup/RV"));
            }
            if (isDraft) {
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                TimelineManager.insertTimeline(activity, taskH);
            }
            else {
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
            }
            TaskHDataAccess.addOrReplace(activity, taskH);
            if(!isAutoSave){
                TaskHDataAccess.doBackup(activity, taskH);
            }
            //do change plan when revisit task has been success saved
            if(Global.PLAN_TASK_ENABLED && CustomerFragment.getIsEditable()){
                changePlanForRevisit(activity,taskH);
            }

            //reset isEditable flag to prevent duplicate save task
            if(CustomerFragment.getIsEditable())
                CustomerFragment.setIsEditable(false);
        }
        if (mode == Global.MODE_SURVEY_TASK && StatusTabFragment.handler != null) {
            StatusTabFragment.handler.sendEmptyMessage(0);
        }
        return success;
    }

    public static void changePlanForRevisit(final Context context, TaskH header){
        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
        if(todayPlanRepo == null)
            return;
        try {
            todayPlanRepo.changePlanRevisit(header.getTask_id());
        }
        catch (Exception e){
            Toast.makeText(context, context.getString(R.string.cannot_change_revisit), Toast.LENGTH_SHORT).show();
        }
    }

    public static List<TaskD> getUnsentImage(Activity activity, String uuidTaskH, List<TaskD> listTaskD) {
        boolean isHaveImage = false;
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        List<TaskD> taskDs = new ArrayList<>();
        int i = 1;

        try {
            List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
            if (taskd != null && !taskd.isEmpty())
                isHaveImage = true;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        if (!isHaveImage) {
            for (TaskD d : listTaskD) {
                if (d.getImage() != null) {
                    isHaveImage = true;
                    break;
                }
            }
        }

        for (TaskD d : listTaskD) {
            if (Global.TRUE_STRING.equals(d.getIs_visible())) {
                if (isPartial(activity)) {
                    if (d.getImage() == null) {
                        taskDs.add(d);
                    }
                } else {
                    if (i == taskDs.size())
                        d.setIs_final(Global.TRUE_STRING);
                    taskDs.add(d);
                }
            }
            i++;
        }
        if (!isPartial(activity)) {
            TaskD d = taskDs.get(taskDs.size() - 1);
            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                d.setIs_final(Global.TRUE_STRING);
            }
        } else {
            if (!isHaveImage && !taskDs.isEmpty()) {
                TaskD d = taskDs.get(taskDs.size() - 1);
                if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                    d.setIs_final(Global.TRUE_STRING);
                }
            }
        }
        return taskDs;
    }

    public static void isSubmitSuccess(Activity activity, TaskH taskH, List<TaskD> newlistTaskD) {
        String uuidTaskH = taskH.getUuid_task_h();
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

        if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
            // 2017/10/02 | olivia : update status Task di table TaskSummary
            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
                TaskSummary taskSummary = TaskSummaryDataAccess.getOne(activity, uuidTaskH, taskH.getUuid_user());
                if (taskSummary != null) {
                    boolean isTaskPaid = TaskDDataAccess.isTaskPaid(activity, uuidUser, uuidTaskH);
                    if (isTaskPaid)
                        taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_PAID);
                    else
                        taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_FAIL);
                    TaskSummaryDataAccess.addOrReplace(activity, taskSummary);
                }
            }

            if ((taskH.getIs_prepocessed() != null && Global.FORM_TYPE_VERIFICATION.equals(taskH.getIs_prepocessed())) && MainMenuActivity.mnSVYApproval != null) {
                TaskHDataAccess.deleteWithRelation(activity, taskH);
                String newUUIDTaskH = Tool.getUUID();
                taskH.setUuid_task_h(newUUIDTaskH);
                TaskHDataAccess.addOrReplace(activity, taskH);
                for (TaskD d : newlistTaskD) {
                    d.setTaskH(taskH);
                    d.setUuid_task_d(Tool.getUUID());
                    TaskDDataAccess.addOrReplace(activity, d);
                }
            }
            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                    TimelineManager.insertTimeline(activity, taskH, false, true);
                else
                    TimelineManager.insertTimeline(activity, taskH, true, false);
            } else {
                TimelineManager.insertTimeline(activity, taskH);
            }
        } else if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
            TimelineManager.insertTimeline(activity, taskH);
            NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
        }
    }

    public static String isSubmitUploading(Activity activity, TaskH taskH, String tempmessage) {
        String message = "";
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        try {
            List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, taskH.getUuid_task_h());
            message = submitImage(activity, taskd);
        } catch (Exception e) {
            FireCrash.log(e);
            message = tempmessage;
            Global.setIsManualUploading(false);
            Global.setIsUploading(false);
        }
        return message;
    }

    public static void isSubmitPending(Context activity, TaskH taskH, String errMessage, String result) {
        if (errMessage != null) {
            if(!TaskHDataAccess.STATUS_SEND_UPLOADING.equalsIgnoreCase(taskH.getStatus())) {
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
            }
            if (result != null && result.equals(STATUS_TASK_NOT_MAPPING)) {
                taskH.setMessage(activity.getString(R.string.message_task_not_mapping) + ERROR + result + ")");
            } else if (result != null && result.equals(STATUS_TASK_DELETED)) {
                taskH.setMessage(activity.getString(R.string.message_sending_deleted) + ERROR + result + ")");
            } else if (result != null && result.equals(STATUS_IMEI_FAILED)) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    taskH.setMessage(activity.getString(R.string.message_android_id_not_registered) + ERROR + result + ")");
                }else
                    taskH.setMessage(activity.getString(R.string.message_imei_not_registered) + ERROR + result + ")");
            } else {
                taskH.setMessage(activity.getString(R.string.message_sending_failed) + ERROR + result + ")");
            }
        }
        TaskHDataAccess.addOrReplace(activity, taskH);
        TimelineManager.insertTimeline(activity, taskH);
    }

    public static void isSubmitFailedDraft(Context activity, TaskH taskH, String errMessage) {
        if (errMessage != null) {
            taskH.setStatus(TaskHDataAccess.STATUS_SEND_FAILEDDRAFT);
            taskH.setMessage(errMessage);
            TaskHDataAccess.addOrReplace(activity, taskH);
            TimelineManager.insertTimeline(activity, taskH);
        }
    }

    public static void showDialogUploading(final Activity activity, final Notification.Builder mBuilder, final Notification.BigTextStyle inboxStyle, final NotificationManager mNotifyManager,
                                           final String taskName, final int totalimage, final int totalPending) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Handler handler2 = new Handler(Looper.getMainLooper());
                handler2.post(new Runnable() {
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            int upload = totalimage - totalPending + 1;
                            String counter = activity.getString(R.string.uploading_image_counter, upload, totalimage);
                            String title = activity.getString(R.string.uploading_image_title, taskName);
                            mBuilder.setContentTitle(title)
                                    .setContentText(counter)
                                    .setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
                            inboxStyle.setBigContentTitle(title);
                            inboxStyle.bigText(counter);
                            mBuilder.setStyle(inboxStyle);
                            mBuilder.setProgress(0, 0, true);
                            mBuilder.setOngoing(true);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                                if(mNotifyManager == null)
                                    throw new NullPointerException("mNotifyManager is null");
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
        }
    }

    public static void showDialogComplete(final Activity activity, final Notification.Builder mBuilder, final Notification.BigTextStyle inboxStyle, final NotificationManager mNotifyManager) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            String counter = activity.getString(R.string.upload_complete);
                            mBuilder.setContentText(counter)
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mBuilder.setOngoing(false);
                            mBuilder.setSmallIcon(R.drawable.icon_notif_new);
                            inboxStyle.bigText(counter);
                            mBuilder.setStyle(inboxStyle);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                            {
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                                if(mNotifyManager == null)
                                    throw new NullPointerException("mNotifyManager is null");
                                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                                mBuilder.setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
                                mNotifyManager.createNotificationChannel(notificationChannel);
                            }

                            mNotifyManager.notify(4, mBuilder.build());
                        }
                    }
                });
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public static void showDialogError(final String message, final Notification.Builder mBuilder, final Notification.BigTextStyle inboxStyle, final NotificationManager mNotifyManager) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mBuilder.setContentText(message)
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mBuilder.setOngoing(false);
                            mBuilder.setSmallIcon(R.drawable.icon_notif_new);
                            inboxStyle.bigText(message);
                            mBuilder.setStyle(inboxStyle);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                            {
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                                if(mNotifyManager == null)
                                    throw new NullPointerException("mNotifyManager is null");
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
        }
    }

    public static void setCounter() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                try {
                    newMainActivitySetCounter();
                    viewAdapter.notifyDataSetChanged();
                    if (NewTimelineFragment.timelineHandler != null)
                        NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                    if (PriorityTabFragment.handler != null)
                        PriorityTabFragment.handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        });
    }

    private static void newMainActivitySetCounter(){
        try {
            NewMainActivity.setCounter();
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public static void pauseThread() {
        try {
            if (MainServices.autoSendTaskThread != null) {
                MainServices.autoSendTaskThread.requestWait();
                MainServices.autoSendImageThread.requestWait();
                MainServices.taskListThread.requestWait();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public static void stopThread() {
        try {
            if (MainServices.autoSendTaskThread != null) {
                MainServices.autoSendTaskThread.stopWaiting();
                MainServices.autoSendImageThread.stopWaiting();
                MainServices.taskListThread.stopWaiting();
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public static class RejectWithReSurveyTaskOnBackground extends AsyncTask<Void, Void, String> {
        private Context activity;
        private TaskH taskH;
        private String flag, applicationFlag;
        private String errMessage = null;
        private String errCode;

        public RejectWithReSurveyTaskOnBackground (Context activity, TaskH taskH, String flag, String applicationFlag) {
            this.activity = activity;
            this.taskH = taskH;
            this.flag = flag;
            this.applicationFlag = applicationFlag;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            if (Tool.isInternetconnected(activity)) {
                RejectWithResurveyApi api = new RejectWithResurveyApi(activity, taskH, flag, applicationFlag);
                HttpConnectionResult serverResult = api.request();

                try {
                    result = serverResult.getResult();
                    MssResponseType response = GsonHelper.fromJson(result,
                            MssResponseType.class);
                    if (response.getStatus().getCode() == 0) {
                        result = SUCCESS;
                    } else {
                        errMessage = response.getStatus().getMessage();
                        errCode = String.valueOf(response.getStatus().getCode());
                        result = errCode;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    errMessage = e.getMessage();
                }

                if (errMessage != null && errMessage.length() > 0) {
                    taskH.setFlag_survey(flag);
                    isSubmitPending(activity, taskH, errMessage, result);
                } else {
                    Global.getSharedGlobal().setIsVerifiedByUser(true);
                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
                    try {
                        taskH.setSubmit_date(Tool.getSystemDateTime());
                        TimelineManager.insertTimeline(activity, taskH, true, true);
                        TaskHDataAccess.addOrReplace(activity, taskH);
                    } catch (Exception e) {
                        FireCrash.log(e);
                        errMessage = e.getMessage();
                    }
                }
            } else {
                taskH.setFlag_survey(flag);
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                taskH.setMessage(activity.getString(com.adins.mss.base.R.string.no_internet_connection));
                TimelineManager.insertTimeline(activity, taskH);
                TaskHDataAccess.addOrReplace(activity, taskH);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //EMPTY
        }
    }

    public static class ApproveTaskOnBackground extends AsyncTask<Void, Void, String>  {
        private Context activity;
        private TaskH taskH;
        private String flag;
        private boolean isApprovalTask;
        private String notes;
        private String errMessage = null;

        public  ApproveTaskOnBackground(Context activity, TaskH taskH, String flag, boolean isApprovalTask, String notes) {
            this.activity = activity;
            this.taskH = taskH;
            this.flag = flag;
            this.isApprovalTask = isApprovalTask;
            this.notes = notes;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            if (Tool.isInternetconnected(activity)) {
                String uuidTaskH = taskH.getUuid_task_h();

                ApproveTaskApi api = new ApproveTaskApi(activity, uuidTaskH, flag, isApprovalTask, notes);
                HttpConnectionResult serverResult = api.request();
                try {
                    result = serverResult.getResult();
                    MssResponseType response = GsonHelper.fromJson(result, MssResponseType.class);
                    if (response.getStatus().getCode() == 0) {
                        result = SUCCESS;
                        taskH.setVerification_notes(notes);
                        if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                            taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
                        } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
                        }

                        insertTimeline();

                    }
                    else if(response.getStatus().getCode() == 2008){
                        errMessage = response.getStatus().getMessage();
                        TaskH taskHeader = TaskHDataAccess.getOneHeader(activity,uuidTaskH);
                        //set taskh status and messages to changed
                        if(taskHeader != null && !taskHeader.getStatus().equals(TaskHDataAccess.STATUS_TASK_CHANGED)){
                            taskHeader.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
                            TaskHDataAccess.addOrReplace(activity,taskHeader);
                            taskHeader.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
                            taskHeader.setMessage(errMessage);
                            TimelineManager.insertTimeline(activity,taskHeader);
                        }
                    }
                    else {
                        result = String.valueOf(response.getStatus().getCode());
                        errMessage = response.getStatus().getMessage();
                        taskH.setFlag_survey(flag);
                        taskH.setVerification_notes(notes);
                        isSubmitPending((Activity) activity, taskH, errMessage, result);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            } else {
                taskH.setSubmit_date(taskH.getSubmit_date());
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                taskH.setFlag_survey(flag);
                taskH.setVerification_notes(notes);
                taskH.setMessage(activity.getString(R.string.no_internet_connection));
                TimelineManager.insertTimeline(activity, taskH);
                TaskHDataAccess.addOrReplace(activity, taskH);
            }

            setCounter();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //EMPTY
        }

        private void insertTimeline(){
            try {
                if (!isApprovalTask) {
                    taskH.setSubmit_date(taskH.getSubmit_date());
                    TimelineManager.insertTimeline(activity, taskH, true, true);
                } else {
                    taskH.setSubmit_date(taskH.getSubmit_date());
                    if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                        TimelineManager.insertTimeline(activity, taskH, false, false);
                    } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                        TimelineManager.insertTimeline(activity, taskH, false, true);
                    }
                }
                TaskHDataAccess.addOrReplace(activity, taskH);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }



    public static class VerifTaskOnBackground extends AsyncTask<Void, Void, String> {
        private Activity activity;
        private int mode;
        private SurveyHeaderBean header;
        private List<QuestionBean> listOfQuestions;
        private String notes;
        private ProgressDialog progressDialog;
        private List<TaskD> listTaskD;
        private TaskH taskH;
        private String errMessage = null;
        private String taskId = null;
        private String messageWait;

        public VerifTaskOnBackground(Activity activity, int mode, SurveyHeaderBean header, List<QuestionBean> listOfQuestions, String notes) {
            this.activity = activity;
            this.mode = mode;
            this.header = header;
            this.listOfQuestions = listOfQuestions;
            this.notes = notes;
            messageWait = activity.getString(R.string.progressSend);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            pauseThread();
            String result = null;
            Global.setIsManualSubmit(true);
            boolean saved = saveTask(activity, mode, header, listOfQuestions, null, true, false, false);
            if (saved) {
                this.taskH = header.getTaskH();
                try {
                    listTaskD = TaskDDataAccess.getAll(activity, header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                if (Tool.isInternetconnected(activity)) {
                    taskId = this.taskH.getTask_id();
                    String uuidTaskH = taskH.getUuid_task_h();
                    List<TaskD> taskDs = getUnsentImage(activity, uuidTaskH, listTaskD);

                    if (taskDs != null && !taskDs.isEmpty()) {
                        this.taskH.setSubmit_date(taskH.getSubmit_date());
                        this.taskH.setVerification_notes(notes);
                        VerifTaskApi api = new VerifTaskApi(activity, taskH, taskDs, notes);
                        HttpConnectionResult serverResult = api.request();

                        if (serverResult.isOK()) {
                            String resultvalue = serverResult.getResult();
                            JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                            if (responseSubmitTask.getStatus().getCode() == 0) {
                                String status = responseSubmitTask.getResult();
                                if (status.equalsIgnoreCase(SUCCESS)) {
                                    result = status;
                                    if (responseSubmitTask.getTaskId() != null)
                                        taskId = responseSubmitTask.getTaskId();
                                    else
                                        taskId = activity.getString(R.string.message_no_task_id_from_server);

                                    for (TaskD taskD : listTaskD) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                    }
                                    TaskDDataAccess.addOrReplace(activity, listTaskD);
                                    if (AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h()) > 0) {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }

                                    if (taskId.contains(activity.getString(R.string.message_task_not_mapping)) || taskId.contains(activity.getString(R.string.message_no_task_id_from_server))) {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                                        TaskHDataAccess.addOrReplace(activity, taskH);
                                    } else if (taskId.contains(BEEN_DELETED)) {
                                        TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                                    } else {
                                        this.taskH.setSubmit_date(taskH.getSubmit_date());
                                        this.taskH.setSubmit_result(result);
                                        this.taskH.setTask_id(taskId);
                                        this.taskH.setLast_saved_question(1);
                                        TaskHDataAccess.addOrReplace(activity, this.taskH);
//
                                        if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                            TimelineManager.insertTimeline(activity, this.taskH, true, false);
                                        }
                                    }
                                } else {
                                    result = status;
                                }
                            }
                            else if(TaskManager.STATUS_TASK_CHANGED.
                                    equals(String.valueOf(responseSubmitTask.getStatus().getCode()))){
                                errMessage = responseSubmitTask.getStatus().getMessage();
                                //set taskh status and messages to changed
                                if(!taskH.getStatus().equals(TaskHDataAccess.STATUS_TASK_CHANGED)){
                                    taskH.setStatus(TaskHDataAccess.STATUS_TASK_CHANGED);
                                    TaskHDataAccess.addOrReplace(activity,taskH);
                                    taskH.setIs_prepocessed(Global.FORM_TYPE_VERIFICATION);
                                    taskH.setMessage(errMessage);
                                    TimelineManager.insertTimeline(activity,taskH);
                                }
                            }
                            else {
                                result = String.valueOf(responseSubmitTask.getStatus().getCode());
                                errMessage = responseSubmitTask.getStatus().getMessage();
                                if(result.equalsIgnoreCase(String.valueOf(Global.FAILED_DRAFT_TASK_CODE))){
                                    isSubmitFailedDraft(activity, taskH, errMessage);
                                }else{
                                    isSubmitPending(activity, taskH, errMessage, result);
                                }
                            }
                        }
                    } else {
                        Global.setIsManualSubmit(false);
                        taskH = header.getTaskH();
                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                        this.taskH.setMessage(activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name()));
                        TaskHDataAccess.addOrReplace(activity, taskH);
                        TimelineManager.insertTimeline(activity, taskH);
                    }

                } else {
                    taskH = header.getTaskH();
                    this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    this.taskH.setMessage(activity.getString(R.string.no_internet_connection));
                    TaskHDataAccess.addOrReplace(activity, taskH);
                    TimelineManager.insertTimeline(activity, taskH);
                }
            }

            setCounter();
            Global.setIsManualSubmit(false);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            stopThread();
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
        }
    }

    public static class ForceSendTaskOnBackground extends AsyncTask<Void, Void, String> {
        private Activity activity;
        private String taskId;
        private Notification.Builder mBuilder;
        private Notification.BigTextStyle inboxStyle;
        private NotificationManager mNotifyManager;
        private String uuidTaskFromServer = null;
        private List<TaskD> listTaskD;
        private TaskH taskH;
        private String errMessage = null;
        private String sec;
        private String size;
        private String nTaskId;

        public ForceSendTaskOnBackground(final Activity activity, final String taskId) {
            this.activity = activity;
            this.taskId = taskId;
            mBuilder = new Notification.Builder(activity);
            inboxStyle = new Notification.BigTextStyle();
            mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String message = "";
            String result = null;
            Global.setIsUploading(true);
            Global.setIsManualSubmit(true);
            int totalPending = 0;
            pauseThread();

            taskH = TaskHDataAccess.getOneTaskHeader(activity, taskId);
            if (taskH == null) {
                taskH = TaskHDataAccess.getOneHeader(activity,taskId);
                if (taskH == null) {
                    errMessage = activity.getString(com.adins.mss.base.R.string.message_sending_failed);
                    return errMessage;
                }
            }
            try {
                listTaskD = TaskDDataAccess.getAll(activity, taskH.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
            } catch (Exception e) {
                FireCrash.log(e);
            }

            if (Tool.isInternetconnected(activity)) {
                String uuidTaskH = taskH.getUuid_task_h();
                String uuidUser = taskH.getUuid_user();
                List<TaskD> taskDs = getUnsentImage(activity, uuidTaskH, listTaskD);
                taskH.setSubmit_date(taskH.getSubmit_date());

                for (TaskD d : taskDs) {
                    if (Global.TAG_RV_NUMBER.equals(d.getTag())) {
                        taskH.setRv_number(d.getLookup().getValue());
                    } else if (Global.TAG_RV_NUMBER_MOBILE.equals(d.getTag())) {
                        taskH.setRv_number(d.getText_answer());
                    }
                }

                if (taskDs != null && !taskDs.isEmpty()) {
                    Date startTime = Tool.getSystemDateTime();
                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);
                    task.setTaskD(taskDs);
                    if(Global.PLAN_TASK_ENABLED){
                        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                        String nextPlan = null;
                        if(todayPlanRepo != null){
                            nextPlan = todayPlanRepo.nextPlanBeforeSubmit(taskH.getTask_id());
                        }
                        if(nextPlan != null){
                            task.setUuidTaskHNext(nextPlan);
                        }
                    }
                    String json = GsonHelper.toJson(task);
                    size = String.valueOf(json.getBytes().length);

                    SubmitTaskApi api = new SubmitTaskApi(activity, task, taskH);
                    HttpConnectionResult serverResult = api.request();

                    if (serverResult != null && serverResult.isOK()) {
                        String resultvalue = serverResult.getResult();
                        JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                        if (responseSubmitTask.getStatus().getCode() == 0) {
                            String status = responseSubmitTask.getResult();
                            Date finishTime = Tool.getSystemDateTime();

                            uuidTaskFromServer = responseSubmitTask.getUuidTask();

                            //change to new uuid
                            if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                TaskHDataAccess.deleteByUuid(activity, this.taskH.getUuid_user(), this.taskH.getUuid_task_h());
                                this.taskH.setUuid_task_h(uuidTaskFromServer);
                                TaskHDataAccess.addOrReplace(activity, this.taskH);
                            }

                            long time = finishTime.getTime() - startTime.getTime();
                            sec = "0";
                            try {
                                sec = String.valueOf((int) Math.ceil((double)time / 1000));
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }

                            if (status.equalsIgnoreCase(SUCCESS)) {
                                result = status;
                                if (responseSubmitTask.getTaskId() != null) {
                                    nTaskId = responseSubmitTask.getTaskId();
                                    String appllication = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                    if (appllication == null)
                                        appllication = GlobalData.getSharedGlobalData().getApplication();
                                    if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(appllication) && responseSubmitTask.getCashOnHand() != null) {
                                        GlobalData.getSharedGlobalData().getUser().setCash_on_hand(responseSubmitTask.getCashOnHand());
                                        UserDataAccess.addOrReplace(activity, GlobalData.getSharedGlobalData().getUser());
                                    }
                                } else {
                                    nTaskId = activity.getString(R.string.message_no_task_id_from_server);
                                }
                            } else {
                                result = status;
                            }
                            if (taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING)) {
                                generatePrintResult(activity, taskH);
                            }
                        } else {
                            result = String.valueOf(responseSubmitTask.getStatus().getCode());
                            errMessage = responseSubmitTask.getStatus().getMessage();
                        }
                    } else {
                        try {
                            errMessage = serverResult.getResult();
                        } catch (Exception e) {             FireCrash.log(e);
                        }
                    }

                    if (result != null && result.equalsIgnoreCase(SUCCESS)) {
                        if (nTaskId != null && nTaskId.length() > 0 && !nTaskId.contains("~")) {
                            List<TaskD> newlistTaskD = new ArrayList<>();
                            for (TaskD taskD : listTaskD) {
                                if (taskD.getImage() == null)
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                else {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                        newlistTaskD.add(taskD);
                                    } else {
                                        if (TaskManager.isPartial(activity)) {
                                            taskD.setIs_sent(Global.FALSE_STRING);
                                        } else {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                    }
                                }
                                //change to new uuid
                                if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                    taskD.setUuid_task_h(uuidTaskFromServer);

                                    List<Timeline> timelineList = TimelineDataAccess.getTimelineByTask(activity, uuidUser, uuidTaskH);
                                    if (null != timelineList) {
                                        for (Timeline timeline : timelineList) {
                                            timeline.setUuid_task_h(uuidTaskFromServer);
                                            TimelineDataAccess.addOrReplace(activity, timeline);
                                        }
                                    }
                                }
                                TaskDDataAccess.addOrReplace(activity, taskD);
                            }
                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, taskH.getUuid_task_h());
                            if (imageLeft > 0) {
                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                            } else {
                                try {
                                    List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, taskH.getUuid_user(), taskH.getUuid_task_h());
                                    if (taskd != null && !taskd.isEmpty()) {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                    ToDoList.removeSurveyFromList(taskId);
                                    ACRA.getErrorReporter().putCustomData(ERROR_SUBMIT, e.getMessage());
                                    ACRA.getErrorReporter().handleSilentException(new Exception(AUTO_SEND_TASK_EXCEPTION));
                                }
                            }
                            taskH.setSubmit_date(taskH.getSubmit_date());
                            taskH.setSubmit_duration(sec);
                            taskH.setSubmit_size(size);
                            taskH.setSubmit_result(result);
                            String oldTaskId = taskH.getTask_id();
                            taskH.setTask_id(nTaskId);
                            taskH.setLast_saved_question(1);
                            TaskHDataAccess.addOrReplace(activity, taskH);

                            if(Global.PLAN_TASK_ENABLED && !oldTaskId.equals(nTaskId)){
                                TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                todayPlanRepo.changeTaskhFromPlan(oldTaskId,uuidTaskFromServer);
                            }

                            isSubmitSuccess(activity, taskH, newlistTaskD);
                        }
                    } else if(result !=null && result.equalsIgnoreCase(String.valueOf(Global.FAILED_DRAFT_TASK_CODE))){
                        isSubmitFailedDraft(activity,taskH,errMessage);
                        ToDoList.removeSurveyFromList(taskId);
                        message = activity.getString(R.string.task_failed_draft);
                    }
                    else {
                        isSubmitPending(activity, taskH, errMessage, result);
                        message = errMessage;
                    }

                    if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                        totalPending = AutoSendImageThread.countPendingImageUpload(activity, taskH.getUuid_task_h());
                        final int totalimage = AutoSendImageThread.countTotalImageTask(activity, taskH.getUuid_task_h());

                        showDialogUploading(activity, mBuilder, inboxStyle, mNotifyManager, taskH.getCustomer_name(), totalimage, totalPending);
                        message = isSubmitUploading(activity, taskH, message);
                    }
                } else {
                    mNotifyManager.cancelAll();
                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    taskH.setMessage(activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name()));
                    TaskHDataAccess.addOrReplace(activity, taskH);
                    TimelineManager.insertTimeline(activity, taskH);
                }
            } else {
                mNotifyManager.cancelAll();
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                taskH.setMessage(activity.getString(R.string.no_internet_connection));
                TaskHDataAccess.addOrReplace(activity, taskH);
                TimelineManager.insertTimeline(activity, taskH);
            }

            stopThread();
            setCounter();
            Global.setIsUploading(false);
            Global.setIsManualSubmit(false);
            return message;
        }

        @Override
        protected void onPostExecute(final String message) {
            try {
                if (message == null || message.length() == 0) {
                    String status = taskH.getStatus();
                    if (status.equals(TaskHDataAccess.STATUS_SEND_PENDING)) {
                        mNotifyManager.cancelAll();
                    } else {

                        //today plan repo trigger update after submit success
                        if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted() && status.equals(TaskHDataAccess.STATUS_SEND_SENT)){
                            GlobalData.getSharedGlobalData().getTodayPlanRepo().updatePlanByTaskH(taskH, PlanTaskDataAccess.STATUS_FINISH);
                        }
                        showDialogComplete(activity, mBuilder, inboxStyle, mNotifyManager);
                    }
                } else {
                    showDialogError(message, mBuilder, inboxStyle, mNotifyManager);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                Global.setIsManualSubmit(false);
                Global.setIsUploading(false);
            }
            Global.setIsManualSubmit(false);
            Global.setIsUploading(false);
        }
    }

    public static class SendTaskOnBackground extends AsyncTask<Void, Void, String> {
        private WeakReference<Activity> activity;
        private SurveyHeaderBean header;
        private List<QuestionBean> listOfQuestions;
        private boolean isTaskPaid;
        private int mode;
        private Notification.Builder mBuilder;
        private Notification.BigTextStyle inboxStyle;
        private NotificationManager mNotifyManager;
        private SubmitResult submitResult = new SubmitResult();
        private ProgressDialog progressDialog;
        private List<TaskD> listTaskD;
        private TaskH taskH;
        private String errMessage = null;
        private String sec;
        private String size;
        private String taskId = null;
        private String uuidTaskFromServer = null;

        public SendTaskOnBackground(Activity activity, int mode, SurveyHeaderBean header, List<QuestionBean> listOfQuestions, boolean isTaskPaid) {
            this.activity = new WeakReference<>(activity);
            this.mode = mode;
            this.header = header;
            this.listOfQuestions = listOfQuestions;
            this.isTaskPaid = isTaskPaid;
            mBuilder = new Notification.Builder(activity).setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
            inboxStyle = new Notification.BigTextStyle();
            mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void setProgressDialog(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            //EMPTY
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String message = "";
            String result = null;
            Global.setIsUploading(true);
            Global.setIsManualSubmit(true);
            int totalPending = 0;
            pauseThread();
            saveTask(activity.get(), mode, header, listOfQuestions, null, true, false, false);

            if (Tool.isInternetconnected(activity.get())) {
                taskH = header.getTaskH();
                taskId = taskH.getTask_id();
                String nTaskId = taskId;
                String uuidTaskH = taskH.getUuid_task_h();
                String uuidUser = taskH.getUuid_user();
                taskH.setSubmit_date(taskH.getSubmit_date());

                //kamil 27/07/2017 permintaan mas raymond tambah lokasi untuk kirim taskH ke server
                LocationInfo loc = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);
                if ("null".equalsIgnoreCase(loc.getLatitude())) {
                    taskH.setLatitude("");
                } else {
                    taskH.setLatitude(loc.getLatitude());
                }
                if ("null".equalsIgnoreCase(loc.getLongitude())) {
                    taskH.setLongitude("");
                } else {
                    taskH.setLongitude(loc.getLongitude());
                }

                //Nendi
                submitResult.setTaskId(taskId);
                submitResult.setTaskH(taskH);
                try {
                    // olivia : untuk revisit
                    if (CustomerFragment.getIsEditable()) {
                        setTaskDForRevisit();
                    } else {
                        listTaskD = TaskDDataAccess.getAll(activity.get(), header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }

                List<TaskD> taskDs = getUnsentImage(activity.get(), uuidTaskH, listTaskD);
                for (TaskD d : taskDs) {
                    if (Global.TAG_RV_NUMBER.equals(d.getTag())) {
                        taskH.setRv_number(d.getLookup().getValue());
                    } else if (Global.TAG_RV_NUMBER_MOBILE.equals(d.getTag())) {
                        taskH.setRv_number(d.getText_answer());
                    }
                }

                if (taskDs != null && !taskDs.isEmpty()) {
                    Date startTime = Tool.getSystemDateTime();
                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);
                    task.setTaskD(taskDs);
                    if(Global.PLAN_TASK_ENABLED){
                        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                        String nextPlan = null;
                        if(todayPlanRepo != null){
                            nextPlan = todayPlanRepo.nextPlanBeforeSubmit(taskH.getTask_id());
                        }
                        if(nextPlan != null){
                            task.setUuidTaskHNext(nextPlan);
                        }
                    }
                    String json = GsonHelper.toJson(task);
                    size = String.valueOf(json.getBytes().length);

                    SubmitTaskApi api = new SubmitTaskApi(activity.get(), task, taskH);
                    HttpConnectionResult serverResult = api.request();

                    if (serverResult != null && serverResult.isOK()) {
                        String resultvalue = serverResult.getResult();
                        JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                        if (responseSubmitTask.getStatus().getCode() == 0) {
                            String status = responseSubmitTask.getResult();

                            uuidTaskFromServer = responseSubmitTask.getUuidTask();
                            //change to new uuid
                            if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                TaskHDataAccess.deleteByUuid(activity.get(), taskH.getUuid_user(), taskH.getUuid_task_h());
                                taskH.setUuid_task_h(uuidTaskFromServer);
                                TaskHDataAccess.addOrReplace(activity.get(), taskH);

                                List<Timeline> timelineList = TimelineDataAccess.getTimelineByTask(activity.get(), uuidUser, uuidTaskH);
                                if (null != timelineList) {
                                    for (Timeline timeline : timelineList) {
                                        timeline.setUuid_task_h(uuidTaskFromServer);
                                        TimelineDataAccess.addOrReplace(activity.get(), timeline);
                                    }
                                }
                            }

                            Date finishTime = Tool.getSystemDateTime();
                            long time = finishTime.getTime() - startTime.getTime();
                            sec = String.valueOf((int) Math.ceil((double)time / 1000));
                            if (status == null)
                                status = SUCCESS;
                            if (status.equalsIgnoreCase(SUCCESS)) {
                                result = status;
                                if (responseSubmitTask.getTaskId() != null) {
                                    nTaskId = responseSubmitTask.getTaskId();
                                    String appllication = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                    if (appllication == null)
                                        appllication = GlobalData.getSharedGlobalData().getApplication();
                                    if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(appllication) && responseSubmitTask.getCashOnHand() != null) {
                                        GlobalData.getSharedGlobalData().getUser().setCash_on_hand(responseSubmitTask.getCashOnHand());
                                        UserDataAccess.addOrReplace(activity.get(), GlobalData.getSharedGlobalData().getUser());
                                    }
                                } else {
                                    nTaskId = activity.get().getString(R.string.message_no_task_id_from_server);
                                }
                            } else {
                                result = status;
                            }
                        } else {
                            if (Global.IS_DEV) {
                                if (responseSubmitTask.getStatus().getMessage() == null)
                                    Log.i(TAG, AUTO_SEND_TASK_THREAD_SERVER_CODE_EQUALS + responseSubmitTask.getStatus().getCode());
                                else
                                    Log.i(TAG, AUTO_SEND_TASK_THREAD_SERVER_CODE_EQUALS + responseSubmitTask.getStatus().getCode() + ":" + responseSubmitTask.getStatus().getCode());
                            }
                            result = String.valueOf(responseSubmitTask.getStatus().getCode());
                            errMessage = responseSubmitTask.getStatus().getMessage();
                        }
                    }

                    if (result != null && result.equalsIgnoreCase(SUCCESS)) {
                        if (nTaskId != null && nTaskId.length() > 0 && !nTaskId.contains("~")) {
                            List<TaskD> newlistTaskD = new ArrayList<>();
                            for (TaskD taskD : listTaskD) {
                                if (taskD.getImage() == null)
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                else {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                        newlistTaskD.add(taskD);
                                    } else {
                                        if (TaskManager.isPartial(activity.get())) {
                                            taskD.setIs_sent(Global.FALSE_STRING);
                                        } else {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                    }
                                }

                                //change to new uuid
                                if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                    taskD.setUuid_task_h(uuidTaskFromServer);
                                }
                                TaskDDataAccess.addOrReplace(activity.get(), taskD);
                            }
                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity.get(), taskH.getUuid_task_h());
                            if (imageLeft > 0) {
                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                            } else {
                                try {
                                    List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity.get(), taskH.getUuid_user(), taskH.getUuid_task_h());
                                    if (taskd != null && !taskd.isEmpty()) {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                    ToDoList.removeSurveyFromList(taskId);
                                    ACRA.getErrorReporter().putCustomData(ERROR_SUBMIT, e.getMessage());
                                    ACRA.getErrorReporter().handleSilentException(new Exception(AUTO_SEND_TASK_EXCEPTION));
                                }
                            }
                            taskH.setSubmit_date(taskH.getSubmit_date());
                            taskH.setSubmit_duration(sec);
                            taskH.setSubmit_size(size);
                            taskH.setSubmit_result(result);
                            final String oldTaskId = taskH.getTask_id();
                            taskH.setTask_id(nTaskId);
                            taskH.setLast_saved_question(1);
                            TaskHDataAccess.addOrReplace(activity.get(), taskH);

                            if(Global.PLAN_TASK_ENABLED && !oldTaskId.equals(nTaskId)){
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                        todayPlanRepo.changeTaskhFromPlan(oldTaskId,uuidTaskFromServer);
                                    }
                                });
                            }

                            if (taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING)) {
                                generatePrintResult(activity.get(), taskH);
                            }

                            //Nendi
                            submitResult.setTaskH(taskH);
                            submitResult.setTaskId(nTaskId);

                            isSubmitSuccess(activity.get(), taskH, newlistTaskD);
                        }
                    }
                    else if(result !=null && result.equalsIgnoreCase(String.valueOf(Global.FAILED_DRAFT_TASK_CODE))){
                        isSubmitFailedDraft(activity.get(),taskH,errMessage);
                        ToDoList.removeSurveyFromList(taskId);
                        message = activity.get().getString(R.string.task_failed_draft);
                    }
                    else {
                        isSubmitPending(activity.get(), taskH, errMessage, result);
                        message = errMessage;
                    }

                    if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                        totalPending = AutoSendImageThread.countPendingImageUpload(activity.get(), taskH.getUuid_task_h());
                        final int totalimage = AutoSendImageThread.countTotalImageTask(activity.get(), taskH.getUuid_task_h());

                        showDialogUploading(activity.get(), mBuilder, inboxStyle, mNotifyManager, taskH.getCustomer_name(), totalimage, totalPending);
                        message = isSubmitUploading(activity.get(), taskH, message);

                        //Nendi
                        submitResult.setTaskH(taskH);
                        submitResult.setTaskId(nTaskId);
                    }
                } else {
                    mNotifyManager.cancelAll();
                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    taskH.setMessage(activity.get().getString(R.string.taskd_was_gone, taskH.getCustomer_name()));
                    TaskHDataAccess.addOrReplace(activity.get(), taskH);
                    TimelineManager.insertTimeline(activity.get(), taskH);

                    //Nendi
                    submitResult.setTaskH(taskH);
                    submitResult.setTaskId(nTaskId);
                }
            } else {
                mNotifyManager.cancelAll();
                taskH = header.getTaskH();
                taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                taskH.setMessage(activity.get().getString(R.string.no_internet_connection));
                TaskHDataAccess.addOrReplace(activity.get(), taskH);
                TimelineManager.insertTimeline(activity.get(), taskH);

                //Nendi
                submitResult.setTaskH(taskH);
                submitResult.setTaskId(taskH.getTask_id());
            }

            //Nendi
            submitResult.setResult(result);

            stopThread();
            setCounter();
            Global.setIsUploading(false);
            Global.setIsManualSubmit(false);
            return message;
        }

        @Override
        protected void onPostExecute(final String message) {
            if (progressDialog!=null && progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }

            TaskHDataAccess.doBackup(activity.get(), taskH);

            //Nendi add submitHandler
            if (isTaskPaid) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt(NewMainActivity.KEY_ACTION, 100);
                bundle.putSerializable("submitResult", submitResult);
                msg.setData(bundle);
                Global.setREDIRECT(Global.REDIRECT_TIMELINE);
                NewMainActivity.submitHandler.sendMessage(msg);
                if(!GlobalData.isRequireRelogin())
                    activity.get().finish();
            }

            try {
                if (message == null || message.length() == 0) {
                    String status = taskH.getStatus();
                    if (status.equals(TaskHDataAccess.STATUS_SEND_PENDING)) {
                        mNotifyManager.cancelAll();
                    } else {
                        showDialogComplete(activity.get(), mBuilder, inboxStyle, mNotifyManager);

                        //today plan repo trigger update after submit success
                        if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted() && status.equals(TaskHDataAccess.STATUS_SEND_SENT)){
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    GlobalData.getSharedGlobalData().getTodayPlanRepo().updatePlanByTaskH(taskH, PlanTaskDataAccess.STATUS_FINISH);
                                }
                            });
                        }
                    }
                } else {
                    showDialogError(message, mBuilder, inboxStyle, mNotifyManager);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                Global.setIsManualSubmit(false);
                Global.setIsUploading(false);
            }
            Global.setIsManualSubmit(false);
            Global.setIsUploading(false);
        }

        private void setTaskDForRevisit(){
            try {
                listTaskD = TaskDDataAccess.getAll(activity.get(), header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                header.setAssignment_date(null);
                TaskHDataAccess.addOrReplace(activity.get(), header);
                CustomerFragment.setIsEditable(false);
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    //mulai dari sini sampai bawah sudah tidak terpakai

    @Override
    public void saveAndSendTask(final Activity activity, final String taskId,
                                boolean isPrintable, boolean finishActivity) {
        new AsyncTask<Void, Void, String>() {
            boolean isHaveImage = false;
            private ProgressDialog progressDialog;
            private List<TaskD> listTaskD;
            private TaskH taskH;
            private String errMessage = null;
            private String errCode = null;
            private String sec;
            private String size;
            private String nTaskId;
            private String messageWait = activity.getString(R.string.progressSend);
            private ErrorMessageHandler errorMessageHandler;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
                errorMessageHandler = new ErrorMessageHandler(new IShowError() {
                    @Override
                    public void showError(String errorSubject, String errorMsg, int notifType) {
                        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                        builder.withTitle(errorSubject)
                                .withMessage(errorMsg)
                                .show();
                    }
                });
            }

            @Override
            protected String doInBackground(Void... params) {
                //bong 8 apr 15 - penjagaan jika autoSend null dari mainMenuActivity
                String result = null;
                Global.setIsUploading(true);
                Global.setIsManualSubmit(true);
                this.taskH = TaskHDataAccess.getOneTaskHeader(activity, taskId);

                try {
                    listTaskD = TaskDDataAccess.getAll(activity, taskH.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                } catch (Exception e) {             FireCrash.log(e);
                }

                if (Tool.isInternetconnected(activity)) {
                    try {
                        if (MainServices.autoSendTaskThread != null) {
                            MainServices.autoSendTaskThread.requestWait();
                            MainServices.autoSendImageThread.requestWait();
                            MainServices.taskListThread.requestWait();
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }

                    //minimalisir data
                    String uuidTaskH = taskH.getUuid_task_h();
                    String uuidUser = taskH.getUuid_user();

                    List<TaskD> taskDs = new ArrayList<>();
                    int i = 1;

                    try {
                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                        if (taskd != null && !taskd.isEmpty())
                            isHaveImage = true;
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                    if (isHaveImage == false) {
                        for (TaskD d : listTaskD) {
                            if (d.getImage() != null) {
                                isHaveImage = true;
                                break;
                            }
                        }
                    }
                    for (TaskD d : listTaskD) {
                        if (Global.TRUE_STRING.equals(d.getIs_visible())) {
                            if (isPartial(activity)) {
                                if (d.getImage() == null) {
                                    taskDs.add(d);
                                }
                            } else {
                                if (i == taskDs.size())
                                    d.setIs_final(Global.TRUE_STRING);
                                taskDs.add(d);
                            }
                        }
                        i++;
                    }

                    if (!isPartial(activity)) {
                        TaskD d = taskDs.get(taskDs.size() - 1);
                        if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                            d.setIs_final(Global.TRUE_STRING);
                        }
                    } else {
                        if (!isHaveImage && !taskDs.isEmpty()) {
                            TaskD d = taskDs.get(taskDs.size() - 1);
                            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                d.setIs_final(Global.TRUE_STRING);
                            }
                        }
                    }
                    //-------------------
                    taskH.setSubmit_date(taskH.getSubmit_date());

                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);
                    task.setTaskD(taskDs);

                    if (task.getTaskD() != null && !task.getTaskD().isEmpty()) {
                        String json = GsonHelper.toJson(task);
                        String url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();

                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                            url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                        }

                        size = String.valueOf(json.getBytes().length);

                        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                        HttpConnectionResult serverResult = null;
                        Date startTime = Tool.getSystemDateTime();

                        //Firebase Performance Trace HTTP Request
                        HttpMetric networkMetric =
                                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                        Utility.metricStart(networkMetric, json);

                        try {
                            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            Utility.metricStop(networkMetric, serverResult);
                        } catch (Exception e) {             FireCrash.log(e);
                            e.printStackTrace();
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e1) {
                                FireCrash.log(e);
                            }
                            errMessage = e.getMessage();
                        }
                        if(serverResult == null){
                            errMessage = activity.getString(R.string.request_error);
                            return null;
                        }

                        if (serverResult.isOK()) {
                            String resultvalue = serverResult.getResult();
                            JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                            if (responseSubmitTask.getStatus().getCode() == 0) {
                                String status = responseSubmitTask.getResult();
                                Date finishTime = Tool.getSystemDateTime();
                                long time = finishTime.getTime() - startTime.getTime();
                                sec = "0";
                                try {
                                    sec = String.valueOf((int) Math.ceil((double)time / 1000));
                                } catch (Exception e) {             FireCrash.log(e);
                                }
                                if (status == null)
                                    status = SUCCESS;
                                if (status.equalsIgnoreCase(SUCCESS)) {
                                    result = status;
                                    if (responseSubmitTask.getTaskId() != null) {
                                        nTaskId = responseSubmitTask.getTaskId();
                                        String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                        if (application == null)
                                            application = GlobalData.getSharedGlobalData().getApplication();
                                        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && responseSubmitTask.getCashOnHand() != null) {
                                            GlobalData.getSharedGlobalData().getUser().setCash_on_hand(
                                                    responseSubmitTask.getCashOnHand());
                                            UserDataAccess.addOrReplace(activity, GlobalData.getSharedGlobalData().getUser());
                                        }
                                    } else
                                        nTaskId = activity.getString(R.string.message_no_task_id_from_server);
                                } else {
                                    result = status;
                                }

                                if (taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING))
                                    generatePrintResult(activity, taskH);
                            } else {
                                errMessage = responseSubmitTask.getStatus().getMessage();
                                if (errMessage == null)
                                    errMessage = "Status Code:" + responseSubmitTask.getStatus().getCode();
                                errCode = String.valueOf(responseSubmitTask.getStatus().getCode());
                            }
                        } else {
                            try {
                                result = serverResult.getResult();
                            } catch (Exception e) {             FireCrash.log(e);
                            }
                        }
                    } else {
                        errCode = STATUS_TASKD_MISSING;
                        result = activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name());
                        errMessage = activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name());
                        Global.setIsManualSubmit(false);
                    }

                } else {
                    if (listTaskD == null || listTaskD.isEmpty()) {
                        errCode = STATUS_TASKD_MISSING;
                        result = activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name());
                        errMessage = activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name());
                        Global.setIsManualSubmit(false);
                        return result;
                    }
                    result = activity.getString(R.string.no_internet_connection);
                    errMessage = activity.getString(R.string.no_internet_connection);
                    Global.setIsManualSubmit(false);
                }

                Global.setIsUploading(false);

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }

                if (errMessage != null) {
                    if (errCode != null && (errCode.equals(STATUS_TASK_NOT_MAPPING) || errCode.equals(STATUS_IMEI_FAILED))) {
                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                        TaskHDataAccess.addOrReplace(activity, taskH);
                        if (StatusTabFragment.handler != null)
                            StatusTabFragment.handler.sendEmptyMessage(0);
                        String message = "";
                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                            message = activity.getString(R.string.message_verification_failed);
                        } else {
                            message = activity.getString(R.string.message_sending_failed);
                        }
                        errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                ,message + errMessage
                                ,ErrorMessageHandler.DIALOG_TYPE);
                    } else if (errCode != null && errCode.equals(STATUS_TASK_DELETED)) {
                        if (StatusTabFragment.handler != null)
                            StatusTabFragment.handler.sendEmptyMessage(0);
                        String message = "";
                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                            message = activity.getString(R.string.message_verification_deleted);
                        } else {
                            message = activity.getString(R.string.message_sending_deleted);
                        }
                        errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                ,message + errMessage
                                ,ErrorMessageHandler.DIALOG_TYPE);
                        TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                    } else if (errCode != null && errCode.equals(STATUS_TASKD_MISSING)) {
                        try {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                ,activity.getString(R.string.message_sending_failed) + "\n" + errMessage
                                ,ErrorMessageHandler.DIALOG_TYPE);
                    } else {
                        try {
                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                            ToDoList.updateStatusSurvey(taskId, this.taskH.getStatus(), imageLeft);
                        } catch (Exception e) {             FireCrash.log(e);

                        }
                        TaskHDataAccess.addOrReplace(activity, this.taskH);
                        errorMessageHandler.processError(activity.getString(R.string.error_capital)
                                ,activity.getString(R.string.message_sending_failed) + "\n" + errMessage
                                ,ErrorMessageHandler.DIALOG_TYPE);
                    }
                } else {
                    if (result != null) {
                        if (result.equalsIgnoreCase(SUCCESS)) {
                            for (TaskD taskD : listTaskD) {
                                if (taskD.getImage() == null)
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                else {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                    } else {
                                        if (isPartial(activity)) {
                                            taskD.setIs_sent(Global.FALSE_STRING);
                                        } else {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                    }
                                }
                            }
                            TaskDDataAccess.addOrReplace(activity, listTaskD);
                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                            if (imageLeft > 0) {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                            } else {
                                try {
                                    List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, this.taskH.getUuid_user(), this.taskH.getUuid_task_h());
                                    if (taskd != null && !taskd.isEmpty()) {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                } catch (Exception e) {             FireCrash.log(e);
                                    if (isHaveImage && isPartial(activity)) {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                    ACRA.getErrorReporter().putCustomData(ERROR_SUBMIT, e.getMessage());
                                    ACRA.getErrorReporter().handleSilentException(new Exception("Just for the stacktrace, kena exception saat submit"));
                                }
                            }

                            if (nTaskId.contains(activity.getString(R.string.message_task_not_mapping)) || nTaskId.contains(activity.getString(R.string.message_no_task_id_from_server))) {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                                TaskHDataAccess.addOrReplace(activity, taskH);
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                String message = "";
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    message = activity.getString(R.string.message_verification_failed);
                                } else {
                                    message = activity.getString(R.string.message_sending_failed);
                                }
                                errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                        ,message + nTaskId
                                        ,ErrorMessageHandler.DIALOG_TYPE);
                            } else if (nTaskId.contains(BEEN_DELETED)) {
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                String message = "";
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    message = activity.getString(R.string.message_verification_deleted);
                                } else {
                                    message = activity.getString(R.string.message_sending_deleted);
                                }
                                errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                        ,message + nTaskId
                                        ,ErrorMessageHandler.DIALOG_TYPE);
                                TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                            } else {
                                this.taskH.setSubmit_date(taskH.getSubmit_date());
                                this.taskH.setSubmit_duration(sec);
                                this.taskH.setSubmit_size(size);
                                this.taskH.setSubmit_result(result);
                                this.taskH.setTask_id(nTaskId);
                                this.taskH.setLast_saved_question(1);
                                TaskHDataAccess.addOrReplace(activity, this.taskH);
                                if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                    String message = "";
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        message = activity.getString(R.string.message_verification_success);
                                        if (MainMenuActivity.mnSVYApproval != null) {
                                            TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                                            String newUUIDTaskH = Tool.getUUID();
                                            this.taskH.setUuid_task_h(newUUIDTaskH);
                                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                                            for (TaskD d : listTaskD) {
                                                d.setTaskH(taskH);
                                                d.setUuid_task_d(Tool.getUUID());
                                            }
                                            TaskDDataAccess.addOrReplace(activity, listTaskD);
                                        }
                                    } else {
                                        message = activity.getString(R.string.message_sending_success);
                                    }
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        TimelineManager.insertTimeline(activity, this.taskH, true, false);
                                    } else {
                                        TimelineManager.insertTimeline(activity, this.taskH);
                                        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                                        if (isRVinFront && Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                                            taskH.setStatus_rv(TaskHDataAccess.STATUS_RV_SENT);
                                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                                        }
                                    }
                                    NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                                    builder.withTitle(SUCCESS)
                                            .withMessage(message)
                                            .show();
                                }
                            }
                        } else if (activity.getString(R.string.no_internet_connection).equals(result)) {
                            errorMessageHandler.processError(activity.getString(R.string.info_capital)
                                    ,activity.getString(R.string.no_internet_connection)
                                    ,ErrorMessageHandler.DIALOG_TYPE);
                        }
                    }
                }
                //bong 8 apr 15 - penjagaan jika autoSend null dari mainMenuActivity
                try {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            // UI code goes here
                            try {
                                setMainMenuActivityDrawerCounter();
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                        }
                    });

                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                TaskHDataAccess.doBackup(activity, taskH);
                Global.setIsManualSubmit(false);

            }

        }.execute();

    }

    private void setMainMenuActivityDrawerCounter(){
        try {
            MainMenuActivity.setDrawerCounter();
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void saveAndSendTask(final Activity activity, final int mode,
                                final SurveyHeaderBean header, final List<QuestionBean> listOfQuestions) {

        new AsyncTask<Void, Void, String>() {
            protected ProgressDialog progressDialog;
            protected List<TaskD> listTaskD;
            protected TaskH taskH;
            protected String errMessage = null;
            protected String errCode = null;
            protected String sec;
            protected String size;
            String taskId = null;
            boolean isHaveImage = false;
            private String messageWait = activity.getString(R.string.progressSend);
            private ErrorMessageHandler errorMessageHandler;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
                errorMessageHandler = new ErrorMessageHandler(new IShowError() {
                    @Override
                    public void showError(String errorSubject, String errorMsg, int notifType) {
                        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
                            NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                            builder.withTitle(errorSubject)
                                    .withMessage(errorMsg)
                                    .show();
                        }
                    }
                });
            }

            @Override
            protected String doInBackground(Void... arg0) {
                String result = null;
                Global.setIsUploading(true);
                Global.setIsManualSubmit(true);
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.requestWait();
                        MainServices.autoSendImageThread.requestWait();
                        MainServices.taskListThread.requestWait();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                boolean saved = saveTask(activity, mode, header, listOfQuestions, null, true, false, false);
                if (saved) {
                    this.taskH = header.getTaskH();

                    //kamil 27/07/2017 permintaan mas raymond tambah lokasi untuk kirim taskH ke server
                    LocationInfo loc = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);
                    if ("null".equalsIgnoreCase(loc.getLatitude())) {
                        this.taskH.setLatitude("");
                    } else {
                        this.taskH.setLatitude(loc.getLatitude());
                    }

                    if ("null".equalsIgnoreCase(loc.getLongitude())) {
                        this.taskH.setLongitude("");
                    } else {
                        this.taskH.setLongitude(loc.getLongitude());
                    }

                    try {
                        listTaskD = TaskDDataAccess.getAll(activity, header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                    if (Tool.isInternetconnected(activity)) {
                        taskId = this.taskH.getTask_id();


                        //minimalisir data
                        String uuidTaskH = taskH.getUuid_task_h();
                        String uuidUser = taskH.getUuid_user();

                        List<TaskD> taskDs = new ArrayList<>();
                        int i = 1;

                        try {
                            List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                            if (taskd != null && !taskd.isEmpty())
                                isHaveImage = true;
                        } catch (Exception e) {             FireCrash.log(e);
                            e.printStackTrace();
                        }
                        if (!isHaveImage) {
                            for (TaskD d : listTaskD) {
                                if (d.getImage() != null) {
                                    isHaveImage = true;
                                    break;
                                }
                            }
                        }

                        for (TaskD d : listTaskD) {
                            if (d.getIs_visible().equals(Global.TRUE_STRING)) {
                                if (isPartial(activity)) {
                                    if (d.getImage() == null) {
                                        taskDs.add(d);
                                    }
                                } else {
                                    if (i == taskDs.size())
                                        d.setIs_final(Global.TRUE_STRING);
                                    taskDs.add(d);
                                }
                            }
                            i++;
                        }
                        if (!isPartial(activity)) {
                            TaskD d = taskDs.get(taskDs.size() - 1);
                            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                d.setIs_final(Global.TRUE_STRING);
                            }
                        } else {
                            if (!isHaveImage && !taskDs.isEmpty()) {
                                TaskD d = taskDs.get(taskDs.size() - 1);
                                if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                    d.setIs_final(Global.TRUE_STRING);
                                }
                            }
                        }
                        this.taskH.setSubmit_date(taskH.getSubmit_date());
                        //-------------------

                        JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                        task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                        task.addImeiAndroidIdToUnstructured();
                        task.setTaskH(taskH);
                        task.setTaskD(taskDs);

                        if (task.getTaskD() != null && !task.getTaskD().isEmpty()) {
                            String json = GsonHelper.toJson(task);
                            String url;

                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                            } else {
                                url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                            }

                            size = String.valueOf(json.getBytes().length);

                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                            HttpConnectionResult serverResult = null;
                            Date startTime = Tool.getSystemDateTime();

                            //Firebase Performance Trace HTTP Request
                            HttpMetric networkMetric =
                                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                            Utility.metricStart(networkMetric, json);

                            try {
                                if (GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK().equals(url)) {
                                    if (Global.IS_DEV)
                                        Log.i(TAG, VERIFY_VERIFICATION_TASK);
                                } else {
                                    if (Global.IS_DEV)
                                        Log.i(TAG, SUBMIT_TASK);
                                }
                                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                                Utility.metricStop(networkMetric, serverResult);
                            } catch (Exception e) {             FireCrash.log(e);
                                e.printStackTrace();
                                try {
                                    progressDialog.dismiss();
                                } catch (Exception e1) {
                                    FireCrash.log(e1);
                                }
                                errMessage = e.getMessage();
                            }
                            if(serverResult == null){
                                errMessage = activity.getString(R.string.request_error);
                                return null;
                            }

                            if (serverResult.isOK()) {
                                String resultvalue = serverResult.getResult();
                                JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                                if (responseSubmitTask.getStatus().getCode() == 0) {
                                    String status = responseSubmitTask.getResult();
                                    Date finishTime = Tool.getSystemDateTime();
                                    long time = finishTime.getTime() - startTime.getTime();
                                    sec = String.valueOf((int) Math.ceil((double)time / 1000));
                                    if (status == null)
                                        status = SUCCESS;
                                    if (status.equalsIgnoreCase(SUCCESS)) {
                                        result = status;
                                        if (responseSubmitTask.getTaskId() != null) {
                                            taskId = responseSubmitTask.getTaskId();
                                            String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                            if (application == null)
                                                application = GlobalData.getSharedGlobalData().getApplication();
                                            if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && responseSubmitTask.getCashOnHand() != null) {
                                                GlobalData.getSharedGlobalData().getUser().setCash_on_hand(
                                                        responseSubmitTask.getCashOnHand()
                                                );
                                                UserDataAccess.addOrReplace(activity, GlobalData.getSharedGlobalData().getUser());
                                            }
                                        } else
                                            taskId = activity.getString(R.string.message_no_task_id_from_server);
                                    } else {
                                        result = status;
                                    }


                                    // bong 8 may 15 - call generate print result
                                    if (taskH.getScheme().getIs_printable() != null && taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING))
                                        generatePrintResult(activity, taskH);

                                } else {
                                    result = String.valueOf(responseSubmitTask.getStatus().getCode());
                                    errMessage = responseSubmitTask.getStatus().getMessage();
                                    errCode = String.valueOf(responseSubmitTask.getStatus().getCode());
                                }
                            } else {
                                try {
                                    result = serverResult.getResult();
                                } catch (Exception e) {             FireCrash.log(e);
                                }
                            }
                        } else {
                            result = activity.getString(R.string.request_error);
                            errMessage = activity.getString(R.string.request_error);
                            errCode = STATUS_TASKD_MISSING;
                            Global.setIsManualSubmit(false);
                        }

                    } else {
                        if (listTaskD == null || listTaskD.isEmpty()) {
                            result = activity.getString(R.string.request_error);
                            errMessage = activity.getString(R.string.request_error);
                            errCode = STATUS_TASKD_MISSING;
                            Global.setIsManualSubmit(false);
                            return result;
                        }

                        result = activity.getString(R.string.no_internet_connection);
                        Global.setIsManualSubmit(false);
                    }
                } else {
                    result = activity.getString(R.string.request_error);
                    errMessage = activity.getString(R.string.request_error);
                    errCode = STATUS_SAVE_FAILED;
                    Global.setIsManualSubmit(false);
                    return result;
                }
                Global.setIsUploading(false);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }

                if (errMessage != null) {
                    if (errCode != null && errCode.equals(STATUS_TASKD_MISSING)) {
                        try {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                        } catch (Exception e) {             FireCrash.log(e);
                        }
                        errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                ,activity.getString(R.string.message_sending_failed) + "\n" + errMessage
                                ,ErrorMessageHandler.DIALOG_TYPE);
                    } else if (errCode != null && errCode.equals(STATUS_SAVE_FAILED)) {
                        errorMessageHandler.processError(activity.getString(R.string.warning_capital)
                                ,activity.getString(R.string.message_sending_failed) + "\n" + errMessage
                                ,ErrorMessageHandler.DIALOG_TYPE);
                    } else {
                        Bundle extras = new Bundle();
                        DialogFragment dialogFragment = new SendResultDialog();
                        if (errCode != null && (errCode.equals(STATUS_TASK_NOT_MAPPING) ||  errCode.equals(STATUS_IMEI_FAILED))) {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                            TaskHDataAccess.addOrReplace(activity, taskH);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, this.errMessage);
                            extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                        } else if (errCode != null && errCode.equals(STATUS_TASK_DELETED)) {
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_deleted));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_deleted));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, this.errMessage);
                            extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                            TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                        } else {
                            try {
                                int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                                ToDoList.updateStatusSurvey(taskId, this.taskH.getStatus(), imageLeft);
                            } catch (Exception e) {             FireCrash.log(e);

                            }
                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, errMessage);
                        }
                        GlobalData.getSharedGlobalData().setDoingTask(false);
                        dialogFragment.setArguments(extras);
                        dialogFragment.show(NewMainActivity.fragmentManager, "TAG");
                    }
                } else {
                    if (result != null) {
                        if (result.equalsIgnoreCase(SUCCESS)) {
                            Bundle extras = new Bundle();
                            Intent intent = new Intent(activity.getApplicationContext(), NewMainActivity.class);
                            for (TaskD taskD : listTaskD) {
                                if (taskD.getImage() == null)
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                else {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                    } else {
                                        if (isPartial(activity)) {
                                            taskD.setIs_sent(Global.FALSE_STRING);
                                        } else {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                    }
                                }
                            }
                            TaskDDataAccess.addOrReplace(activity, listTaskD);
                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                            if (imageLeft > 0) {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                            } else {
                                try {
                                    List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, this.taskH.getUuid_user(), this.taskH.getUuid_task_h());
                                    if (taskd != null && !taskd.isEmpty()) {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                } catch (Exception e) {             FireCrash.log(e);
                                    if (isHaveImage && isPartial(activity)) {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                    ACRA.getErrorReporter().putCustomData(ERROR_SUBMIT, e.getMessage());
                                    ACRA.getErrorReporter().handleSilentException(new Exception("Just for the stacktrace, kena exception saat submit"));
                                }
                            }

                            if (taskId.contains(activity.getString(R.string.message_task_not_mapping)) ||
                                    taskId.contains(activity.getString(R.string.message_no_task_id_from_server))) {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                                TaskHDataAccess.addOrReplace(activity, taskH);
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                                } else {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                                }
                                extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                                extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                                extras.putString(Global.BUND_KEY_TASK_ID, this.taskId);
                                extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                            } else if (taskId.contains(BEEN_DELETED)) {
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_deleted));
                                } else {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_deleted));
                                }
                                extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                                extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                                extras.putString(Global.BUND_KEY_TASK_ID, this.taskId);
                                extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                                TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                            } else {
                                this.taskH.setSubmit_date(taskH.getSubmit_date());
                                this.taskH.setSubmit_duration(sec);
                                this.taskH.setSubmit_size(size);
                                this.taskH.setSubmit_result(result);
                                this.taskH.setTask_id(taskId);
                                this.taskH.setLast_saved_question(1);
                                TaskHDataAccess.addOrReplace(activity, this.taskH);

                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, false);
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_success));
                                    if (MainMenuActivity.mnSVYApproval != null) {
                                        TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                                        String newUUIDTaskH = Tool.getUUID();
                                        this.taskH.setUuid_task_h(newUUIDTaskH);
                                        TaskHDataAccess.addOrReplace(activity, this.taskH);
                                        for (TaskD d : listTaskD) {
                                            d.setTaskH(taskH);
                                            d.setUuid_task_d(Tool.getUUID());
                                        }
                                        TaskDDataAccess.addOrReplace(activity, listTaskD);
                                    }
                                } else {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_success));
                                }
                                extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                                extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                                extras.putString(Global.BUND_KEY_TASK_ID, this.taskId);
                                try {
                                    extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, Formatter.stringToBoolean(header.getIs_printable()));
                                } catch (Exception e) {             FireCrash.log(e);
                                    extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                                }

                                if (this.taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        TimelineManager.insertTimeline(activity, this.taskH, true, false);
                                    } else {
                                        TimelineManager.insertTimeline(activity, this.taskH);
                                        boolean isRVinFront = GeneralParameterDataAccess.isRvInFrontEnable(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                                        if (isRVinFront && Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                                            taskH.setStatus_rv(TaskHDataAccess.STATUS_RV_SENT);
                                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                                        }
                                    }
                                }
                            }

                            GlobalData.getSharedGlobalData().setDoingTask(false);
                            intent.putExtras(extras);
                            activity.finish();

                        } else if (activity.getString(R.string.no_internet_connection).equals(result)) {
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler.sendEmptyMessage(0);

                            Bundle extras = new Bundle();
                            DialogFragment dialogFragment = new SendResultDialog();
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            extras.putString(Global.BUND_KEY_SEND_RESULT, "Failed");
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, result);
                            GlobalData.getSharedGlobalData().setDoingTask(false);
                            dialogFragment.setArguments(extras);
                            dialogFragment.show(NewMainActivity.fragmentManager, "TAG");
                        } else {
                            TaskHDataAccess.addOrReplace(activity, this.taskH);

                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_PENDING, imageLeft);
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler.sendEmptyMessage(0);

                            Bundle extras = new Bundle();
                            DialogFragment dialogFragment = new SendResultDialog();
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, "error code: " + result);
                            dialogFragment.setArguments(extras);
                            dialogFragment.show(NewMainActivity.fragmentManager, "TAG");
                            GlobalData.getSharedGlobalData().setDoingTask(false);
                        }
                    }

                }
                //bong 8 apr 15 - penjagaan jika autoSend null dari mainMenuActivity
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                if (errMessage == null && result != null && (result.equalsIgnoreCase(SUCCESS) && taskH != null && taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING))) {
                    try {
                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, taskH.getUuid_user(), taskH.getUuid_task_h());
                        ManualUploadImage(activity, taskd);
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                    }
                }
                Global.setIsManualSubmit(false);
                TaskHDataAccess.doBackup(activity, taskH);
            }
        }.execute();

    }

    @Override
    public void sendTaskManual(final Activity activity, final SurveyHeaderBean header,
                               boolean finishActivity) {
        new AsyncTask<Void, Void, String>() {
            String taskId = null;
            private ProgressDialog progressDialog;
            private List<TaskD> listTaskD;
            private TaskH taskH;
            private String errMessage = null;
            private String sec;
            private String size;
            private ErrorMessageHandler errorMessageHandler;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.progressSave), true);
                errorMessageHandler = new ErrorMessageHandler(new IShowError() {
                    @Override
                    public void showError(String errorSubject, String errorMsg, int notifType) {
                        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                        builder.withTitle(errorSubject)
                                .withMessage(errorMsg)
                                .show();
                    }
                });
            }

            @Override
            protected String doInBackground(Void... params) {
                Global.setIsManualSubmit(true);
                if (MainServices.autoSendTaskThread != null) {
                    MainServices.autoSendTaskThread.requestWait();
                    MainServices.autoSendImageThread.requestWait();
                    MainServices.taskListThread.requestWait();
                }
                this.taskH = header.getTaskH();

                taskId = this.taskH.getTask_id();
                String result = null;
                try {
                    listTaskD = TaskDDataAccess.getAll(activity, taskH.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                } catch (Exception e) {             FireCrash.log(e);
                }
                //minimalisir data
                String uuidTaskH = taskH.getUuid_task_h();
                String uuidUser = taskH.getUuid_user();
                boolean isHaveImage = false;
                List<TaskD> taskDs = new ArrayList<>();
                int i = 1;

                try {
                    List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                    if (taskd != null && !taskd.isEmpty())
                        isHaveImage = true;
                } catch (Exception e) {             FireCrash.log(e);
                    e.printStackTrace();
                }
                if (isHaveImage == false) {
                    for (TaskD d : listTaskD) {
                        if (d.getImage() != null) {
                            isHaveImage = true;
                            break;
                        }
                    }
                }

                for (TaskD d : listTaskD) {
                    if (d.getIs_visible().equals(Global.TRUE_STRING)) {
                        if (isPartial(activity)) {
                            if (d.getImage() == null) {
                                taskDs.add(d);
                            }
                        } else {
                            if (i == taskDs.size())
                                d.setIs_final(Global.TRUE_STRING);
                            taskDs.add(d);
                        }
                    }
                    i++;
                }
                if (!isPartial(activity)) {
                    TaskD d = taskDs.get(taskDs.size() - 1);
                    if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                        d.setIs_final(Global.TRUE_STRING);
                    }
                } else {
                    if (!isHaveImage && !taskDs.isEmpty()) {
                        TaskD d = taskDs.get(taskDs.size() - 1);
                        if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                            d.setIs_final(Global.TRUE_STRING);
                        }
                    }
                }
                this.taskH.setSubmit_date(taskH.getSubmit_date());

                JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                task.addImeiAndroidIdToUnstructured();
                task.setTaskH(taskH);
                task.setTaskD(taskDs);

                String json = GsonHelper.toJson(task);
                String url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                size = String.valueOf(json.getBytes().length);

                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                HttpConnectionResult serverResult = null;
                Date startTime = Tool.getSystemDateTime();

                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                } catch (Exception e) {             FireCrash.log(e);
                    e.printStackTrace();
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e1) {
                        FireCrash.log(e1);
                    }
                    errMessage = e.getMessage();
                }
                if(serverResult == null){
                    errMessage = activity.getString(R.string.request_error);
                    return null;
                }

                if (serverResult.isOK()) {
                    String resultvalue = serverResult.getResult();
                    JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                    if (responseSubmitTask.getStatus().getCode() == 0) {
                        String status = responseSubmitTask.getResult();
                        Date finishTime = Tool.getSystemDateTime();
                        long time = finishTime.getTime() - startTime.getTime();
                        sec = String.valueOf((int) Math.ceil((double) time / 1000));
                        if (status.equalsIgnoreCase(SUCCESS)) {
                            result = status;
                            if (responseSubmitTask.getTaskId() != null) {
                                taskId = responseSubmitTask.getTaskId();
                                String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                if (application == null)
                                    application = GlobalData.getSharedGlobalData().getApplication();
                                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(application) && responseSubmitTask.getCashOnHand() != null) {
                                    GlobalData.getSharedGlobalData().getUser().setCash_on_hand(
                                            responseSubmitTask.getCashOnHand()
                                    );
                                    UserDataAccess.addOrReplace(activity, GlobalData.getSharedGlobalData().getUser());
                                }
                            }
                        } else {
                            result = status;
                        }

                    } else {
                        errMessage = responseSubmitTask.getStatus().getMessage();
                    }

                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }

                if (errMessage != null) {
                    TaskHDataAccess.addOrReplace(activity, this.taskH);
                    errorMessageHandler.processError(activity.getString(R.string.error_capital)
                            ,activity.getString(R.string.message_sending_failed) + "\n" + errMessage
                            ,ErrorMessageHandler.DIALOG_TYPE);
                } else {
                    if (result.equalsIgnoreCase(SUCCESS)) {
                        for (TaskD taskD : listTaskD) {
                            if (taskD.getImage() == null)
                                taskD.setIs_sent(Global.TRUE_STRING);
                            else {
                                if (isPartial(activity)) {
                                    taskD.setIs_sent(Global.FALSE_STRING);
                                } else {
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                }
                            }
                        }
                        TaskDDataAccess.addOrReplace(activity, listTaskD);
                        if (AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h()) > 0) {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                            TimelineManager.insertTimeline(activity, taskH);
                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                        } else {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                            ToDoList.removeSurveyFromList(taskId);
                        }
                        TaskHDataAccess.doBackup(activity, taskH);
                        this.taskH.setSubmit_date(taskH.getSubmit_date());
                        this.taskH.setSubmit_duration(sec);
                        this.taskH.setSubmit_size(size);
                        this.taskH.setSubmit_result(result);
                        this.taskH.setTask_id(taskId);
                        this.taskH.setLast_saved_question(1);
                        TaskHDataAccess.addOrReplace(activity, this.taskH);
                        if (this.taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT))
                            TimelineManager.insertTimeline(activity, this.taskH);
                    }
                }
                if (MainServices.autoSendTaskThread != null) {
                    MainServices.autoSendTaskThread.stopWaiting();
                    MainServices.autoSendImageThread.stopWaiting();
                    MainServices.taskListThread.stopWaiting();
                }
                Global.setIsManualSubmit(false);
            }

        }.execute();
    }

    public void sendRejectedWithReSurveyTask(final Context activity, final TaskH taskH,
                                             final String flag, final String applicationFlag) {
        new AsyncTask<String, Void, String>() {
            private ProgressDialog progressDialog;
            private String errMessage = null;
            private String errCode;

            @Override
            protected void onPreExecute() {
                //EMPTY
            }

            @Override
            protected String doInBackground(String... params) {
                String result = null;
                if (Tool.isInternetconnected(activity)) {
                    String uuidTaskH = taskH.getUuid_task_h();
                    RequestRejectedWithResurvey request = new RequestRejectedWithResurvey();
                    request.setUuid_task_h(uuidTaskH);
                    request.setFlag(flag);
                    request.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    request.setUuid_ms_user(taskH.getUuid_resurvey_user());
                    request.setIs_suggested(taskH.getResurvey_suggested());
                    request.setNotes(taskH.getVerification_notes());
                    request.setAudit(GlobalData.getSharedGlobalData()
                            .getAuditData());
                    request.addImeiAndroidIdToUnstructured();

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData()
                            .getURL_SUBMITVERIFICATIONTASK();
                    if (applicationFlag.equals(Global.APPROVAL_FLAG)) {
                        url = GlobalData.getSharedGlobalData()
                                .getURL_SUBMITAPPROVALTASK();
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
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e1) {
                            FireCrash.log(e1);
                        }
                        errMessage = e.getMessage();
                    }

                    try {
                        result = serverResult.getResult();
                        MssResponseType response = GsonHelper.fromJson(result,
                                MssResponseType.class);
                        if (response.getStatus().getCode() == 0) {
                            result = SUCCESS;
                        } else {
                            errMessage = result;
                            errCode = String.valueOf(response.getStatus().getCode());
                        }
                    } catch (Exception e) {             FireCrash.log(e);
                        errMessage = e.getMessage();
                    }

                    if (errMessage != null && errMessage.length() > 0) {
                        taskH.setFlag_survey(flag);
                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);

                        if (errCode != null && errCode.equals(STATUS_TASK_NOT_MAPPING)) {
                            taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_task_not_mapping) + ERROR + errCode + ")");
                        } else if (errCode != null && errCode.equals(STATUS_TASK_DELETED)) {
                            taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_sending_deleted) + ERROR + errCode + ")");
                        }  else if (result != null && result.equals(STATUS_IMEI_FAILED)) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                taskH.setMessage(activity.getString(R.string.message_android_id_not_registered) + ERROR + result + ")");
                            }else
                                taskH.setMessage(activity.getString(R.string.message_imei_not_registered) + ERROR + result + ")");
                        } else {
                            taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_sending_failed) + ERROR + errCode + ")");
                        }

                        TimelineManager.insertTimeline(activity, taskH);
                        TaskHDataAccess.addOrReplace(activity, taskH);

                    } else {
                        if (activity.getString(R.string.no_internet_connection).equals(
                                result)) {
                            taskH.setFlag_survey(flag);
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                            taskH.setMessage(activity.getString(com.adins.mss.base.R.string.no_internet_connection));
                            TimelineManager.insertTimeline(activity, taskH);
                            TaskHDataAccess.addOrReplace(activity, taskH);
                        } else {
                            Global.getSharedGlobal().setIsVerifiedByUser(true);
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
                            try {
                                taskH.setSubmit_date(Tool.getSystemDateTime());
                                TimelineManager.insertTimeline(activity, taskH, true, true);
                                TaskHDataAccess.addOrReplace(activity, taskH);
                            } catch (Exception e) {             FireCrash.log(e);
                                errMessage = e.getMessage();
                            }
                        }
                    }
                } else {
                    result = activity.getString(R.string.no_internet_connection);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                //EMPTY
            }
        }.execute();
    }

    public void sendApprovalTask(final Activity activity, final SurveyHeaderBean header, final String flag, final boolean isApprovalTask) {
        new AsyncTask<Void, Void, String>() {
            private ProgressDialog progressDialog;
            private String errMessage = null;
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.progressSend), true);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = null;
                if (Tool.isInternetconnected(activity)) {
                    String uuidTaskH = header.getUuid_task_h();
                    JsonRequestApprovalTask request = new JsonRequestApprovalTask();
                    request.setUuid_task_h(uuidTaskH);
                    request.setFlag(flag);
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    request.addImeiAndroidIdToUnstructured();

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_SUBMITAPPROVALTASK();
                    if (!isApprovalTask)
                        url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();

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
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e1) {
                            FireCrash.log(e1);
                        }
                        errMessage = e.getMessage();
                    }
                    if(serverResult == null){
                        errMessage = activity.getString(R.string.request_error);
                        return null;
                    }

                    try {
                        result = serverResult.getResult();
                        MssResponseType response = GsonHelper.fromJson(result, MssResponseType.class);
                        if (response.getStatus().getCode() == 0) {
                            result = SUCCESS;
                        } else {
                            errMessage = result;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    result = activity.getString(R.string.no_internet_connection);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if (errMessage != null && errMessage.length() > 0) {
                    Bundle extras = new Bundle();
                    Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                    extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                    if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                        extras.putString(Global.BUND_KEY_SEND_RESULT, APPROVAL_TASK_FAILED);
                    } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                        extras.putString(Global.BUND_KEY_SEND_RESULT, REJECTION_TASK_FAILED);
                    }
                    extras.putString(Global.BUND_KEY_TASK_ID, errMessage);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                    activity.finish();
                    GlobalData.getSharedGlobalData().setDoingTask(false);
                } else {
                    if (activity.getString(R.string.no_internet_connection).equals(result)) {
                        Bundle extras = new Bundle();
                        Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                        extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                        if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, APPROVAL_TASK_FAILED);
                        } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, REJECTION_TASK_FAILED);
                        }
                        extras.putString(Global.BUND_KEY_TASK_ID, result);
                        intent.putExtras(extras);
                        activity.startActivity(intent);
                        activity.finish();
                        GlobalData.getSharedGlobalData().setDoingTask(false);
                    } else {
                        TaskH taskH = header.getTaskH();
                        Bundle extras = new Bundle();
                        Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                        extras.putString(Global.BUND_KEY_TASK_ID, taskH.getTask_id());
                        extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, false);
                        if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, "Task Approved");
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                            taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
                        } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, "Task Rejected");
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
                        }

                        intent.putExtras(extras);
                        try {
                            if (!isApprovalTask) {
                                taskH.setSubmit_date(taskH.getSubmit_date());
                                TimelineManager.insertTimeline(activity, taskH, true, true);
                            } else {
                                taskH.setSubmit_date(taskH.getSubmit_date());
                                if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                                    TimelineManager.insertTimeline(activity, taskH, false, false);
                                } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                                    TimelineManager.insertTimeline(activity, taskH, false, true);
                                }
                            }
                            TaskHDataAccess.addOrReplace(activity, taskH);
                            TaskHDataAccess.doBackup(activity, taskH);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        activity.startActivity(intent);
                        activity.finish();
                        GlobalData.getSharedGlobalData().setDoingTask(false);
                    }
                }
            }
        }.execute();
    }

    public void sendApprovalTask(final Activity activity, final SurveyHeaderBean header, final String flag, final boolean isApprovalTask, final String notes) {
        new AsyncTask<Void, Void, String>() {
            private ProgressDialog progressDialog;
            private String errMessage = null;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.progressSend), true);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = null;
                if (Tool.isInternetconnected(activity)) {
                    String uuidTaskH = header.getUuid_task_h();
                    JsonRequestApprovalTask request = new JsonRequestApprovalTask();
                    request.setUuid_task_h(uuidTaskH);
                    request.setFlag(flag);
                    request.setNotes(notes);
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    request.addImeiAndroidIdToUnstructured();

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_SUBMITAPPROVALTASK();
                    if (!isApprovalTask)
                        url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();

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
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e1) {
                            FireCrash.log(e1);
                        }
                        errMessage = e.getMessage();
                    }
                    if(serverResult == null){
                        errMessage = activity.getString(R.string.request_error);
                        return null;
                    }

                    try {
                        result = serverResult.getResult();
                        MssResponseType response = GsonHelper.fromJson(result, MssResponseType.class);
                        if (response.getStatus().getCode() == 0) {
                            result = SUCCESS;
                        } else {
                            errMessage = result;
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    result = activity.getString(R.string.no_internet_connection);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }
                if (errMessage != null && errMessage.length() > 0) {
                    Bundle extras = new Bundle();
                    Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                    extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                    if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                        extras.putString(Global.BUND_KEY_SEND_RESULT, APPROVAL_TASK_FAILED);
                    } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                        extras.putString(Global.BUND_KEY_SEND_RESULT, REJECTION_TASK_FAILED);
                    }
                    extras.putString(Global.BUND_KEY_TASK_ID, errMessage);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    if (activity.getString(R.string.no_internet_connection).equals(result)) {
                        Bundle extras = new Bundle();
                        Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                        extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                        if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, APPROVAL_TASK_FAILED);
                        } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, REJECTION_TASK_FAILED);
                        }
                        extras.putString(Global.BUND_KEY_TASK_ID, result);
                        intent.putExtras(extras);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        TaskH taskH = header.getTaskH();
                        Bundle extras = new Bundle();
                        Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                        extras.putString(Global.BUND_KEY_TASK_ID, taskH.getTask_id());
                        extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, false);
                        if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, "Task Approved");
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                            taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
                        } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                            extras.putString(Global.BUND_KEY_SEND_RESULT, "Task Rejected");
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
                        }

                        intent.putExtras(extras);
                        try {
                            if (!isApprovalTask) {
                                taskH.setSubmit_date(taskH.getSubmit_date());
                                TimelineManager.insertTimeline(activity, taskH, true, true);
                            } else {
                                taskH.setSubmit_date(taskH.getSubmit_date());
                                if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                                    TimelineManager.insertTimeline(activity, taskH, false, false);
                                } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                                    TimelineManager.insertTimeline(activity, taskH, false, true);
                                }
                            }
                            TaskHDataAccess.addOrReplace(activity, taskH);
                            TaskHDataAccess.doBackup(activity, taskH);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
            }
        }.execute();
    }

    public void sendApprovalTaskOnBackground(final Context activity, final TaskH taskH, final String flag, final boolean isApprovalTask, final String notes) {
        new AsyncTask<Void, Void, String>() {
            String taskId = null;
            private ProgressDialog progressDialog;
            private String errMessage = null;

            @Override
            protected void onPreExecute() {
                //EMPTY
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = null;
                if (Tool.isInternetconnected(activity)) {
                    String uuidTaskH = taskH.getUuid_task_h();
                    JsonRequestApprovalTask request = new JsonRequestApprovalTask();
                    request.setUuid_task_h(uuidTaskH);
                    request.setFlag(flag);
                    request.setNotes(notes);
                    request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    request.addImeiAndroidIdToUnstructured();

                    String json = GsonHelper.toJson(request);
                    String url = GlobalData.getSharedGlobalData().getURL_SUBMITAPPROVALTASK();
                    if (!isApprovalTask)
                        url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();

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
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                        try {
                            progressDialog.dismiss();
                        } catch (Exception e1) {
                            FireCrash.log(e1);
                        }
                        errMessage = e.getMessage();
                    }
                    if(serverResult == null){
                        errMessage = activity.getString(R.string.request_error);
                        return null;
                    }

                    try {
                        result = serverResult.getResult();
                        MssResponseType response = GsonHelper.fromJson(result, MssResponseType.class);
                        if (response.getStatus().getCode() == 0) {
                            result = SUCCESS;
                            taskH.setVerification_notes(notes);
                            if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                taskH.setIs_prepocessed(Global.FORM_TYPE_APPROVAL);
                            } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_REJECTED);
                            }
                            sendApprovalTaskInsertTimeline(isApprovalTask,taskH, flag,activity);

                        } else {
                            errMessage = String.valueOf(response.getStatus().getCode());
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                            taskH.setFlag_survey(flag);
                            taskH.setVerification_notes(notes);

                            if (errMessage != null && errMessage.equals(STATUS_TASK_NOT_MAPPING)) {
                                taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_task_not_mapping) + ERROR + errMessage + ")");
                            } else if (errMessage != null && errMessage.equals(STATUS_TASK_DELETED)) {
                                taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_sending_deleted) + ERROR + errMessage + ")");
                            }  else if (result != null && result.equals(STATUS_IMEI_FAILED)) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                    taskH.setMessage(activity.getString(R.string.message_android_id_not_registered) + ERROR + result + ")");
                                }else
                                    taskH.setMessage(activity.getString(R.string.message_imei_not_registered) + ERROR + result + ")");
                            } else {
                                taskH.setMessage(activity.getString(com.adins.mss.base.R.string.message_sending_failed) + ERROR + errMessage + ")");
                            }

                            TimelineManager.insertTimeline(activity, taskH);
                            TaskHDataAccess.addOrReplace(activity, taskH);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                } else {
                    result = activity.getString(R.string.no_internet_connection);
                    taskH.setSubmit_date(taskH.getSubmit_date());
                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    taskH.setFlag_survey(flag);
                    taskH.setVerification_notes(notes);
                    taskH.setMessage(activity.getString(R.string.no_internet_connection));
                    TimelineManager.insertTimeline(activity, taskH);
                    TaskHDataAccess.addOrReplace(activity, taskH);
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            newMainActivitySetCounter();
                            if (NewTimelineFragment.timelineHandler != null)
                                NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }
                });
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                //EMPTY
            }
        }.execute();
    }

    private void sendApprovalTaskInsertTimeline(boolean isApprovalTask, TaskH taskH ,String flag ,Context activity){
        try {
            if (!isApprovalTask) {
                taskH.setSubmit_date(taskH.getSubmit_date());
                TimelineManager.insertTimeline(activity, taskH, true, true);

            } else {
                taskH.setSubmit_date(taskH.getSubmit_date());
                if (flag.equals(Global.FLAG_FOR_APPROVALTASK)) {
                    TimelineManager.insertTimeline(activity, taskH, false, false);
                } else if (flag.equals(Global.FLAG_FOR_REJECTEDTASK)) {
                    TimelineManager.insertTimeline(activity, taskH, false, true);
                }
            }
            TaskHDataAccess.addOrReplace(activity, taskH);
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public void sendVerificationTaskOnBackground(final Activity activity, final int mode,
                                                 final SurveyHeaderBean header, final List<QuestionBean> listOfQuestions, final String notes) {

        final NotificationManager mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        new AsyncTask<Void, Void, String>() {
            protected ProgressDialog progressDialog;
            protected List<TaskD> listTaskD;
            protected TaskH taskH;
            protected String errMessage = null;
            protected String errCode = null;
            String taskId = null;
            private String messageWait = activity.getString(R.string.progressSend);

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
            }

            @Override
            protected String doInBackground(Void... arg0) {
                String result = null;
                Global.setIsManualSubmit(true);
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.requestWait();
                        MainServices.autoSendImageThread.requestWait();
                        MainServices.taskListThread.requestWait();
                    }
                } catch (Exception e) {             FireCrash.log(e);
                }
                boolean saved = saveTask(activity, mode, header, listOfQuestions, null, true, false, false);
                if (saved) {
                    this.taskH = header.getTaskH();
                    try {
                        listTaskD = TaskDDataAccess.getAll(activity, header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                    if (Tool.isInternetconnected(activity)) {

                        taskId = this.taskH.getTask_id();

                        String uuidTaskH = taskH.getUuid_task_h();
                        String uuidUser = taskH.getUuid_user();
                        boolean isHaveImage = false;
                        List<TaskD> taskDs = new ArrayList<>();
                        int i = 1;

                        try {
                            List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                            if (taskd != null && !taskd.isEmpty())
                                isHaveImage = true;
                        } catch (Exception e) {             FireCrash.log(e);
                            e.printStackTrace();
                        }
                        if (isHaveImage == false) {
                            for (TaskD d : listTaskD) {
                                if (d.getImage() != null) {
                                    isHaveImage = true;
                                    break;
                                }
                            }
                        }

                        for (TaskD d : listTaskD) {
                            if (d.getIs_visible().equals(Global.TRUE_STRING)) {
                                if (isPartial(activity)) {
                                    if (d.getImage() == null) {
                                        taskDs.add(d);
                                    }
                                } else {
                                    if (i == taskDs.size())
                                        d.setIs_final(Global.TRUE_STRING);
                                    taskDs.add(d);
                                }
                            }
                            i++;
                        }
                        if (!isPartial(activity)) {
                            TaskD d = taskDs.get(taskDs.size() - 1);
                            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                d.setIs_final(Global.TRUE_STRING);
                            }
                        } else {
                            if (!isHaveImage && !taskDs.isEmpty()) {
                                TaskD d = taskDs.get(taskDs.size() - 1);
                                if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                    d.setIs_final(Global.TRUE_STRING);
                                }
                            }
                        }
                        this.taskH.setSubmit_date(taskH.getSubmit_date());
                        this.taskH.setVerification_notes(notes);
                        //-------------------

                        JsonRequestVerificationTask task = new JsonRequestVerificationTask();
                        task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                        task.addImeiAndroidIdToUnstructured();
                        task.setTaskH(taskH);
                        task.setTaskD(taskDs);
                        task.setNotes(notes);

                        if (task.getTaskD() != null && !task.getTaskD().isEmpty()) {
                            String json = GsonHelper.toJson(task);
                            String url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();

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
                            } catch (Exception e) {             FireCrash.log(e);
                                e.printStackTrace();
                                try {
                                    progressDialog.dismiss();
                                } catch (Exception e1) {
                                    FireCrash.log(e1);
                                }
                                errMessage = e.getMessage();
                            }
                            if (serverResult.isOK()) {
                                String resultvalue = serverResult.getResult();
                                JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                                if (responseSubmitTask.getStatus().getCode() == 0) {
                                    String status = responseSubmitTask.getResult();
                                    if (status == null)
                                        status = SUCCESS;
                                    if (status.equalsIgnoreCase(SUCCESS)) {
                                        result = status;
                                        if (responseSubmitTask.getTaskId() != null)
                                            taskId = responseSubmitTask.getTaskId();
                                        else
                                            taskId = activity.getString(R.string.message_no_task_id_from_server);

                                        for (TaskD taskD : listTaskD) {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                        TaskDDataAccess.addOrReplace(activity, listTaskD);
                                        if (AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h()) > 0) {
                                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                        } else {
                                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                            ToDoList.removeSurveyFromList(taskId);
                                        }

                                        if (taskId.contains(activity.getString(R.string.message_task_not_mapping)) || taskId.contains(activity.getString(R.string.message_no_task_id_from_server))) {
                                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                                            TaskHDataAccess.addOrReplace(activity, taskH);
                                        } else if (taskId.contains(BEEN_DELETED)) {
                                            TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                                        } else {
                                            this.taskH.setSubmit_date(taskH.getSubmit_date());
                                            this.taskH.setSubmit_result(result);
                                            this.taskH.setTask_id(taskId);
                                            this.taskH.setLast_saved_question(1);
                                            TaskHDataAccess.addOrReplace(activity, this.taskH);
//
                                            if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                                TimelineManager.insertTimeline(activity, this.taskH, true, false);
                                            }
                                        }
                                    } else {
                                        result = status;
                                    }

                                } else {
                                    result = String.valueOf(responseSubmitTask.getStatus().getCode());
                                    errMessage = responseSubmitTask.getStatus().getMessage();
                                    errCode = String.valueOf(responseSubmitTask.getStatus().getCode());
                                }

                                if (errMessage != null) {
                                    if (mNotifyManager != null)
                                        mNotifyManager.cancelAll();
                                    this.taskH = header.getTaskH();
                                    this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                                    if (result != null && result.equals(STATUS_TASK_NOT_MAPPING)) {
                                        this.taskH.setMessage(activity.getString(R.string.message_task_not_mapping) + ERROR + errCode + ")");
                                    } else if (result != null && result.equals(STATUS_TASK_DELETED)) {
                                        this.taskH.setMessage(activity.getString(R.string.message_sending_deleted) + ERROR + errCode + ")");
                                    } else if (result != null && result.equals(STATUS_IMEI_FAILED)) {
                                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                            taskH.setMessage(activity.getString(R.string.message_android_id_not_registered) + ERROR + result + ")");
                                        }else
                                            taskH.setMessage(activity.getString(R.string.message_imei_not_registered) + ERROR + result + ")");
                                    } else {
                                        this.taskH.setMessage(activity.getString(R.string.message_sending_failed) + ERROR + errCode + ")");
                                    }
                                    TaskHDataAccess.addOrReplace(activity, this.taskH);
                                    TimelineManager.insertTimeline(activity, taskH);
                                }
                            }
                        } else {
                            Global.setIsManualSubmit(false);
                            taskH = header.getTaskH();
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                            this.taskH.setMessage(activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name()) + ERROR + errCode + ")");
                            TaskHDataAccess.addOrReplace(activity, taskH);
                            TimelineManager.insertTimeline(activity, taskH);
                        }

                    } else {
                        taskH = header.getTaskH();
                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                        this.taskH.setMessage(activity.getString(R.string.no_internet_connection));
                        TaskHDataAccess.addOrReplace(activity, taskH);
                        TimelineManager.insertTimeline(activity, taskH);
                    }
                }

                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {             FireCrash.log(e);
                }
                Global.setIsManualSubmit(false);

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }
            }
        }.execute();
    }

    public void saveAndVerificationTask(final Activity activity, final int mode,
                                        final SurveyHeaderBean header, final List<QuestionBean> listOfQuestions, final String notes) {

        new AsyncTask<Void, Void, String>() {
            protected ProgressDialog progressDialog;
            protected List<TaskD> listTaskD;
            protected TaskH taskH;
            protected String errMessage = null;
            protected String errCode = null;
            protected String sec;
            protected String size;
            String taskId = null;
            private String messageWait = activity.getString(R.string.progressSend);

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
            }

            @Override
            protected String doInBackground(Void... arg0) {
                //bong 8 apr 15 - penjagaan jika autoSend null dari mainMenuActivity
                String result = null;
                Global.setIsManualSubmit(true);
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.requestWait();
                        MainServices.autoSendImageThread.requestWait();
                        MainServices.taskListThread.requestWait();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                boolean saved = saveTask(activity, mode, header, listOfQuestions, null, true, false, false);
                if (saved) {
                    this.taskH = header.getTaskH();
                    try {
                        listTaskD = TaskDDataAccess.getAll(activity, header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                    if (Tool.isInternetconnected(activity)) {

                        taskId = this.taskH.getTask_id();

                        //minimalisir data
                        String uuidTaskH = taskH.getUuid_task_h();
                        String uuidUser = taskH.getUuid_user();
                        boolean isHaveImage = false;
                        List<TaskD> taskDs = new ArrayList<>();
                        int i = 1;

                        try {
                            List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                            if (taskd != null && !taskd.isEmpty())
                                isHaveImage = true;
                        } catch (Exception e) {             FireCrash.log(e);
                            e.printStackTrace();
                        }
                        if (isHaveImage == false) {
                            for (TaskD d : listTaskD) {
                                if (d.getImage() != null) {
                                    isHaveImage = true;
                                    break;
                                }
                            }
                        }

                        for (TaskD d : listTaskD) {
                            if (d.getIs_visible().equals(Global.TRUE_STRING)) {
                                if (isPartial(activity)) {
                                    if (d.getImage() == null) {
                                        taskDs.add(d);
                                    }
                                } else {
                                    if (i == taskDs.size())
                                        d.setIs_final(Global.TRUE_STRING);
                                    taskDs.add(d);
                                }
                            }
                            i++;
                        }
                        if (!isPartial(activity)) {
                            TaskD d = taskDs.get(taskDs.size() - 1);
                            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                d.setIs_final(Global.TRUE_STRING);
                            }
                        } else {
                            if (!isHaveImage && !taskDs.isEmpty()) {
                                TaskD d = taskDs.get(taskDs.size() - 1);
                                if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                    d.setIs_final(Global.TRUE_STRING);
                                }
                            }
                        }
                        this.taskH.setSubmit_date(taskH.getSubmit_date());
                        //-------------------

                        JsonRequestVerificationTask task = new JsonRequestVerificationTask();
                        task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                        task.addImeiAndroidIdToUnstructured();
                        task.setTaskH(taskH);
                        task.setTaskD(taskDs);
                        task.setNotes(notes);

                        if (task.getTaskD() != null && !task.getTaskD().isEmpty()) {
                            String json = GsonHelper.toJson(task);
                            String url;

                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                            } else {
                                url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                            }

                            size = String.valueOf(json.getBytes().length);

                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                            HttpConnectionResult serverResult = null;
                            Date startTime = Tool.getSystemDateTime();

                            //Firebase Performance Trace HTTP Request
                            HttpMetric networkMetric =
                                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                            Utility.metricStart(networkMetric, json);

                            try {
                                if (Global.IS_DEV) {
                                    if (GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK().equals(url)) {
                                        Log.i(TAG, VERIFY_VERIFICATION_TASK);
                                    } else {
                                        Log.i(TAG, SUBMIT_TASK);
                                    }
                                }
                                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                                Utility.metricStop(networkMetric, serverResult);
                            } catch (Exception e) {             FireCrash.log(e);
                                e.printStackTrace();
                                try {
                                    progressDialog.dismiss();
                                } catch (Exception e1) {
                                    FireCrash.log(e1);
                                }
                                errMessage = e.getMessage();
                            }
                            if(serverResult == null){
                                errMessage = activity.getString(R.string.request_error);
                                return null;
                            }

                            if (serverResult.isOK()) {
                                String resultvalue = serverResult.getResult();
                                JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                                if (responseSubmitTask.getStatus().getCode() == 0) {
                                    String status = responseSubmitTask.getResult();
                                    Date finishTime = Tool.getSystemDateTime();
                                    long time = finishTime.getTime() - startTime.getTime();
                                    sec = String.valueOf((int) Math.ceil((double) time / 1000));
                                    if (status == null)
                                        status = SUCCESS;
                                    if (status.equalsIgnoreCase(SUCCESS)) {
                                        result = status;
                                        if (responseSubmitTask.getTaskId() != null)
                                            taskId = responseSubmitTask.getTaskId();
                                        else
                                            taskId = activity.getString(R.string.message_no_task_id_from_server);
                                    } else {
                                        result = status;
                                    }

                                    // bong 8 may 15 - call generate print result
                                    if (taskH.getScheme().getIs_printable() != null && taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING))
                                        generatePrintResult(activity, taskH);

                                } else {
                                    result = String.valueOf(responseSubmitTask.getStatus().getCode());
                                    errMessage = responseSubmitTask.getStatus().getMessage();
                                    errCode = String.valueOf(responseSubmitTask.getStatus().getCode());
                                }
                            } else {
                                try {
                                    result = serverResult.getResult();
                                } catch (Exception e) {             FireCrash.log(e);
                                }
                            }
                        } else {
                            result = activity.getString(R.string.request_error);
                            errMessage = activity.getString(R.string.request_error);
                            errCode = STATUS_TASKD_MISSING;
                            Global.setIsManualSubmit(false);
                        }

                    } else {
                        if (listTaskD == null || listTaskD.isEmpty()) {
                            result = activity.getString(R.string.request_error);
                            errMessage = activity.getString(R.string.request_error);
                            errCode = STATUS_TASKD_MISSING;
                            Global.setIsManualSubmit(false);
                            return result;
                        }
                        result = activity.getString(R.string.no_internet_connection);
                        Global.setIsManualSubmit(false);
                    }
                } else {
                    result = activity.getString(R.string.request_error);
                    errMessage = activity.getString(R.string.request_error);
                    errCode = STATUS_SAVE_FAILED;
                    Global.setIsManualSubmit(false);
                    return result;
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }

                if (errMessage != null) {
                    if (errCode != null && errCode.equals(STATUS_TASKD_MISSING)) {
                        try {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                        } catch (Exception e) {             FireCrash.log(e);
                        }
                        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                        builder.withTitle(activity.getString(R.string.warning_capital))
                                .withMessage(activity.getString(R.string.message_sending_failed) + "\n" + errMessage)
                                .show();
                    } else if (errCode != null && errCode.equals(STATUS_SAVE_FAILED)) {
                        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                        builder.withTitle(activity.getString(R.string.warning_capital))
                                .withMessage(activity.getString(R.string.message_sending_failed) + "\n" + errMessage)
                                .show();
                    } else {
                        Bundle extras = new Bundle();
                        Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                        if (errCode != null && (errCode.equals(STATUS_TASK_NOT_MAPPING) || errCode.equals(STATUS_IMEI_FAILED))) {
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                            TaskHDataAccess.addOrReplace(activity, taskH);
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler.sendEmptyMessage(0);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, this.errMessage);
                            extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                        } else if (errCode != null && errCode.equals(STATUS_TASK_DELETED)) {
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler.sendEmptyMessage(0);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_deleted));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_deleted));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, this.errMessage);
                            extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                            TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                        } else {
                            try {
                                int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                                ToDoList.updateStatusSurvey(taskId, this.taskH.getStatus(), imageLeft);
                            } catch (Exception e) {             FireCrash.log(e);

                            }
                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, errMessage);
                        }
                        intent.putExtras(extras);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                } else {
                    if (result != null) {
                        if (result.equalsIgnoreCase(SUCCESS)) {
                            Bundle extras = new Bundle();
                            Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                            for (TaskD taskD : listTaskD) {
                                if (taskD.getImage() == null)
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                else {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                    } else {
                                        if (isPartial(activity)) {
                                            taskD.setIs_sent(Global.FALSE_STRING);
                                        } else {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                    }
                                }
                            }
                            TaskDDataAccess.addOrReplace(activity, listTaskD);
                            if (AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h()) > 0) {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                            } else {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                ToDoList.removeSurveyFromList(taskId);
                            }

                            if (taskId.contains(activity.getString(R.string.message_task_not_mapping)) || taskId.contains(activity.getString(R.string.message_no_task_id_from_server))) {
                                this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_SAVEDRAFT, 0);
                                TaskHDataAccess.addOrReplace(activity, taskH);
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                                } else {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                                }
                                extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                                extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                                extras.putString(Global.BUND_KEY_TASK_ID, this.taskId);
                                extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);

                            } else if (taskId.contains(BEEN_DELETED)) {
                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_deleted));
                                } else {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_deleted));
                                }
                                extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                                extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                                extras.putString(Global.BUND_KEY_TASK_ID, this.taskId);
                                extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                                TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                            } else {
                                this.taskH.setSubmit_date(taskH.getSubmit_date());
                                this.taskH.setSubmit_duration(sec);
                                this.taskH.setSubmit_size(size);
                                this.taskH.setSubmit_result(result);
                                this.taskH.setTask_id(taskId);
                                this.taskH.setLast_saved_question(1);
                                TaskHDataAccess.addOrReplace(activity, this.taskH);

                                if (StatusTabFragment.handler != null)
                                    StatusTabFragment.handler.sendEmptyMessage(0);
                                extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, false);
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_success));
                                    if (MainMenuActivity.mnSVYApproval != null) {
                                        TaskHDataAccess.deleteWithRelation(activity, this.taskH);
                                        String newUUIDTaskH = Tool.getUUID();
                                        this.taskH.setUuid_task_h(newUUIDTaskH);
                                        TaskHDataAccess.addOrReplace(activity, this.taskH);
                                        for (TaskD d : listTaskD) {
                                            d.setTaskH(taskH);
                                            d.setUuid_task_d(Tool.getUUID());
                                        }
                                        TaskDDataAccess.addOrReplace(activity, listTaskD);
                                    }
                                } else {
                                    extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_success));
                                }
                                extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                                extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                                extras.putString(Global.BUND_KEY_TASK_ID, this.taskId);
                                try {
                                    extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, Formatter.stringToBoolean(header.getIs_printable()));
                                } catch (Exception e) {             FireCrash.log(e);
                                    extras.putBoolean(Global.BUND_KEY_TASK_IS_PRINTABLE, false);
                                }

                                if (this.taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        TimelineManager.insertTimeline(activity, this.taskH, true, false);
                                    } else {
                                        TimelineManager.insertTimeline(activity, this.taskH);
                                    }
                                }
                            }

                            intent.putExtras(extras);
                            activity.startActivity(intent);
                            activity.finish();

                        } else if (activity.getString(R.string.no_internet_connection).equals(result)) {
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler.sendEmptyMessage(0);

                            Bundle extras = new Bundle();
                            Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            extras.putString(Global.BUND_KEY_SEND_RESULT, "Failed");
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, result);
                            intent.putExtras(extras);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            TaskHDataAccess.addOrReplace(activity, this.taskH);

                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, this.taskH.getUuid_task_h());
                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_PENDING, imageLeft);
                            if (StatusTabFragment.handler != null)
                                StatusTabFragment.handler.sendEmptyMessage(0);

                            Bundle extras = new Bundle();
                            Intent intent = new Intent(activity.getApplicationContext(), SendResultActivity.class);
                            extras.putBoolean(Global.BUND_KEY_SURVEY_ERROR, true);
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_verification_failed));
                            } else {
                                extras.putString(Global.BUND_KEY_SEND_RESULT, activity.getString(R.string.message_sending_failed));
                            }
                            extras.putString(Global.BUND_KEY_SEND_SIZE, this.size);
                            extras.putString(Global.BUND_KEY_SEND_TIME, this.sec);
                            extras.putString(Global.BUND_KEY_TASK_ID, "error code: " + result);
                            intent.putExtras(extras);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }

                }
                //bong 8 apr 15 - penjagaan jika autoSend null dari mainMenuActivity
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                TaskHDataAccess.doBackup(activity, taskH);
                Global.setIsManualSubmit(false);

            }
        }.execute();

    }


    protected static String UploadImage(final Context context, List<TaskD> sendImage) {
        String errMessage = "";
        Global.setIsUploading(true);
        Global.setIsManualUploading(true);
        if (Tool.isInternetconnected(context)) {
            String taskId = null;
            int count = sendImage.size();
            if (count > 0) {
                Global.setIsUploading(true);
                Global.setIsManualUploading(true);
                for (TaskD taskd : sendImage) {
                    try {
                        if (Tool.isInternetconnected(context)) {
                            final String uuid_task_h = taskd.getTaskH().getTask_id();
                            String task_id = taskd.getTaskH().getTask_id();
                            int totalPendingImageTask = AutoSendImageThread.countPendingImageUpload(context, uuid_task_h);
                            final int totalimage = AutoSendImageThread.countTotalImageTask(context, uuid_task_h);
                            boolean finale = AutoSendImageThread.checkFinal(totalPendingImageTask);
                            if (finale) {
                                taskd.setIs_final(Global.TRUE_STRING);
                            }
                            taskd.setTaskH(null);
                            taskd.setUuid_task_h(uuid_task_h);

                            if (taskd.getCount() == null) {
                                taskd.setCount(Global.FALSE_STRING);
                            }
                            final JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                            task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                            task.addImeiAndroidIdToUnstructured();
                            task.setTaskH(taskd.getTaskH());
                            List<TaskD> ds = new ArrayList<>();
                            taskd.getTaskH().getUser();
                            taskd.getTaskH().getScheme();
                            ds.add(taskd);
                            task.setTaskD(ds);
                            if(Global.PLAN_TASK_ENABLED){
                                TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                String nextPlan = null;
                                if(todayPlanRepo != null){
                                    nextPlan = todayPlanRepo.nextPlanBeforeSubmit(uuid_task_h);
                                }
                                if(nextPlan != null){
                                    task.setUuidTaskHNext(nextPlan);
                                }
                            }
                            String json = GsonHelper.toJson(task);
                            String url;
                            final TaskH taskH = taskd.getTaskH();
                            if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                            } else {
                                url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                            }

                            setUploadImageNotif(totalPendingImageTask, taskH, uuid_task_h, context, totalimage);

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
                                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                                Utility.metricStop(networkMetric, serverResult);
                            } catch (Exception ex) {
                                errMessage = context.getString(R.string.upload_queue);
                                break;
                            }
                            if (serverResult != null && serverResult.isOK()) {
                                String resultvalue = serverResult.getResult();
                                try {
                                    JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);

                                    int statusCode = responseSubmitTask.getStatus().getCode();
                                    if (statusCode == 0) {
                                        Date finishSend = Tool.getSystemDateTime();
                                        if (Global.IS_DEV)
                                            Log.i(TAG, SUCCESS_SEND);
                                        if (finale) {
                                            ToDoList.removeSurveyFromList(task_id);
                                            if (responseSubmitTask.getTaskId() != null)
                                                taskId = responseSubmitTask.getTaskId();
                                            if (taskId != null && taskId.length() > 0) {
                                                doAfterFinish(context, taskd, startSend, finishSend, taskId);
                                                errMessage = "";
                                                if (PriorityTabFragment.handler != null)
                                                    PriorityTabFragment.handler.sendEmptyMessage(0);
                                                if (NewTimelineFragment.timelineHandler != null)
                                                    NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                                                Utility.freeMemory();
                                            }
                                        } else {
                                            errMessage = context.getString(R.string.upload_queue);
                                            totalPendingImageTask--;
                                            ToDoList.updateStatusSurvey(task_id, TaskHDataAccess.STATUS_SEND_UPLOADING, totalPendingImageTask);
                                            taskd.setIs_sent(Global.TRUE_STRING);
                                            TaskDDataAccess.addOrReplace(context, taskd);
                                            if (PriorityTabFragment.handler != null)
                                                PriorityTabFragment.handler.sendEmptyMessage(0);
                                            if (NewTimelineFragment.timelineHandler != null)
                                                NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                                        }
                                    }
                                    else if(statusCode == Global.FAILED_DRAFT_TASK_CODE){
                                        errMessage = context.getString(R.string.task_failed_draft);
                                        isSubmitFailedDraft(context, taskH, errMessage);
                                    }else {
                                        if (resultvalue != null) {
                                            if (Global.IS_DEV)
                                                Log.i(TAG, STATIC_AUTO_SEND_IMAGE_EQUALS_TO + resultvalue);
                                            ACRA.getErrorReporter().handleSilentException(new Exception(UPLOAD_IMAGE_EXCEPTION + resultvalue));
                                        }
                                        errMessage = context.getString(R.string.upload_failed);
                                    }
                                } catch (Exception e) {             FireCrash.log(e);
                                    errMessage = context.getString(R.string.upload_queue);
                                    break;
                                }
                            } else if(serverResult != null && !serverResult.isOK()) {
                                errMessage = context.getString(R.string.upload_queue);
                                String statusCode = String.valueOf(serverResult.getStatusCode()).trim();
                                if (!statusCode.isEmpty() && statusCode.charAt(0) != '5') {
                                    //set counter for taskD
                                    taskd.setCount(Global.TRUE_STRING);
                                    taskd.setIs_sent(Global.FALSE_STRING);
                                    TaskDDataAccess.addOrReplace(context, taskd);
                                }
                                break;
                            }
                            TaskHDataAccess.doBackup(context, taskH);
                        } else {
                            errMessage = context.getString(R.string.no_internet_connection);
                            break;
                        }
                    } catch (Exception ex) {
                        errMessage = context.getString(R.string.upload_failed);
                        ex.printStackTrace();
                        break;
                    }
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // UI code goes here
                        try {
                            newMainActivitySetCounter();
                            if (NewTimelineFragment.timelineHandler != null)
                                NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                            if (PriorityTabFragment.handler != null)
                                PriorityTabFragment.handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }
                });
            }
        } else {
            errMessage = context.getString(R.string.no_internet_connection);
        }

        Global.setIsUploading(false);
        Global.setIsManualUploading(false);
        AutoSendImageThread.removeTaskUploadManual(sendImage.get(0).getUuid_task_h());

        return errMessage;
    }

    private static void setUploadImageNotif(int totalPendingImageTask,final TaskH taskH, final String uuid_task_h, final Context context ,final int totalimage){
        final Notification.Builder mBuilder = new Notification.Builder(context);
        final Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        final NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                final int totPending = totalPendingImageTask;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && (taskH.getUuid_task_h()).equals(uuid_task_h)) {
                            int uplod = totalimage - totPending + 1;
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
                            mBuilder.setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
                            mNotifyManager.notify(4, mBuilder.build());
                        }
                    }
                });
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    @Override
    public void saveAndSendTaskOnBackground(final Context activity, final String taskId,
                                            boolean isPrintable, boolean finishActivity) {
        final Notification.Builder mBuilder = new Notification.Builder(activity);
        final Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        final NotificationManager mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setContentTitle(activity.getString(R.string.uploading_image))
                    .setContentText(activity.getString(R.string.progress_uploading))
                    .setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
            inboxStyle.setBigContentTitle(activity.getString(R.string.uploading_image));
            inboxStyle.bigText(activity.getString(R.string.progress_uploading));
            mBuilder.setStyle(inboxStyle);
        }
        new AsyncTask<Void, Void, String>() {
            boolean isHaveImage = false;
            String uuidTaskFromServer = null;
            String oldUuidTask = null;
            private List<TaskD> listTaskD;
            private TaskH taskH;
            private String errMessage = null;
            private int errCode;
            private String sec;
            private String size;
            private String nTaskId;

            @Override
            protected String doInBackground(Void... params) {
                String message = "";
                String result = null;
                String list = null;
                Global.setIsUploading(true);
                Global.setIsManualSubmit(true);
                this.taskH = TaskHDataAccess.getOneTaskHeader(activity, taskId);
                if(this.taskH == null){
                    this.taskH = TaskHDataAccess.getOneHeader(activity,taskId);
                }

                AutoSendTaskThread.addTaskSubmitManual(this.taskH.getUuid_task_h());
                AutoSendImageThread.addTaskUploadManual(this.taskH.getUuid_task_h());

                try {
                    listTaskD = TaskDDataAccess.getAll(activity, taskH.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                } catch (Exception e) {             FireCrash.log(e);
                }

                if (Tool.isInternetconnected(activity)) {
                    try {
                        if (MainServices.autoSendTaskThread != null) {
                            MainServices.autoSendTaskThread.requestWait();
                            MainServices.autoSendImageThread.requestWait();
                            MainServices.taskListThread.requestWait();
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }

                    String uuidTaskH = taskH.getUuid_task_h();
                    String uuidUser = taskH.getUuid_user();

                    List<TaskD> taskDs = new ArrayList<>();
                    int i = 1;

                    try {
                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                        if (taskd != null && !taskd.isEmpty())
                            isHaveImage = true;
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                    }

                    List<TaskD> taskDList = getUnsentImage((Activity) activity, uuidTaskH, listTaskD);
                    for (TaskD d : taskDList) {
                        if (Global.TAG_RV_NUMBER.equals(d.getTag())) {
                            taskH.setRv_number(d.getLookup().getValue());
                        } else if (Global.TAG_RV_NUMBER_MOBILE.equals(d.getTag())) {
                            taskH.setRv_number(d.getText_answer());
                        }
                    }

                    if (isHaveImage == false) {
                        for (TaskD d : listTaskD) {
                            if (d.getImage() != null) {
                                isHaveImage = true;
                                break;
                            }
                        }
                    }

                    for (TaskD d : listTaskD) {
                        d.getTaskH().getScheme();
                        d.getTaskH().getUser();
                        if (Global.TRUE_STRING.equals(d.getIs_visible())) {
                            if (isPartial(activity)) {
                                if (d.getImage() == null) {
                                    taskDs.add(d);
                                }
                            } else {
                                if (i == taskDs.size())
                                    d.setIs_final(Global.TRUE_STRING);
                                taskDs.add(d);
                            }
                        }
                        i++;
                    }
                    if (!isPartial(activity)) {
                        TaskD d = taskDs.get(taskDs.size() - 1);
                        if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                            d.setIs_final(Global.TRUE_STRING);
                        }
                    } else {
                        if (!isHaveImage && !taskDs.isEmpty()) {
                            TaskD d = taskDs.get(taskDs.size() - 1);
                            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                d.setIs_final(Global.TRUE_STRING);
                            }
                        }
                    }
                    taskH.setSubmit_date(taskH.getSubmit_date());
                    if (isHaveImage == false) {
                        message = NO_IMAGE;
                    }

                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);
                    task.setTaskD(taskDs);
                    if(Global.PLAN_TASK_ENABLED){
                        TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                        String nextPlan = null;
                        if(todayPlanRepo != null){
                            nextPlan = todayPlanRepo.nextPlanBeforeSubmit(taskH.getTask_id());
                        }
                        if(nextPlan != null){
                            task.setUuidTaskHNext(nextPlan);
                        }
                    }

                    if (task.getTaskD() != null && !task.getTaskD().isEmpty()) {
                        String json = GsonHelper.toJson(task);
                        String url;

                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                            url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                        } else {
                            url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();
                        }

                        size = String.valueOf(json.getBytes().length);

                        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                        HttpConnectionResult serverResult = null;
                        Date startTime = Tool.getSystemDateTime();

                        //Firebase Performance Trace HTTP Request
                        HttpMetric networkMetric =
                                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                        Utility.metricStart(networkMetric, json);

                        try {
                            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            Utility.metricStop(networkMetric, serverResult);
                        } catch (Exception e) {             FireCrash.log(e);
                            e.printStackTrace();
                            errMessage = e.getMessage();
                        }

                        if (serverResult != null && serverResult.isOK()) {
                            String resultvalue = serverResult.getResult();
                            JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                            if (responseSubmitTask.getStatus().getCode() == 0) {
                                String status = responseSubmitTask.getResult();
                                Date finishTime = Tool.getSystemDateTime();

                                uuidTaskFromServer = responseSubmitTask.getUuidTask();
                                oldUuidTask = this.taskH.getUuid_task_h();

                                //change to new uuid
                                if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                    TaskHDataAccess.deleteByUuid(activity, this.taskH.getUuid_user(), this.taskH.getUuid_task_h());
                                    this.taskH.setUuid_task_h(uuidTaskFromServer);
                                    TaskHDataAccess.addOrReplace(activity, this.taskH);
                                }

                                long time = finishTime.getTime() - startTime.getTime();
                                sec = "0";
                                try {
                                    sec = String.valueOf((int) Math.ceil((double)time / 1000));
                                } catch (Exception e) {             FireCrash.log(e);

                                }
                                if (status == null)
                                    status = SUCCESS;
                                if (status.equalsIgnoreCase(SUCCESS)) {
                                    result = status;
                                    if (responseSubmitTask.getTaskId() != null) {
                                        nTaskId = responseSubmitTask.getTaskId();
                                        String appllication = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                        if (appllication == null)
                                            appllication = GlobalData.getSharedGlobalData().getApplication();
                                        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(appllication) && responseSubmitTask.getCashOnHand() != null) {
                                            GlobalData.getSharedGlobalData().getUser().setCash_on_hand(responseSubmitTask.getCashOnHand());
                                            UserDataAccess.addOrReplace(activity, GlobalData.getSharedGlobalData().getUser());
                                        }
                                    } else {
                                        nTaskId = activity.getString(R.string.message_no_task_id_from_server);
                                    }
                                } else {
                                    result = status;
                                }
                                if (taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING)) {
                                    generatePrintResult(activity, taskH);
                                }
                            } else {
                                result = String.valueOf(responseSubmitTask.getStatus().getCode());
                                errMessage = responseSubmitTask.getStatus().getMessage();
                                errCode = responseSubmitTask.getStatus().getCode();
                            }
                        } else {
                            try {
                                errMessage = serverResult.getResult();
                            } catch (Exception e) {             FireCrash.log(e);
                            }
                        }

                        if (result != null && result.equalsIgnoreCase(SUCCESS)) {
                            if (nTaskId != null && nTaskId.length() > 0 && !nTaskId.contains("~")) {
                                List<TaskD> newlistTaskD = new ArrayList<>();
                                for (TaskD taskD : listTaskD) {
                                    if (taskD.getImage() == null)
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                    else {
                                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                            newlistTaskD.add(taskD);
                                        } else {
                                            if (TaskManager.isPartial(activity)) {
                                                taskD.setIs_sent(Global.FALSE_STRING);
                                            } else {
                                                taskD.setIs_sent(Global.TRUE_STRING);
                                            }
                                        }
                                    }
                                    //change to new uuid
                                    if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                        taskD.setUuid_task_h(uuidTaskFromServer);

                                        List<Timeline> timelineList = TimelineDataAccess.getTimelineByTask(activity, uuidUser, uuidTaskH);
                                        if (null != timelineList) {
                                            for (Timeline timeline : timelineList) {
                                                timeline.setUuid_task_h(uuidTaskFromServer);
                                                TimelineDataAccess.addOrReplace(activity, timeline);
                                            }
                                        }
                                    }
                                    TaskDDataAccess.addOrReplace(activity, taskD);
                                }
                                int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, taskH.getUuid_task_h());
                                if (imageLeft > 0) {
                                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                    ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                } else {
                                    try {
                                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, taskH.getUuid_user(), taskH.getUuid_task_h());
                                        if (taskd != null && !taskd.isEmpty()) {
                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                            ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                        } else {
                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                            ToDoList.removeSurveyFromList(taskId);
                                        }
                                    } catch (Exception e) {             FireCrash.log(e);
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                        ACRA.getErrorReporter().putCustomData(ERROR_SUBMIT, e.getMessage());
                                        ACRA.getErrorReporter().handleSilentException(new Exception(AUTO_SEND_TASK_EXCEPTION));
                                    }
                                }
                                taskH.setSubmit_date(taskH.getSubmit_date());
                                taskH.setSubmit_duration(sec);
                                taskH.setSubmit_size(size);
                                taskH.setSubmit_result(result);
                                String oldTaskId = taskH.getTask_id();
                                taskH.setTask_id(nTaskId);
                                taskH.setLast_saved_question(1);
                                TaskHDataAccess.addOrReplace(activity, taskH);

                                if(Global.PLAN_TASK_ENABLED && !oldTaskId.equals(nTaskId)){
                                    TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                    todayPlanRepo.changeTaskhFromPlan(oldTaskId,uuidTaskFromServer);
                                }

                                if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                    // 2017/10/02 | olivia : update status Task di table TaskSummary
                                    if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
                                        //today plan repo trigger update after submit success
                                        if(Global.PLAN_TASK_ENABLED && Global.isPlanStarted()){
                                            GlobalData.getSharedGlobalData().getTodayPlanRepo().updatePlanByTaskH(taskH, PlanTaskDataAccess.STATUS_FINISH);
                                        }

                                        TaskSummary taskSummary = TaskSummaryDataAccess.getOne(activity, uuidTaskH, taskH.getUuid_user());
                                        if (taskSummary != null) {
                                            boolean isTaskPaid = TaskDDataAccess.isTaskPaid(activity, uuidUser, uuidTaskH);
                                            if (isTaskPaid)
                                                taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_PAID);
                                            else
                                                taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_FAIL);
                                            TaskSummaryDataAccess.addOrReplace(activity, taskSummary);
                                        }
                                    }

                                    if ((taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION))
                                            && MainMenuActivity.mnSVYApproval != null) {
                                        TaskHDataAccess.deleteWithRelation(activity, taskH);
                                        String newUUIDTaskH = Tool.getUUID();
                                        taskH.setUuid_task_h(newUUIDTaskH);
                                        TaskHDataAccess.addOrReplace(activity, taskH);
                                        for (TaskD d : newlistTaskD) {
                                            d.setTaskH(taskH);
                                            d.setUuid_task_d(Tool.getUUID());
                                            TaskDDataAccess.addOrReplace(activity, d);
                                        }
                                    }
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        if (taskH.getFlag_survey() != null && taskH.getFlag_survey().equals(Global.FLAG_FOR_REJECTEDTASK))
                                            TimelineManager.insertTimeline(activity, taskH, false, true);
                                        else
                                            TimelineManager.insertTimeline(activity, taskH, true, false);
                                    } else {
                                        TimelineManager.insertTimeline(activity, taskH);
                                    }
                                } else if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                                    TimelineManager.insertTimeline(activity, taskH);
                                }
                            }
                        }
                        else if(result !=null && result.equalsIgnoreCase(String.valueOf(Global.FAILED_DRAFT_TASK_CODE))){
                            isSubmitFailedDraft(activity,taskH,errMessage);
                            ToDoList.removeSurveyFromList(taskId);
                            message = activity.getString(R.string.task_failed_draft);
                        }
                        else {
                            mNotifyManager.cancelAll();
                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                            if (result != null && result.equals(STATUS_TASK_NOT_MAPPING)) {
                                taskH.setMessage(activity.getString(R.string.message_task_not_mapping) + ERROR + errCode + ")");
                            } else if (result != null && result.equals(STATUS_TASK_DELETED)) {
                                taskH.setMessage(activity.getString(R.string.message_sending_deleted) + ERROR + errCode + ")");
                            } else if (result != null && result.equals(STATUS_IMEI_FAILED)) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                    taskH.setMessage(activity.getString(R.string.message_android_id_not_registered) + ERROR + result + ")");
                                }else
                                    taskH.setMessage(activity.getString(R.string.message_imei_not_registered) + ERROR + result + ")");
                            } else {
                                taskH.setMessage(activity.getString(R.string.message_sending_failed) + ERROR + errCode + ")");
                            }
                            TaskHDataAccess.addOrReplace(activity, taskH);
                            TimelineManager.insertTimeline(activity, taskH);
                            Global.setIsManualSubmit(false);
                        }
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                // UI code goes here
                                try {
                                    newMainActivitySetCounter();
                                    if (NewTimelineFragment.timelineHandler != null)
                                        NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                                    if (PriorityTabFragment.handler != null)
                                        PriorityTabFragment.handler.sendEmptyMessage(0);
                                    viewAdapter.notifyDataSetChanged();
                                    PriorityTabFragment.viewAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                }
                            }
                        });
                        try {
                            if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                                message = saveAndSendTaskUploadImageNotif(activity, taskH);
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }
                    }else{
                        AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                        AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                    }
                }else{
                    AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                    AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                }
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }

                Global.setIsUploading(false);
                return message;
            }

            @Override
            protected void onPostExecute(final String message) {
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                try {
                    if (message == null || message.length() == 0) {
                        String status = taskH.getStatus();
                        if (status.equals(TaskHDataAccess.STATUS_SEND_PENDING)) {
                            mNotifyManager.cancelAll();
                        } else {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            String counter = activity.getString(R.string.upload_complete);
                                            mBuilder.setContentText(counter)
                                                    // Removes the progress bar
                                                    .setProgress(0, 0, false);
                                            mBuilder.setOngoing(false);
                                            inboxStyle.bigText(counter);
                                            mBuilder.setStyle(inboxStyle);
                                            mNotifyManager.notify(4, mBuilder.build());
                                        }
                                    }
                                });
                            }
                        }
                    } else if (message.equals(NO_IMAGE)) {
                        mNotifyManager.cancelAll();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        mBuilder.setContentText(message)
                                                // Removes the progress bar
                                                .setProgress(0, 0, false);
                                        mBuilder.setOngoing(false);
                                        inboxStyle.bigText(message);
                                        mBuilder.setStyle(inboxStyle);
                                        mNotifyManager.notify(4, mBuilder.build());
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {             FireCrash.log(e);
                    Global.setIsManualUploading(false);
                    AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                }
                Global.setIsManualSubmit(false);
                AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
            }
        }.execute();
    }

    private String saveAndSendTaskUploadImageNotif(final Context activity , TaskH taskH){
        final Notification.Builder mBuilder = new Notification.Builder(activity);
        final Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        final NotificationManager mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                Handler handler2 = new Handler(Looper.getMainLooper());
                handler2.post(new Runnable() {
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mBuilder.setContentTitle(activity.getString(R.string.uploading_image))
                                    .setContentText(activity.getString(R.string.progress_uploading))
                                    .setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
                            inboxStyle.setBigContentTitle(activity.getString(R.string.uploading_image));
                            inboxStyle.bigText(activity.getString(R.string.progress_uploading));
                            mBuilder.setStyle(inboxStyle);
                            mBuilder.setProgress(0, 0, true);
                            mBuilder.setOngoing(true);
                            mNotifyManager.notify(4, mBuilder.build());
                        }
                    }
                });
            }
            return setUploadImageMessage(taskH, activity);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        return "";
    }

    private String setUploadImageMessage(TaskH taskH, final Context activity){
        try {
            List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, taskH.getUuid_user(), taskH.getUuid_task_h());
            return UploadImage(activity, taskd);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            Global.setIsManualUploading(false);
            Global.setIsUploading(false);
        }
        return "";
    }

    @Override
    public void saveAndSendTaskOnBackground(final Activity activity, final int mode,
                                            final SurveyHeaderBean header, final List<QuestionBean> listOfQuestions, final boolean isTaskPaid) {
        final Notification.Builder mBuilder = new Notification.Builder(activity)
                                                .setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
        final Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        final NotificationManager mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setContentTitle(activity.getString(R.string.uploading_image))
                    .setContentText(activity.getString(R.string.progress_uploading))
                    .setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
            inboxStyle.setBigContentTitle(activity.getString(R.string.uploading_image));
            inboxStyle.bigText(activity.getString(R.string.progress_uploading));
            mBuilder.setStyle(inboxStyle);
        }

        final SubmitResult submitResult = new SubmitResult();
        new AsyncTask<Void, Void, String>() {
            protected ProgressDialog progressDialog;
            protected List<TaskD> listTaskD;
            protected TaskH taskH;
            protected String errMessage = null;
            protected String sec;
            protected String size;
            int errCode = 0;
            String taskId = null;
            String uuidTaskFromServer = null;
            String oldUuidTask = null;
            private String messageWait = activity.getString(R.string.progressSend);

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity, "", this.messageWait, true);
            }

            @Override
            protected String doInBackground(Void... arg0) {
                //bong 8 apr 15 - penjagaan jika autoSend null dari mainMenuActivity
                String message = "";
                String result = null;
                Global.setIsUploading(true);
                Global.setIsManualSubmit(true);
                AutoSendTaskThread.addTaskSubmitManual(header.getUuid_task_h());
                AutoSendImageThread.addTaskUploadManual(header.getUuid_task_h());
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.requestWait();
                        MainServices.autoSendImageThread.requestWait();
                        MainServices.taskListThread.requestWait();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                saveTask(activity, mode, header, listOfQuestions, null, true, false, false);

                if (Tool.isInternetconnected(activity)) {
                    this.taskH = header.getTaskH();
                    this.taskH.getScheme();
                    this.taskH.getUser();
                    this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    taskId = this.taskH.getTask_id();
                    String nTaskId = taskId;

                    //Nendi
                    submitResult.setTaskId(taskId);
                    try {
                        // olivia : untuk revisit
                        if (CustomerFragment.getIsEditable()) {
                            try {
                                listTaskD = TaskDDataAccess.getAll(activity, header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                                header.setAssignment_date(null);
                                TaskHDataAccess.addOrReplace(activity, header);
                                CustomerFragment.setIsEditable(false);

                            } catch (Exception e) {
                                FireCrash.log(e);
                            }
                        } else {
                            listTaskD = TaskDDataAccess.getAll(activity, header.getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                        }
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }

                    //minimalisir data
                    String uuidTaskH = taskH.getUuid_task_h();
                    String uuidUser = taskH.getUuid_user();
                    boolean isHaveImage = false;
                    List<TaskD> taskDs = new ArrayList<>();
                    int i = 1;

                    try {
                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, uuidUser, uuidTaskH);
                        if (taskd != null && !taskd.isEmpty())
                            isHaveImage = true;
                    } catch (Exception e) {
                        FireCrash.log(e);
                        e.printStackTrace();
                    }
                    if (isHaveImage == false) {
                        for (TaskD d : listTaskD) {
                            if (d.getImage() != null) {
                                isHaveImage = true;
                                break;
                            }
                        }
                    }

                    for (TaskD d : listTaskD) {
                        if (d.getIs_visible().equals(Global.TRUE_STRING)) {
                            if (isPartial(activity)) {
                                if (d.getImage() == null) {
                                    taskDs.add(d);
                                }
                            } else {
                                if (i == taskDs.size())
                                    d.setIs_final(Global.TRUE_STRING);
                                taskDs.add(d);
                            }
                        }
                        i++;
                    }
                    if (!isPartial(activity)) {
                        TaskD d = taskDs.get(taskDs.size() - 1);
                        if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                            d.setIs_final(Global.TRUE_STRING);
                        }
                    } else {
                        if (!isHaveImage && !taskDs.isEmpty()) {
                            TaskD d = taskDs.get(taskDs.size() - 1);
                            if (!d.getIs_final().equals(Global.TRUE_STRING)) {
                                d.setIs_final(Global.TRUE_STRING);
                            }
                        }
                    }

                    this.taskH.setSubmit_date(taskH.getSubmit_date());
                    //-------------------
                    //kamil 27/07/2017 permintaan mas raymond tambah lokasi untuk kirim taskH ke server
                    LocationInfo loc = Global.LTM.getCurrentLocation(Global.FLAG_LOCATION_TRACKING);
                    if ("null".equalsIgnoreCase(loc.getLatitude())) {
                        this.taskH.setLatitude("");
                    } else {
                        this.taskH.setLatitude(loc.getLatitude());
                    }

                    if ("null".equalsIgnoreCase(loc.getLongitude())) {
                        this.taskH.setLongitude("");
                    } else {
                        this.taskH.setLongitude(loc.getLongitude());
                    }

                    JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                    task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                    task.addImeiAndroidIdToUnstructured();
                    task.setTaskH(taskH);
                    task.setTaskD(taskDs);

                    if (task.getTaskD() != null && !task.getTaskD().isEmpty()) {
                        String json = GsonHelper.toJson(task);
                        String url = GlobalData.getSharedGlobalData().getURL_SUBMITTASK();

                        if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                            url = GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK();
                        }

                        if (isHaveImage == false) {
                            message = NO_IMAGE;
                        }

                        size = String.valueOf(json.getBytes().length);

                        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                        HttpConnectionResult serverResult = null;
                        Date startTime = Tool.getSystemDateTime();

                        //Firebase Performance Trace HTTP Request
                        HttpMetric networkMetric =
                                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                        Utility.metricStart(networkMetric, json);

                        try {
                            if (Global.IS_DEV) {
                                if (GlobalData.getSharedGlobalData().getURL_SUBMITVERIFICATIONTASK().equals(url)) {
                                    Log.i(TAG, VERIFY_VERIFICATION_TASK);
                                } else {
                                    Log.i(TAG, SUBMIT_TASK);
                                }
                            }
                            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            Utility.metricStop(networkMetric, serverResult);
                            if (Global.IS_DEV)
                                Log.i(TAG, "Manual Send Task " + taskH.getCustomer_name());
                        } catch (Exception e) {             FireCrash.log(e);
                            e.printStackTrace();
                            errMessage = e.getMessage();
                        }

                        if (serverResult != null && serverResult.isOK()) {
                            String resultvalue = serverResult
                                    .getResult();
                            JsonResponseSubmitTask responseSubmitTask = GsonHelper.fromJson(resultvalue, JsonResponseSubmitTask.class);
                            if (responseSubmitTask.getStatus().getCode() == 0) {
                                String status = responseSubmitTask.getResult();

                                uuidTaskFromServer = responseSubmitTask.getUuidTask();
                                oldUuidTask = this.taskH.getUuid_task_h();

                                //change to new uuid
                                if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                    TaskHDataAccess.deleteByUuid(activity, this.taskH.getUuid_user(), this.taskH.getUuid_task_h());
                                    this.taskH.setUuid_task_h(uuidTaskFromServer);
                                    TaskHDataAccess.addOrReplace(activity, this.taskH);

                                    List<Timeline> timelineList = TimelineDataAccess.getTimelineByTask(activity, uuidUser, uuidTaskH);
                                    if (null != timelineList) {
                                        for (Timeline timeline : timelineList) {
                                            timeline.setUuid_task_h(uuidTaskFromServer);
                                            TimelineDataAccess.addOrReplace(activity, timeline);
                                        }
                                    }
                                }

                                Date finishTime = Tool.getSystemDateTime();
                                long time = finishTime.getTime() - startTime.getTime();
                                sec = String.valueOf((int) Math.ceil((double)time / 1000));
                                if (status == null)
                                    status = SUCCESS;
                                if (status.equalsIgnoreCase(SUCCESS)) {
                                    result = status;
                                    if (responseSubmitTask.getTaskId() != null) {
                                        nTaskId = responseSubmitTask.getTaskId();
                                        String appllication = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                                        if (appllication == null)
                                            appllication = GlobalData.getSharedGlobalData().getApplication();
                                        if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(appllication) && responseSubmitTask.getCashOnHand() != null) {
                                            GlobalData.getSharedGlobalData().getUser().setCash_on_hand(responseSubmitTask.getCashOnHand());
                                            UserDataAccess.addOrReplace(activity, GlobalData.getSharedGlobalData().getUser());
                                        }
                                    } else {
                                        nTaskId = activity.getString(R.string.message_no_task_id_from_server);
                                    }
                                } else {
                                    result = status;
                                }
                                if (taskH.getScheme().getIs_printable().equals(Global.TRUE_STRING)) {
                                    generatePrintResult(activity, taskH);
                                }
                            } else {
                                if (Global.IS_DEV) {
                                    if (responseSubmitTask.getStatus().getMessage() == null)
                                        Log.i(TAG, AUTO_SEND_TASK_THREAD_SERVER_CODE_EQUALS + responseSubmitTask.getStatus().getCode());
                                    else
                                        Log.i(TAG, AUTO_SEND_TASK_THREAD_SERVER_CODE_EQUALS + responseSubmitTask.getStatus().getCode() + ":" + responseSubmitTask.getStatus().getCode());
                                }
                                result = String.valueOf(responseSubmitTask.getStatus().getCode());
                                errMessage = responseSubmitTask.getStatus().getMessage();
                                errCode = responseSubmitTask.getStatus().getCode();
                            }
                        }

                        if (result != null && result.equalsIgnoreCase(SUCCESS) && (nTaskId != null && nTaskId.length() > 0 && !nTaskId.contains("~"))) {

                            List<TaskD> newlistTaskD = new ArrayList<>();
                            for (TaskD taskD : listTaskD) {
                                if (taskD.getImage() == null)
                                    taskD.setIs_sent(Global.TRUE_STRING);
                                else {
                                    if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                        taskD.setIs_sent(Global.TRUE_STRING);
                                        newlistTaskD.add(taskD);
                                    } else {
                                        if (TaskManager.isPartial(activity)) {
                                            taskD.setIs_sent(Global.FALSE_STRING);
                                        } else {
                                            taskD.setIs_sent(Global.TRUE_STRING);
                                        }
                                    }
                                }

                                //change to new uuid
                                if (!"".equals(uuidTaskFromServer) && uuidTaskFromServer != null) {
                                    taskD.setUuid_task_h(uuidTaskFromServer);
                                }
                                TaskDDataAccess.addOrReplace(activity, taskD);
                            }

                            if(listTaskD != null && listTaskD.size()>0){
                                TaskDDataAccess.addOrReplace(activity, listTaskD);
                            }

                            int imageLeft = AutoSendImageThread.countPendingImageBeforeUpload(activity, taskH.getUuid_task_h());
                            if (imageLeft > 0) {
                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                            } else {
                                try {
                                    List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, taskH.getUuid_user(), taskH.getUuid_task_h());
                                    if (taskd != null && !taskd.isEmpty()) {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_UPLOADING);
                                        ToDoList.updateStatusSurvey(taskId, TaskHDataAccess.STATUS_SEND_UPLOADING, imageLeft);
                                    } else {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                        ToDoList.removeSurveyFromList(taskId);
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_SENT);
                                    ToDoList.removeSurveyFromList(taskId);
                                    ACRA.getErrorReporter().putCustomData(ERROR_SUBMIT, e.getMessage());
                                    ACRA.getErrorReporter().handleSilentException(new Exception(AUTO_SEND_TASK_EXCEPTION));
                                }
                            }
                            taskH.setSubmit_date(taskH.getSubmit_date());
                            taskH.setSubmit_duration(sec);
                            taskH.setSubmit_size(size);
                            taskH.setSubmit_result(result);
                            taskH.setTask_id(nTaskId);
                            taskH.setLast_saved_question(1);
                            TaskHDataAccess.addOrReplace(activity, taskH);

                            //Nendi
                            submitResult.setTaskH(taskH);
                            submitResult.setTaskId(nTaskId);

                            if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)) {
                                // 2017/10/02 | olivia : update status Task di table TaskSummary
                                if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getAuditData().getApplication())) {
                                    TaskSummary taskSummary = TaskSummaryDataAccess.getOne(activity, uuidTaskH, taskH.getUuid_user());
                                    if (taskSummary != null) {
                                        boolean isTaskPaid = TaskDDataAccess.isTaskPaid(activity, uuidUser, uuidTaskH);
                                        if (isTaskPaid)
                                            taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_PAID);
                                        else
                                            taskSummary.setTask_status(TaskSummaryDataAccess.STATUS_FAIL);
                                        TaskSummaryDataAccess.addOrReplace(activity, taskSummary);
                                    }
                                }

                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)
                                        && NewMainActivity.mnSurveyApproval != null) {

                                    TaskHDataAccess.deleteWithRelation(activity, taskH);
                                    String newUUIDTaskH = Tool.getUUID();
                                    taskH.setUuid_task_h(newUUIDTaskH);
                                    TaskHDataAccess.addOrReplace(activity, taskH);
                                    for (TaskD d : newlistTaskD) {
                                        d.setTaskH(taskH);
                                        d.setUuid_task_d(Tool.getUUID());
                                        TaskDDataAccess.addOrReplace(activity, d);
                                    }

                                }
                                if (taskH.getIs_prepocessed() != null && taskH.getIs_prepocessed().equals(Global.FORM_TYPE_VERIFICATION)) {
                                    TimelineManager.insertTimeline(activity, taskH, true, false);
                                } else {
                                    TimelineManager.insertTimeline(activity, taskH);
                                }
                            } else if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                                TimelineManager.insertTimeline(activity, taskH);
                            }

                        }
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                // UI code goes here
                                try {
                                    newMainActivitySetCounter();
                                    if(viewAdapter!=null) {
                                        viewAdapter.notifyDataSetChanged();
                                    }
                                    if(PriorityTabFragment.viewAdapter!=null){
                                        PriorityTabFragment.viewAdapter.notifyDataSetChanged();
                                    }

                                    if (NewTimelineFragment.timelineHandler != null)
                                        NewTimelineFragment.timelineHandler.sendEmptyMessage(0);
                                    if (PriorityTabFragment.handler != null)
                                        PriorityTabFragment.handler.sendEmptyMessage(0);
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                }
                            }
                        });
                        try {
                            if (taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)) {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                                        Handler handler2 = new Handler(Looper.getMainLooper());
                                        handler2.post(new Runnable() {
                                            public void run() {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    mBuilder.setContentTitle(activity.getString(R.string.uploading_image))
                                                            .setContentText(activity.getString(R.string.progress_uploading))
                                                            .setSmallIcon(AutoSendImageThread.getNotificationUploadingIcon());
                                                    inboxStyle.setBigContentTitle(activity.getString(R.string.uploading_image));
                                                    inboxStyle.bigText(activity.getString(R.string.progress_uploading));
                                                    mBuilder.setStyle(inboxStyle);
                                                    mBuilder.setProgress(0, 0, true);
                                                    mBuilder.setOngoing(true);
                                                    mNotifyManager.notify(4, mBuilder.build());
                                                }
                                            }
                                        });
                                    }
                                    try {
                                        List<TaskD> taskd = TaskDDataAccess.getUnsentImageByTaskH(activity, taskH.getUuid_user(), taskH.getUuid_task_h());
                                        message = UploadImage(activity, taskd);
                                    } catch (Exception e) {             FireCrash.log(e);
                                        Global.setIsManualUploading(false);
                                        Global.setIsUploading(false);
                                        AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                                    }
                                } catch (Exception e) {
                                    FireCrash.log(e);
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                        }

                        if (errMessage != null) {
                            mNotifyManager.cancelAll();
                            this.taskH = header.getTaskH();
                            this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                            if (result != null && result.equals(STATUS_TASK_NOT_MAPPING)) {
                                this.taskH.setMessage(activity.getString(R.string.message_task_not_mapping) + ERROR + errCode + ")");
                            } else if (result != null && result.equals(STATUS_TASK_DELETED)) {
                                this.taskH.setMessage(activity.getString(R.string.message_sending_deleted) + ERROR + errCode + ")");
                            } else if (result != null && result.equals(STATUS_IMEI_FAILED)) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                    taskH.setMessage(activity.getString(R.string.message_android_id_not_registered) + ERROR + result + ")");
                                }else
                                    taskH.setMessage(activity.getString(R.string.message_imei_not_registered) + ERROR + result + ")");
                            } else {
                                this.taskH.setMessage(activity.getString(R.string.message_sending_failed) + ERROR + errCode + ")");
                            }
                            TaskHDataAccess.addOrReplace(activity, this.taskH);
                            TimelineManager.insertTimeline(activity, taskH);

                            //Nendi
                            submitResult.setTaskH(taskH);
                            submitResult.setTaskId(nTaskId);
                        }
                    } else {
                        mNotifyManager.cancelAll();
                        this.taskH = header.getTaskH();
                        this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                        this.taskH.setMessage(activity.getString(R.string.taskd_was_gone, taskH.getCustomer_name()) + ERROR + errCode + ")");
                        TaskHDataAccess.addOrReplace(activity, taskH);
                        Global.setIsManualSubmit(false);

                        //Nendi
                        submitResult.setTaskH(taskH);
                        submitResult.setTaskId(nTaskId);
                        AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                        AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                        TimelineManager.insertTimeline(activity, taskH);
                    }
                } else if (!Tool.isInternetconnected(activity)) {
                    mNotifyManager.cancelAll();
                    this.taskH = header.getTaskH();
                    this.taskH.setStatus(TaskHDataAccess.STATUS_SEND_PENDING);
                    this.taskH.setMessage(activity.getString(R.string.no_internet_connection));
                    TaskHDataAccess.addOrReplace(activity, this.taskH);
                    AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                    AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                    TimelineManager.insertTimeline(activity, taskH);

                    //Nendi
                    submitResult.setTaskH(taskH);
                    submitResult.setTaskId(taskH.getTask_id());
                } else {
                    Global.setIsManualSubmit(false);
                    AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                    AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                }
                TaskHDataAccess.doBackup(activity, taskH);
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }

                //Nendi
                submitResult.setResult(result);

                Global.setIsUploading(false);
                return message;
            }

            @Override
            protected void onPostExecute(final String message) {
                try {
                    if (MainServices.autoSendTaskThread != null) {
                        MainServices.autoSendTaskThread.stopWaiting();
                        MainServices.autoSendImageThread.stopWaiting();
                        MainServices.taskListThread.stopWaiting();
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }

                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);
                    }
                }

                //Nendi add submitHandler
                if (isTaskPaid) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(NewMainActivity.KEY_ACTION, 100);
                    bundle.putSerializable("submitResult", submitResult);
                    msg.setData(bundle);
                    activity.finish();
                    NewMainActivity.submitHandler.sendMessage(msg);
                }

                try {
                    if (message == null || message.length() == 0) {
                        String status = taskH.getStatus();
                        if (status.equals(TaskHDataAccess.STATUS_SEND_PENDING)) {
                            mNotifyManager.cancelAll();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            String counter = activity.getString(R.string.upload_complete);
                                            mBuilder.setContentText(counter)
                                                    // Removes the progress bar
                                                    .setProgress(0, 0, false);
                                            mBuilder.setOngoing(false);
                                            inboxStyle.bigText(counter);
                                            mBuilder.setStyle(inboxStyle);
                                            mNotifyManager.notify(4, mBuilder.build());
                                        }
                                    }
                                });
                            }
                        }
                    } else if (message.equals(NO_IMAGE)) {
                        mNotifyManager.cancelAll();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        mBuilder.setContentText(message)
                                                // Removes the progress bar
                                                .setProgress(0, 0, false);
                                        mBuilder.setOngoing(false);
                                        inboxStyle.bigText(message);
                                        mBuilder.setStyle(inboxStyle);
                                        mNotifyManager.notify(4, mBuilder.build());
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    Global.setIsManualUploading(false);
                    AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
                }
                Global.setIsManualSubmit(false);
                AutoSendTaskThread.removeTaskSubmitManual(this.taskH.getUuid_task_h());
                AutoSendImageThread.removeTaskUploadManual(this.taskH.getUuid_task_h());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void checkResubmit(final Context context, final SurveyHeaderBean header, final OnCheckResubmit listener) {
        new AsyncTask<Void, Void, MssResponseType>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(context,
                        "", context.getString(R.string.progressWait), true);
            }

            @Override
            protected MssResponseType doInBackground(Void... params) {
                CheckResubmitApi api = new CheckResubmitApi(context);
                try {
                    if (Tool.isInternetconnected(context)) {
                        return api.request(header.getTask_id());
                    }
                    return null;
                } catch (IOException e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final MssResponseType checkResubmitResponse) {
                super.onPostExecute(checkResubmitResponse);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if (checkResubmitResponse != null) {
                    if (checkResubmitResponse.getStatus().getMessage().equalsIgnoreCase("OK")) {
                        listener.isSuccess(true, checkResubmitResponse);
                    } else {
                        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                        dialogBuilder.withTitle(context.getString(R.string.info_capital))
                                .isCancelableOnTouchOutside(false)
                                .withIcon(android.R.drawable.ic_dialog_alert)
                                .withMessage(checkResubmitResponse.getStatus().getMessage())
                                .withButton1Text(context.getString(R.string.btnOk))
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();
                                        GlobalData.getSharedGlobalData().setDoingTask(false);
                                        TaskHDataAccess.deleteWithRelation(context, header.getTaskH());
                                        listener.isSuccess(false, checkResubmitResponse);
                                    }
                                })
                                .show();
                    }
                } else {
                    listener.isSuccess(false, checkResubmitResponse);
                    Toast.makeText(context, context.getString(R.string.failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    //untuk print
    public static void getTaskHAndTaskD(Context activity){
        List<TaskH> taskHs = TaskHDataAccess
                .getAllTaskByStatus(activity, GlobalData
                                .getSharedGlobalData().getUser()
                                .getUuid_user(),
                        TaskHDataAccess.STATUS_SEND_PENDING);

        List<JsonRequestSubmitTask> taskList = new ArrayList<>();
        if (taskHs != null && !taskHs.isEmpty()){
            for (int i = 0; taskHs.size() > i; i++){
                List<TaskD> taskDs = TaskDDataAccess.getAll(activity, taskHs.get(i).getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                JsonRequestSubmitTask task = new JsonRequestSubmitTask();
                task.setTaskH(taskHs.get(i));
                task.setTaskD(taskDs);
                taskList.add(task);
            }
        }
    }
    //comment sampai sini

    public interface OnCheckResubmit {
        void isSuccess(boolean success, MssResponseType response);
    }
}
