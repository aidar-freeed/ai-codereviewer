package com.adins.mss.foundation.print;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.http.MssResponseType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public class SubmitPrintSender extends AsyncTask<Void, Void, MssResponseType> {
    private static final String TAG = SubmitPrintSender.class.getSimpleName();

    protected String errorMessage;
    private MssRequestType entity;
    private Context context;

    public SubmitPrintSender(Context context, MssRequestType entity) {
        this.context = context;
        this.entity = entity;
    }

    @Override
    protected MssResponseType doInBackground(Void... params) {
        entity.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context,
                GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
        HttpConnectionResult serverResult = null;
        String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_PRINT_COUNT();

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, GsonHelper.toJson(entity));

        try {
            serverResult = httpConn.requestToServer(url, GsonHelper.toJson(entity), Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        MssResponseType resultBean = null;

        if (serverResult != null) {
            if (serverResult.isOK()) {
                try {
                    resultBean = GsonHelper.fromJson(serverResult.getResult(), MssResponseType.class);
                } catch (Exception e) {
                    FireCrash.log(e);
                    errorMessage = serverResult.getResult();
                }
            } else {
                errorMessage = serverResult.getResult();
            }
        }

        return resultBean;
    }

    @Override
    protected void onPostExecute(MssResponseType mssResponseType) {
        super.onPostExecute(mssResponseType);

        if (mssResponseType != null) {
            if (mssResponseType.getStatus().getCode() == 0) {
                Logger.d(TAG, "success");
            } else {

            }
        }

        if (errorMessage != null) {
            if (context != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

