package com.adins.mss.foundation.datatable;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.models.GetDataTableRequest;
import com.adins.mss.base.models.GetDataTableResponse;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.db.dataaccess.TableDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.net.HttpClient;
import com.adins.mss.foundation.http.net.HttpsClient;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;

import java.util.Map;

public class GetDataTableTask extends AsyncTask<Void, Void, GetDataTableResponse> {

    private Context context;
    private Map<String, String> params;
    private HttpClient httpClient;
    private HttpsClient httpsClient;
    private String errMessage = null;

    public GetDataTableTask(Context context, Map<String, String> params) {
        this.context = context;
        this.params = params;

        try {
            httpsClient = new HttpsClient(context);
            httpsClient.setAcceptAllCertificate(true);
            httpsClient.setBypassHostnameVerification(true);
            httpsClient.setConnectionTimeout((long) 120000);
            httpsClient.initialize();

            httpClient = new HttpClient(context);
            httpClient.setConnectionTimeout(120000);
        } catch (Exception e) {
            Log.e(GetDataTableTask.class.getSimpleName(), e.getMessage());
        }
    }

    @Override
    protected GetDataTableResponse doInBackground(Void... voids) {
        String query = params.get("message");

        if (Global.IS_DEV) {
            Log.d(this.getClass().getSimpleName(), query);
        }

        JSONArray listData = null;
        try {
            listData = TableDataAccess.getDataByQuery(context, query);
        } catch (Exception e) {
            errMessage = "ERROR when execute query: " + query + " Caused By: " + e.getMessage();
        }

        if (null == listData || listData.length() < 1) {
            if (errMessage == null || "".equalsIgnoreCase(errMessage)) {
                errMessage = context.getResources().getString(R.string.no_data_available);
            }
        }

        if (!Tool.isInternetconnected(context)) {
            errMessage = context.getString(com.adins.mss.base.R.string.no_internet_connection);
        }

        if (errMessage != null && !"".equalsIgnoreCase(errMessage)) {
            return submitDataToServer(errMessage);
        } else {
            return submitDataToServer(listData.toString());
        }
    }

    @Override
    protected void onPostExecute(GetDataTableResponse jsonGetDataTableResponse) {
        super.onPostExecute(jsonGetDataTableResponse);
        if (null == errMessage && jsonGetDataTableResponse != null) {
            String filename = params.get("filename");
            if (1 != jsonGetDataTableResponse.getStatus().getCode()) {
                String message = "Error GET DATA TABLE TASK filename '" + filename + "': ";
                FireCrash.log(new RemoteException(), message + jsonGetDataTableResponse.getStatus().getMessage());
            }
        }
    }

    private String getAppInfo(Context context) {
        String packageName = null;
        String version = null;
        try {
            PackageInfo pInfo = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0);
            packageName = pInfo.packageName;
            String[] appVersion = pInfo.versionName.split("-");
            version = appVersion[0];
        } catch (PackageManager.NameNotFoundException e) {
            if (Global.IS_DEV) {
                e.printStackTrace();
            }
        }

        String loginId = GlobalData.getSharedGlobalData().getUser().getLogin_id();

        return packageName + "-" + version + "-" + loginId;
    }

    private GetDataTableResponse submitDataToServer(String data) {
        String url = params.get("url");
        String query = params.get("message");
        String filename = params.get("filename");

        GetDataTableRequest jsonRequest = new GetDataTableRequest();
        jsonRequest.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        jsonRequest.setFileName(filename);
        jsonRequest.setAppInfo(getAppInfo(context));
        jsonRequest.setQuery(query);
        jsonRequest.setData(data);
        if (GlobalData.getSharedGlobalData().getUser() != null) {
            jsonRequest.setUser(GlobalData.getSharedGlobalData().getUser().getLogin_id());
        }

        String json = GsonHelper.toJson(jsonRequest);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded");

        if (params.containsKey("X-Api-Key")) {
            builder.addHeader("X-Api-Key", params.get("X-Api-Key"));
        }

        builder.post(RequestBody.create(MediaType.parse("Content-Type"), json));
        Request request = builder.build();

        try {
            Response response = null;
            if (url.startsWith("https://")) {
                response = httpsClient.execute(request);
            } else if (url.startsWith("http://")) {
                response = httpClient.execute(request);
            }

            if (response.code() != 200) {
                errMessage = "Error SEND DATA TABLE TO SERVER filename '" + filename + "': " + response.message();
                FireCrash.log(new RemoteException(), errMessage);
                return null;
            }

            return GsonHelper.fromJson(response.body().string(), GetDataTableResponse.class);
        } catch (Exception e) {
            errMessage = "Error SEND DATA TABLE TO SERVER filename '" + filename + "': " + e.getMessage();
            FireCrash.log(e, errMessage);
        }
        return null;
    }

}
