package com.adins.mss.odr.accounts;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.GroupTask;
import com.adins.mss.foundation.db.dataaccess.GroupTaskDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.api.LoadOpportunityResponse;
import com.adins.mss.odr.accounts.api.LoadProductContactRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/20/2017.
 */

public class LoadOpportunity extends AsyncTask<Void, Void, List<GroupTask>> {
    private FragmentActivity activity;
    private Account account;
    private String uuidAccount;
    private ProgressDialog progressDialog;
    private String errMessage;
    private List<GroupTask> result = new ArrayList<GroupTask>();

    public LoadOpportunity(FragmentActivity activity, Account account) {
        this.activity = activity;
        this.account = account;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }
    @Override
    protected List<GroupTask> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            uuidAccount = account.getUuid_account();
            LoadProductContactRequest request = new LoadProductContactRequest();
            request.setUuid_account(uuidAccount);
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_OPPORTUNITY();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadOpportunityResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, LoadOpportunityResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage=e.getMessage();
                    }
                }

                List<GroupTask> groupTaskList = response.getListOppor();
                if (groupTaskList != null && groupTaskList.size() != 0) {
                    result = groupTaskList;
                    for (GroupTask temp : groupTaskList) {
                        GroupTask gt = GroupTaskDataAccess.getOneHeader(activity, temp.getGroup_task_id());
                        if (gt != null) {
                            gt.setLast_status(temp.getLast_status());
                            gt.setDtm_crt(temp.getDtm_crt());
                            GroupTaskDataAccess.addOrReplace(activity, gt);
                        } else {
                            temp.setUuid_group_task(Tool.getUUID());
                            temp.setUuid_account(uuidAccount);
                            temp.setDtm_crt(Tool.getSystemDateTime());
                            GroupTaskDataAccess.add(activity, temp);
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
    protected void onPostExecute(List<GroupTask> groupTasks) {
        super.onPostExecute(groupTasks);

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
            LoadProductContact loadProductContact = new LoadProductContact(activity, account);
            loadProductContact.execute();
        }
    }
}
