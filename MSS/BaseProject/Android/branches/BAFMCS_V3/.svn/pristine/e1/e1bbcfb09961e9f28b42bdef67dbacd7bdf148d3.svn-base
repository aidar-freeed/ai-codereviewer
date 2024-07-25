package com.adins.mss.odr.catalogue.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.NewMainActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.Catalogue;
import com.adins.mss.foundation.db.dataaccess.CatalogueDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.odr.R;
import com.adins.mss.odr.catalogue.FragmentPromotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class GetCatalogue extends AsyncTask<Void, Void, List<Catalogue>> {
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private String errMessage;
    private List<Catalogue> result = new ArrayList<Catalogue>();

    public GetCatalogue(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }

    @Override
    protected List<Catalogue> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            MssRequestType request = new MssRequestType();
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_CATALOGUE_HEADER();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GetCatalogueResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, GetCatalogueResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage=e.getMessage();
                    }
                }

                List<Catalogue> list = response.getListProductCatalogue();
                if (list != null && list.size() != 0) {
                    result = list;
                    CatalogueDataAccess.addOrReplace(activity, list);
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
    protected void onPostExecute(List<Catalogue> result) {
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

        Fragment fragment = new FragmentPromotion();

        FragmentTransaction transaction = NewMainActivity.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_open_translate,R.anim.activity_close_scale,R.anim.activity_open_scale,R.anim.activity_close_translate);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
