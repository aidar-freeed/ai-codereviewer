package com.adins.mss.base.todo.form;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.PrintItem;
import com.adins.mss.dao.Scheme;
import com.adins.mss.foundation.db.dataaccess.PrintItemDataAccess;
import com.adins.mss.foundation.db.dataaccess.SchemeDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;
import java.util.List;

public class GetSchemeTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<FragmentActivity> activity;
    FragmentManager fragmentManager;
    DialogFragment fragment;
    boolean isNewTask = false;
    private ProgressDialog progressDialog;
    private String errMsg = null;
    private ErrorMessageHandler errorMessageHandler;
    NiftyDialogBuilder dialogBuilder;

    public GetSchemeTask(final FragmentActivity activity, DialogFragment newTaskActivity, boolean isNewTask) {
        this.activity = new WeakReference<>(activity);
        fragmentManager = activity.getSupportFragmentManager();
        fragment = newTaskActivity;
        this.isNewTask = isNewTask;
        errorMessageHandler = new ErrorMessageHandler(new IShowError() {
            @Override
            public void showError(String errorSubject, String errorMsg, int notifType) {
                if(notifType == ErrorMessageHandler.DIALOG_TYPE){
                    dialogBuilder = NiftyDialogBuilder.getInstance(activity);
                    dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .show();
                }
            }
        });
    }

    @Override
    protected void onPreExecute() {
        if (isNewTask) {
            this.progressDialog = ProgressDialog.show(activity.get(), "", activity.get().getString(R.string.progressWait), true);
        }
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        JsonRequestScheme requestScheme = new JsonRequestScheme();
        requestScheme.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        requestScheme.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
        requestScheme.setTask(Global.TASK_GETLIST);

        String json = GsonHelper.toJson(requestScheme);
        String url = GlobalData.getSharedGlobalData().getURL_GET_SCHEME();
        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            errMsg = e.getMessage();
        }
        if (null != serverResult && serverResult.isOK()) {
            try {
                String result = serverResult.getResult();
                JsonResponseScheme responseScheme = GsonHelper.fromJson(result, JsonResponseScheme.class);
                List<Scheme> schemes = responseScheme.getListScheme();
                List<PrintItem> printItems = responseScheme.getListPrintItems();

                //bong 19 may 15 - delete scheme dulu baru di add dari server
                if (!schemes.isEmpty()) {
                    SchemeDataAccess.clean(activity.get());
                }

                for (Scheme scheme : schemes) {
                    Scheme scheme2 = null;
                    try {
                        scheme2 = SchemeDataAccess.getOneByLastUpdate(activity.get(), scheme.getUuid_scheme(), scheme.getScheme_last_update());
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }

                    if (scheme2 == null) {
                        if (scheme.getUuid_scheme() != null) {
                            SchemeDataAccess.addOrReplace(activity.get(), scheme);
                        }
                    } else {
                        if (scheme.getScheme_last_update().after(scheme2.getScheme_last_update()) && scheme.getUuid_scheme() != null) {
                            SchemeDataAccess.addOrReplace(activity.get(), scheme);
                        }
                    }
                }

                for (PrintItem printItem : printItems) {
                    Scheme scheme = SchemeDataAccess.getOne(activity.get(), printItem.getUuid_scheme());
                    printItem.setScheme(scheme);
                    PrintItemDataAccess.addOrReplace(activity.get(), printItem);
                }
            } catch (Exception e) {
                FireCrash.log(e);
                errMsg = e.getMessage();
            }
        } else {
            try {
                errMsg = serverResult.getResult();
            } catch (NullPointerException e) {
                Log.w("NULL_POINTER_EXCEPTION","Server result is empty on : " + getClass().getSimpleName());
                errMsg = activity.get().getString(R.string.something_went_wrong);
            }
        }
        return serverResult.isOK();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (isNewTask) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FireCrash.log(e);
                }
            }
            if (result) {
                if (errMsg != null) {
                    String application = GlobalData.getSharedGlobalData().getAuditData().getApplication();
                    if (application.equalsIgnoreCase(Global.APPLICATION_SURVEY)) {
                        if (SchemeDataAccess.getAllSurveyScheme(activity.get()) != null ||
                                !SchemeDataAccess.getAllSurveyScheme(activity.get()).isEmpty()) {
                            if(!GlobalData.isRequireRelogin())
                                fragment.show(fragmentManager, "New Task");
                        } else {
                            errorMessageHandler.processError(activity.get().getString(R.string.error_capital)
                                    , errMsg
                                    , ErrorMessageHandler.DIALOG_TYPE);
                        }
                    } else if (application.equalsIgnoreCase(Global.APPLICATION_ORDER)) {
                        if (SchemeDataAccess.getAllOrderScheme(activity.get()) != null ||
                                !SchemeDataAccess.getAllOrderScheme(activity.get()).isEmpty()) {
                            if(!GlobalData.isRequireRelogin()){
                                FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate);
                                transaction.replace(R.id.content_frame, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        } else {
                            errorMessageHandler.processError(activity.get().getString(R.string.error_capital)
                                    , errMsg
                                    , ErrorMessageHandler.DIALOG_TYPE);
                        }
                    }
                } else {
                    if(!GlobalData.isRequireRelogin())
                         fragment.show(fragmentManager, "New Task");
                }
            } else {
                Toast.makeText(activity.get(), errMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
