package com.adins.mss.svy.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.ToDoList;
import com.adins.mss.base.todolist.form.JsonResponseTaskList;
import com.adins.mss.base.todolist.form.TasklistListener;
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
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.svy.R;
import com.adins.mss.svy.assignment.OrderAssignmentResult;
import com.adins.mss.svy.common.Toaster;
import com.adins.mss.svy.models.SurveyorSearchRequest;
import com.adins.mss.svy.models.SurveyorSearchResponse;
import com.adins.mss.svy.reassignment.JsonRequestCheckOrder;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by olivia.dg on 1/30/2018.
 */

public class SurveyActivityImpl implements SurveyActivityInterface {
    private static Context context;
    private RefreshBackgroundTask refreshTask;
    private ResultOrderTask resultOrderTask;

    public SurveyActivityImpl(Context context) {
        this.context = context;
    }

    @Override
    public void executeSearch(final TaskListener listener, SurveyorSearchRequest request, final String date, final int year, final int month, final String startDate,
                              final String endDate, final int position) throws ParseException, IOException {
        new AsyncTask<Void, Void, SurveyorSearchResponse>(){
            final ProgressDialog progress = new ProgressDialog(context);
            String errMessage;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage(context.getString(R.string.contact_server));
                progress.show();
            }

            @Override
            protected SurveyorSearchResponse doInBackground(Void... params) {
                try {
                    SurveyorSearchRequest searchRequest = new SurveyorSearchRequest();
                    searchRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());

                    try {
                        if (position == 0) {
                            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
                            Date date1 = f.parse(date);
                            searchRequest.setDate1(date1);
                            Date today = Tool.getSystemDate();
                            if(date1.after(today)) {
                                errMessage = context.getString(R.string.date_greater_than_today);
                                return null;
                            }
                        }
                        if (position == 2) {
                            SimpleDateFormat f = new SimpleDateFormat(Global.DATE_STR_FORMAT);
                            Date sDate, eDate;
                            long sLong = 0;
                            long eLong = 0;
                            try {
                                sDate = f.parse(startDate);
                                searchRequest.setDate1(sDate);
                                eDate = f.parse(endDate);
                                eDate.setHours(23);
                                eDate.setMinutes(59);
                                eDate.setSeconds(59);
                                searchRequest.setDate2(eDate);
                                sLong = sDate.getTime();
                                eLong = eDate.getTime();
                            } catch (ParseException e) {
                                errMessage = context.getString(R.string.enter_valid_date);
                                return null;
                            }
                            long milisecond = eLong - sLong;
                            if (milisecond > 604799000) {
                                errMessage = context.getString(R.string.data_range_not_allowed);
                                return null;
                            } else if (milisecond < 0) {
                                errMessage = context.getString(R.string.input_not_valid);
                                return null;
                            }

                        }
                        if (position == 1) {
                            if (month == 0 && year == 0){
                                errMessage = context.getString(R.string.enter_valid_date);
                                return null;
                            }
                            searchRequest.setMonth(String.valueOf(month + 1));
                            searchRequest.setYear(String.valueOf(year));
                        }
                    } catch (ParseException parseEx) {
                        errMessage = context.getString(R.string.enter_valid_date);
                        return null;
                    }

                    String json = GsonHelper.toJson(searchRequest);

                    String url = GlobalData.getSharedGlobalData().getURL_GET_SVYPERFORMANCE();
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
                    } catch (Exception e) {             FireCrash.log(e);
                        e.printStackTrace();
                    }
                    SurveyorSearchResponse serverResponse = null;
                    if (serverResult != null && serverResult.isOK()) {
                        try {
                            String responseBody = serverResult.getResult();
                            serverResponse = GsonHelper.fromJson(responseBody, SurveyorSearchResponse.class);

                        } catch (Exception e) {             FireCrash.log(e);
                            if(Global.IS_DEV) {
                                e.printStackTrace();
                                errMessage=e.getMessage();
                            }
                        }
                    } else {
                        errMessage = serverResult.getResult();
                    }

                    return serverResponse;
                } catch (Exception e) {             FireCrash.log(e);
                    errMessage = context.getString(R.string.jsonParseFailed);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(SurveyorSearchResponse serverResponse) {
                super.onPostExecute(serverResponse);
                if(context != null) {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (GlobalData.isRequireRelogin()) {

                    } else if (errMessage != null) {
                        Toaster.error(context, errMessage);
                    } else if (serverResponse != null && serverResponse.getListKeyValue() != null) {
//                        layout.setVisibility(View.VISIBLE);
//                        SurveyPerformanceFragment.this.getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                listResult.setAdapter(new SearchResultListAdapter(getActivity(), serverResponse.getListKeyValue()));
//                            }
//                        });
                        listener.onCompleteTask(serverResponse);
                    }
                }
            }
        }.execute();
    }

    @Override
    public void getBackgroundTask(TasklistListener listener, boolean isVerification, boolean isBranch) {
        refreshTask = new RefreshBackgroundTask(listener, isVerification, isBranch);
        refreshTask.execute();
    }

    @SuppressLint({"NewApi"})
    private class RefreshBackgroundTask extends AsyncTask<Void, Void, List<TaskH>> {
        static final int TASK_DURATION = 2000;
        String errMessage;
        boolean isVerification = false;
        boolean isBranch = false;
        private TasklistListener listener;

        private RefreshBackgroundTask(TasklistListener listener, boolean isVerification, boolean isBranch) {
            this.listener = listener;
            this.isVerification = isVerification;
            this.isBranch = isBranch;
        }

        protected List<TaskH> doInBackground(Void... params) {
            List<TaskH> result = null;
            User user = GlobalData.getSharedGlobalData().getUser();

            if (Tool.isInternetconnected(context)) {
                MssRequestType requestType = new MssRequestType();
                requestType.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                requestType.addImeiAndroidIdToUnstructured();

                String json = GsonHelper.toJson(requestType);
                String url;
                if (isVerification)
                    url = GlobalData.getSharedGlobalData().getURL_GET_LIST_VERIFICATION();
                else
                    url = GlobalData.getSharedGlobalData().getURL_GET_LIST_APPROVAL();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    if (isVerification) {
                        if (isBranch)
                            result = TaskHDataAccess.getAllVerifiedForBranch(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                        else
                            result = TaskHDataAccess.getAllVerifiedForUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    } else {
                        if (isBranch)
                            result = TaskHDataAccess.getAllApprovalForBranch(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                        else
                            result = TaskHDataAccess.getAllApprovalForUser(context, GlobalData.getSharedGlobalData().getUser().getUuid_user());
                    }
                    if (result != null && result.size() > 0)
                        serverResult = httpConn.requestToServer(url, json, Global.SORTCONNECTIONTIMEOUT);
                    else
                        serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                    errMessage = context.getString(R.string.jsonParseFailed);
                }

                String stringResult = serverResult.getResult();

                try {
                    JsonResponseTaskList taskList = GsonHelper.fromJson(stringResult, JsonResponseTaskList.class);
                    if (taskList.getStatus().getCode() == 0) {
                        List<TaskH> listTaskH = taskList.getListTaskList();
                        if (listTaskH != null && listTaskH.size() > 0) {
                            String uuid_timelineType;
                            if (isVerification) {
                                uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_VERIFICATION).getUuid_timeline_type();
                            } else {
                                uuid_timelineType = TimelineTypeDataAccess.getTimelineTypebyType(context, Global.TIMELINE_TYPE_APPROVAL).getUuid_timeline_type();
                            }
                            for (TaskH taskHLocal : result) {
                                boolean wasDeleted = true;
                                for (TaskH taskH : listTaskH) {
                                    if (taskH.getUuid_task_h().equals(taskHLocal.getUuid_task_h()))
                                        wasDeleted = false;
                                }
                                if (wasDeleted) {
                                    TaskHDataAccess.delete(context, taskHLocal);
                                }
                            }

                            for (TaskH taskH : listTaskH) {
                                taskH.setUser(user);
                                taskH.setIs_verification(Global.TRUE_STRING);

                                String uuid_scheme = taskH.getUuid_scheme();
                                Scheme scheme = SchemeDataAccess.getOne(context, uuid_scheme);
                                if (scheme != null) {
                                    taskH.setScheme(scheme);

                                    TaskH h = TaskHDataAccess.getOneHeader(context, taskH.getUuid_task_h());
                                    boolean wasInTimeline = TimelineDataAccess.getOneTimelineByTaskH(context, user.getUuid_user(), taskH.getUuid_task_h(), uuid_timelineType) != null;
                                    if (h != null && h.getStatus() != null) {
                                        if (!ToDoList.isOldTask(h)) {
                                            TaskHDataAccess.addOrReplace(context, taskH);
                                            if (!wasInTimeline)
                                                TimelineManager.insertTimeline(context, taskH);
                                        }
                                    } else {
                                        TaskHDataAccess.addOrReplace(context, taskH);
                                        if (!wasInTimeline)
                                            TimelineManager.insertTimeline(context, taskH);
                                    }
                                } else {
                                    errMessage = context.getString(com.adins.mss.base.R.string.scheme_not_found);
                                }
                            }
                        } else {
                            errMessage = context.getString(R.string.no_data_from_server);
                        }
                    } else {
                        errMessage = stringResult;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    errMessage = context.getString(R.string.jsonParseFailed);
                }
            } else {
                errMessage = context.getString(R.string.no_internet_connection);
            }

            return result;
        }

        protected void onPostExecute(List<TaskH> result) {
            super.onPostExecute(result);
            if (GlobalData.isRequireRelogin()) {

            } else if (errMessage != null && !errMessage.equals("")) {
                NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(context);
                builder.withTitle("INFO")
                        .withMessage(errMessage)
                        .show();
            }
            listener.onRefreshBackgroundComplete(result);
        }
    }

    public void gotoDetailData(int taskType, String nomorOrder, String uuid_task_h, String formName){
        Intent intent = null;

        switch (taskType) {
            case Global.TASK_ORDER_ASSIGNMENT:
//			intent = new Intent(this, OrderAssignmentActivity.class);
                break;
            case Global.TASK_ORDER_REASSIGNMENT:
                intent = new Intent(context, OrderAssignmentResult.class);
                intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
                intent.putExtra(Global.BUND_KEY_FORM_NAME, formName);
                break;
            default:
//			intent = new Intent(this, OrderAssignmentActivity.class);
                break;
        }
        intent.putExtra(Global.BUND_KEY_TASK_TYPE, taskType);
        intent.putExtra(Global.BUND_KEY_ORDERNO, nomorOrder);
        intent.putExtra(Global.BUND_KEY_UUID_TASKH, uuid_task_h);
        context.startActivity(intent);
    }

    public void getResultTask(TaskListener listener, List<NameValuePair> param) {
        resultOrderTask = new ResultOrderTask(listener, param);
        resultOrderTask.execute();
    }

    public String getDataFromURL(String url, List<NameValuePair> param) throws IOException {
        String resp = null;
        try {

            JsonRequestCheckOrder requestCheckOrder = new JsonRequestCheckOrder();
            requestCheckOrder.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            if(param.get(3).getValue().equals(Global.FLAG_BY_DATE)){
                requestCheckOrder.setStartDate(param.get(0).getValue());
                requestCheckOrder.setEndDate(param.get(1).getValue());
            }
            else if(param.get(3).getValue().equals(Global.FLAG_BY_ORDERNUMBER)){
                requestCheckOrder.setOrderNumber(param.get(2).getValue());
            }
            else if(param.get(3).getValue().equals(Global.FLAG_FOR_CANCELORDER)){
                if(param.get(2).getValue().equals("1")){
                    requestCheckOrder.setStartDate(param.get(0).getValue());
                    requestCheckOrder.setEndDate(param.get(1).getValue());
                }else{
                    requestCheckOrder.setOrderNumber(param.get(2).getValue());
                }
            }
            else if(param.get(3).getValue().equals(Global.FLAG_FOR_ORDERREASSIGNMENT)){
                if(param.get(2).getValue().equals("1")){
                    requestCheckOrder.setStartDate(param.get(0).getValue());
                    requestCheckOrder.setEndDate(param.get(1).getValue());
                }else{
                    requestCheckOrder.setOrderNumber(param.get(2).getValue());
                }
                requestCheckOrder.setFlag(param.get(3).getValue());
            }
            String json = GsonHelper.toJson(requestCheckOrder);
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

            resp = serverResult.getResult();
        } catch (Exception e) {
            FireCrash.log(e);
            resp = e.getMessage();
        }

        return resp;
    }

    private class ResultOrderTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;
        private String errMessage;
        private TaskListener listener;
        private  List<NameValuePair> param;

        private ResultOrderTask(TaskListener listener, List<NameValuePair> param) {
            this.listener = listener;
            this.param = param;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.progressWait), true);
        }

        @Override
        protected String doInBackground(String... arg0) {

            String result = null;
            if(Tool.isInternetconnected(context)){
                try {
                    result = getDataFromURL(GlobalData.getSharedGlobalData().getURL_CHECKORDER(), param);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {             FireCrash.log(e);}
                    errMessage = ioe.getMessage();
                }
            } else {
                result = context.getString(R.string.no_internet_connection);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (progressDialog!=null&&progressDialog.isShowing()){
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {             FireCrash.log(e);
                }
            }
            if (context.getString(R.string.no_internet_connection).equals(result)) {
                NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(context);
                builder.withTitle("INFO")
                        .withMessage(context.getString(R.string.no_internet_connection))
                        .show();
            }else{
                listener.onCompleteTask(result);
            }
        }
    }
}
