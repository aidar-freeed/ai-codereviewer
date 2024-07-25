package com.adins.mss.svy.assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.adins.mss.base.GlobalData;
import com.adins.mss.base.R;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.dynamicform.JsonResponseSubmitTask;
import com.adins.mss.base.mainmenu.MainMenuActivity;
import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.base.util.Utility;
import com.adins.mss.foundation.dialog.NiftyDialogBuilder;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.http.HttpConnectionResult;
import com.adins.mss.foundation.http.HttpCryptedConnection;
import com.adins.mss.svy.reassignment.JsonRequestSubmitAssign;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.HttpMetric;

import java.lang.ref.WeakReference;

public class SubmitAssignTask extends AsyncTask<Void, Void, String> {
	private ProgressDialog progressDialog;
	private String errMessage = null;
	private WeakReference<Activity> activity;
	String uuid_task_h;
	String assignId;
	String flag;
	String notes;
	public SubmitAssignTask(Activity activity, String notes, String uuid_task_h, String assignId, String flag){
		this.activity = new WeakReference<Activity>(activity);
		this.uuid_task_h= uuid_task_h;
		this.assignId= assignId;
		this.flag= flag;
		this.notes=notes;
	}
	
	@Override
	protected void onPreExecute() {
    	progressDialog = ProgressDialog.show(activity.get(), "", activity.get().getString(R.string.progressWait), true);
	}
	
	@Override
	protected String doInBackground(Void... params) {
		String result = null;
		if(Tool.isInternetconnected(activity.get())){
			JsonRequestSubmitAssign request = new JsonRequestSubmitAssign();
	        request.setAudit(GlobalData.getSharedGlobalData().getAuditData());
	        request.setFlag(flag);
	        request.setUuidUserAssign(assignId);
	        request.setUuid_task_h(uuid_task_h);
	        request.setNotes(notes);
	        String json = GsonHelper.toJson(request);
	        String url = GlobalData.getSharedGlobalData().getURL_SUBMIT_ASSIGN();
			boolean encrypt = GlobalData.getSharedGlobalData().isEncrypt();
			boolean decrypt = GlobalData.getSharedGlobalData().isDecrypt();
			HttpCryptedConnection httpConn = new HttpCryptedConnection(activity.get(), encrypt, decrypt);
			HttpConnectionResult serverResult = null;

			//Firebase Performance Trace HTTP Request
			HttpMetric networkMetric =
					FirebasePerformance.getInstance().newHttpMetric(url, FirebasePerformance.HttpMethod.POST);
			Utility.metricStart(networkMetric, json);

			try {
				serverResult = httpConn.requestToServer(url, json);
				Utility.metricStop(networkMetric, serverResult);
			} catch (Exception e) {
				FireCrash.log(e);
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
				FireCrash.log(e);
				result=resp;
			}
		}else{
			errMessage = activity.get().getString(R.string.no_internet_connection);
		}

		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		if (progressDialog.isShowing()){
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				FireCrash.log(e);
			}
		}
		if (errMessage != null) {
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
			dialogBuilder.withTitle(activity.get().getString(R.string.error_capital))
				.withMessage(errMessage)
				.show();
		}
		else if (result.equals("Success")){
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
			dialogBuilder.withTitle(activity.get().getString(R.string.success))
				.withMessage(activity.get().getString(R.string.task_submitted))
				.isCancelable(false)
				.withButton1Text(activity.get().getString(R.string.btnOk))
				.setButton1Click(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						OrderAssignmentActivity.isChange=true;
						activity.get().finish();
					}
				})
				.show();
		}
		else{
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(activity.get());
			dialogBuilder.withTitle(activity.get().getString(R.string.error_capital))
				.withMessage(result)
				.show();
		}
	}
}
