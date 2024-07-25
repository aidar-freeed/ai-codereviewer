package com.adins.mss.foundation.broadcast;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.List;

public class SubmitSubscribedTopicTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private String uuidUser;
    private List<String> listTopic;
    private String errMsg;

    public SubmitSubscribedTopicTask(Context context, String uuidUser, List<String> listTopic) {
        this.context = context;
        this.uuidUser = uuidUser;
        this.listTopic = listTopic;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        SubmitTopicRequest request = new SubmitTopicRequest();
        request.setUuidUser(uuidUser);
        request.setSubscribedTopic(listTopic);

        String json = GsonHelper.toJson(request);
        String url = "";
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            if(!Tool.isInternetconnected(context)){
                errMsg = context.getString(R.string.no_internet_connection);
                return false;
            }

            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);

            if (serverResult == null) {
                return false;
            }

            if(!serverResult.isOK()){
                errMsg = serverResult.getResult();
                return false;
            }

            MssResponseType responseType = GsonHelper.fromJson(serverResult.getResult(), MssResponseType.class);
            if(responseType.getStatus().getCode() != 0){
                errMsg = responseType.getStatus().getMessage();
                return false;
            }

            return true;
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            errMsg = e.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
    }
}
