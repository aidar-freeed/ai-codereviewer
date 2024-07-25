package com.adins.mss.base.todolist;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.SurveyHeaderBean;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.todo.Task;
import com.adins.mss.base.todolist.form.PriorityTabFragment;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author bong.rk
 */
public class ToDoList {
    // Bong Dec 10th, 2014 - to determine query list
        /* SEARCH TYPE */
    public static final int SEARCH_BY_TASK_ID = 1;
    public static final int SEARCH_BY_ALL = 3;
    public static final int SEARCH_BY_PRIORITY_HIGH = 1;
    public static final int SEARCH_BY_PRIORITY_NORMAL = 2;
    public static final int SEARCH_BY_PRIORITY_LOW = 3;
    public static final int SEARCH_BY_STATUS_PENDING = 4;
    public static final int SEARCH_BY_STATUS_UPLOADING = 5;
    public static final int SEARCH_BY_STATUS_DRAFT = 6;
    public static final int SEARCH_BY_BATCH_ID = 7;
    public static final int SEARCH_BY_ALL_SENT = 8;
    // Bong Dec 11th, 2014 - to determine query while refresh taskList
    public static final int TASK_LIST_PRIORITY = 1;
    public static final int TASK_LIST_STATUS = 2;
    public static List<SurveyHeaderBean> listOfSurveyStatus;
    private static List<TaskH> listTask;
    private static int searchType;
    private static String searchContent;
    private Context context;
    private String userId;

    public ToDoList(Context context) {
        this.context = context;
        if (GlobalData.getSharedGlobalData().getUser() == null)
            NewMainActivity.InitializeGlobalDataIfError(context);
        userId = GlobalData.getSharedGlobalData().getUser().getUuid_user();
    }

    public static List<TaskH> getListTaskInPriority(Context context, int searchType, String searchContent) {
        ToDoList.searchType = searchType;
        ToDoList.searchContent = searchContent;

        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        if (uuid_user == null) {
            try {
                NewMainActivity.InitializeGlobalDataIfError(context);
            } catch (Exception e) {
                FireCrash.log(e);
            }
            uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        }
        if (searchContent == null || "".equals(searchContent) || searchContent.length() == 0)
            return listTask = TaskHDataAccess.getAllTaskInPriority(context, uuid_user)
                    ;
        else if (searchType == SEARCH_BY_TASK_ID)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByTask(context, uuid_user, searchContent)
                    ;
        else if (searchType == SEARCH_BY_ALL)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByAll(context, uuid_user, searchContent)
                    ;
        else {
            return listTask = TaskHDataAccess.getAllTaskInPriorityByCustomer(context, uuid_user, searchContent);
        }
    }

    public static List<TaskH> getListTaskInSequence(Context context) {
        ToDoList.searchType = searchType;
        ToDoList.searchContent = searchContent;

        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        if (uuid_user == null) {
            try {
                NewMainActivity.InitializeGlobalDataIfError(context);
            } catch (Exception e) {
                FireCrash.log(e);
            }
            uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        }
        return listTask = TaskHDataAccess.getAllTaskInPriority(context, uuid_user);
    }

    /**
     * Return task list which has been queried
     *
     * @return
     */
    public static List<TaskH> getListTask() {
        return listTask;
    }

    public static long getCounterTaskList(Context context) {
        long counter = 0;
        try {
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();

            long counterPriority = TaskHDataAccess.getTaskInPriorityCounter(context, uuidUser);
            long counterStatus = TaskHDataAccess.getTaskInStatusCounter(context, uuidUser);
            counter = counterPriority + counterStatus;
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static long getAllCounter(Context context) {
        long counter = 0;
        String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        try {
            long counterPriority = TaskHDataAccess.getTaskInPriorityCounter(context, uuidUser);
            long counterStatus = TaskHDataAccess.getTaskInStatusCounter(context, uuidUser);
            long counterVerify = getCounterVerificationTask(context);
            long counterApproval = getCounterApprovalTask(context);
            long counterVerifyBranch = getCounterVerificationTaskByBranch(context);
            long counterApprovalBranch = getCounterApprovalTaskByBranch(context);
            long counterAssignmentTask = getCounterAssignment(context);
            counter = counterPriority + counterStatus;

            if (MainMenuActivity.mnSVYApproval != null)
                counter += counterApproval;
            if (MainMenuActivity.mnSVYVerify != null)
                counter += counterVerify;
            if (MainMenuActivity.mnSVYApprovalByBranch != null)
                counter += counterApprovalBranch;
            if (MainMenuActivity.mnSVYVerifyByBranch != null)
                counter += counterVerifyBranch;
            if (MainMenuActivity.mnSVYAssignment != null)
                counter += counterAssignmentTask;
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static long getCounterAssignment(Context context) {
        if (NewMainActivity.mnSurveyAssign == null) return 0;

        long counter = 0;

        try {
            counter = Long.parseLong(String.valueOf(GlobalData.getSharedGlobalData().getCounterAssignment()));
        } catch (Exception e) {
            FireCrash.log(e);
            // empty
        }

        return counter;
    }

    public static long getCounterVerificationTask(Context context) {
        long counter = 0;
        try {
            counter = TaskHDataAccess.getVerificationTaskCounterByUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static long getCounterApprovalTask(Context context) {
        long counter = 0;
        try {
            counter = TaskHDataAccess.getApprovalTaskCounterByUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        } catch (Exception e) {
            FireCrash.log(e);
            e.getMessage();
        }
        return counter;
    }

    public static long getCounterVerificationTaskByBranch(Context context) {
        long counter = 0;
        try {
            counter = TaskHDataAccess.getVerificationTaskCounterByBranch(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static long getCounterApprovalTaskByBranch(Context context) {
        long counter = 0;
        try {
            counter = TaskHDataAccess.getApprovalTaskCounterByBranch(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static long getCounterStatusTask(Context context) {
        long counter = 0;
        try {
            counter = TaskHDataAccess.getTaskInStatusCounter(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static void addSurveyToList(SurveyHeaderBean bean, boolean removeOld) {
        List<SurveyHeaderBean> tempList = new ArrayList<>();
        if (getListOfSurveyStatus() == null)
            setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());

        if (removeOld) {
            int idx = 0;
            for (SurveyHeaderBean headerBean : getListOfSurveyStatus()) {
                try {
                    if (bean.getTask_id().equals(headerBean.getTask_id())) {
                        getListOfSurveyStatus().remove(idx);
                        break;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                idx++;
            }
        }
        getListOfSurveyStatus().add(getListOfSurveyStatus().size(), bean);
        tempList.addAll(getListOfSurveyStatus());
        setListOfSurveyStatus(null);
        setListOfSurveyStatus(tempList);
    }

    public static void refreshSurveyToList() {
        List<SurveyHeaderBean> tempList = new ArrayList<SurveyHeaderBean>();
        if (getListOfSurveyStatus() == null)
            setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());
        tempList.addAll(getListOfSurveyStatus());
        setListOfSurveyStatus(null);
        setListOfSurveyStatus(tempList);
    }

    public static void removeSurveyFromList(String taskId) {
        List<SurveyHeaderBean> tempList = new ArrayList<SurveyHeaderBean>();
        if (getListOfSurveyStatus() == null)
            setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());

        int idx = 0;
        for (SurveyHeaderBean headerBean : getListOfSurveyStatus()) {
            if (taskId.equals(headerBean.getTask_id())) {
                getListOfSurveyStatus().remove(idx);
                break;
            }
            idx++;
        }

        tempList.addAll(getListOfSurveyStatus());
        setListOfSurveyStatus(null);
        setListOfSurveyStatus(tempList);
    }

    public static void updateStatusSurvey(String taskId, String status, int imageLeft) {
        List<SurveyHeaderBean> tempList = new ArrayList<SurveyHeaderBean>();
        if (getListOfSurveyStatus() == null)
            setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());

        for (SurveyHeaderBean headerBean : getListOfSurveyStatus()) {
            if (taskId.equals(headerBean.getTask_id())) {
                headerBean.setStatus(status);
                headerBean.setImageLeft(imageLeft);
            }
            tempList.add(headerBean);
        }
        setListOfSurveyStatus(null);
        setListOfSurveyStatus(tempList);
    }

    public static void updatePrioritySurvey(String taskId, String priority) {
        List<SurveyHeaderBean> tempList = new ArrayList<SurveyHeaderBean>();
        if (getListOfSurveyStatus() == null)
            setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());

        int idx = 0;
        for (SurveyHeaderBean headerBean : getListOfSurveyStatus()) {
            if (taskId.equals(headerBean.getTask_id())) {
                headerBean.setPriority(priority);

                break;
            }
            idx++;
        }

        tempList.addAll(getListOfSurveyStatus());
        setListOfSurveyStatus(null);
        setListOfSurveyStatus(tempList);
    }

    public static void updateTaskIdStatusSurvey(String taskId, String newTaskID) {
        List<SurveyHeaderBean> tempList = new ArrayList<>();
        if (getListOfSurveyStatus() == null)
            setListOfSurveyStatus(new ArrayList<SurveyHeaderBean>());

        int idx = 0;
        for (SurveyHeaderBean headerBean : getListOfSurveyStatus()) {
            if (taskId.equals(headerBean.getTask_id())) {
                headerBean.setTask_id(newTaskID);
                break;
            }
            idx++;
        }

        tempList.addAll(getListOfSurveyStatus());
        setListOfSurveyStatus(null);
        setListOfSurveyStatus(tempList);
    }

    public static boolean isOldTask(TaskH h) {
        return !(h.getStatus().equals(TaskHDataAccess.STATUS_SEND_INIT) ||
                h.getStatus().equals(TaskHDataAccess.STATUS_TASK_APPROVAL) ||
                h.getStatus().equals(TaskHDataAccess.STATUS_TASK_VERIFICATION));
    }


    public List<TaskH> getListTask(int searchType, String uuidScheme, int ptpPosition, String tenorFrom, String tenorTo, String osFrom, String osTo, String custName) {
        ToDoList.searchType = searchType;
        return listTask = TaskHDataAccess.getTaskByFilter(context, uuidScheme,searchType, ptpPosition, tenorFrom, tenorTo, osFrom, osTo, custName);
    }

    /**
     * This method is used to get task list according to search by and search content.
     * All task in this method will be shown if they have not been submitted or
     * have not been downloaded yet from server or not new task.
     *
     * @param searchType
     * @param searchContent
     * @return
     */
    public List<TaskH> getListTaskInStatus(int searchType, String searchContent) {
        ToDoList.searchType = searchType;
        ToDoList.searchContent = searchContent;
        if (searchContent == null || "".equals(searchContent) || searchContent.length() == 0)
            return listTask = TaskHDataAccess.getAllTaskInStatus(context, userId)
                    ;
        else if (searchType == SEARCH_BY_TASK_ID)
            return listTask = TaskHDataAccess.getAllTaskInStatusByTask(context, userId, searchContent)
                    ;
        else if (searchType == SEARCH_BY_ALL)
            return listTask = TaskHDataAccess.getAllTaskInStatusByAll(context, userId, searchContent)
                    ;
        else {
            return listTask = TaskHDataAccess.getAllTaskInStatusByCustomer(context, userId, searchContent);
        }
    }

    public List<TaskH> getListTaskInStatusForMultiUser(int searchType, String searchContent) {
        ToDoList.searchType = searchType;
        ToDoList.searchContent = searchContent;
        if(searchContent==null || "".equals(searchContent) || searchContent.length()==0)
            return listTask = TaskHDataAccess.getAllTaskInStatusForMultiUser(context)
                    ;
        else if(searchType==SEARCH_BY_TASK_ID)
            return listTask = TaskHDataAccess.getAllTaskInStatusByTaskForMultiUser(context, searchContent)
                    ;
        else if(searchType==SEARCH_BY_ALL)
            return listTask = TaskHDataAccess.getAllTaskInStatusByAllForMultiUser(context, searchContent)
                    ;
        else if(searchType==SEARCH_BY_ALL_SENT)
            return listTask = TaskHDataAccess.getAllTaskSentByAllForMultiUser(context, searchContent)
                    ;
        else if(searchType==SEARCH_BY_BATCH_ID)
            return listTask = TaskHDataAccess.getAllTaskInStatusByBatchIdForMultiUser(context, searchContent)
                    ;
        else{
            return listTask = TaskHDataAccess.getAllTaskInStatusByCustomerForMultiUser(context, searchContent);
        }
//		return listTask;
    }

    /**
     * This method is used to get task list according to search by and search content.
     * All task in this method will be shown if they are new task or they have been downloaded from server.
     *
     * @param searchType
     * @param searchContent
     * @return
     */
    public List<TaskH> getListTaskInPriority(int searchType, String searchContent) {
        ToDoList.searchType = searchType;
        ToDoList.searchContent = searchContent;
        if (searchContent == null || "".equals(searchContent) || searchContent.length() == 0)
            return listTask = TaskHDataAccess.getAllTaskInPriority(context, userId)
                    ;
        else if (searchType == SEARCH_BY_TASK_ID)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByTask(context, userId, searchContent)
                    ;
        else if (searchType == SEARCH_BY_ALL)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByAll(context, userId, searchContent)
                    ;
        else {
            return listTask = TaskHDataAccess.getAllTaskInPriorityByCustomer(context, userId, searchContent);
        }
    }

    public List<TaskH> getListTaskInPriority(int searchType, String searchContent, String uuidScheme) {
        ToDoList.searchType = searchType;
        ToDoList.searchContent = searchContent;
        if (uuidScheme.equals(PriorityTabFragment.uuidSchemeDummy)) {
            return listTask = TaskHDataAccess.getAllTaskInPriority(context, userId);
        } else if (searchContent == null || "".equals(searchContent) || searchContent.length() == 0)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByScheme(context, userId, uuidScheme);
        else if (searchType == SEARCH_BY_TASK_ID)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByTaskAndScheme(context, userId, searchContent, uuidScheme);
        else if (searchType == SEARCH_BY_ALL)
            return listTask = TaskHDataAccess.getAllTaskInPriorityByAllAndScheme(context, userId, searchContent, uuidScheme);
        else {
            return listTask = TaskHDataAccess.getAllTaskInPriorityByCustomerAndScheme(context, userId, searchContent, uuidScheme);
        }
    }

    public List<TaskH> getListTaskInHighPriority() {
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return listTask = TaskHDataAccess.getAllTaskInHighPriority(context, uuid_user);
    }

    public List<TaskH> getListTaskInNormalPriority() {
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return listTask = TaskHDataAccess.getAllTaskInNormalPriority(context, uuid_user);
    }

    public List<TaskH> getListTaskInLowPriority() {
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return listTask = TaskHDataAccess.getAllTaskInLowPriority(context, uuid_user);
    }

    public List<TaskH> getListTaskInHighPriority(String uuidSCheme) {
        if (uuidSCheme.equals(PriorityTabFragment.uuidSchemeDummy)) {
            return getListTaskInHighPriority();
        }
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return listTask = TaskHDataAccess.getAllTaskInHighPriorityByScheme(context, uuid_user, uuidSCheme);
    }

    public List<TaskH> getListTaskInNormalPriority(String uuidScheme) {
        if (uuidScheme.equals(PriorityTabFragment.uuidSchemeDummy)) {
            return getListTaskInNormalPriority();
        }
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return listTask = TaskHDataAccess.getAllTaskInNormalPriorityByScheme(context, uuid_user, uuidScheme);
    }

    public List<TaskH> getListTaskInLowPriority(String uuidScheme) {
        if (uuidScheme.equals(PriorityTabFragment.uuidSchemeDummy)) {
            return getListTaskInLowPriority();
        }
        String uuid_user = GlobalData.getSharedGlobalData().getUser().getUuid_user();
        return listTask = TaskHDataAccess.getAllTaskInLowPriorityByScheme(context, uuid_user, uuidScheme);
    }

    /**
     * This method will redisplay all task list according to search by and search content.
     *
     * @param menuBar
     * @return
     */
    public List<TaskH> doRefresh(int menuBar) {
        if (menuBar == TASK_LIST_PRIORITY)
            return getListTaskInPriority(searchType, searchContent);
        else if (menuBar == TASK_LIST_STATUS)
            return getListTaskInStatus(searchType, searchContent);
        else return listTask;
    }

    /**
     * This method will get status from a spesific taskId.
     *
     * @param taskId
     * @return
     */
    public String getStatus(String taskId) {
        String statusTask = null;
        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
        statusTask = taskH.getStatus();
        return statusTask;
    }

    public TaskH getOneTaskFromServer(Scheme scheme) {
        RequestOneTaskBean rotb = new RequestOneTaskBean();
        rotb.setSchemeId(scheme.getForm_id());
        rotb.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        rotb.addImeiAndroidIdToUnstructured();

        String json = GsonHelper.toJson(rotb);
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult result = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_GET_TASKLIST(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            result = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_GET_TASKLIST(), json);
            Utility.metricStop(networkMetric, result);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        if(result == null){
            return null;
        }

        JsonOneTask jot = GsonHelper.fromJson(result.getResult(), JsonOneTask.class);
        TaskHDataAccess.addOrReplace(context, jot.getOneTaskH());
        return jot.getOneTaskH();

    }

    /**
     * This method is used to retrieve list of TaskH from server
     *
     * @return
     */
    public List<TaskH> getListTaskFromServer() {
        RequestTaskListBean rtlb = new RequestTaskListBean();
        rtlb.setUserId(userId);
        rtlb.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        rtlb.addImeiAndroidIdToUnstructured();
        // to JSON
        String json = GsonHelper.toJson(rtlb);

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult result = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_GET_TASKLIST(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            result = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_GET_TASKLIST(), json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, result);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        if(result == null){
            return new ArrayList<>();
        }

        JsonTaskList jtl = GsonHelper.fromJson(result.getResult(), JsonTaskList.class);
        TaskHDataAccess.addOrReplace(context, jtl.getListTaskH());
        return jtl.getListTaskH();
    }

    public long getCounterAllPriority() {
        long counter = 0;
        try {
            String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
            counter = TaskHDataAccess.getTaskInPriorityCounter(context, uuidUser);
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public long getCounterHighPriority() {
        long counter = 0;
        try {
            counter = getListTaskInHighPriority().size();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public long getCounterNormalPriority() {
        long counter = 0;
        try {
            counter = getListTaskInNormalPriority().size();
        } catch (Exception e) {
            FireCrash.log(e);
        }
        return counter;
    }

    public static List<SurveyHeaderBean> getListOfSurveyStatus() {
        return listOfSurveyStatus;
    }

    public static void setListOfSurveyStatus(List<SurveyHeaderBean> listOfSurveyStatus) {
        ToDoList.listOfSurveyStatus = listOfSurveyStatus;
    }
}
