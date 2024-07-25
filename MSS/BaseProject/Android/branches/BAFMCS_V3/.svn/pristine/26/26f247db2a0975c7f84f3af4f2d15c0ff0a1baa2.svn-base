package com.adins.mss.odr.tool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.constant.Global;
import com.adins.mss.dao.TaskD;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.R;
import com.adins.mss.odr.model.JsonRequestImage;
import com.adins.mss.odr.update.OrderCancelActivity;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import org.apache.http.NameValuePair;

import java.util.List;

public class getImageTask extends AsyncTask<Void, Void, byte[]> {
	private ProgressDialog progressDialog;
	private String errMessage = null;
	private Activity activity;
	static byte[] imagebyte =null;
	List<NameValuePair> params;
	public getImageTask(Activity activity, List<NameValuePair> params){
		this.activity = activity;
		this.params = params;
	}
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(activity,
				"", activity.getString(R.string.progressWait), true);
	}

	@Override
	protected byte[] doInBackground(Void... arg0) {
		byte[] imageResult =null;

		JsonRequestImage request = new JsonRequestImage();
		request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
		request.setUuid_task_h(params.get(0).getValue());
		request.setQuestion_id(params.get(1).getValue());
		
		String json = GsonHelper.toJson(request);
		String url = GlobalData.getSharedGlobalData().getURL_GET_IMAGE();

		boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
		boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
		HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
		HttpConnectionResult serverResult = null;

		HttpMetric networkMetric =
				FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
		Utility.metricStart(networkMetric, json);

		try {
			serverResult = httpConn.requestToServer(url, json, Global.DEFAULTCONNECTIONTIMEOUT);
			Utility.metricStop(networkMetric, serverResult);
		} catch (Exception e) {
			e.printStackTrace();		
		}
		
		String result = serverResult.getResult();

		JsonResponseImage resultServer = null;
		try {
			resultServer = GsonHelper.fromJson(result, JsonResponseImage.class);
			if(resultServer.getStatus().getCode()==0){
				List<TaskD> taskDs = resultServer.getImg();
				TaskD d = taskDs.get(0);
				imageResult = d.getImage();
			}else{
				errMessage = resultServer.getStatus().getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			errMessage = e.getMessage();
		}
		
		return imageResult;
	}
	
	@Override
	protected void onPostExecute(byte[] result){
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
			}
		}
		if(errMessage!=null){
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
			dialogBuilder.withTitle("ERROR")
				.withMessage(errMessage)
				.show();
		}else{				
			if(null==result){
				NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
				dialogBuilder.withTitle("INFO")
					.withMessage("Cannot get Image")
					.show();
			}else{
				try {
					OrderCancelActivity.targetThumbnail.setResultImg(result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
