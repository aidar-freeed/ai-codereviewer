package com.adins.mss.odr.catalogue.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileContentD;
import com.adins.mss.dao.MobileContentH;
import com.adins.mss.foundation.db.dataaccess.MobileContentDDataAccess;
import com.adins.mss.foundation.db.dataaccess.MobileContentHDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.http.MssRequestType;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.odr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivia.dg on 11/28/2017.
 */

public class GetPromo extends AsyncTask<Void, Void, List<PromoCatalogBean>> {
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private String errMessage;
    private List<PromoCatalogBean> result = new ArrayList<PromoCatalogBean>();

    public GetPromo(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }

    @Override
    protected List<PromoCatalogBean> doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            MssRequestType request = new MssRequestType();
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_CATALOGUE_PROMO();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GetPromoResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, GetPromoResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage = e.getMessage();
                    }
                }

                List<PromoCatalogBean> list = response.getListImage();
                if (list != null && list.size() != 0) {
                    MobileContentDDataAccess.clean(activity);
                    MobileContentHDataAccess.clean(activity);
                    result = list;
                    for (PromoCatalogBean bean : result) {
                        String uuid = bean.getUuidMobileContentD();
                        byte[] img = Utils.base64ToByte(bean.getContent());

                        MobileContentH tempHeader = new MobileContentH();
                        tempHeader.setUuid_mobile_content_h(bean.getUuidMobileContentH());
                        tempHeader.setContent_name(bean.getContentName());
                        tempHeader.setContent_description(bean.getContentDescription());
                        tempHeader.setUuid_user(GlobalData.getSharedGlobalData().getUser().getUuid_user());
                        MobileContentHDataAccess.addOrReplace(activity, tempHeader);

                        MobileContentD tempData = new MobileContentD();
                        tempData.setUuid_mobile_content_d(uuid);
                        tempData.setContent(img);
                        tempData.setUuid_mobile_content_h(bean.getUuidMobileContentH());
                        MobileContentDDataAccess.addOrReplace(activity, tempData);
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
    protected void onPostExecute(List<PromoCatalogBean> result) {
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

        GetCatalogue task = new GetCatalogue(activity);
        task.execute();

    }
}
