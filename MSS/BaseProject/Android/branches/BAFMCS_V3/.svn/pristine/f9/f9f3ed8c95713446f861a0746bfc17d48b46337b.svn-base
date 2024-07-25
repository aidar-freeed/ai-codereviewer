package com.adins.mss.coll.services;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.db.dataaccess.UserDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;
import com.services.models.JsonResponseTokenId;
import com.services.models.JsonSendTokenId;

public class RefreshToken extends FirebaseInstanceIdService {
    Context context;

    public RefreshToken() {
    }

    public RefreshToken(Context context) {
        this.context = context;
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Logger.d("Firebase", " masuk onToken ");
        if (null != token) {
            Global.Token = token;
            registerToken(token);
        }
    }

    private void registerToken(final String token) {
        if (null != token && !"".equalsIgnoreCase(token)) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String result = null;
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                    User user = GlobalData.getSharedGlobalData().getUser();
                    if (null != user && null == user.getToken_id_fcm()) {
                        if (Tool.isInternetconnected(context)) {
                            JsonSendTokenId task = new JsonSendTokenId();
                            task.setAudit(GlobalData.getSharedGlobalData().getAuditData());
                            task.addImeiAndroidIdToUnstructured();
                            task.setToken_id(token);
                            task.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                            String json = GsonHelper.toJson(task);
                            String url = GlobalData.getSharedGlobalData().getURL_UPDATE_FCM();
                            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                            HttpCryptedConnection httpConn = new HttpCryptedConnection(getBaseContext(), encrypt, decrypt);
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


                            if (null != serverResult && serverResult.isOK()){
                                result = serverResult.getResult();
                            }
                        }
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(final String message) {
                    if (null != message) {
                        JsonResponseTokenId responseTokenId = GsonHelper.fromJson(message, JsonResponseTokenId.class);
                        if (responseTokenId.getStatus().getCode() == 0){
                            if (!"".equalsIgnoreCase(responseTokenId.getToken_id())) {
                                User user = GlobalData.getSharedGlobalData().getUser();
                                user.setToken_id_fcm(token);
                                GlobalData.getSharedGlobalData().setUser(user);
                                UserDataAccess.update(context, user);
                            }
                        }
                    }
                }
            }.execute();
        }
    }

}
