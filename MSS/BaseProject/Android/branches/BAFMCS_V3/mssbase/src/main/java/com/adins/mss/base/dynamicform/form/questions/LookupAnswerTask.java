package com.adins.mss.base.dynamicform.form.questions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.Constant;
import com.adins.mss.base.dynamicform.form.FragmentQuestion;
import com.adins.mss.base.dynamicform.form.models.CriteriaParameter;
import com.adins.mss.base.dynamicform.form.models.LookupCriteriaBean;
import com.adins.mss.base.dynamicform.form.models.LookupOnlineRequest;
import com.adins.mss.base.dynamicform.form.models.ReviewResponse;
import com.adins.mss.base.dynamicform.form.questions.viewholder.LookupCriteriaOnlineActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.KeyValue;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.adins.mss.base.dynamicform.form.questions.viewholder.LookupCriteriaOnlineActivity.KEY_WITH_FILTER;

/**
 * Created by gigin.ginanjar on 07/10/2016.
 */

public class LookupAnswerTask extends AsyncTask<String, Void, String> {
    private WeakReference<Activity> activity;
    private ProgressDialog progressDialog;
    private String errMessage;
    private QuestionBean bean;
    private CriteriaParameter criteriaParameter;
    private int mode;

    public LookupAnswerTask(Activity activity, QuestionBean bean, CriteriaParameter criteriaParameter, int mode) {
        this.activity = new WeakReference<>(activity);
        this.bean = bean;
        this.criteriaParameter = criteriaParameter;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity.get(), "", activity.get().getString(R.string.please_wait), true);
    }

    @Override
    protected String doInBackground(String... params) {
        if (Tool.isInternetconnected(activity.get())) {
            String jsonRequest = getJsonRequest();
            String url = GlobalData.getSharedGlobalData().getURL_LOOKUP_ANSWER();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
            HttpConnectionResult serverResult = null;

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, jsonRequest);

            try {
                serverResult = httpConn.requestToServer(url, jsonRequest, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);
            } catch (Exception e) {
                FireCrash.log(e);
                errMessage = activity.get().getString(R.string.msgConnectionFailed);
                return null;
            }
            if (serverResult != null && serverResult.isOK()) {
                String body = serverResult.getResult();
                return body;
            } else {
                errMessage = activity.get().getString(R.string.connection_failed);
            }
        } else {
            errMessage = activity.get().getString(R.string.no_internet_connection);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(!GlobalData.isRequireRelogin()) {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = null;
            }
            if (errMessage != null && !errMessage.isEmpty()) {
                Toast.makeText(activity.get(), errMessage, Toast.LENGTH_SHORT).show();
            } else if (result != null) {
                processingResponseServer(result);

            }
        } else{
            Message message = new Message();
            Bundle bundle = new Bundle();

            bundle.putInt(Global.BUND_KEY_MODE_SURVEY, mode);
            bundle.putInt(FragmentQuestion.BUND_KEY_ACTION, FragmentQuestion.SAVE_QUESTION);
            message.setData(bundle);
            FragmentQuestion.questionHandler.sendMessage(message);
        }
    }

    private void processingResponseServer(String result) {
        processLuOnline(result);
    }

    private void processLuOnline(String result) {
        ReviewResponse response = GsonHelper.fromJson(result, ReviewResponse.class);
        if (response.getStatus().getCode() == 0) {
            List<KeyValue> valueList = response.getListField();
            if (valueList != null) {
                List<LookupCriteriaBean> beanList = new ArrayList<>();
                if (!valueList.isEmpty()) {
                    for (KeyValue value : valueList) {
                        LookupCriteriaBean bean = new LookupCriteriaBean();
                        bean.setCode(value.getKey());
                        bean.setValue(value.getValue());
                        beanList.add(bean);
                    }
                    bean.setLookupCriteriaList(beanList);
                    LookupCriteriaOnlineActivity.setBeanList(beanList);
                    Intent intent = new Intent(activity.get(), LookupCriteriaOnlineActivity.class);
                    if (criteriaParameter != null)
                        intent.putExtra(KEY_WITH_FILTER, true);
                    activity.get().startActivityForResult(intent, Global.REQUEST_LOOKUP_ANSWER);
                } else {
                    errMessage = activity.get().getString(R.string.data_not_found);
                    Toast.makeText(activity.get(), errMessage, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            errMessage = response.getStatus().getMessage();
            Toast.makeText(activity.get(), errMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public String getJsonRequest() {
        return getJsonRequestLuOnline();
    }

    public String getJsonRequestLuOnline() {
        LookupOnlineRequest request = new LookupOnlineRequest();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.setIdentifier(bean.getIdentifier_name());
        if (bean.getChoice_filter() != null) {
            List<String> tempfilters = getFilterAnswer(bean);
            StringBuilder stringBuilder = new StringBuilder();
            for (String filter : tempfilters) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append(Global.DELIMETER_DATA_LOOKUP);
                stringBuilder.append(filter);
            }
            request.setChoiceFilter(stringBuilder.toString());
        }
        request.setUserFilter(criteriaParameter.getParameters().get(0).getAnswer());
        request.setLovGroup(bean.getLov_group());
        request.setIdentifier(bean.getIdentifier_name());
        String jsonRequest = GsonHelper.toJson(request);
        return jsonRequest;
    }

    public List<String> getFilterAnswer(QuestionBean bean) {
        List<String> filters = new ArrayList<>();
        if (bean.getChoice_filter() != null) {
            String[] tempfilters = Tool.split(bean.getChoice_filter(), Global.DELIMETER_DATA3);

            for (String newFilter : tempfilters) {
                int idxOfOpenBrace = newFilter.indexOf('{');
                if (idxOfOpenBrace != -1) {
                    int idxOfCloseBrace = newFilter.indexOf('}');
                    String tempIdentifier = newFilter.substring(idxOfOpenBrace + 1, idxOfCloseBrace).toUpperCase();
                    if (tempIdentifier.contains("%")) {
                        filters.add(tempIdentifier);
                    } else {
                        int idxOfOpenAbs = tempIdentifier.indexOf("$");
                        if (idxOfOpenAbs != -1) {
                            String finalIdentifier = tempIdentifier.substring(idxOfOpenAbs + 1);
                            if (finalIdentifier.equals(Global.IDF_LOGIN_ID)) {
                                String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();
                                int idxOfOpenAt = loginId.indexOf('@');
                                if (idxOfOpenAt != -1) {
                                    loginId = loginId.substring(0, idxOfOpenAt);
                                }
                                filters.add(loginId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_ID)) {
                                String branchId = GlobalData.getSharedGlobalData().getUser().getBranch_id();
                                filters.add(branchId);
                            } else if (finalIdentifier.equals(Global.IDF_BRANCH_NAME)) {
                                String branchName = GlobalData.getSharedGlobalData().getUser().getBranch_name();
                                filters.add(branchName);
                            } else if (finalIdentifier.equals(Global.IDF_UUID_USER)) {
                                String uuidUser = GlobalData.getSharedGlobalData().getUser().getUuid_user();
                                filters.add(uuidUser);
                            } else if (finalIdentifier.equals(Global.IDF_JOB)) {
                                String job = GlobalData.getSharedGlobalData().getUser().getFlag_job();
                                filters.add(job);
                            } else if (finalIdentifier.equals(Global.IDF_DEALER_NAME)) {
                                String dealerName = GlobalData.getSharedGlobalData().getUser().getDealer_name();
                                filters.add(dealerName);
                            }
                        } else {
                            QuestionBean bean2 = Constant.getListOfQuestion().get(tempIdentifier);
                            if (bean2 != null) {
                                String answer = QuestionBean.getAnswer(bean2);
                                filters.add(answer);
                                bean2.setRelevanted(true);
                            }
                        }
                    }
                }
            }
        }
        return filters;
    }
}
