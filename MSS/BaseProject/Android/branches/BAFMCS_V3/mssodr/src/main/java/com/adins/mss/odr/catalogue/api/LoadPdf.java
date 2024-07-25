package com.adins.mss.odr.catalogue.api;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.util.Base64;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.BuildConfig;
import com.adins.mss.odr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by olivia.dg on 11/30/2017.
 */

public class LoadPdf extends AsyncTask<Void, Void, String> {
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private String errMessage;
    private String result;
    private String uuid;

    public LoadPdf(FragmentActivity activity, String uuid) {
        this.activity = activity;
        this.uuid = uuid;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.contact_server), true);
    }

    @Override
    protected String doInBackground(Void... params) {
        if (Tool.isInternetconnected(activity)) {
            LoadPdfRequest request = new LoadPdfRequest();
            request.setUuidCatalogue(uuid);
            request.setAudit(GlobalData.getSharedGlobalData().getAuditData());

            String json = GsonHelper.toJson(request);

            String url = GlobalData.getSharedGlobalData().getURL_GET_CATALOGUE_PDF();
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
            HttpConnectionResult serverResult = null;
            try {
                serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoadPdfResponse response = null;
            if (serverResult != null && serverResult.isOK()) {
                try {
                    String responseBody = serverResult.getResult();
                    response = GsonHelper.fromJson(responseBody, LoadPdfResponse.class);
                } catch (Exception e) {
                    if(Global.IS_DEV) {
                        e.printStackTrace();
                        errMessage=e.getMessage();
                    }
                }

                result = response.getCataloguePdf();
                if (result != null && !result.isEmpty()) {
//                    Catalogue catalogue = CatalogueDataAccess.getOne(activity, uuid);
//                    catalogue.setCatalogue_file(result);
//                    CatalogueDataAccess.addOrReplace(activity, catalogue);
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
    protected void onPostExecute(String result) {
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
        } else {
            GetFilePathAndStatus filepath = getFile(result, "catalogue", "pdf");
            File file = new File(filepath.filePath);
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            } else
                uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf");
            activity.startActivity(intent);
        }
    }

    public static GetFilePathAndStatus getFile(String base64, String filename, String extension){
        GetFilePathAndStatus getFilePathAndStatus = new GetFilePathAndStatus();

        try(FileOutputStream  os = new FileOutputStream(getReportPath(filename,extension), false)) {
            byte[] pdfAsBytes = Base64.decode(base64, 0);
            os.write(pdfAsBytes);
            os.flush();
            getFilePathAndStatus.filStatus = true;
            getFilePathAndStatus.filePath = getReportPath(filename, extension);
            return getFilePathAndStatus;
        } catch (IOException e) {
            e.printStackTrace();
            getFilePathAndStatus.filStatus = false;
            getFilePathAndStatus.filePath = getReportPath(filename, extension);
            return getFilePathAndStatus;
        }
    }

    public static String getReportPath(String filename,String extension) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ParentFolder/Report");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + filename + "."+extension);
        return uriSting;

    }
    public static class GetFilePathAndStatus{
        public boolean filStatus;
        public String filePath;

    }
}
