package com.adins.mss.base.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.models.ChangePasswordRequestModel;
import com.adins.mss.base.models.ChangePasswordResponseModel;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.net.HttpClient;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

/**
 * Created by adityapurwa on 30/03/15.
 */
public class ChangePasswordApi {
    private final Context context;
    private final Activity activity;
    private final HttpClient http;
    private ChangePasswordApiCallback callback;

    public ChangePasswordApi(Activity context) {
        this.activity = context;
        this.context = context;
        this.http = new HttpClient(context);
    }

    public ChangePasswordApiCallback getCallback() {
        return callback;
    }

    public void setCallback(ChangePasswordApiCallback callback) {
        this.callback = callback;
    }

    public void execute(final ChangePasswordRequestModel request) {

        new AsyncTask<Void, Void, ChangePasswordResponseModel>() {
            private ProgressDialog progressDialog;
            private String errMessage;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(context, "", context.getString(R.string.contact_server), true);
            }

            @Override
            protected ChangePasswordResponseModel doInBackground(Void... params) {
                try {
                    if (Tool.isInternetconnected(context)) {
                        String json = GsonHelper.toJson(request);
                        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
                        HttpConnectionResult serverResult = null;
                        String url = GlobalData.getSharedGlobalData().getURL_CHANGEPASSWORD();

                        //Firebase Performance Trace HTTP Request
                        HttpMetric networkMetric =
                                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                        Utility.metricStart(networkMetric, json);

                        try {
                            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                            Utility.metricStop(networkMetric, serverResult);
                        } catch (Exception e) {
                            errMessage = e.getMessage();
                            FireCrash.log(e);
                        }
                        String body = null;
                        if (serverResult != null && serverResult.isOK()) {
                            body = serverResult.getResult();
                            ChangePasswordResponseModel result = null;
                            try {
                                result = GsonHelper.fromJson(body, ChangePasswordResponseModel.class);
                                return result;
                            } catch (Exception e) {
                                FireCrash.log(e);
                                errMessage = e.getMessage();
                                return null;
                            }
                        } else {
                            if(serverResult != null && serverResult.getResult() != null)
                                errMessage = serverResult.getResult();
                            return null;
                        }
                    } else {
                        errMessage = context.getString(R.string.no_internet_connection);
                        return null;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                    if (Global.IS_DEV)
                        e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ChangePasswordResponseModel result) {
                super.onPostExecute(result);
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if (result == null) {
                    if (errMessage != null && getCallback() != null){
                        getCallback().onFailed(errMessage);
                    }else{
                        getCallback().onFailed(context.getString(R.string.error_unknown));
                    }
                    return;
                }
                if (result.getStatus().getCode() == 0) {
                    if (getCallback() != null) getCallback().onSuccess();
                } else {
                    if (getCallback() != null) {
                        if (result.getStatus().getMessage() != null)
                            getCallback().onFailed(result.getStatus().getMessage());
                        else
                            getCallback().onFailed(String.valueOf(result.getStatus().getCode()));
                    }
                }
            }
        }.execute();

    }

}
