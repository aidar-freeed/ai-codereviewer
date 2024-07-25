package com.adins.mss.base.depositreport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.errorhandler.ErrorMessageHandler;
import com.adins.mss.base.errorhandler.IShowError;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.db.dataaccess.TaskDDataAccess;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.foundation.image.JsonResponseImage;
import com.adins.mss.foundation.image.Utils;
import com.adins.mss.foundation.questiongenerator.QuestionBean;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.apache.http.NameValuePair;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by angga.permadi on 8/30/2016.
 */
public class GetImageTask extends AsyncTask<Void, Void, byte[]> implements IShowError {

    static byte[] imagebyte = null;
    List<NameValuePair> params;
    GetImageData getImageData;
    private ProgressDialog progressDialog;
    private String errMessage = null;
    private WeakReference<Activity> activity;
    private ImageView imageView;
    private TextView textView;
    private String taskh;
    private String questionId;
    private QuestionBean bean;
    private ErrorMessageHandler errorMessageHandler;

    public GetImageTask(Activity activity, List<NameValuePair> params, ImageView imageView, TextView textView, QuestionBean bean, GetImageData listener) {
        this.activity = new WeakReference<>(activity);
        this.params = params;
        this.imageView = imageView;
        this.textView = textView;
        this.bean = bean;
        getImageData = listener;
        errorMessageHandler = new ErrorMessageHandler(this);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity.get(),
                "", activity.get().getString(R.string.progressWait), true);
    }

    @Override
    protected byte[] doInBackground(Void... arg0) {
        byte[] imageResult = null;
        JsonRequestImage request = new JsonRequestImage();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.setUuid_task_h(params.get(0).getValue());
        taskh = params.get(0).getValue();
        request.setQuestion_id(params.get(1).getValue());
        questionId = params.get(1).getValue();

        String json = GsonHelper.toJson(request);
        String url = GlobalData.getSharedGlobalData().getURL_GET_IMAGE();

        boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
        boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
        HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
        HttpConnectionResult serverResult = null;

        HttpMetric networkMetric =
                FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
        Utility.metricStart(networkMetric, json);

        try {
            serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
            Utility.metricStop(networkMetric, serverResult);
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }

        if(serverResult == null){
            errMessage = activity.get().getString(R.string.request_error);
            return new byte[0];
        }

        String result = serverResult.getResult();
        JsonResponseImage resultServer = null;
        try {
            resultServer = GsonHelper.fromJson(result, JsonResponseImage.class);
            if (resultServer.getStatus().getCode() == 0) {
                List<TaskD> taskDs = resultServer.getImg();
                TaskD d = taskDs.get(0);
                imageResult = d.getImage();
            } else {
                errMessage = resultServer.getStatus().getMessage();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
            errMessage = activity.get().getString(R.string.request_error);
        }

        return imageResult;
    }

    @Override
    protected void onPostExecute(byte[] result) {
        if (progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FireCrash.log(e);
            }
        }
        if (errMessage != null) {
            errorMessageHandler.processError("ERROR",errMessage,ErrorMessageHandler.DIALOG_TYPE);
        } else {
            if (null == result) {
                errorMessageHandler.processError("INFO"
                        ,activity.get().getString(R.string.no_image)
                        ,ErrorMessageHandler.DIALOG_TYPE);
            } else {
                try {
                    setResultImg(result);
                } catch (Exception e) {
                    FireCrash.log(e);
                    e.printStackTrace();
                }
            }
        }
    }

    public void setResultImg(byte[] resultImg) throws Exception {
        try {
            Bitmap bm = Utils.byteToBitmap(resultImg);
            int[] res = Tool.getThumbnailResolution(bm.getWidth(), bm.getHeight());
            Bitmap thumbnail = Bitmap.createScaledBitmap(bm, res[0], res[1], true);
            imageView.setImageBitmap(thumbnail);
            TaskD task = TaskDDataAccess.getAllByUUIDTaskHandQuestionId(activity.get(), taskh, questionId);
            task.setImage(resultImg);
            task.setText_answer("   " + bm.getWidth() + " x " + bm.getHeight() + ". Size " + resultImg.length + " Bytes");
            bean.setIntTextAnswer("   " + bm.getWidth() + " x " + bm.getHeight() + ". Size " + resultImg.length + " Bytes");
            bean.setImgAnswer(resultImg);
            TaskDDataAccess.addOrReplace(activity.get(), task);
            textView.setText("   " + bm.getWidth() + " x " + bm.getHeight() + ". Size " + resultImg.length + " Bytes");
        } catch (Exception e) {
            FireCrash.log(e);
            throw e;
        }
    }

    @Override
    public void showError(String errorSubject, String errorMsg, int notifType) {
        if(notifType == ErrorMessageHandler.DIALOG_TYPE){
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
            dialogBuilder.withTitle(errorSubject)
                    .withMessage(errorMsg)
                    .show();
        }
    }

    public interface GetImageData {
        void getImageData(boolean data);
    }
}
