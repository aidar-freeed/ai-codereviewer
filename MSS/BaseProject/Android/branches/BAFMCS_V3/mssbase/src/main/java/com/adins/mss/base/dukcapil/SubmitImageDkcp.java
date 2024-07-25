package com.adins.mss.base.dukcapil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.DynamicFormActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;

public class SubmitImageDkcp extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private WeakReference<Activity> activity;
    private Context context;
    private String errMessage;
    private QuestionBean questionBean;
    private LinkedHashMap<String, List<QuestionBean>> listOfQuestionBean;

    public SubmitImageDkcp(Activity activity, Context context, LinkedHashMap<String, List<QuestionBean>> listOfQuestionBean,QuestionBean questionBean){
        this.activity = new WeakReference<>(activity);
        this.questionBean = questionBean;
        this.context = context;
        this.listOfQuestionBean = listOfQuestionBean;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity.get(), "", activity.get().getString(R.string.generate_image_data), true);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String imgBase64 = params[0];
        if (Tool.isInternetconnected(activity.get())) {
            JsonRequestImageDkcp jsonRequestImageIdentity = new JsonRequestImageDkcp();
            jsonRequestImageIdentity.setAudit(GlobalData.getSharedGlobalData().getAuditData());
            jsonRequestImageIdentity.setImgIdentity(imgBase64);

            String jsonRequest = GsonHelper.toJson(jsonRequestImageIdentity);

            String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_DKCP();
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
                questionBean.setImgAnswer(null);
                questionBean.setAnswer(null);
                errMessage = activity.get().getString(R.string.msgConnectionFailed);
                return null;
            }
            if (serverResult != null && serverResult.isOK()) {
                String body = serverResult.getResult();
                return body;
            } else {
                questionBean.setImgAnswer(null);
                questionBean.setAnswer(null);
                errMessage = activity.get().getString(R.string.connection_failed);
            }
        } else {
            questionBean.setImgAnswer(null);
            questionBean.setAnswer(null);
            errMessage = activity.get().getString(R.string.no_internet_connection);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(errMessage==null) {
            ResponseImageDkcp responseImageDkcp = new ResponseImageDkcp();

            responseImageDkcp = GsonHelper.fromJson(response, ResponseImageDkcp.class);

            if (responseImageDkcp.getStatus().getCode() == 0 && responseImageDkcp.getDataDkcp()!=null) {
                if(responseImageDkcp.getDataDkcp().getGeneratedStatus().equals("Failed")){
                    Toast.makeText(context,activity.get().getString(R.string.copy_value_dkcp_failed), Toast.LENGTH_SHORT).show();
                }
                questionBean.setResponseImageDkcp(responseImageDkcp);
                TaskH taskH = DynamicFormActivity.getHeader().getTaskH();
                taskH.setCustomer_name(questionBean.getResponseImageDkcp().getDataDkcp().getValueRead("nama"));
                taskH.setCustomer_address(questionBean.getResponseImageDkcp().getDataDkcp().getValueRead("alamat"));
            } else {
                Toast.makeText(context,activity.get().getString(R.string.copy_value_dkcp_failed), Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(context,errMessage, Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

}
