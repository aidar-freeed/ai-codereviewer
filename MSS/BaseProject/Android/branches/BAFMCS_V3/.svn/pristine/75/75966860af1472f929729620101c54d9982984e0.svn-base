package com.adins.mss.foundation.print.rv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.util.EventBusHelper;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.logger.Logger;
import com.google.gson.JsonParseException;

/**
 * Created by angga.permadi on 4/20/2016.
 */
public class RVNumberSender extends DataTask<Void, Void, RVNumberResponse> {
    private ProgressDialog progressDialog;
    private int reqCode;
    private boolean isShowLoading = false;
    private InputRVNumberActivity.OnSendRVListener listener;

    public RVNumberSender(Activity context, RVNumberRequest entity, int reqCode) {
        this(context, entity, reqCode, null);
    }

    public RVNumberSender(Activity context, RVNumberRequest entity, int reqCode, InputRVNumberActivity.OnSendRVListener listener) {
        super(context, entity);
        this.reqCode = reqCode;
        this.isShowLoading = true;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isShowLoading)
            progressDialog = ProgressDialog.show(context.get(), "", context.get().getString(R.string.please_wait), true);
    }

    @Override
    protected RVNumberResponse onBackgroundResult(HttpConnectionResult serverResult) {
        RVNumberResponse resultBean = new RVNumberResponse();

        if (errorMessage != null) {
            resultBean.setErrorMessage(errorMessage);
        } else {
            if (serverResult != null) {
                Logger.d(this, serverResult.getResult());
                if (serverResult.isOK()) {
                    try {
                        resultBean = GsonHelper.fromJson(serverResult.getResult(), RVNumberResponse.class);
                    } catch (JsonParseException e) {
                        resultBean.setErrorMessage(context.get().getString(R.string.input_rv_number_failed));
                        resultBean.setErrorCode(ErrorCodes.ERROR_PARSING_JSON);
                    }
                } else {
                    resultBean.setErrorMessage(serverResult.getResult());
                }
            }
        }

        resultBean.setReqCode(reqCode);
        return resultBean;
    }

    @Override
    protected void onPostExecute(RVNumberResponse rvNumberResponse) {
        super.onPostExecute(rvNumberResponse);

        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }

        if (listener == null)
            return;

        listener.onSendReceiptVoucher(rvNumberResponse);
    }

    @Override
    protected String getUrl() {
        return GlobalData.getSharedGlobalData().getURL_RV_NUMBER();
    }
}
