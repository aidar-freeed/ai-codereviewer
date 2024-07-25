package com.adins.mss.odr.accounts;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.api.AccountsSearchRequest;
import com.adins.mss.odr.accounts.api.AccountsSearchResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.aap on 11/27/2018.
 */

public class GetAccount extends AsyncTask<Void, Void, ArrayList<String>>{
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private String errMessage;
    private ArrayList<String> result = new ArrayList<String>();

    public GetAccount(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            AccountsSearchRequest request = new AccountsSearchRequest();
            request.setUuid_account("");
            request.setUuid_product("");
            request.setUuid_status("");
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_ACCOUNT();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            AccountsSearchResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, AccountsSearchResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage=e.getMessage();
                    }
                }

                List<Account> listAccounts = response.getListAccount();
                if (listAccounts != null && listAccounts.size() != 0) {
                    for(Account acc : listAccounts){
                        result.add(acc.getUuid_account());
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
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);

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
        }

        Fragment fragment = new FragmentAccountList();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Global.BUND_KEY_ACCOUNT_ID, result);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(com.adins.mss.base.R.anim.activity_open_translate, com.adins.mss.base.R.anim.activity_close_scale, com.adins.mss.base.R.anim.activity_open_scale, com.adins.mss.base.R.anim.activity_close_translate);
        transaction.replace(com.adins.mss.base.R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
