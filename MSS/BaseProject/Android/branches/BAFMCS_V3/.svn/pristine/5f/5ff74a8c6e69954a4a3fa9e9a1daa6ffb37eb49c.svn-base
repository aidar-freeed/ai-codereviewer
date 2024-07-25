package com.adins.mss.odr.accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.dao.TaskH;
import com.adins.mss.foundation.db.dataaccess.TaskHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.api.LoadOpportunityDetailRequest;
import com.adins.mss.odr.accounts.api.LoadOpportunityDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/21/2017.
 */

public class LoadOpportunityDetail extends AsyncTask<Void, Void, List<TaskH>> {
    private FragmentActivity activity;
    private GroupTask groupTask;
    private String groupTaskId;
    private ProgressDialog progressDialog;
    private String errMessage;
    private List<TaskH> result = new ArrayList<TaskH>();

    public LoadOpportunityDetail(FragmentActivity activity, GroupTask groupTask) {
        this.activity = activity;
        this.groupTask = groupTask;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }

    @Override
    protected List<TaskH> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            groupTaskId = groupTask.getGroup_task_id();
            LoadOpportunityDetailRequest request = new LoadOpportunityDetailRequest();
            request.setGroup_task_id(groupTaskId);
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_OPPORTUNITY_DETAIL();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadOpportunityDetailResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, LoadOpportunityDetailResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage = e.getMessage();
                    }
                }

                List<TaskH> taskHList = response.getListOpporDetail();
                if (taskHList != null && taskHList.size() != 0) {
                    result = taskHList;
                    for (TaskH temp : taskHList) {
                        TaskH task = TaskHDataAccess.getOneHeader(activity, temp.getUuid_task_h());
                        if (task != null) {
                            TaskHDataAccess.addOrReplace(activity, temp);
                        } else {
//                            temp.setUuid_task_h(Tool.getUUID());
                            temp.setDtm_crt(Tool.getSystemDateTime());
                            TaskHDataAccess.add(activity, temp);
                        }
                    }
                } else {
                    errMessage = activity.getString(R.string.no_data_from_server);
                }
            } else {
                errMessage = activity.getString(R.string.server_down);
            }
        } else {
            errMessage = activity.getString(R.string.no_internet_connection);
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<TaskH> taskHs) {
        super.onPostExecute(taskHs);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (errMessage != null) {
            if (errMessage.equals(activity.getString(R.string.no_data_from_server))) {
                NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(activity);
                builder.withTitle("INFO")
                        .withMessage(errMessage)
                        .show();
            } else {
                Toast.makeText(activity, errMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            ArrayList<String> taskTodo = new ArrayList<>();
            ArrayList<String> taskHistory = new ArrayList<>();
            for (TaskH task : taskHs) {
                if (task.getSubmit_date() != null && !task.getSubmit_date().equals(""))
                    taskHistory.add(task.getUuid_task_h());
                else
                    taskTodo.add(task.getUuid_task_h());
            }

            Intent intent = new Intent(activity, OpportunityDetailActivity.class);
            Bundle extra = new Bundle();
            extra.putString(Global.BUND_KEY_GROUPTASK_ID, groupTaskId);
            extra.putStringArrayList("taskTodo", taskTodo);
            extra.putStringArrayList("taskHistory", taskHistory);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(extra);
            activity.startActivity(intent);
        }
    }
}
