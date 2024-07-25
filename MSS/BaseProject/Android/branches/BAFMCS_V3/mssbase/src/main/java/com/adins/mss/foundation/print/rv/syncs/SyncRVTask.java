package com.adins.mss.foundation.print.rv.syncs;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.http.HttpConnection;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.print.rv.DataTask;
import com.google.gson.JsonParseException;

/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncRVTask extends DataTask<Void, Void, SyncRVResponse> {
    private SyncRvListener listener;

    public SyncRVTask(Context context, MssRequestType entity, SyncRvListener listener) {
        super(context, entity);
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (listener != null) {
            listener.onProgress();
        }
    }

    @Override
    protected SyncRVResponse onBackgroundResult(HttpConnectionResult serverResult) {
        SyncRVResponse resultBean = new SyncRVResponse();

        if (errorMessage != null) {
            resultBean.setErrorMessage(errorMessage);
        } else {
            if (serverResult != null) {
                if (serverResult.isOK()) {
                    try {
                        resultBean = GsonHelper.fromJson(serverResult.getResult(), SyncRVResponse.class);
                    } catch (JsonParseException e) {
                        resultBean.setErrorMessage(context.get().getString(R.string.msgErrorParsingJson));
                    }
                } else {
                    resultBean.setErrorMessage(serverResult.getResult());
                }
            }
        }

        return resultBean;
    }

    @Override
    protected void onPostExecute(SyncRVResponse syncRVResponse) {
        super.onPostExecute(syncRVResponse);

        if (syncRVResponse.getErrorMessage() != null) {
            if (!syncRVResponse.getErrorMessage().equals(HttpConnection.ERROR_STATUSCODE_FROM_SERVER)) {
                if (listener != null) {
                    listener.onError(syncRVResponse);
                }
            }
            return;
        }

        if (syncRVResponse.getStatus() != null) {
            if (syncRVResponse.getStatus().getCode() == 0) {
                if (listener != null) {
                    listener.onSuccess(syncRVResponse);
                }
            } else {
                syncRVResponse.setErrorMessage(syncRVResponse.getStatus().getMessage());
                if (listener != null) {
                    listener.onError(syncRVResponse);
                }
            }
        } else {
            syncRVResponse.setErrorMessage("status is null");
            if (listener != null) {
                listener.onError(syncRVResponse);
            }
        }
    }

    @Override
    protected String getUrl() {
        return GlobalData.getSharedGlobalData().getURL_SYNC_RV_NUMBERS();
    }
}
