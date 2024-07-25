package com.adins.mss.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintResult;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.PrintResultDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;

import java.util.List;

/**
 * Created by winy.firdasari on 30/01/2015.
 */

public class PrintTask extends AsyncTask<String, Void, List<PrintResult>> {
    private ProgressDialog progressDialog;
    private String errMessage = null;
    private Activity activity;
    private String messageWait;
    private String messageEmpty;
    private String taskId;

    //inijiniunnij dasfasfs

    public PrintTask(Activity activity) {
        this.activity = activity;
        this.messageWait = activity.getString(R.string.progressWait);
        this.messageEmpty = activity.getString(R.string.msgNoPrintItem);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(this.activity, "", this.messageWait, true);
    }


    @Override
    protected List<PrintResult> doInBackground(String... params) {
        List<PrintResult> result = null;

        try {
            String taskId = params[0];
            this.taskId = taskId;

            //bong 7 may 15 - asumsi data print result di lokal sudah siap untuk di-print
            TaskH taskH = TaskHDataAccess.getOneTaskHeader(activity, taskId);
            result = PrintResultDataAccess.getAll(activity, taskH.getUuid_task_h());
            //  FormManager formManager = new FormManager();
            //  result = formManager.getReadyPrintItem(activity, taskId);
        } catch (Exception ex) {
            if (Global.IS_DEV)
                ex.printStackTrace();
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
            errMessage = ex.getMessage();
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<PrintResult> result) {
        if (progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

        if (errMessage != null) {
            DialogManager.showAlert(activity, DialogManager.TYPE_ERROR, errMessage, activity.getString(R.string.test_label));
        } else if (result == null || result.size() == 0) {
            DialogManager.showAlert(activity, DialogManager.TYPE_INFO, messageEmpty, activity.getString(R.string.test_label));
        } else {
            PrintActivity.setListOfPrint(result);
            Intent i = new Intent(this.activity, PrintActivity.class);
            this.activity.startActivity(i);
        }
    }

}
