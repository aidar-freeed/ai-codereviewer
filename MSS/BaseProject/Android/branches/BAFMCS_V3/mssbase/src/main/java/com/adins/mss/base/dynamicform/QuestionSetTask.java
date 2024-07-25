package com.adins.mss.base.dynamicform;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.commons.BackupManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.form.DynamicQuestionActivity;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.base.todolist.todayplanrepository.TodayPlanRepository;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GeneralParameter;
import com.adins.mss.dao.Lookup;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.GeneralParameterDataAccess;
import com.adins.mss.foundation.db.dataaccess.LookupDataAccess;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.questiongenerator.OptionAnswerBean;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.acra.ACRA;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestionSetTask extends AsyncTask<Void, Void, LinkedHashMap<String, QuestionBean>> implements IShowError {
    private static final String TAG = "QuestionSetTask";
    private static final String ERROR_QUESTION_SET_TASK = "ErrorQuestionSetTask";
    private static final String DATE_FORMAT = "yyyy.MM.dd G \'at\' HH:mm:ss z";
    private WeakReference<FragmentActivity> activity;
    Bundle bundle;
    LinkedHashMap<String, QuestionBean> questions = new LinkedHashMap<>();
    HttpCryptedConnection httpConn;
    HttpConnectionResult serverResult = null;
    int mode;
    private ProgressDialog progressDialog;
    private String errMsg = null;
    private ErrorMessageHandler errorMessageHandler;
    private boolean isCorrupt;
    private int threshold = 3;
    private int retry = 0;

    public QuestionSetTask(FragmentActivity activity, Bundle bundle) {
        this.activity = new WeakReference<>(activity);
        this.bundle = bundle;
        this.errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog = ProgressDialog.show(activity.get(), "", activity.get().getString(R.string.progressWait), true);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected LinkedHashMap<String, QuestionBean> doInBackground(Void... arg0) {
        // Nendi: 2019.09.25 | Set Max Retry download threshold
        GeneralParameter maxRetryGS = GeneralParameterDataAccess.getOne(activity.get(), GlobalData.getSharedGlobalData().getUser().getUuid_user(), Global.GS_RETRYDOWNLOADTD);
        threshold = (maxRetryGS == null) ? threshold : Integer.parseInt(maxRetryGS.getGs_value());

        String formVersion = CustomerFragment.getHeader().getForm_version();
        mode = bundle.getInt(Global.BUND_KEY_MODE_SURVEY);
        if (mode == Global.MODE_SURVEY_TASK) {
            List<QuestionSet> checkQuestion;
            try {
                checkQuestion = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
            } catch (Exception e) {
                FireCrash.log(e);
                Logger.e(TAG, e);
                ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, e.getMessage());
                ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, DateFormat.format(DATE_FORMAT, Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception(ERROR_QUESTION_SET_TASK));
                errMsg = activity.get().getString(R.string.task_cant_seen);
                return null;
            }
            boolean isHaveQuestion = (checkQuestion != null && !checkQuestion.isEmpty());
            if (!isHaveQuestion)
                getNewQuestionSet();

            String status = CustomerFragment.getHeader().getStatus();
            if (TaskHDataAccess.STATUS_SEND_SAVEDRAFT.equals(status)
                    || TaskHDataAccess.STATUS_SEND_DOWNLOAD.equals(status)
                    || TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD.equals(status)
                    || TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD.equals(status)) {
                questions.clear();
                List<QuestionSet> qs = new ArrayList<>();
                try {
                    qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
                } catch (Exception e) {
                    FireCrash.log(e);
                    Logger.e(TAG, e);
                    ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, e.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, DateFormat.format(DATE_FORMAT, Calendar.getInstance().getTime()).toString());
                }
                List<QuestionBean> listOfQuestions = new ArrayList<>();
                if (!qs.isEmpty()) {
                    try {
                        for (QuestionSet set : qs) {
                            QuestionBean bean = new QuestionBean(set);
                            listOfQuestions.add(bean);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        FireCrash.log(e);
                    }
                }

                try {
                    List<TaskD> listOfAnswers = TaskDDataAccess.getAll(activity.get(), CustomerFragment.getHeader().getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                    List<QuestionBean> questions2 = matchAnswerAndQuestion(listOfQuestions, listOfAnswers);

                    // Nendi: 2019.09.20 Check is any answer has corrupt?
                    isCorrupt = isCorrupted(questions2, listOfAnswers);
                    while (isCorrupt && retry < threshold) {
                        questions2 = retrieveTaskD(CustomerFragment.getHeader(), listOfQuestions, true);
                    }

                    if (!isCorrupt) {
                        activity.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage(activity.get().getString(R.string.please_wait_almost_done));
                            }
                        });

                        for (QuestionBean bean : questions2) {
                            if (questions.get(bean.getIdentifier_name().toUpperCase()) != null)
                                questions.remove(bean.getIdentifier_name().toUpperCase());
                            questions.put(bean.getIdentifier_name().toUpperCase(), bean);

                            try {
                                if (CustomerFragment.getIsEditable() && null != bean.getTag() && bean.getTag().equalsIgnoreCase("RV NUMBER")) {
                                    QuestionBean.resetAnswer(bean);
                                } else if(CustomerFragment.getIsEditable() && Global.AT_VALIDATION.equalsIgnoreCase(bean.getAnswer_type())) {
                                    bean.setAnswer(null);
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // Nendi: 2019.09.25 | Backup data to firebase for analytic purpose
                        BackupManager backupManager = new BackupManager(activity.get());
                        backupManager.backup(BackupManager.ACTION_BACKUP_TASK, CustomerFragment.getHeader());
                        errMsg = activity.get().getString(R.string.task_corrupt);
                    }

                } catch (Exception ex) {
                    errMsg = activity.get().getString(R.string.request_error);
                }

            } else if (TaskHDataAccess.STATUS_SEND_INIT.equals(status) ||
                    TaskHDataAccess.STATUS_TASK_VERIFICATION.equals(status) ||
                    TaskHDataAccess.STATUS_TASK_APPROVAL.equals(status)) {

                JsonRequestTaskD request = new JsonRequestTaskD();
                request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                request.setuuid_task_h(CustomerFragment.getHeader().getUuid_task_h());
                String uuidScheme = CustomerFragment.getHeader().getUuid_scheme();
                Scheme scheme = SchemeDataAccess.getOne(activity.get(), uuidScheme);
                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getURL_GET_VERIFICATION();

                if (CustomerFragment.getHeader().getStatus().equals(TaskHDataAccess.STATUS_SEND_DOWNLOAD)) {
                    questions.clear();
                    List<QuestionSet> qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), scheme.getUuid_scheme(), formVersion);
                    List<QuestionBean> listOfQuestions = new ArrayList<>();

                    for (QuestionSet set : qs) {
                        QuestionBean bean = new QuestionBean(set);
                        listOfQuestions.add(bean);
                    }

                    List<TaskD> listOfAnswers = TaskDDataAccess.getAll(activity.get(), CustomerFragment.getHeader().getUuid_task_h(), TaskDDataAccess.ALL_TASK);
                    List<QuestionBean> questions2 = QuestionBean.matchQuestionWithAnswer(activity.get(), listOfQuestions, listOfAnswers);

                    // Nendi: 2019.09.20 Check is any answer has corrupt?
                    isCorrupt = isCorrupted(questions2, listOfAnswers);
                    while (isCorrupt && retry < threshold) {
                        questions2 = retrieveTaskD(CustomerFragment.getHeader(), listOfQuestions, true);
                    }

                    if (!isCorrupt) {
                        activity.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage(activity.get().getString(R.string.please_wait_almost_done));
                            }
                        });

                        for (QuestionBean bean : questions2) {
                            if (questions.get(bean.getIdentifier_name().toUpperCase()) != null)
                                questions.remove(bean.getIdentifier_name().toUpperCase());
                            questions.put(bean.getIdentifier_name().toUpperCase(), bean);
                        }
                    } else {
                        // Nendi: 2019.09.25 | Backup data to firebase for analytic purpose
                        BackupManager backupManager = new BackupManager(activity.get());
                        backupManager.backup(BackupManager.ACTION_BACKUP_TASK, CustomerFragment.getHeader());
                        errMsg = activity.get().getString(R.string.task_corrupt);
                    }

                } else {
                    //Firebase Performance Trace HTTP Request
                    HttpMetric networkMetric =
                            FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                    Utility.metricStart(networkMetric, json);

                    if (Tool.isInternetconnected(activity.get())) {
                        try {
                            questions.clear();
                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                            httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
                            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            Utility.metricStop(networkMetric, serverResult);
                        } catch (Exception e) {
                            FireCrash.log(e);
                            if (Global.IS_DEV)
                                e.printStackTrace();
                            errMsg = e.getMessage();
                        }
                        if (serverResult != null && serverResult.isOK()) {
                            try {
                                String result = serverResult.getResult();

                                JsonResponseTaskD response = GsonHelper.fromJson(result, JsonResponseTaskD.class);
                                if (response.getStatus().getCode() == 0) {
                                    List<TaskD> taskDs = response.getListTask();
                                    TaskH taskH = CustomerFragment.getHeader().getTaskH();
                                    for (TaskD taskD : taskDs) {
                                        taskD.setTaskH(taskH);
                                    }
                                    if (!taskDs.isEmpty())
                                        TaskDDataAccess.addOrReplace(activity.get(), taskDs);
                                    List<QuestionSet> qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
                                    if (qs == null) {
                                        getNewQuestionSet();
                                        qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
                                    }

                                    List<QuestionBean> listOfQuestions = new ArrayList<>();
                                    for (QuestionSet set : qs) {
                                        QuestionBean bean = new QuestionBean(set);
                                        listOfQuestions.add(bean);
                                    }

                                    if (!taskDs.isEmpty()) {
                                        List<QuestionBean> questions2 = QuestionBean.matchAnswerToQuestion(activity.get(), listOfQuestions, taskDs, CustomerFragment.getHeader().getUuid_scheme());

                                        for (QuestionBean bean : questions2) {
                                            questions.put(bean.getIdentifier_name().toUpperCase(), bean);
                                        }

                                        if (questions.size() > 0) {
                                            if (taskH.getStatus().equals(TaskHDataAccess.STATUS_TASK_VERIFICATION)) {
                                                taskH.setStatus(TaskHDataAccess.STATUS_TASK_VERIFICATION_DOWNLOAD);
                                            } else if (taskH.getStatus().equals(TaskHDataAccess.STATUS_TASK_APPROVAL)) {
                                                taskH.setStatus(TaskHDataAccess.STATUS_TASK_APPROVAL_DOWNLOAD);
                                            } else {
                                                taskH.setStatus(TaskHDataAccess.STATUS_SEND_DOWNLOAD);
                                            }

                                            if (taskH.getPriority() == null || taskH.getPriority().length() == 0) {
                                                taskH.setStart_date(Tool.getSystemDateTime());
                                            }

                                            TaskHDataAccess.addOrReplace(activity.get(), taskH);
                                        }
                                    } else {
                                        for (QuestionBean bean : listOfQuestions) {
                                            questions.put(bean.getIdentifier_name().toUpperCase(), bean);
                                        }
                                    }
                                } else {
                                    errMsg = result;
                                }

                            } catch (Exception e) {
                                FireCrash.log(e);
                                try {
                                    if (CustomerFragment.getHeader().getPriority() != null && CustomerFragment.getHeader().getPriority().length() > 0) {
                                        errMsg = activity.get().getString(R.string.msgNoServerResponeString);
                                    } else {
                                        List<QuestionSet> qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
                                        for (QuestionSet set : qs) {
                                            QuestionBean bean = new QuestionBean(set);
                                            if (questions.get(bean.getIdentifier_name().toUpperCase()) != null)
                                                questions.remove(bean.getIdentifier_name().toUpperCase());
                                            questions.put(bean.getIdentifier_name().toUpperCase(), bean);
                                        }
                                    }
                                } catch (Exception e2) {
                                    errMsg = e.getMessage();
                                }
                            }
                        } else {
                            errMsg = activity.get().getString(R.string.msgNoServerResponeString);
                        }
                    } else {
                        errMsg = activity.get().getString(R.string.no_data_found_offline);
                    }
                }
            }
        } else if (mode == Global.MODE_VIEW_SENT_SURVEY) {
            List<QuestionSet> checkQuestion = new ArrayList<>();
            try {
                checkQuestion = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
            } catch (Exception e) {
                FireCrash.log(e);
                try {
                    Scheme scheme = SchemeDataAccess.getOne(activity.get(), CustomerFragment.getHeader().getUuid_scheme());
                    CustomerFragment.getHeader().getForm().setForm_version(scheme.getForm_version());
                    if (mode == Global.MODE_VIEW_SENT_SURVEY) {
                        if (null != scheme.getForm_version()) {
                            CustomerFragment.getHeader().setForm_version(scheme.getForm_version());
                        } else {
                            CustomerFragment.getHeader().setForm_version(CustomerFragment.getHeader().getScheme().getForm_version());
                        }
                    } else {
                        if (null == CustomerFragment.getHeader().getForm_version()) {
                            CustomerFragment.getHeader().setForm_version(scheme.getForm_version());
                        }
                    }
                } catch (Exception er) {
                    Logger.e(TAG, er);
                    ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, er.getMessage());
                    ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, DateFormat.format(DATE_FORMAT, Calendar.getInstance().getTime()).toString());
                    ACRA.getErrorReporter().handleSilentException(new Exception(ERROR_QUESTION_SET_TASK));
                    errMsg = activity.get().getString(R.string.task_cant_seen);
                    return null;
                }
            }
            boolean isHaveQuestion = (checkQuestion != null && !checkQuestion.isEmpty());
            List<QuestionSet> qs = new ArrayList<>();
            if (!isHaveQuestion)
                getNewQuestionSet();
            try {
                qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersion);
            } catch (Exception e) {
                FireCrash.log(e);
            }
            List<QuestionBean> listOfQuestions = new ArrayList<>();
            if (!qs.isEmpty()) {
                for (QuestionSet set : qs) {
                    QuestionBean bean = new QuestionBean(set);
                    listOfQuestions.add(bean);
                }
            }
            List<TaskD> listOfAnswers = TaskDDataAccess.getAll(activity.get(), CustomerFragment.getHeader().getUuid_task_h(), TaskDDataAccess.ALL_TASK);
            List<QuestionBean> questions2 = QuestionBean.matchAnswerToQuestion(activity.get(), listOfQuestions, listOfAnswers, CustomerFragment.getHeader().getUuid_scheme());
            for (QuestionBean bean : questions2) {
                questions.put(bean.getIdentifier_name().toUpperCase(), bean);
            }
        } else { //if new task
            try {
                Scheme scheme = SchemeDataAccess.getOne(activity.get(), CustomerFragment.getHeader().getUuid_scheme());
                List<QuestionSet> checkQuestion;
                checkQuestion = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), scheme.getForm_version());
                CustomerFragment.getHeader().getForm().setForm_version(scheme.getForm_version());
                if (mode == 2) {
                    if (null != scheme.getForm_version()) {
                        CustomerFragment.getHeader().setForm_version(scheme.getForm_version());
                    } else {
                        CustomerFragment.getHeader().setForm_version(CustomerFragment.getHeader().getScheme().getForm_version());
                    }
                } else {
                    if (null == CustomerFragment.getHeader().getForm_version()) {
                        CustomerFragment.getHeader().setForm_version(scheme.getForm_version());
                    }
                }
                boolean isHaveQuestion = (checkQuestion != null && !checkQuestion.isEmpty());
                if (!isHaveQuestion)
                    getNewQuestionSet();
                else {
                    getQuestionFromDB();
                }
            } catch (Exception e) {
                FireCrash.log(e);
                Logger.e(TAG, e);
                ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, e.getMessage());
                ACRA.getErrorReporter().putCustomData(ERROR_QUESTION_SET_TASK, DateFormat.format(DATE_FORMAT, Calendar.getInstance().getTime()).toString());
                ACRA.getErrorReporter().handleSilentException(new Exception(ERROR_QUESTION_SET_TASK));
                errMsg = activity.get().getString(R.string.task_cant_seen);
            }
        }

        setQuestionSet(questions);
        if (errMsg == null && questions != null && questions.size() > 0) {
            DynamicFormActivity.setListOfIdentifier(new ArrayList<>(questions.keySet()));
            generateTaskPlanRevisit();
        }

        return questions;
    }

    private void generateTaskPlanRevisit(){
        if(Global.PLAN_TASK_ENABLED && CustomerFragment.getIsEditable()){
            //set task header with new uuid but still use old task id , dynamic form use this new header
            TaskH header = CustomerFragment.getHeader();
            header.setUuid_task_h(Tool.getUUID());
            header.setStatus(TaskHDataAccess.STATUS_SEND_SAVEDRAFT);
            header.setRv_number(null);
            header.setStatus_rv(null);
            header.setSubmit_date(null);
            //save task header
            TaskHDataAccess.addOrReplace(activity.get(),header);

            //duplicate task d and set to new task header
            List<TaskD> copiedAnswers = TaskDDataAccess.getAll(activity.get(), header.getTask_id(), TaskDDataAccess.ALL_TASK);
            if(copiedAnswers == null)
                return;
            for(TaskD taskD:copiedAnswers){
                taskD.setUuid_task_d(Tool.getUUID());
                taskD.setUuid_task_h(header.getUuid_task_h());
            }

            //save task details
            TaskDDataAccess.addOrReplace(activity.get(),copiedAnswers);
            CustomerFragment.setIsEditable(false);
            TimelineManager.insertTimeline(activity.get(), header);
            TodayPlanRepository todayPlanRepo = GlobalData.getSharedGlobalData().getTodayPlanRepo();
            if(todayPlanRepo == null)
                return;
            try {
                todayPlanRepo.changePlanRevisit(header.getTask_id());
            }
            catch (Exception e){
                Toast.makeText(activity.get(), activity.get().getString(R.string.cannot_change_revisit), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (activity != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    private void sendAcraReport(String message) {
        ACRA.getErrorReporter().putCustomData("errorSetQuestion", message);
        ACRA.getErrorReporter().handleSilentException(new Exception("Error: Set Question Error " + message));
    }

    @Override
    protected void onPostExecute(final LinkedHashMap<String, QuestionBean> result) {
        if (retry > 0) retry = 0;
        if (errMsg != null) {
            if (errMsg.equalsIgnoreCase(activity.get().getString(R.string.question_not_found))) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
                dialogBuilder.withTitle("WARNING").withMessage(errMsg)
                        .isCancelable(false)
                        .isCancelableOnTouchOutside(true)
                        .withButton1Text("OK")
                        .setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                dialogBuilder.dismiss();
                                GlobalData.getSharedGlobalData().setDoingTask(false);
                            }
                        })
                        .show();
            } else if (result != null && result.size() > 0) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
                dialogBuilder.withTitle("WARNING").withMessage(errMsg + "\n" + activity.get().getString(R.string.offline_mode))
                        .isCancelable(false)
                        .isCancelableOnTouchOutside(true)
                        .withButton1Text("OK")
                        .setButton1Click(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Nendi: 2019.06.21 - Notify will come Dynamic Form
                                Global.setLockTask(!Global.isLockTask());

                                    CustomerFragment.doBack(activity.get());
                                Constant.setListOfQuestion(result);
                                //penjagaan untuk task tertukar karena CustomerFragment.header dapat berubah pada waktu delay Handler
                                final SurveyHeaderBean customerFragmentHeader = CustomerFragment.getHeader();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogBuilder.dismiss();
                                        GlobalData.getSharedGlobalData().setDoingTask(true);
                                        CustomerFragment.setHeader(customerFragmentHeader);
                                        Intent intent = new Intent(activity.get(), DynamicQuestionActivity.class);
                                        intent.putExtras(bundle);
                                        activity.get().startActivity(intent);
                                    }
                                }, 500);
                            }
                        })
                        .show();
            } else {
                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
                if (errMsg.equals(activity.get().getString(R.string.no_data_found_offline)))
                    dialogBuilder.withTitle("INFO");
                else
                    dialogBuilder.withTitle("ERROR");
                dialogBuilder.withMessage(errMsg).isCancelable(true).show();
                GlobalData.getSharedGlobalData().setDoingTask(false);
            }
        } else {
            if (result != null) {
                if (result.size() > 0) {
                    //Nendi: 2019.06.21 - Notify will come Dynamic Form
                    Global.setLockTask(!Global.isLockTask());

                    if (!bundle.getBoolean(Global.BUND_KEY_MODE_SIMULASI, false))
                        CustomerFragment.doBack(activity.get());
                    Constant.setListOfQuestion(result);
                    //penjagaan untuk task tertukar karena CustomerFragment.header dapat berubah pada waktu delay Handler
                    final SurveyHeaderBean customerFragmentHeader = CustomerFragment.getHeader();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!GlobalData.isRequireRelogin()) {
                                GlobalData.getSharedGlobalData().setDoingTask(true);
                                bundle.putString(Global.BUND_KEY_FORM_BEAN, GsonHelper.toJson(bundle.getSerializable(Global.BUND_KEY_FORM_BEAN)));
                                bundle.putString(Global.BUND_KEY_SURVEY_BEAN, GsonHelper.toJson(bundle.getSerializable(Global.BUND_KEY_SURVEY_BEAN)));
                                Intent intent = new Intent(activity.get(), DynamicQuestionActivity.class);
                                intent.putExtras(bundle);
                                activity.get().startActivity(intent);
                                CustomerFragment.setHeader(customerFragmentHeader);
                            }
                        }
                    }, 500);
                } else {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
                    dialogBuilder.withTitle("INFO").withMessage(activity.get().getString(R.string.question_not_found))
                            .withButton1Text("OK")
                            .setButton1Click(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialogBuilder.dismiss();
                                    if (!bundle.getBoolean(Global.BUND_KEY_MODE_SIMULASI, false))
                                        CustomerFragment.doBack(activity.get());
                                }
                            })
                            .show();

                }

            }
        }

        if (progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
    }

    public void getNewQuestionSet() {
        if (Tool.isInternetconnected(activity.get())) {
            JsonRequestQuestionSet request = new JsonRequestQuestionSet();
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            request.setUuid_scheme(CustomerFragment.getHeader().getUuid_scheme());
            Scheme scheme = CustomerFragment.getHeader().getScheme();
            String formVersionScheme = scheme.getForm_version();
            String formVersionTaskH = CustomerFragment.getHeader().getForm_version();
            if (mode == Global.MODE_NEW_SURVEY) {
                request.setForm_version(formVersionScheme);
            } else if (mode == Global.MODE_SURVEY_TASK) {
                request.setForm_version(formVersionTaskH);
            } else if (mode == Global.MODE_VIEW_SENT_SURVEY) {
                request.setForm_version(formVersionTaskH);
            }
            String json = GsonHelper.toJson(request);
            String url = GlobalData.getSharedGlobalData().getURL_GET_QUESTIONSET();
            try {

                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric =
                        FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);

                Utility.metricStop(networkMetric, serverResult);
            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                errMsg = e.getMessage();
            }
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String result = serverResult.getResult();

                    JsonResponseQuestionSet response = GsonHelper.fromJson(result, JsonResponseQuestionSet.class);
                    if (response.getStatus().getCode() == 0) {

                        FormBean formBean = (FormBean) bundle.getSerializable(Global.BUND_KEY_FORM_BEAN);
                        if (formBean != null) {
                            Scheme lastScheme = formBean;
                            SchemeDataAccess.addOrReplace(activity.get(), lastScheme);
                            scheme = lastScheme;
                        }

                        List<QuestionSet> questionSets = response.getListQuestionSet();
                        List<String> listVersionScheme = TaskHDataAccess.getAllVersionSchemeTaskByUuidScheme(activity.get(), scheme.getUuid_scheme());
                        listVersionScheme.add(scheme.getForm_version());
                        QuestionSetDataAccess.deleteBySchemeVersion(activity.get(), scheme.getUuid_scheme(), listVersionScheme);
                        for (QuestionSet questionSet : questionSets) {
                            questionSet.setUuid_question_set(Tool.getUUID());
                            questionSet.setScheme(scheme);
                        }

                        try { // Delete QuestionSet existing if has been inserted
                            QuestionSetDataAccess.delete(activity.get(), request.getUuid_scheme(), request.getForm_version());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        QuestionSetDataAccess.addOrReplace(activity.get(), scheme.getUuid_scheme(), questionSets);
                        List<QuestionSet> qs;
                        if (mode == 2) {
                            qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersionScheme);
                        } else {
                            qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersionTaskH);
                        }

                        if (qs == null || qs.isEmpty()) {
                            if (mode == 2) {
                                qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersionScheme);
                            } else {
                                qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersionTaskH);
                            }
                        }

                        for (QuestionSet set : qs) {
                            QuestionBean bean = new QuestionBean(set);
                            questions.put(bean.getIdentifier_name().toUpperCase(), bean);

                        }
                    } else {
                        errMsg = result;
                        List<QuestionSet> qs;
                        if (mode == 2) {
                            qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersionScheme);
                        } else {
                            qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), formVersionTaskH);
                        }

                        if (qs != null && !qs.isEmpty()) {
                            for (QuestionSet set : qs) {
                                QuestionBean bean = new QuestionBean(set);
                                questions.put(bean.getIdentifier_name().toUpperCase(), bean);

                            }
                        }
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    getQuestionFromDB();
                }
            }
        } else {
            getQuestionFromDB();
        }
    }

    public void getQuestionFromDB() {
        try {
            List<QuestionSet> qs;
            if (mode == 2) {
                qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), CustomerFragment.getHeader().getScheme().getForm_version());
            } else {
                qs = QuestionSetDataAccess.getAllByFormVersion(activity.get(), CustomerFragment.getHeader().getUuid_scheme(), CustomerFragment.getHeader().getForm_version());
            }

            for (QuestionSet set : qs) {
                QuestionBean bean = new QuestionBean(set);
                questions.put(bean.getIdentifier_name().toUpperCase(), bean);
            }
        } catch (Exception e2) {
            sendAcraReport(e2.getMessage());
            errMsg = e2.getMessage();
        }
    }

    public void setQuestionSet(LinkedHashMap<String, QuestionBean> result) {
        for (Map.Entry<String, QuestionBean> entry : result.entrySet()) {
            QuestionBean bean = entry.getValue();
            String relevantExpression = bean.getRelevant_question();
            if (relevantExpression == null) relevantExpression = "";
            String convertedExpression = relevantExpression;
            setRelevanted(result, convertedExpression);

            String validateExpression = bean.getQuestion_validation();
            if (validateExpression == null) validateExpression = "";
            setRelevanted(result, validateExpression);

            String questionValue = bean.getQuestion_value();
            if (questionValue == null) questionValue = "";
            setRelevanted(result, questionValue);

            String formula = bean.getCalculate();
            setRelevanFromCalculate(result, formula);

            String choiceFilter = bean.getChoice_filter();
            setRelevanFromChoiceFilter(result, choiceFilter, bean);
        }
    }

    private void setRelevanFromChoiceFilter(LinkedHashMap<String, QuestionBean> result, String choiceFilter, QuestionBean mBean) {
        if (choiceFilter != null) {
            String[] tempfilters = Tool.split(choiceFilter, Global.DELIMETER_DATA3);

            for (String newFilter : tempfilters) {
                int idxOfOpenBrace = newFilter.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = newFilter.indexOf('}');
                    String tempIdentifier = newFilter.substring(idxOfOpenBrace + 1, idxOfCloseBrace);
                    if (!tempIdentifier.contains("%")) {
                        int idxOfOpenAbs = tempIdentifier.indexOf("$");
                        if (idxOfOpenAbs == -1) {
                            QuestionBean bean2 = result.get(tempIdentifier.toUpperCase());
                            if (bean2 != null) {
                                bean2.setRelevanted(true);
                                if(Global.AT_LOOKUP_DUKCAPIL.equals(mBean.getAnswer_type()) ||
                                        Global.AT_LOOKUP.equals(mBean.getAnswer_type())  ){
                                    bean2.addToAffectedQuestionBeanOptions(mBean);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setRelevanFromCalculate(LinkedHashMap<String, QuestionBean> result, String formula) {
        try {
            if (formula != null && formula.length() > 0) {
                String resultformula2 = formula.substring(0, formula.indexOf("for"));
                resultformula2 = resultformula2.replace("_var = 0", "");
                resultformula2 = resultformula2.replace("var ", "");
                resultformula2 = resultformula2.replace(" ", "");
                String[] newIdentifier = resultformula2.split(";");
                for (String identifier : newIdentifier) {
                    if (Global.IS_DEV)
                        Log.i(TAG,identifier);
                    QuestionBean bean2 = result.get(identifier.toUpperCase());

                    if (bean2 != null) {
                        bean2.setRelevanted(true);
                    }
                }
            }
        } catch (Exception e) {
            FireCrash.log(e);
        }
    }

    public void setRelevanted(LinkedHashMap<String, QuestionBean> result, String convertedExpression) {
        if (convertedExpression != null && convertedExpression.length() > 0) {
            boolean needReplacing = true;
            while (needReplacing) {
                convertedExpression = replaceModifiers(convertedExpression);
                int idxOfOpenBrace = convertedExpression.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = convertedExpression.indexOf('}');
                    String identifier = convertedExpression.substring(idxOfOpenBrace + 1, idxOfCloseBrace);

                    int idxOfOpenAbs = identifier.indexOf("$");
                    if (idxOfOpenAbs != -1) {
                        convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                    } else {
                        QuestionBean bean2 = result.get(identifier.toUpperCase());
                        if (bean2 != null) {
                            bean2.setRelevanted(true);
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        } else {
                            convertedExpression = convertedExpression.replace("{" + identifier + "}", "\"\"");
                        }
                    }
                } else {
                    needReplacing = false;
                }
            }
        }
    }

    private List<OptionAnswerBean> GetLookupFromDB(QuestionBean bean, List<String> filters) {
        List<OptionAnswerBean> optionAnswers = new ArrayList<>();
        if (!filters.isEmpty()) {
            if (filters.size() == 1) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(activity.get(), bean.getLov_group(), filters.get(0));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 2) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(activity.get(), bean.getLov_group(), filters.get(0), filters.get(1));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 3) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(activity.get(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 4) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(activity.get(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            } else if (filters.size() == 5) {
                List<Lookup> nLookups = LookupDataAccess.getAllByFilter(activity.get(), bean.getLov_group(), filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4));
                optionAnswers = OptionAnswerBean.getOptionList(nLookups);
            }

        } else {
            if (bean.getChoice_filter() != null && bean.getChoice_filter().length() > 0) {
                List<Lookup> lookups = new ArrayList<>();
                optionAnswers = OptionAnswerBean.getOptionList(lookups);
            } else {
                List<Lookup> lookups = LookupDataAccess.getAllByLovGroup(activity.get(), bean.getLov_group());
                if (lookups != null)
                    optionAnswers = OptionAnswerBean.getOptionList(lookups);
            }
        }
        return optionAnswers;
    }

    protected String replaceModifiers(String sourceString) {
        //replace branch modifier
        String branch = GlobalData.getSharedGlobalData().getUser().getBranch_id();
        //replace user modifier
        String user = GlobalData.getSharedGlobalData().getUser().getUuid_user();

        return  sourceString.replace(QuestionBean.PLACEMENT_KEY_BRANCH, branch).replace(QuestionBean.PLACEMENT_KEY_USER, user);
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        //SHOW ERROR
    }

    // Method for check is_corrupt question and answer
    private boolean isCorrupted(List<QuestionBean> listOfQuestions, List<TaskD> listOfAnswer) {
        boolean is_corrupt = false;
        Map<String, TaskD> mapAnswer = new HashMap<>();

        for (TaskD taskD : listOfAnswer) {
            mapAnswer.put(taskD.getQuestion_id(), taskD);
        }

        for (QuestionBean qBean : listOfQuestions) {
            if (qBean.isMandatory() && qBean.isReadOnly() && ((qBean.getQuestion_value() == null ||
                    qBean.getQuestion_value().equalsIgnoreCase("") ||
                    qBean.getQuestion_value().equalsIgnoreCase("null")) && (qBean.getCalculate() == null || qBean.getCalculate().equalsIgnoreCase("null") ||
                    qBean.getCalculate().equalsIgnoreCase("")))) {

                TaskD answer = mapAnswer.get(qBean.getQuestion_id());
                if (answer == null || listOfAnswer.isEmpty()) {
                    is_corrupt = true;
                    break;
                }
            }
        }

        return is_corrupt;
    }

    private List<QuestionBean> retrieveTaskD(SurveyHeaderBean header, List<QuestionBean> listOfQuestions, boolean isCorrupt) {
        if (isCorrupt) retry++;

        JsonRequestTaskD request = new JsonRequestTaskD();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.setuuid_task_h(header.getUuid_task_h());
        String json   = GsonHelper.toJson(request);
        String url    = GlobalData.getSharedGlobalData().getURL_GET_VERIFICATION();

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        if (Tool.isInternetconnected(activity.get())) {
            try {
                activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("Please wait, trying #" + (retry));
                    }
                });

                questions.clear();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);

                if (serverResult != null && serverResult.isOK()) {

                    try {
                        String result = serverResult.getResult();
                        JsonResponseTaskD response = GsonHelper.fromJson(result, JsonResponseTaskD.class);

                        if (response.getStatus().getCode() == 0) {

                            List<TaskD> taskDs = response.getListTask();
                            TaskH taskH = CustomerFragment.getHeader().getTaskH();

                            for (TaskD taskD : taskDs) {
                                taskD.setTaskH(taskH);
                            }

                            if (!taskDs.isEmpty())
                                TaskDDataAccess.addOrReplace(activity.get(), taskDs);

                            List<QuestionBean> questions2 = matchAnswerAndQuestion(listOfQuestions, taskDs);
                            if (isCorrupt) this.isCorrupt = isCorrupted(questions2, taskDs);

                            return questions2;

                        } else {
                            errMsg = result;
                        }

                    } catch (Exception e) {
                        FireCrash.log(e);
                    }

                } else {
                    errMsg = activity.get().getString(R.string.msgNoServerResponeString);
                }

            } catch (Exception e) {
                FireCrash.log(e);
                if (Global.IS_DEV)
                    e.printStackTrace();
                errMsg = e.getMessage();
            }

        } else {
            errMsg = activity.get().getString(R.string.no_data_found_offline);
        }

        return new ArrayList<>();
    }

    private List<QuestionBean> matchAnswerAndQuestion(List<QuestionBean> listOfQuestions, List<TaskD> listOfAnswers) {
        List<QuestionBean> questions2 = QuestionBean.matchAnswerToQuestion(activity.get(), listOfQuestions, listOfAnswers, CustomerFragment.getHeader().getUuid_scheme());
        isCorrupt = isCorrupted(questions2, listOfAnswers);
        return questions2;
    }
}