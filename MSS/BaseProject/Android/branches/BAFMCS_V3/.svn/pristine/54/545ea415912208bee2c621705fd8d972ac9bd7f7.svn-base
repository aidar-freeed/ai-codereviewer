package com.adins.mss.odr.accounts;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Account;
import com.adins.mss.dao.Contact;
import com.adins.mss.foundation.db.dataaccess.ContactDataAccess;
import com.adins.mss.foundation.dialog.DialogManager;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.accounts.api.LoadContactRequest;
import com.adins.mss.odr.accounts.api.LoadContactResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by olivia.dg on 11/20/2017.
 */

public class LoadContact extends AsyncTask<Void, Void, List<Contact>> {
    private FragmentActivity activity;
    private Account account;
    private String uuidAccount;
    private ProgressDialog progressDialog;
    private String errMessage;
    private List<Contact> result = new ArrayList<Contact>();
    private String uuid;

    public LoadContact(FragmentActivity activity, Account account, String uuid) {
        this.activity = activity;
        this.account = account;
        this.uuid = uuid;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }

    @Override
    protected List<Contact> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            uuidAccount = account.getUuid_account();
            LoadContactRequest request = new LoadContactRequest();
            request.setUuid_account(uuidAccount);
            request.setUuid_product(uuid);
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_CONTACT();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadContactResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, LoadContactResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage=e.getMessage();
                    }
                }

                List<Contact> contactList = response.getListContact();
                ContactDataAccess.clean(activity);

                if (contactList != null && contactList.size() != 0) {
                    result = response.getListContact();
                    if (result != null){
                        for (Contact temp : result) {
                            String uuidContact = Tool.getUUID();
                            String uuidAccount = account.getUuid_account();
                            Date dtm_crt = Tool.getSystemDateTime();
                            Contact contact = new Contact(uuidContact, temp.getContact_name(), temp.getContact_dept(), temp.getContact_phone(), temp.getContact_email(), uuidAccount, dtm_crt);
                            ContactDataAccess.add(activity, contact);
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
    protected void onPostExecute(List<Contact> contacts) {
        super.onPostExecute(contacts);

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
            Contact contact = new Contact();
            for (Contact temp : contacts) {
                contact = temp;
                break;
            }
            DialogManager.showDetailContact(activity, contact);
        }
    }
}
