package com.adins.mss.odr.assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.dynamicform.JsonResponseSubmitTask;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.odr.model.JsonRequestSubmitAssign;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

public class SubmitAssignTask extends AsyncTask<Void, Void, String> {
	private ProgressDialog progressDialog;
	private String errMessage = null;
	private Activity activity;
	String uuid_task_h;
	String assignId;
	String flag;
	String notes;
	public SubmitAssignTask(Activity activity, String notes, String uuid_task_h, String assignId, String flag){
		this.activity=activity;
		this.uuid_task_h= uuid_task_h;
		this.assignId= assignId;
		this.flag= flag;
		this.notes=notes;
	}
	
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(activity, "", activity.getString(com.adins.mss.base.R.string.progressWait), true);
	}
	
	@Override
	protected String doInBackground(Void... params) {
		String result = null;

		JsonRequestSubmitAssign request = new JsonRequestSubmitAssign();
        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
        request.setFlag(flag);
        request.setUuid_user(assignId);
        request.setUuid_task_h(uuid_task_h);
        request.setNotes(notes);
        String json = GsonHelper.toJson(request);
        String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_ASSIGN();
		boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
		boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
		HttpCryptedConnection httpConn = new HttpCryptedConnection(activity, encrypt, decrypt);
		HttpConnectionResult serverResult = null;

		//Firebase Performance Trace HTTP Request
		HttpMetric networkMetric =
				FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
		Utility.metricStart(networkMetric, json);

		try {
			serverResult = httpConn.requestToServer(url, json);
			Utility.metricStop(networkMetric, serverResult);
		} catch (Exception e) {
			e.printStackTrace();
			errMessage=e.getMessage();
		}
		String resp = null;
        resp = serverResult.getResult();
		JsonResponseSubmitTask response ;
		try {
			response = GsonHelper.fromJson(resp, JsonResponseSubmitTask.class);
			if(response.getStatus().getCode()==0){
				result=response.getResult();
			}else{
				errMessage=response.getStatus().getMessage();
			}
		} catch (Exception e) {
			result=resp;
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
			}
		}
		if (errMessage != null) {
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
			dialogBuilder.withTitle(activity.getString(com.adins.mss.base.R.string.error_capital))
				.withMessage(errMessage)
				.show();
		}
		else if (result.equals("Success")){
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
			dialogBuilder.withTitle(activity.getString(com.adins.mss.base.R.string.success))
				.withMessage(activity.getString(com.adins.mss.base.R.string.task_submitted))
				.isCancelable(false)
				.withButton1Text(activity.getString(com.adins.mss.base.R.string.btnOk))
				.setButton1Click(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						activity.finish();
					}
				})
				.show();
		}
		else{
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity);
			dialogBuilder.withTitle(activity.getString(com.adins.mss.base.R.string.warning_capital))
				.withMessage(result)
				.show();
		}
	}
}
