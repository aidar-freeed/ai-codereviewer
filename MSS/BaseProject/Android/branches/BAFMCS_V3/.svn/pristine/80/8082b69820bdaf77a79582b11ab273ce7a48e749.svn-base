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
import com.adins.mss.dao.Account;
import com.adins.mss.dao.Product;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.api.LoadProductContactRequest;
import com.adins.mss.odr.accounts.api.LoadProductContactResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 12/19/2017.
 */

public class LoadProductContact extends AsyncTask<Void, Void, ArrayList<String>> {
    private FragmentActivity activity;
    private Account account;
    private String uuidAccount;
    private ProgressDialog progressDialog;
    private String errMessage;
    private ArrayList<String> result = new ArrayList<String>();

    public LoadProductContact(FragmentActivity activity, Account account) {
        this.activity = activity;
        this.account = account;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }
    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            uuidAccount = account.getUuid_account();
            LoadProductContactRequest request = new LoadProductContactRequest();
            request.setUuid_account(uuidAccount);
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_PRODUCT_CONTACT();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadProductContactResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, LoadProductContactResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage=e.getMessage();
                    }
                }

                List<Product> productList = response.getListProduct();

                if (productList != null && productList.size() != 0) {
                    for (Product product : productList) {
                        result.add(product.getUuid_product());
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
    protected void onPostExecute(ArrayList<String> products) {
        super.onPostExecute(products);

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
            String accountId = account.getUuid_account();
            Intent intent = new Intent(activity, AccountResultActivity.class);
            Bundle extra = new Bundle();
            extra.putString(Global.BUND_KEY_ACCOUNT_ID, accountId);
            extra.putStringArrayList(Global.BUND_KEY_PRODUCT_ID, products);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(extra);
            activity.startActivity(intent);
        }
    }
}
