package com.adins.mss.base.todolist.form;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.adins.mss.base.Backup;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.helper.JsonRequestRetrieveVerificationForm;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.PlanTask;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.TaskHSequence;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.PlanTaskDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHSequenceDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineDataAccess;
import com.adins.mss.foundation.db.dataaccess.TimelineTypeDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 27/07/2017.
 */

public class TasklistImpl implements TasklistInterface {
    public static final int TASK_DURATION = 2 * 1000; // 2 seconds
    public static final String uuidSchemeDummy = "schemeDummy";

    public Activity activity;
    public int position = 0;
    public String errMessage = "";
    public boolean isGetFromServer;
    private List<TaskH> listTaskH;
    private ToDoList toDoList;
    private Context context;
    private String param;
    private Scheme selectedScheme;
    private List<Scheme> listScheme;
    private boolean isPriorityOpen = false;
    private RefreshBackgroundTask backgroundTask;
    private TasklistListener listener;
    public int ptp;
    public String tenorFromValue = "";
    public String tenorToValue = "";
    public String osFromValue = "";
    public String osToValue = "";
    public String custName = "";

    public TasklistImpl(Fragment fragment, TasklistListener listener) {
        this.activity = fragment.getActivity();
        this.context = fragment.getContext();
        this.toDoList = new ToDoList(context);
        this.listener = listener;
    }

    public int getSelectedTask() {
        return position;
    }

    public void setSelectedTask(int taskPosition) {
        this.position = taskPosition;
    }

    @Override
    public int getPtp() {
        return ptp;
    }

    @Override
    public void setPtp(int ptp) {
        this.ptp = ptp;
    }

    @Override
    public String getTenorFromValue() {
        return tenorFromValue;
    }

    @Override
    public void setTenorFromValue(String tenorFrom) {
        this.tenorFromValue = tenorFrom;
    }

    @Override
    public String getTenorToValue() {
        return tenorToValue;
    }

    @Override
    public void setTenorToValue(String tenorTo) {
        this.tenorToValue = tenorTo;
    }

    @Override
    public String getOsFromValue() {
        return osFromValue;
    }

    @Override
    public void setOsFromValue(String osFrom) {
        this.osFromValue = osFrom;
    }

    @Override
    public String getOsToValue() {
        return osToValue;
    }

    @Override
    public void setOsToValue(String osTo) {
        this.osToValue = osTo;
    }

    @Override
    public String getCustName() {
        return custName;
    }

    @Override
    public void setCustName(String custName) {
        this.custName = custName;

    }


    @Override
    public List<Scheme> listScheme() {

        Scheme schemeDummy = new Scheme();
        schemeDummy.setUuid_scheme(uuidSchemeDummy);
        schemeDummy.setScheme_description("All");
        schemeDummy.setForm_id("schemeDummy");
        listScheme.add(0, schemeDummy);

        return listScheme;
    }

    public void setListScheme(List<Scheme> list) {
        this.listScheme = list;
    }

    @Override
    public List<TaskHSequence> getTaskHSequences() {
        return Collections.emptyList();
    }

    @Override
    public List<TaskH> listTaskH() {
        return Collections.emptyList();
    }

    @Override
    public List<TaskH> getSelectedTaskH(int position) {
        if (listTaskH != null && !listTaskH.isEmpty())
            listTaskH.clear();

        selectedScheme = getSelectedScheme();

        switch (position) {
            case 0:
                listTaskH = getTaskH(selectedScheme, 0);
                break;
            case 1:
                listTaskH = getTaskH(selectedScheme, 1);
                break;
            case 2:
                listTaskH = getTaskH(selectedScheme, 2);
                break;
            case 3:
                listTaskH = getTaskH(selectedScheme, 3);
                break;
            case 4:
                listTaskH = getTaskH(selectedScheme, 4);
                break;
            case 5:
                listTaskH = getTaskH(selectedScheme, 5);
                break;
            case 6:
                listTaskH = getTaskH(selectedScheme, 6);
                break;
            default:
                break;
        }

        return listTaskH;
    }

    @Override
    public List<TaskH> getTaskH(Scheme selectedScheme, int searchType) {
        listTaskH = new ArrayList<>();
        listTaskH = toDoList.getListTask(searchType, (null == selectedScheme) ? uuidSchemeDummy : selectedScheme.getUuid_scheme(), ptp, tenorFromValue, tenorToValue, osFromValue,  osToValue, custName);

        List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
        List<TaskH> taskHLists = new ArrayList<>();
        if (taskHSequences.isEmpty() || GlobalData.isNewTaskAvailable() || needUpdateTaskHSequence(listTaskH,taskHSequences)) {
            TaskHSequenceDataAccess.clean(context);
            TaskHSequenceDataAccess.insertAllNewTaskHSeq(context, listTaskH);
            taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
            GlobalData.setNewTaskAvailable(false);
        }
        for (int i = 0; i < taskHSequences.size(); i++) {
            taskHLists.add(taskHSequences.get(i).getTaskH());
        }
        listTaskH = taskHLists;

        //check for planned tasks
        if(Global.PLAN_TASK_ENABLED){
            List<TaskH> nonPlannedTasks = new ArrayList<>();
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            List<PlanTask> planTasks = PlanTaskDataAccess.getAllPlan(context,uuidUser);
            if(!planTasks.isEmpty()){
                for(TaskH _taskH : listTaskH){
                    if(_taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT) ||
                            _taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_UPLOADING)
                            || _taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_PENDING))
                        continue;
                    boolean taskHasPlanned = false;
                    for (PlanTask planTask: planTasks){
                        if(_taskH.getTask_id().equals(planTask.getUuid_task_h())){
                            taskHasPlanned = true;
                            break;
                        }
                    }
                    if(!taskHasPlanned){
                        nonPlannedTasks.add(_taskH);
                    }
                }
                //set taskh with non planned tasks
                listTaskH = nonPlannedTasks;
            }
        }

        return listTaskH;
    }

    private boolean needUpdateTaskHSequence(List<TaskH> taskHList, List<TaskHSequence> taskHSequences){
        boolean result = false;
        if(taskHSequences.size() != taskHList.size())
            return true;

        //check if there is taskH created from handset
        for (TaskH taskH : taskHList) {
            if (taskH.getAssignment_date() == null) {
                result = true;
                break;
            }
        }
        if(!result){
            for (int i = 0; i<taskHSequences.size(); i++) {
                TaskH taskH = taskHSequences.get(i).getTaskH();
                if(taskH == null || !taskHList.get(i).getTask_id().equals(taskH.getTask_id()) || taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_SENT)
                        || taskH.getStatus().equals(TaskHDataAccess.STATUS_SEND_DELETED)){
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public List<TaskH> getAllTaskH(Scheme selectedScheme) {
        listTaskH = new ArrayList<>();
        listTaskH = toDoList.getListTaskInPriority(ToDoList.SEARCH_BY_ALL, "",
                (selectedScheme == null) ? uuidSchemeDummy : selectedScheme.getUuid_scheme());

        List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
        List<TaskH> taskHLists = new ArrayList<>();
        TaskHSequenceDataAccess.clean(context);
        TaskHSequenceDataAccess.insertAllNewTaskHSeq(context, listTaskH);
        taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
        for (int i = 0; i < taskHSequences.size(); i++) {
            taskHLists.add(taskHSequences.get(i).getTaskH());
        }
        listTaskH = taskHLists;
        return listTaskH;
    }

    @Override
    public List<TaskH> getTaskHInHighPriority(Scheme selectedScheme) {
        listTaskH = new ArrayList<>();
        listTaskH.addAll(toDoList.getListTaskInHighPriority(selectedScheme.getUuid_scheme()));
        if (listTaskH.isEmpty()) {
            return listTaskH;
        }
        List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
        List<TaskH> taskHLists = new ArrayList<>();
        if (taskHSequences.isEmpty()) {
            TaskHSequenceDataAccess.clean(context);
            TaskHSequenceDataAccess.insertAllNewTaskHSeq(context, listTaskH);
            TaskHSequenceDataAccess.getAllOrderAsc(context);
        } else {
            for (int i = 0; i < listTaskH.size(); i++) {
                TaskHSequence taskHSequence = TaskHSequenceDataAccess.getTaskHSeqByUUIDTaskH(context, listTaskH.get(i).getUuid_task_h());
                taskHLists.add(taskHSequence.getTaskH());
            }
        }
        listTaskH = taskHLists;
        return listTaskH;
    }

    @Override
    public List<TaskH> getTaskHInLowPriority(Scheme selectedScheme) {
        listTaskH = new ArrayList<>();
        listTaskH.addAll(toDoList.getListTaskInLowPriority(selectedScheme.getUuid_scheme()));
        if (listTaskH.isEmpty()) {
            return listTaskH;
        }
        List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
        List<TaskH> taskHLists = new ArrayList<>();
        if (taskHSequences.isEmpty()) {
            TaskHSequenceDataAccess.clean(context);
            TaskHSequenceDataAccess.insertAllNewTaskHSeq(context, listTaskH);
            TaskHSequenceDataAccess.getAllOrderAsc(context);
        } else {
            for (int i = 0; i < listTaskH.size(); i++) {
                TaskHSequence taskHSequence = TaskHSequenceDataAccess.getTaskHSeqByUUIDTaskH(context, listTaskH.get(i).getUuid_task_h());
                taskHLists.add(taskHSequence.getTaskH());
            }
        }
        listTaskH = taskHLists;
        return listTaskH;
    }

    @Override
    public List<TaskH> getTaskHInNormalPriority(Scheme selectedScheme) {
        listTaskH.addAll(toDoList.getListTaskInNormalPriority(selectedScheme.getUuid_scheme()));
        if (listTaskH.isEmpty()) {
            return listTaskH;
        }
        List<TaskHSequence> taskHSequences = TaskHSequenceDataAccess.getAllOrderAsc(context);
        List<TaskH> taskHLists = new ArrayList<>();
        if (taskHSequences.isEmpty()) {
            TaskHSequenceDataAccess.clean(context);
            TaskHSequenceDataAccess.insertAllNewTaskHSeq(context, listTaskH);
            TaskHSequenceDataAccess.getAllOrderAsc(context);
        } else {
            for (int i = 0; i < listTaskH.size(); i++) {
                TaskHSequence taskHSequence = TaskHSequenceDataAccess.getTaskHSeqByUUIDTaskH(context, listTaskH.get(i).getUuid_task_h());
                taskHLists.add(taskHSequence.getTaskH());
            }
        }
        listTaskH = taskHLists;
        return listTaskH;
    }

    @Override
    public String getTasklistFromServer(Context context) {
        String errMsg = "";
        if (Tool.isInternetconnected(activity)) {
            String result;
            User user = GlobalData.getSharedGlobalData().getUser();
            JsonRequestRetrieveVerificationForm requestType = new JsonRequestRetrieveVerificationForm();
            requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            requestType.addImeiAndroidIdToUnstructured();

            String json = GsonHelper.toJson(requestType);
            String url = GlobalData.getSharedGlobalData().getURL_GET_TASKLIST();
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
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorRequestToServer", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorRequestToServer", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat request to server"));
                e.printStackTrace();
                errMsg = activity.getString(R.string.jsonParseFailed);
                return errMsg;
            }


            List<String> listUuidTaskH = new ArrayList<>();

            if (serverResult != null && serverResult.isOK()) {
                try {
                    result = serverResult.getResult();
                    JsonResponseTaskList taskList = GsonHelper.fromJson(result, JsonResponseTaskList.class);
                    if (taskList.getStatus().getCode() == 0) {
                        List<TaskH> listTaskH = taskList.getListTaskList();
                        if (listTaskH != null && !listTaskH.isEmpty()) {
                            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                            TaskHDataAccess.deleteTaskHByStatus(activity, uuidUser, TaskHDataAccess.STATUS_SEND_INIT);

                            for (TaskH taskH : listTaskH) {
                                taskH.setUser(user);
                                taskH.setIs_verification(Global.TRUE_STRING);

                                String uuid_scheme = taskH.getUuid_scheme();
                                listUuidTaskH.add(taskH.getUuid_task_h());
                                Scheme scheme = SchemeDataAccess.getOne(activity, uuid_scheme);
                                if (scheme != null) {
                                    taskH.setScheme(scheme);
                                    TaskH h = TaskHDataAccess.getOneHeader(activity, taskH.getUuid_task_h());
                                    String uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(activity, Global.TIMELINE_TYPE_TASK).getUuid_timeline_type();
                                    boolean wasInTimeline = TimelineDataAccess.getOneTimelineByTaskH(activity, user.getUuid_user(), taskH.getUuid_task_h(), uuid_timelineType) != null;
                                    if (h != null && h.getStatus() != null) {
                                        if (!ToDoList.isOldTask(h)) {
                                            taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                            TaskHDataAccess.addOrReplace(activity, taskH);
                                            if (!wasInTimeline)
                                                TimelineManager.insertTimeline(activity, taskH);
                                        } else {
                                            if (taskH.getPts_date() != null) {
                                                h.setPts_date(taskH.getPts_date());
                                                TaskHDataAccess.addOrReplace(activity, h);
                                            }
                                        }
                                    } else {
                                        taskH.setStatus(TaskHDataAccess.STATUS_SEND_INIT);
                                        TaskHDataAccess.addOrReplace(activity, taskH);
                                        if (!wasInTimeline)
                                            TimelineManager.insertTimeline(activity, taskH);
                                    }
                                }
                            }
                            List<TaskH> taskHs = TaskHDataAccess.getAllTaskByStatus(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                            taskHs.addAll(TaskHDataAccess.getAllTaskByStatus(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), TaskHDataAccess.STATUS_SEND_SAVEDRAFT));

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
                                    TaskHDataAccess.deleteWithRelation(activity, h);
                                    if(h.getStatus().equals(TaskHDataAccess.STATUS_SEND_SAVEDRAFT)){
                                         needRemoveFromBackup.add(h);
                                    }
                                }
                            }

                            Backup backup = new Backup(activity);
                            backup.removeTask(needRemoveFromBackup);

                            if (Global.PLAN_TASK_ENABLED) {
                                TodayPlanRepository todayPlanRepository = GlobalData.getSharedGlobalData().getTodayPlanRepo();
                                todayPlanRepository.generatePlansFromTaskList(listTaskH);
                            }
                        }
                        errMsg = "noError";
                        return errMsg;
                    } else {
                        errMsg = activity.getString(R.string.jsonParseFailed);
                        return errMsg;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorGetMessageFromServer", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert json dari Server"));
                    errMsg = activity.getString(R.string.jsonParseFailed);
                    return errMsg;
                }
            }else {
                errMsg = activity.getString(R.string.jsonParseFailed);
                return errMsg;
            }
        } else {
            return errMsg;
        }
    }

    @Override
    public ToDoList getTodoList() {
        return new ToDoList(context);
    }

    @Override
    public void initiateRefresh(String type) {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask(type);
        backgroundTask.execute();
    }

    @Override
    public void initiateRefresh(boolean getDataFromServer) {
        cancelRefreshTask();
        backgroundTask = new RefreshBackgroundTask(getDataFromServer);
        backgroundTask.execute();
    }

    @Override
    public void cancelRefreshTask() {
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
            backgroundTask = null;
        }
    }

    @Override
    public AsyncTask<Void, Void, List<TaskH>> refreshBackgroundTask(boolean isFromServer) {
        this.isGetFromServer = isFromServer;
        return new AsyncTask<Void, Void, List<TaskH>>() {
            @Override
            protected void onCancelled() {
                super.onCancelled();
                if (listener != null) listener.onRefreshBackgroundCancelled(true);
            }

            @Override
            protected List<TaskH> doInBackground(Void... params) {
                try {
                    if (isGetFromServer)
                        errMessage = getTasklistFromServer(context);
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("errorGetErrorMessage", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("errorGetErrorMessage", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert taskList in error message"));
                    e.printStackTrace();
                }
                List<TaskH> listTaskH = new ArrayList<>();
                listTaskH = getSelectedTaskH(getSelectedTask());
                // Return a new random list of cheeses
                return listTaskH;
            }

            @Override
            protected void onPostExecute(List<TaskH> result) {
                super.onPostExecute(result);
                if (!errMessage.isEmpty() && !errMessage.equals("noError")) {
                    Toast.makeText(activity, errMessage, Toast.LENGTH_SHORT).show();
                }
                try {
                    NewMainActivity.setCounter();
                } catch (Exception e) {
                    FireCrash.log(e);
                    ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                    ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
                }

                if (listener != null) listener.onRefreshBackgroundComplete(result);
            }

        };
    }

    @Override
    public List<TaskH> refreshBackgroundTask(boolean isGetFromServer, int pos, SwipeRefreshLayout swipeRefreshLayout) {

        try {
            this.isGetFromServer = isGetFromServer;
            this.position = pos;
        } catch (Exception e) {
            FireCrash.log(e);

        }

        new AsyncTask<Void, Void, List<TaskH>>() {
            @Override
            protected List<TaskH> doInBackground(Void... args) {
                return Collections.emptyList();
            }

            @Override
            protected void onPostExecute(List<TaskH> result) {
                //EMPTY
            }
        }.execute();

        return Collections.emptyList();
    }

    @Override
    public String getParam() {
        try {

            GeneralParameter layoutSetting;
            if (Global.APPLICATION_SURVEY.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                layoutSetting = GeneralParameterDataAccess.getOne(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_TASK_LAYOUT_MS);
                param = layoutSetting ==  null ? "1" : layoutSetting.getGs_value();
            } else if (Global.APPLICATION_COLLECTION.equalsIgnoreCase(GlobalData.getSharedGlobalData().getApplication())) {
                layoutSetting = GeneralParameterDataAccess.getOne(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_TASK_LAYOUT_MC);
                param = layoutSetting ==  null ? "1" : layoutSetting.getGs_value();
            } else {
                layoutSetting = GeneralParameterDataAccess.getOne(activity, GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_TASK_LAYOUT_MO);
            }

            param = layoutSetting ==  null ? "1" : layoutSetting.getGs_value();

        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            ACRA.getErrorReporter().putCustomData("ErrorPriorityTabFragment", e.getMessage());
            ACRA.getErrorReporter().handleSilentException(new Exception("Error get param Priority Tab Fragment" + e.getMessage()));
        }

        if (null == param) {
            param = "1";
        }

        return param;
    }

    @Override
    public Scheme getSelectedScheme() {
        return selectedScheme;
    }

    @Override
    public void setSelectedScheme(Scheme scheme) {
        this.selectedScheme = scheme;
    }

    @Override
    public PriorityHandler getPriorityHandler(boolean isPriorityOpen) {
        return new PriorityHandler(isPriorityOpen);
    }

    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {
        private String tabs = "priority";

        //Default constructor
        public RefreshBackgroundTask(String tabs) {
            this.tabs = tabs;
        }

        public RefreshBackgroundTask(boolean isRequestToServer) {
            isGetFromServer = isRequestToServer;
        }

        public RefreshBackgroundTask(boolean isRequestToServer, int pos) {
            isGetFromServer = isRequestToServer;
            position = pos;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            listener.onRefreshBackgroundCancelled(true);
        }

        @Override
        protected List<TaskH> doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                if (isGetFromServer && !tabs.equals("status"))
                    errMessage = getTasklistFromServer(context);
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorGetErrorMessage", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorGetErrorMessage", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat convert taskList in error message"));
                e.printStackTrace();
            }

            List<TaskH> listTaskH = new ArrayList<>();
            switch (tabs) {
                case "status":
                    listTaskH.addAll(toDoList.getListTaskInStatus(ToDoList.SEARCH_BY_ALL, ""));
                    ToDoList.setListOfSurveyStatus(null);
                    List<SurveyHeaderBean> list = new ArrayList<>();
                    for (TaskH h : listTaskH) {
                        list.add(new SurveyHeaderBean(h));
                    }
                    ToDoList.setListOfSurveyStatus(list);
                    break;

                default:
                    listTaskH = getSelectedTaskH(getSelectedTask());
                    break;
            }

            // Return a new random list of cheeses
            return listTaskH;
        }

        @Override
        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
            if (!errMessage.isEmpty() && !errMessage.equals("noError")) {
                Toast.makeText(activity, errMessage, Toast.LENGTH_SHORT).show();
            }
            try {
                NewMainActivity.setCounter();
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", e.getMessage());
                ACRA.getErrorReporter().putCustomData("ErrorMainMenuActivity", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat set Drawer Counter"));
            }

            listener.onRefreshBackgroundComplete(result);
        }
    }

    public class PriorityHandler extends Handler {

        public PriorityHandler(boolean isPriority) {
            isPriorityOpen = isPriority;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                if (isPriorityOpen) {
                    initiateRefresh(false);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                ACRA.getErrorReporter().putCustomData("errorRefresh", e.getMessage());
                ACRA.getErrorReporter().putCustomData("errorRefresh", DateFormat.format("yyyy.MM.dd G \'at\' HH:mm:ss z", Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception("Exception saat initiate Refresh"));
            }
        }
    }
}
