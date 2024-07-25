package com.adins.mss.foundation.scheme;

import android.content.Context;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.scheme.sync.SyncQuestionSetRequest;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public abstract class DataScheme<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected String errorMessage;
    protected WeakReference<Context> context;
    protected SyncQuestionSetRequest entity;

    public DataScheme(Context context, SyncQuestionSetRequest entity) {
        this.context = new WeakReference<Context>(context);
        this.entity = entity;
    }

    @SafeVarargs
    @Override
    protected final Result doInBackground(Params... params) {
        entity.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context.get(),
                GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
        HttpConnectionResult serverResult = null;
        String json = GsonHelper.toJson(entity);

        //Firebase Performance Trace HTTP Request
        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(getUrl(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        if (Tool.isInternetconnected(context.get())) {
            try {
                serverResult = httpConn.requestToServer(getUrl(), json, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = context.get().getString(R.string.failed_send_data);
                return onBackgroundResult(null, null);
            }
        } else {
            errorMessage = context.get().getString(R.string.no_internet_connection);
        }

        return onBackgroundResult(serverResult, entity.getUuid_scheme());
    }

    protected void onDestroy() {

    }

    protected abstract Result onBackgroundResult(HttpConnectionResult serverResult, String uuidScheme);

    protected abstract String getUrl();
}

