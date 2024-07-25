package com.adins.mss.base.todolist;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.QuestionSet;
import com.adins.mss.dao.Scheme;
import com.adins.mss.dao.TaskD;
import com.adins.mss.dao.TaskH;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.db.dataaccess.QuestionSetDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

public class DoList {
    private static Context context;
    private static List<Scheme> listScheme;
    private static List<QuestionSet> listQuestionSet;
    private TaskH taskH;
    private List<TaskD> listTaskD;
    private User user;

    public DoList(Context context) {
        DoList.context = context;
        user = GlobalData.getSharedGlobalData().getUser();
    }

    /**
     * For the first call, this method will download scheme list and all question set of each scheme from server and store them to local storage.
     * For the next call, this method will return a list from local storage.
     *
     * @return
     */
    public List<Scheme> getListScheme() {
        listScheme = SchemeDataAccess.getAll(context);
        if (listScheme == null)
            listScheme = getListSchemeFromServer();
        for (Scheme scheme : listScheme) {
            getQuestionSetFromServer(scheme);
        }
        return listScheme;
    }

    /**
     * it will return null if not be initialzed first
     *
     * @return
     */
    public TaskH getTaskH() {
        return taskH;
    }

    /**
     * This method will download list scheme from server and store it to local storage
     *
     * @return
     */
    public List<Scheme> getListSchemeFromServer() {
        MssRequestType mrt = new MssRequestType();
        mrt.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        mrt.addImeiAndroidIdToUnstructured();

        // to JSON
        String json = GsonHelper.toJson(mrt);

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult result = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_GET_SCHEME(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            result = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_GET_SCHEME(), json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, result);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        if(result == null){
            return listScheme;
        }

        JsonGetScheme jgs = GsonHelper.fromJson(result.getResult(), JsonGetScheme.class);
        listScheme = jgs.getListScheme();

        for (Scheme scheme : listScheme) {
            if (scheme.getUuid_scheme() == null) {
                listScheme.remove(scheme);
            }
        }

        //bong 19 may 15 - delete scheme dulu baru di add dari server
        if (!listScheme.isEmpty()) {
            SchemeDataAccess.clean(context);
        }

        SchemeDataAccess.addOrReplace(context, listScheme);
        return listScheme;
    }

    /**
     * It will initiate a new object for TaskH data type
     *
     * @param scheme
     * @return
     */
    public TaskH getNewTaskH(Scheme scheme) {
        taskH = null;
        taskH = new TaskH();

        taskH.setUser(user);
        taskH.setScheme(scheme);
        return taskH;
    }

    /**
     * It will return all Task Detail for a taskId
     *
     * @param taskId
     * @param withImage
     * @return
     */
    public List<TaskD> getListTaskD(String taskId, int withImage) {
        listTaskD = null;
        listTaskD = TaskDDataAccess.getAllByTaskId(context, user.getUuid_user(), taskId, withImage);
        return listTaskD;
    }

    // need of getQuestionSet

    /**
     * It will return all question set for a scheme from local
     *
     * @param scheme
     * @return
     */
    public List<QuestionSet> getQuestionSet(Scheme scheme) {
        List<QuestionSet> listQuestionSet = QuestionSetDataAccess.getAll(context, scheme.getUuid_scheme());
        if (listQuestionSet.isEmpty())
            return getQuestionSetFromServer(scheme);
        else return listQuestionSet;
    }

    /**
     * It will return all question set for a scheme from server
     *
     * @param scheme
     * @return
     */
    public List<QuestionSet> getQuestionSetFromServer(Scheme scheme) {
        RequestQuestionSetBean rqsb = new RequestQuestionSetBean();
        rqsb.setFormId(scheme.getForm_id());
        rqsb.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        rqsb.addImeiAndroidIdToUnstructured();
        // to JSON
        String json = GsonHelper.toJson(rqsb);

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult result = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(GlobalData.getSharedGlobalData().getURL_GET_QUESTIONSET(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            result = httpConn.requestToServer(GlobalData.getSharedGlobalData().getURL_GET_QUESTIONSET(), json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, result);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }
        if(result == null){
            return listQuestionSet;
        }

        JsonQuestionSet jqs = GsonHelper.fromJson(result.getResult(), JsonQuestionSet.class);
        listQuestionSet = jqs.getListQuestionSet();
        QuestionSetDataAccess.addOrReplace(context, scheme.getUuid_scheme(), listQuestionSet);
        return listQuestionSet;
    }

    /**
     * This method will redownload scheme list and all question set of each scheme from server, then store them to local storage
     */
    public void doRefresh() {
        listScheme = getListSchemeFromServer();
        for (Scheme scheme : listScheme) {
            getQuestionSetFromServer(scheme);
        }
    }

    /**
     * Gets List Scheme for LeadIn
     *
     * @return Scheme List
     * @author gigin.ginanjar
     */
    public List<Scheme> getOrderListScheme() {
        return SchemeDataAccess.getAllOrderScheme(context);
    }

    /**
     * Gets List Scheme for Mobile Survey
     *
     * @return Scheme List
     * @author gigin.ginanjar
     */
    public List<Scheme> getSurveyListScheme() {
        return SchemeDataAccess.getAllSurveyScheme(context);
    }

    /**
     * Gets List Scheme for Mobile Survey
     *
     * @return Scheme List
     * @author gigin.ginanjar
     */
    public List<Scheme> getCollListScheme() {
        return SchemeDataAccess.getAllCollectionScheme(context);
    }

    public List<Scheme> getMarketingListScheme() {
        return SchemeDataAccess.getAllMarketingScheme(context);
    }
}
