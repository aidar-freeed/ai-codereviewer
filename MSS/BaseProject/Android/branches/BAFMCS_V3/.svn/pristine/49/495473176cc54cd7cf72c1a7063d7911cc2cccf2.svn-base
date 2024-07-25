package com.adins.mss.base.syncfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.authentication.AuthenticationTask;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.login.DefaultLoginModel;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.MobileDataFile;
import com.adins.mss.foundation.http.AuditDataType;
import com.adins.mss.foundation.http.AuditDataTypeGenerator;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.util.Date;
import java.util.List;

/**
 * Created by loise on 10/19/2017.
 */

/**
 * Class untuk menampung context, counter ,data , dan method untuk keseluruhan proses import external db
 * mulai dari get list file, download, decrypt, extract, dan insert.
 */
public class FileSyncHelper {
    private static List<MobileDataFile> data;
    public static List<MobileDataFile> activeData;
    public static int currentidx = 0;
    public static MobileDataFile metadata;
    public static Context instance;
    //untuk menentukan menggunakan handler yang mana
    //0= login activity, 1= main activity
    public static int senderID = -1;
    static String link, savePath, message;

    public static List<MobileDataFile> getData() {
        return data;
    }

    public static void setData(List<MobileDataFile> data) {
        FileSyncHelper.data = data;
    }

    /**
     * Untuk memulai proses panggil method ini
     *
     * @param context
     */
    public static void startFileSync(Context context) {
        instance = context;
        getDownloadList();
    }

    /**
     * method untuk melakukan request ke WS untuk list file yang butuh didownload
     */

    private static void getDownloadList() {

        new AsyncTask<Void, Void, HttpConnectionResult>() {
            boolean success;
            ErrorMessageHandler errorMessageHandler;
            ProgressDialog pDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(instance);
                pDialog.setMessage(instance.getString(R.string.getting_file_list));
                success = true;
                pDialog.setIndeterminate(true);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(false);
                pDialog.show();
                //error handler initialization
                errorMessageHandler = new ErrorMessageHandler();
                errorMessageHandler.setiShowError(new IShowError() {
                    @Override
                    public void showError(String errorSubject, String errorMsg, int notifType) {
                        if (errorSubject == null || errorSubject.equals("")) {
                            Toast.makeText(instance, errorMsg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(instance, errorSubject + ": " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            protected HttpConnectionResult doInBackground(Void... params) {
                AuditDataType audit = AuditDataTypeGenerator.generateActiveUserAuditData();
                //GlobalData.getSharedGlobalData().loadFromProperties(instance);
                GlobalData.getSharedGlobalData().setAuditData(audit);
                SyncFileRequest request = new SyncFileRequest();
                request.setAudit(audit);
                request.setImei(audit.getDeviceId());
                Date maxdtm = MobileDataFileDataAccess.getMaxTimestamp(instance);
                request.setDtm_upd(maxdtm);
                String json = GsonHelper.toJson(request);
                String url = GlobalData.getSharedGlobalData().getUrlSyncFiles();
                boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
                boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
                HttpCryptedConnection httpConn = new HttpCryptedConnection(instance, encrypt, decrypt);
                HttpConnectionResult serverResult = null;

                //Firebase Performance Trace HTTP Request
                HttpMetric networkMetric = FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
                Utility.metricStart(networkMetric, json);

                try {
                    serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
                    Utility.metricStop(networkMetric, serverResult);
                    if (!serverResult.isOK()) {
                        success = false;
                        return serverResult;
                    }
                    JsonResponseSyncFile response = GsonHelper.fromJson(serverResult.getResult(), JsonResponseSyncFile.class);
                    if (response.getStatus().getCode() == 0) {
                        setData(response.getListMobileDataFile());
                    } else {
                        success = false;
                    }

                } catch (Exception e) {
                    FireCrash.log(e);
                    success = false;
                    e.printStackTrace();
                }
                return serverResult;
            }

            @Override
            protected void onPostExecute(HttpConnectionResult connectionResult) {
                super.onPostExecute(connectionResult);
                try {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                        pDialog = null;
                    }
                } catch (Exception e) {
                    FireCrash.log(e);
                }
                //reset index counter
                currentidx = -1;

                //jika list tidak kosong maka masuk proses download file
                if (success) {
                    if (getData() == null || getData().size() == 0) {
                        Toast.makeText(instance, instance.getString(R.string.no_new_files), Toast.LENGTH_SHORT).show();
                        synchronizeCallback();
                    } else {
                        downloadFiles();
                    }
                } else {
                    if (GlobalData.isRequireRelogin()) {
                        com.adins.mss.foundation.dialog.DialogManager.showForceExitAlert(instance, instance.getString(com.adins.mss.base.R.string.msgLogout));
                        return;
                    }

                    errorMessageHandler.processError(connectionResult, instance.getString(R.string.failed_to_get_file), ErrorMessageHandler.TOAST_TYPE);
                }
                if (instance != null) {
                    instance = null;
                }
            }
        }.execute();
    }

    /**
     * method untuk proses download file
     */
    public static void downloadFiles() {
        currentidx++;
        int i = currentidx;
        metadata = getData().get(i);
        message = instance.getString(R.string.downloading_file, (i + 1), getData().size());
        link = getData().get(i).getFile_url();
        savePath = GlobalData.getSharedGlobalData().getSavePath();
        if (link == null || link.isEmpty()) {
            link = getData().get(i).getAlternate_file_url();
            if (link == null || link.isEmpty()) {
                //popup message bila link kosong
                Toast.makeText(instance, instance.getString(R.string.no_links_found, getData().get(i).getId_datafile()), Toast.LENGTH_SHORT).show();
            }
        } else {
            //memanggil asynctask filedownloader
            DownloadParams parameters = new DownloadParams(savePath, instance, message, metadata);
            FileDownloader downloader = new FileDownloader(instance);
            downloader.execute(parameters);
        }
    }

    /**
     * method untuk memanggil proses import file
     */
    public static void importFiles() {
        currentidx++;
        int i = currentidx;
        metadata = activeData.get(i);
        message = instance.getResources().getString(R.string.import_file_to, i + 1, activeData.size());
        ImportDbParams importParams = new ImportDbParams(instance, message, metadata);
        new ImportDbFromCsv(instance).execute(importParams);
    }

    public static void synchronizeCallback() {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putBoolean("importSuccess", true);
        msg.setData(bundle);
        if (instance != null) {
            instance = null;
        }
        try {
            switch (senderID) {
                case 0:
                    DefaultLoginModel.importSuccess.sendMessage(msg);
                    break;
                case 1:
                    AuthenticationTask.importSuccess.sendMessage(msg);
                    break;
                case 2:
                    DefaultLoginModel.importSuccess.sendMessage(msg);
                    break;
                default:
                    break; //handle kalau belum dikirim idnya
            }
        } catch (Exception e) {
            FireCrash.log(e);
            try {
                AuthenticationTask.importSuccess.sendMessage(msg);
            } catch (Exception ex) {

            }
        }
    }

}
