package com.adins.mss.foundation.scheme.sync;

import android.content.Context;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.http.HttpConnection;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.scheme.DataScheme;
import com.google.gson.JsonParseException;


/**
 * Created by angga.permadi on 5/10/2016.
 */
public class SyncQuestionSet extends DataScheme<Void, Void, SyncQuestionSetResponse> {
    private SyncQuestionSetListener listener;

    public SyncQuestionSet(Context context, SyncQuestionSetRequest entity, SyncQuestionSetListener listener) {
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
    protected SyncQuestionSetResponse onBackgroundResult(HttpConnectionResult serverResult, String uuidScheme) {
        SyncQuestionSetResponse resultBean = new SyncQuestionSetResponse();

        if (errorMessage != null) {
            resultBean.setErrorMessage(errorMessage);
        } else {
            if (serverResult != null) {
                if (serverResult.isOK()) {
                    try {
                        resultBean = GsonHelper.fromJson(serverResult.getResult(), SyncQuestionSetResponse.class);
                    } catch (JsonParseException e) {
                        FireCrash.log(e);

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
    protected void onPostExecute(SyncQuestionSetResponse syncQuestionResponse) {
        super.onPostExecute(syncQuestionResponse);

        if (syncQuestionResponse.getErrorMessage() != null) {
            if (!syncQuestionResponse.getErrorMessage().equals(HttpConnection.ERROR_STATUSCODE_FROM_SERVER)) {
                if (listener != null) {
                    listener.onError(syncQuestionResponse);
                }
            }
            return;
        }

        if (syncQuestionResponse.getStatus() != null) {
            if (syncQuestionResponse.getStatus().getCode() == 0) {
                if (listener != null) {
                    listener.onSuccess(syncQuestionResponse, entity.getUuid_scheme());
                }
            } else {
                syncQuestionResponse.setErrorMessage(syncQuestionResponse.getStatus().getMessage());
                if (listener != null) {
                    listener.onError(syncQuestionResponse);
                }
            }
        } else {
            syncQuestionResponse.setErrorMessage("status is null");
            if (listener != null) {
                listener.onError(syncQuestionResponse);
            }
        }
    }

    @Override
    protected String getUrl() {
        return GlobalData.getSharedGlobalData().getURL_GET_QUESTIONSET();
    }
}
