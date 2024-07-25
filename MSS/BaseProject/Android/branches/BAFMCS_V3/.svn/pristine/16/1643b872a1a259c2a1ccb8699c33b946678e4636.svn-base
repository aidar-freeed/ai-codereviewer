package com.adins.mss.coll.interfaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.CommonImpl;
import com.adins.mss.base.commons.TaskListener;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.coll.R;
import com.adins.mss.coll.api.InstallmentScheduleApi;
import com.adins.mss.coll.interfaces.callback.SaveDataInstallmentCallback;
import com.adins.mss.coll.interfaces.callback.TaskReportCallback;
import com.adins.mss.coll.models.InstallmentScheduleResponse;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.InstallmentSchedule;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.InstallmentScheduleDataAccess;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Formatter;
import com.adins.mss.foundation.formatter.Tool;

import java.io.IOException;
import java.util.List;

/**
 * Created by kusnendi.muhamad on 31/07/2017.
 */

public class TasksImpl extends CommonImpl implements TasksInterface {
    private Activity activity;
    private Context context;

    public TasksImpl(Activity activity) {
        this.activity= activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    public void saveDataInstallmentSchedule(Context context, InstallmentScheduleResponse installmentSchedResp, String taskId, SaveDataInstallmentCallback callback) {
        if(installmentSchedResp!=null && installmentSchedResp.getStatus().getCode()==0){
            List<InstallmentSchedule> installmentScheduleList = installmentSchedResp.getInstallmentScheduleList();
            if(installmentScheduleList!=null && installmentScheduleList.size()>0){
                TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
                InstallmentScheduleDataAccess.delete(context, taskH.getUuid_task_h());
                for(InstallmentSchedule installmentSchedule : installmentScheduleList){
                    if (installmentSchedule.getUuid_installment_schedule() == null){
                        installmentSchedule.setUuid_installment_schedule(Tool.getUUID());
                    }
                    installmentSchedule.setUuid_task_h(taskH.getUuid_task_h());
                }
                InstallmentScheduleDataAccess.addOrReplace(context, installmentScheduleList);
            }
            callback.onSaveFinished(true);
        }else{
            callback.onSaveFinished(false);
        }
    }

    @Override
    public void getDataInstallmentSchedule(final String taskId, final TaskListener listener) {
        new AsyncTask<Void, Void, InstallmentScheduleResponse>() {
            private ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity,
                        "", context.getString(R.string.progressWait), true);

            }
            @Override
            protected InstallmentScheduleResponse doInBackground(Void... params) {
                InstallmentScheduleApi api = new InstallmentScheduleApi(activity);
                try {
                    if(isInternetConnected(context)){
                        return api.request(taskId);
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(InstallmentScheduleResponse installmentScheduleResponse) {
                super.onPostExecute(installmentScheduleResponse);
                if (progressDialog!=null&&progressDialog.isShowing()){
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        FireCrash.log(e);
                    }
                }
                if(!GlobalData.isRequireRelogin()) {
                    if (installmentScheduleResponse == null) {
                        //bong 25 mei 15 - display local data
                        TaskH taskH = TaskHDataAccess.getOneTaskHeader(context, taskId);
                        if (taskH != null) {
                            List<InstallmentSchedule> installmentScheduleList = InstallmentScheduleDataAccess.getAllByTask(context, taskH.getUuid_task_h());
                            if (installmentScheduleList != null && !installmentScheduleList.isEmpty()) {
                                listener.onLocalData(installmentScheduleList);
                            } else {
                                NiftyDialogBuilder.getInstance(activity)
                                        .withMessage(activity.getString(R.string.no_data_found_offline))
                                        .withTitle(activity.getString(R.string.warning_capital))
                                        .isCancelableOnTouchOutside(false)
                                        .withButton1Text(activity.getString(R.string.btnClose))
                                        .setButton1Click(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                NiftyDialogBuilder.getInstance(activity).dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    } else if (installmentScheduleResponse.getStatus().getCode() != 0) {
                        NiftyDialogBuilder.getInstance(activity)
                                .withMessage(installmentScheduleResponse.getStatus().getMessage())
                                .withTitle(activity.getString(R.string.server_error))
                                .isCancelableOnTouchOutside(false)
                                .withButton1Text(activity.getString(R.string.btnClose))
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        activity.finish();
                                    }
                                })
                                .show();
                        return;
                    } else {
                        listener.onCompleteTask(installmentScheduleResponse);
                    }
                }
            }
        }.execute();
    }
}
