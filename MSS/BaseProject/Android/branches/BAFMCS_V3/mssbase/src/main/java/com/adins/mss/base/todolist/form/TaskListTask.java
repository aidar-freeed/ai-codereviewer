package com.adins.mss.base.todolist.form;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.Backup;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todo.form.JsonRequestScheme;
import com.adins.mss.base.todo.form.JsonResponseScheme;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.adins.mss.base.todolist.form.PriorityTabFragment.uuidSchemeDummy;

@SuppressLint("NewApi")
public class TaskListTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<FragmentActivity> activity;
    private ProgressDialog progressDialog;
    private String errMsg = null;
    private String messageWait;
    private String messageEmpty;
    private int contentFrame;

    public TaskListTask(FragmentActivity mainActivity, String messageWait, String messageEmpty, int contentFrame) {
        this.activity = new WeakReference<>(mainActivity);
        this.messageWait = messageWait;
        this.messageEmpty = messageEmpty;
        this.contentFrame = contentFrame;
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog = ProgressDialog.show(activity.get(), "", this.messageWait, true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity.get())) {
            User user = GlobalData.getSharedGlobalData().getUser();
            long totalData = 0;
            List<TaskH> taskHList = new ToDoList(activity.get()).getListTask(0, uuidSchemeDummy, 0, "", "", "", "", "");
            totalData = taskHList.size();
            if (null == taskHList || totalData == 0) {
                String result;
                MssRequestType requestType = new MssRequestType();
                requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                requestType.addImeiAndroidIdToUnstructured();

                String json = GsonHelper.toJson(requestType);
                String url = GlobalData.getSharedGlobalData().getURL_GET_TASKLIST();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
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
                    errMsg = e.getMessage();
                }


                List<String> listUuidTaskH = new ArrayList<>();

                if (serverResult != null) {
                    if (serverResult.isOK()) {
                        try {
                            result = serverResult.getResult();
                            JsonResponseTaskList taskList = GsonHelper.fromJson(result, JsonResponseTaskList.class);
                            if (taskList.getStatus().getCode() == 0) {
                                List<TaskH> listTaskH = taskList.getListTaskList();
                                if (listTaskH != null && !listTaskH.isEmpty()) {

                                    // bong 19 may 15 - delete all new taskH local before replace with the new ones from server
                                    String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                    TaskHDataAccess.deleteTaskHByStatus(activity.get(), uuidUser, TaskHDataAccess.STATUS_SEND_INIT);

                                    for (TaskH taskH : listTaskH) {
                                        taskH.setUser(user);
                                        taskH.setIs_verification(Global.TRUE_STRING);

                                        String uuid_scheme = taskH.getUuid_scheme();
                                        listUuidTaskH.add(taskH.getUuid_task_h());
                                        Scheme scheme = SchemeDataAccess.getOne(activity.get(), uuid_scheme);
                                        if (scheme != null) {
                                            taskH.setScheme(scheme);
                                            TaskH h = TaskHDataAccess.getOneHeader(activity.get(), taskH.getUuid_task_h());
                                            String uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(activity.get(), Global.TIMELINE_TYPE_TASK).getUuid_timeline_type();
                                            boolean wasInTimeline = TimelineDataAccess.getOneTimelineByTaskH(activity.get(), user.getUuid_user(), taskH.getUuid_task_h(), uuid_timelineType) != null;
                                            if (h != null && h.getStatus() != null) {
                                                if (!ToDoList.isOldTask(h)) {
                                                    taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                                    TaskHDataAccess.addOrReplace(activity.get(), taskH);
                                                    if (!wasInTimeline)
                                                        TimelineManager.insertTimeline(activity.get(), taskH);
                                                } else {
                                                    if (taskH.getPts_date() != null) {
                                                        h.setPts_date(taskH.getPts_date());
                                                        TaskHDataAccess.addOrReplace(activity.get(), h);
                                                    }
                                                }
                                            } else {
                                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                                TaskHDataAccess.addOrReplace(activity.get(), taskH);
                                                if (!wasInTimeline)
                                                    TimelineManager.insertTimeline(activity.get(), taskH);
                                            }
                                        } else {
                                            errMsg = activity.get().getString(R.string.scheme_not_found);
                                            ACRA.getErrorReporter().putCustomData("uuid Scheme", uuid_scheme);
                                            ACRA.getErrorReporter().handleSilentException(new Exception("Error: Scheme not available in DB. " + errMsg));
                                        }
                                    }
                                    List<TaskH> taskHs = TaskHDataAccess.getAllTaskByStatus(activity.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                                    taskHs.addAll(TaskHDataAccess.getAllTaskByStatus(activity.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_SAVEDRAFT));

                                    List<TaskH> needRemoveFromBackup = new ArrayList<>();
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
                                            TaskHDataAccess.deleteWithRelation(activity.get(), h);
                                            if(h.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                                                needRemoveFromBackup.add(h);
                                            }
                                        }
                                    }

                                    Backup backup = new Backup(activity.get());
                                    backup.removeTask(needRemoveFromBackup);

                                    //generate plan task if needed
                                    if(Global.PLAN_TASK_ENABLED){
                                        TodayPlanRepository todayPlanRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                        todayPlanRepository.generatePlansFromTaskList(listTaskH);
                                    }
                                }
                            } else {
                                errMsg = result;
                            }
                        } catch (Exception e) {
                            FireCrash.log(e);
                            errMsg = e.getMessage();
                        }
                    } else {
                        errMsg = serverResult.getResult();
                    }
                    return serverResult.isOK();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            errMsg = activity.get().getString(R.string.use_offline_mode);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
        Bundle argument = new Bundle();
        if (errMsg != null) {
            argument.putBoolean(TaskList_Fragment.BUND_KEY_ISERROR, true);
            argument.putString(TaskList_Fragment.BUND_KEY_MESSAGE, errMsg);
        } else if (Boolean.TRUE.equals(result)) {
            argument.putBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
        } else {
            if (!TaskHDataAccess.getAll(activity.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user()).isEmpty()) {
                argument.putBoolean(TaskList_Fragment.BUND_KEY_ISERROR, false);
            } else {
                argument.putBoolean(TaskList_Fragment.BUND_KEY_ISERROR, true);
                argument.putString(TaskList_Fragment.BUND_KEY_MESSAGE, errMsg);
            }
        }

        Fragment fragment1 = null;
        if(Global.PLAN_TASK_ENABLED){
            fragment1 = new TaskListFragment_new();
        }
        else {
            fragment1 = new PriorityTabFragment();
        }

        fragment1.setArguments(argument);
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment1);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void cekScheme(Context context, String uuid_scheme) {
        JsonRequestScheme requestScheme = new JsonRequestScheme();
        requestScheme.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        requestScheme.setUuid_user(GlobalData.getSharedGlobalData().getUser()
                .getUuid_user());
        requestScheme.setUuid_scheme(uuid_scheme);
        requestScheme.setTask(Global.TASK_GETONE);

        String json = GsonHelper.toJson(requestScheme);
        String url = GlobalData.getSharedGlobalData().getURL_GET_SCHEME();
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            serverResult = httpConn.requestToServer(url, json,
                    Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        if(serverResult == null){
            return;
        }

        if (serverResult.isOK()) {
            try {
                String result = serverResult.getResult();
                JsonResponseScheme responseScheme = GsonHelper.fromJson(result,
                        JsonResponseScheme.class);
                List<Scheme> schemes = responseScheme.getListScheme();
                try {
                    if (!schemes.isEmpty())
                        SchemeDataAccess.addOrReplace(context, schemes);
                } catch (Exception e) {
                    FireCrash.log(e);
                }

            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

    }
}
