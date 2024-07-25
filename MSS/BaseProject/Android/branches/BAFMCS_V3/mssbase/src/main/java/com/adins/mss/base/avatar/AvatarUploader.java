package com.adins.mss.base.avatar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.net.SocketTimeoutException;

/**
 * Created by intishar.fa on 02/10/2018.
 */

public class AvatarUploader {

    public interface AvatarUploadHandler{
        void onUploadSuccess(String resultmsg, String image);
        void onUploadFail(String errormsg);
    }
    private Context context;
    private AvatarUploadTask uploadTask;
    private AvatarUploadHandler avatarUploadHandler;
    private ProgressDialog pDialog;
    private String image;

    public AvatarUploader(Context context,AvatarUploadHandler handler){
        this.avatarUploadHandler = handler;
        this.context = context;
    }

    public void uploadAvatar(AvatarUploadRequestJson requestJson){
        if(avatarUploadHandler != null){
            image = requestJson.getBase64Img();
            String json = GsonHelper.toJson(requestJson);
            uploadTask = new AvatarUploadTask(context,json,avatarUploadHandler);
            uploadTask.execute();
        }
        else {
            System.out.println("No handler found!");
        }
    }

    class AvatarUploadTask extends AsyncTask<Void,Void,String>{

        private String json;
        private Context context;
        private String result;
        private AvatarUploadHandler handler;
        private String errMsg = null;

        public AvatarUploadTask(Context context,String json,AvatarUploadHandler handler){
            this.context = context;
            this.json = json;
            this.handler = handler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Uploading avatar...");
            pDialog.setIndeterminate(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
            boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
            String url = GlobalData.getSharedGlobalData().getURL_UPLOAD_AVATAR();
            HttpCryptedConnection httpConn = new HttpCryptedConnection(context, encrypt, decrypt);
            HttpConnectionResult serverResult = null;

            //Firebase Performance Trace HTTP Request
            HttpMetric networkMetric =
                    FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
            Utility.metricStart(networkMetric, json);

            try {
                serverResult = httpConn.requestToServer(url,json, Global.DEFAULTCONNECTIONTIMEOUT);
                Utility.metricStop(networkMetric, serverResult);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(serverResult != null){
                if(serverResult.isOK()){
                    String resultValue = serverResult.getResult();
                    if(resultValue != null){
                        this.result = resultValue;
                    }
                } else {
                    errMsg = serverResult.getResult();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDialog != null)
                pDialog.dismiss();
            if(handler != null){
                //parse result to response obj
                try {
                    if(errMsg != null){
                        handler.onUploadFail(errMsg);
                    }
                    else if(null != result && !result.isEmpty()) {
                        AvatarUploadResponseJson avatarUploadResponseJson = GsonHelper.fromJson(result, AvatarUploadResponseJson.class);
                        if (avatarUploadResponseJson.getStatus().getCode() == 0) {
                            handler.onUploadSuccess(avatarUploadResponseJson.getResult(), image);
                        } else {
                            handler.onUploadFail(avatarUploadResponseJson.getResult());
                        }
                    }
                }
                catch (Exception e) {
                    StringBuilder errMsg = new StringBuilder();
                    errMsg.append(context.getString(R.string.upload_avatar));
                    if(result != null)
                        errMsg.append(result);
                    else
                        errMsg.append(context.getString(R.string.error_unknown));
                    /*if(result.contains("Unable to resolve host")){
                        errMsg.append(context.getString(R.string.no_internet_connection));
                    }
                    else if(result.contains("failed to connect to")){
                        errMsg.append(context.getString(R.string.connection_timeout));
                    }
                    else {
                        errMsg.append(context.getString(R.string.error_unknown));
                    }*/
                    handler.onUploadFail(errMsg.toString());
                    e.printStackTrace();
                }
            }
        }

    }
}
