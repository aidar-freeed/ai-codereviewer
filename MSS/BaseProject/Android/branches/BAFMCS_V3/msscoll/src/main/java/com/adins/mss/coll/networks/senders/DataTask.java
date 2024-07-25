package com.adins.mss.coll.networks.senders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;

/**
 * Created by angga.permadi on 3/3/2016.
 */
public abstract class DataTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    protected String errorMessage;
    private MssRequestType entity;
    private WeakReference<Context> context;

    public DataTask(Context context, MssRequestType entity)
    {
        this.context = new WeakReference<Context>(context);
        this.entity = entity;
    }

    @SafeVarargs
    @Override
    protected final Result doInBackground(Params... params)
    {
        entity.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        HttpCryptedConnection httpConn = new HttpCryptedConnection(context.get(),
                GlobalData.getSharedGlobalData().isEncrypt(), GlobalData.getSharedGlobalData().isDecrypt());
        HttpConnectionResult serverResult;

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(getUrl(), FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric,GsonHelper.toJson(entity));

        try {
            serverResult = httpConn.requestToServer(getUrl(), GsonHelper.toJson(entity), Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            errorMessage = e.getMessage();
            return null;
        }

        return onBackgroundResult(serverResult);
    }

    protected void onDestroy()
    {

    }

    protected void log(String message)
    {
        Logger.d(getClass().getSimpleName(), message);
    }

    protected abstract Result onBackgroundResult(HttpConnectionResult serverResult);

    protected abstract String getUrl();
}

