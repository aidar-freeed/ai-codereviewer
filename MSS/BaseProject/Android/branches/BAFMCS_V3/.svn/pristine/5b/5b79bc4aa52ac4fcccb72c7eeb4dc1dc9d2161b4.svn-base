package com.adins.mss.base.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.adins.mss.base.crashlytics.FireCrash;

/**
 * Modification of GenericAsyncTask which will display progress dialog when executing background process
 *
 * @author glen.iglesias
 */
public class LoadingTask extends GenericAsyncTask {
    private ProgressDialog progressDialog;
    private Context context;
    private String loadingMessage = "Loading";

    public LoadingTask(Context context, GenericTaskInterface delegate, String loadingMessage) {
        super(delegate);
        this.context = context;
        if (loadingMessage != null) {
            this.loadingMessage = loadingMessage;
        }
    }

    public LoadingTask(Context context, String loadingMessage) {
        super(null);
        this.context = context;
        if (loadingMessage != null) {
            this.loadingMessage = loadingMessage;
        }
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context,
                "", loadingMessage, true);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

        super.onPostExecute(result);
    }
}
