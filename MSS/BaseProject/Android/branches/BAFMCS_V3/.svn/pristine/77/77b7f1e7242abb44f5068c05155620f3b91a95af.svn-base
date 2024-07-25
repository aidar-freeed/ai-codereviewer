package com.adins.mss.base.tasklog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;

import java.lang.ref.WeakReference;
import java.util.List;

public class TaskLogListTask extends AsyncTask<Void, Void, List<TaskH>> implements IShowError {
    public Fragment LogResultFragment;
    private ProgressDialog progressDialog;
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String messageWait;
    private String messageEmpty;
    private String errMessage = null;
    private int contentFrame;
    private ErrorMessageHandler errorMessageHandler;

    public TaskLogListTask(Activity activity, String messageWait, String messageEmpty, int contentFrame, Fragment LogResultFragment) {
        this.context = new WeakReference<Context>(activity);
        this.activity = new WeakReference<>(activity);
        this.messageWait = messageWait;
        this.messageEmpty = messageEmpty;
        this.contentFrame = contentFrame;
        this.LogResultFragment = LogResultFragment;
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context.get(), "", messageWait, true);
    }

    @Override
    protected List<TaskH> doInBackground(Void... params) {
        List<TaskH> result = null;

        try {
            if (context != null) {
                TaskLogImpl log = new TaskLogImpl(context.get());
                result = log.getListTaskLog();
            } else {
                TaskLogImpl log = new TaskLogImpl(activity.get());
                result = log.getListTaskLog();
            }
        } catch (Exception ex) {
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
    protected void onPostExecute(List<TaskH> result) {
        if (progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }

        if (errMessage != null) {
            errorMessageHandler.processError(context.get().getString(R.string.error_capital)
                    ,errMessage,ErrorMessageHandler.DIALOG_TYPE);
        } else if (result == null || result.isEmpty()) {
            if (GlobalData.getSharedGlobalData().getApplication().equals(Global.APPLICATION_COLLECTION)) {
                gotoLog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageHandler.processError(context.get().getString(R.string.info_capital)
                                ,messageEmpty,ErrorMessageHandler.DIALOG_TYPE);
                    }
                }, 1000);
            } else {
                errorMessageHandler.processError(context.get().getString(R.string.info_capital)
                        ,messageEmpty,ErrorMessageHandler.DIALOG_TYPE);
            }
        } else {
            if (result == null || result.isEmpty())
                Global.setListOfSentTask(result);
            gotoLog();

        }
    }

    public void gotoLog() {
        //2017/08/07: Nendi
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
        transaction.replace(contentFrame, LogResultFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context.get());
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .show();
        }
    }
}
